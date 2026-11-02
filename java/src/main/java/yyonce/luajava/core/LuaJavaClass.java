/*
 * Copyright 2026 yyonce
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package yyonce.luajava.core;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public final class LuaJavaClass {
  private static final LuaJavaClass INSTANCE = new LuaJavaClass();

  public static LuaJavaClass get() {
    return INSTANCE;
  }

  private LuaJavaClass() {
    if (INSTANCE != null) {
      throw new LuaError();
    }
  }

  /**
   * Attempts to create an object from {@code clz}: array ({@code ARRAY} marker),
   * interface proxy, or instance via constructor. Pushes the result and returns 1.
   *
   * @param L      Lua instance
   * @param pidx   index of the first argument on the Lua stack
   * @param pcount number of arguments
   * @param clz    target class; {@code ARRAY} for array, interface for proxy, otherwise constructor
   * @return 1 (one value pushed)
   */
  public int newInstance(Lua L, int pidx, int pcount, Class<?> clz)
      throws Exception {
    if (L == null || clz == null) {
      throw new NullPointerException();
    }
    // Create an array
    if (clz == LuaJavaApi.ARRAY) {
      L.pushJavaObject(LuaJavaApi.newArray(L, pidx, pcount));
      return 1;
    }
    // Create a proxy
    if (clz.isInterface()) {
      if (pcount != 1) {
        throw new Exception("Number of bad parameters");
      }
      L.pushJavaObject(
          LuaConverter.toObject(L, pidx, LuaObject.class).createProxy(clz));
      return 1;
    }
    // Create an object
    L.pushJavaObject(
        doConstructor(L, pidx, pcount, null, LuaJavaApi.toWrapper(clz)));
    return 1;
  }

  /**
   * Attempts to invoke method {@code key} on {@code obj}.
   * Pushes the return value and returns 1, or returns -1 for void methods.
   *
   * @param L      Lua instance
   * @param pidx   index of the first argument on the Lua stack
   * @param pcount number of arguments
   * @param obj    receiver (instance or Class object)
   * @param type   receiver type (CLASS or OBJECT)
   * @param key    method name
   * @return 1 (return value pushed), or -1 for void methods
   */
  public int invoke(
      Lua L,
      int pidx,
      int pcount,
      Object obj,
      JavaObjectMap.Type type,
      String key)
      throws Exception {
    if (L == null || obj == null || type == null || key == null) {
      throw new NullPointerException();
    }
    boolean isClz = type == JavaObjectMap.Type.CLASS;
    Class<?> clz = toClz(type, obj);
    Object ret;
    try {
      ret = doMethod(L, pidx, pcount, null, clz, isClz, key, obj);
    } catch (InvocationTargetException e) {
      Throwable x = e.getCause();
      if (x == null) {
        throw e;
      }
      if (x instanceof Exception) {
        throw (Exception) x;
      }
      if (x instanceof Error) {
        throw (Error) x;
      }
      throw new InvocationTargetException(x, x.getMessage());
    }
    if (ret == LuaJavaApi.VOID) {
      return -1;
    }
    L.pushObject(ret);
    return 1;
  }

  /**
   * Attempts to read {@code obj[key]} with a string key.
   * Resolves inner classes (CLASS only), methods (returns -1, dispatched via indexCall),
   * fields, and Map entries.
   *
   * @param L    Lua instance
   * @param obj  receiver (instance or Class object)
   * @param type receiver type (CLASS or OBJECT)
   * @param key  property name
   * @return 1 (value pushed), or -1 if {@code key} is a method (dispatched via indexCall)
   */
  public int get(Lua L, Object obj, JavaObjectMap.Type type, String key)
      throws Exception {
    if (L == null || obj == null || type == null || key == null) {
      throw new NullPointerException();
    }
    boolean isClz = type == JavaObjectMap.Type.CLASS;
    Class<?> clz = toClz(type, obj);
    JavaClass<?> jclz = JavaClass.get(clz);
    // Find class
    if (isClz) {
      for (Class<?> c : jclz.getClasses()) {
        if (c.getName().endsWith(key)) {
          if (c.getSimpleName().equals(key)) {
            L.pushObject(c);
            return 1;
          }
        }
      }
    }
    // Find method
    if (hasMethod(jclz, isClz, key)) {
      return -1;
    }
    // Find field
    Field fid = jclz.getField(key, isClz);
    if (fid == null) {
      if (obj instanceof Map) {
        L.pushObject(((Map<?, ?>) obj).get(key));
        return 1;
      }
      throw new NoSuchFieldException(clz.getName() + ": " + key);
    }
    L.pushObject(fid.get(isClz ? null : obj));
    return 1;
  }

  /**
   * Attempts to read {@code obj[key]} with an integer key.
   * Supports arrays (type ARRAY) and Lists (obj instanceof List).
   *
   * @param L    Lua instance
   * @param obj  receiver (array or List)
   * @param type receiver type (ARRAY or OBJECT)
   * @param key  integer index
   * @return 1 (element pushed)
   */
  public int getI(Lua L, Object obj, JavaObjectMap.Type type, int key)
      throws Exception {
    if (L == null || obj == null || type == null) {
      throw new NullPointerException();
    }
    if (type == JavaObjectMap.Type.ARRAY) {
      L.pushObject(Array.get(obj, key));
      return 1;
    }
    if (obj instanceof List) {
      L.pushObject(((List<?>) obj).get(key));
      return 1;
    }
    Class<?> clz = toClz(type, obj);
    throw new NoSuchFieldException(clz.getName() + ": " + key);
  }

  /**
   * Attempts to write {@code obj[key] = value} with a string key.
   * Handles fields (static for CLASS, instance for OBJECT), Map entries, and
   * {@code onXxx} listeners.
   *
   * @param L    Lua instance
   * @param idx  index of the value on the Lua stack
   * @param obj  receiver (instance or Class object)
   * @param type receiver type (CLASS or OBJECT)
   * @param key  property name
   * @return 0 (no value pushed)
   */
  @SuppressWarnings("unchecked")
  public int set(
      Lua L, int idx, Object obj, JavaObjectMap.Type type, String key)
      throws Exception {
    if (L == null || obj == null || type == null || key == null) {
      throw new NullPointerException();
    }
    boolean isClz = type == JavaObjectMap.Type.CLASS;
    Class<?> clz = toClz(type, obj);
    JavaClass<?> jclz = JavaClass.get(clz);
    // Find field
    Field fid = jclz.getField(key, isClz);
    if (fid == null) {
      if (obj instanceof Map) {
        ((Map<Object, Object>) obj).put(key, L.toObject(idx));
        return 0;
      }
      // setListener
      if (key.length() > 2
          && key.startsWith("on")
          && LuaHelper.callable(L.type(idx))) {
        doMethod(
            L,
            idx,
            1,
            jclz,
            clz,
            isClz,
            "setOn" + key.substring(2) + "Listener",
            obj);
        return 0;
      }
      throw new NoSuchFieldException(clz.getName() + ": " + key);
    }
    fid.set(isClz ? null : obj, LuaConverter.toObject(L, idx, fid.getType()));
    return 0;
  }

  /**
   * Attempts to write {@code obj[key] = value} with an integer key.
   * Supports arrays (type ARRAY) and Lists (obj instanceof List).
   *
   * @param L    Lua instance
   * @param idx  index of the value on the Lua stack
   * @param obj  receiver (array or List)
   * @param type receiver type (ARRAY or OBJECT)
   * @param key  integer index
   * @return 0 (no value pushed)
   */
  @SuppressWarnings("unchecked")
  public int setI(Lua L, int idx, Object obj, JavaObjectMap.Type type, int key)
      throws Exception {
    if (L == null || obj == null || type == null) {
      throw new NullPointerException();
    }
    boolean isClz = type == JavaObjectMap.Type.CLASS;
    Class<?> clz = toClz(type, obj);
    if (type == JavaObjectMap.Type.ARRAY) {
      Array.set(
          obj, key, LuaConverter.toObject(L, idx, clz.getComponentType()));
      return 0;
    }
    if (obj instanceof List) {
      ((List<Object>) obj).set(key, L.toObject(idx));
      return 0;
    }
    throw new NoSuchFieldException(clz.getName() + ": " + key);
  }

  private static Class<?> toClz(JavaObjectMap.Type type, Object obj) {
    return type == JavaObjectMap.Type.CLASS ? (Class<?>) obj : obj.getClass();
  }

  /**
   * Internal
   */
  private static final class Internal {
    private static final class Key {
      private Class<?> clazz;
      private String name;
      private int[] type;
      private int hash;

      private Key update(Class<?> clz, String n, int[] t) {
        clazz = clz;
        name = n;
        type = t;
        int h = clazz.hashCode();
        h = 31 * h + Objects.hashCode(name);
        h = 31 * h + Arrays.hashCode(type);
        hash = h;
        return this;
      }

      @Override
      public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Key) {
          Key k = (Key) o;
          if (clazz == k.clazz
              && Objects.equals(name, k.name)
              && Arrays.equals(type, k.type)) {
            return true;
          }
        }
        return false;
      }

      @Override
      public int hashCode() {
        return hash;
      }
    }

    private static final class Value {
      private final Executable x;
      private final Class<?>[] paramsType;

      private Value(Executable e, Class<?>[] pt) {
        x = e;
        paramsType = pt;
      }
    }

    private static final class X {
      private final Key key = new Key();
      private final Map<Key, Value> map = LuaJavaApi.lru(40);
    }

    private static final Supplier<X> NEW = X::new;

    private static final class Arg {
      private static final int SIZE = 8;
      private final int[][] types = new int[SIZE][];
      private final Object[][] params = new Object[SIZE][];

      {
        for (int i = 0; i < types.length; i++) {
          types[i] = new int[i + 1];
        }
        for (int i = 0; i < params.length; i++) {
          params[i] = new Object[i + 1];
        }
      }

      private int[] types(int pcount) {
        return pcount <= SIZE ? types[pcount - 1] : new int[pcount];
      }

      private Object[] params(int pcount) {
        return pcount <= SIZE ? params[pcount - 1] : new Object[pcount];
      }
    }

    private static final ThreadLocal<Arg> ARG =
        ThreadLocal.withInitial(() -> new Arg());

    /**
     * Core method/constructor dispatcher: looks up a cached method or constructor
     * matching the argument types, converts the Lua arguments, invokes it, and
     * returns the result.
     *
     * @param L      Lua instance
     * @param pidx   index of the first argument on the Lua stack
     * @param pcount number of arguments
     * @param jclz   target class wrapper (lazily created if null)
     * @param clz    target class
     * @param isClz  true for Class object receiver, false for instance receiver
     * @param name   method name, or null for constructor
     * @param obj    receiver (Class object or instance); ignored for constructor
     * @return method return value, or {@link LuaJavaApi#VOID} for void methods
     */
    private static Object doExecutable(
        final Lua L,
        final int pidx,
        final int pcount,
        JavaClass<?> jclz,
        final Class<?> clz,
        final boolean isClz,
        String name,
        Object obj)
        throws Exception {
      final boolean corOrMtd = name == null;
      if (true) { // Optimization
        if (pcount == 0) {
          if (!corOrMtd) {
            switch (name) {
              case "getClass":
                return isClz ? clz.getClass() : obj.getClass();
              case "hashCode":
                return isClz ? clz.hashCode() : obj.hashCode();
              case "toString":
                return isClz ? clz.toString() : obj.toString();
              default:
                break;
            }
          }
        }
      }
      if (jclz == null) {
        jclz = JavaClass.get(clz);
      }
      X xobj = jclz.tag(NEW);
      Arg arg = pcount == 0 ? null : ARG.get();
      int[] types = pcount == 0 ? null : arg.types(pcount);
      for (int i = 0; i < pcount; i++) {
        types[i] = L.type(pidx + i);
      }
      Value value;
      synchronized (xobj) {
        value = xobj.map.get(xobj.key.update(clz, name, types));
      }
      if (value == null) {
        boolean skip = false;
        do {
          next:
          for (Executable x : (corOrMtd
              ? jclz.getConstructors()
              : obtainMethod(isClz, jclz, name, skip))) {
            if (x.getParameterCount() == pcount) {
              Class<?>[] paramsType = x.getParameterTypes();
              for (int i = 0; i < pcount; i++) {
                if (!LuaConverter.can(types[i], L, pidx + i, paramsType[i])) {
                  continue next;
                }
              }
              Key key = new Key()
                  .update(clz, name, types == null ? null : types.clone());
              value = new Value(x, paramsType);
              synchronized (xobj) {
                xobj.map.put(key, value);
              }
              break;
            }
          }
          if (value != null || corOrMtd || !isClz || skip) {
            break;
          }
          skip = true;
        } while (true);
        if (value == null) {
          throw new NoSuchMethodException(clz.getName() + ": "
              + Objects.toString(name, "<init>") + " pcount=" + pcount);
        }
      }
      Object[] params = pcount == 0 ? null : arg.params(pcount);
      try {
        for (int i = 0; i < pcount; i++) {
          params[i] =
              LuaConverter.to(types[i], L, pidx + i, value.paramsType[i]);
        }
        if (corOrMtd) {
          return ((Constructor<?>) value.x).newInstance(params);
        }
        Object receiver = isClz ? null : obj;
        if (isClz) {
          if (!Modifier.isStatic(value.x.getModifiers())) {
            receiver = clz; // obj
          }
        }
        Object ret = ((Method) value.x).invoke(receiver, params);
        return ((Method) value.x).getReturnType() == Void.TYPE
            ? LuaJavaApi.VOID
            : ret;
      } finally {
        if (params != null) {
          Arrays.fill(params, null);
        }
      }
    }

    /**
     * Returns candidate methods for {@code name} callable on the receiver:
     * all methods for an instance, static + Object methods for a Class object.
     *
     * @param isClz true for a Class object, false for an instance
     * @param jclz  the receiver's class wrapper
     * @param name  method name
     * @param skip  null: single full pass; false: first pass, static only;
     *              true: second pass, skip static and check Object only
     * @return candidate methods; empty if none are callable
     */
    private static List<Method> obtainMethod(
        boolean isClz, JavaClass<?> jclz, String name, Boolean skip) {
      if (!isClz) {
        return jclz.getMethod(name, false);
      }
      if (skip == null || !skip) {
        List<Method> list = jclz.getMethod(name, true);
        if (!list.isEmpty() || skip != null) {
          return list;
        }
      }
      return JavaClass.get(Class.class).getMethod(name, false);
    }
  }

  private final boolean hasMethod(
      JavaClass<?> jclz, boolean isClz, String name) {
    return !Internal.obtainMethod(isClz, jclz, name, null).isEmpty();
  }

  final Object doConstructor(
      Lua L, int pidx, int pcount, JavaClass<?> jclz, Class<?> clz)
      throws Exception {
    return Internal.doExecutable(L, pidx, pcount, jclz, clz, true, null, null);
  }

  final Object doMethod(
      Lua L,
      int pidx,
      int pcount,
      JavaClass<?> jclz,
      Class<?> clz,
      boolean isClz,
      String name,
      Object obj)
      throws Exception {
    return Internal.doExecutable(
        L, pidx, pcount, jclz, clz, isClz, name.toString(), obj);
  }
}
