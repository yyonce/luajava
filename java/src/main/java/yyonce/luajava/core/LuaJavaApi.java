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

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class LuaJavaApi {
  private LuaJavaApi() {
    throw new LuaError();
  }

  /**
   * Create a java array using lua table.
   */
  public static Object createArray(Lua L, int idx, Class<?> tar) {
    if (L == null || tar == null) throw new NullPointerException();
    if (!L.isTable(idx)) {
      throw new LuaException.Unchecked("Must be a table");
    }
    int len = (int) L.rawLen(idx);
    Object array = Array.newInstance(tar, len);
    fillArray(L, idx, array);
    return array;
  }

  /** Fill the array with a table. */
  static void fillArray(Lua L, int idx, Object array) {
    Class<?> tar = array.getClass().getComponentType();
    int len = (int) L.rawLen(idx);
    for (int i = 0; i < len; i++) {
      L.rawGetI(idx, i + 1);
      try {
        Array.set(array, i, LuaConverter.toObject(L, -1, tar));
      } finally {
        L.pop(1);
      }
    }
  }

  /**
   * Create a java list using lua table.
   */
  @SuppressWarnings("unchecked")
  public static <V> List<V> createList(Lua L, int idx, List<V> list) {
    if (!L.isTable(idx)) {
      throw new LuaException.Unchecked("Must be a table");
    }
    int len = (int) L.rawLen(idx);
    if (list == null) {
      list = new ArrayList<>(len);
    }
    for (int i = 0; i < len; i++) {
      L.rawGetI(idx, i + 1);
      try {
        list.add((V) L.toObject(-1));
      } finally {
        L.pop(1);
      }
    }
    return list;
  }

  /**
   * Create a java map using lua table.
   */
  @SuppressWarnings("unchecked")
  public static <K, V> Map<K, V> createMap(Lua L, int idx, Map<K, V> map) {
    if (!L.isTable(idx)) {
      throw new LuaException.Unchecked("Must be a table");
    }
    if (map == null) {
      map = new HashMap<>();
    }
    L.pushNil();
    while (L.next(idx) != 0) {
      Object k = L.toObject(-2);
      Object v = L.toObject(-1);
      try {
        map.put((K) k, (V) v);
      } catch (RuntimeException | Error e) {
        L.pop(2);
        throw e;
      }
      L.pop(1);
    }
    return map;
  }

  public static Object setter(Lua L, Object obj, String k, Object v)
      throws Exception {
    if (L == null || obj == null || k == null) throw new NullPointerException();
    L.pushObject(v);
    try {
      String name = k.isEmpty()
          ? "set"
          : "set" + Character.toUpperCase(k.charAt(0)) + k.substring(1);
      boolean isClz = obj instanceof Class;
      Object r = LuaJavaClass.get()
          .doMethod(
              L,
              -1, // L.getTop()
              1,
              null,
              isClz ? (Class<?>) obj : obj.getClass(),
              isClz,
              name,
              obj);
      return r == VOID ? obj : r;
    } finally {
      L.pop(1);
    }
  }

  public static Object getter(Lua L, Object obj, String k) throws Exception {
    if (L == null || obj == null || k == null) throw new NullPointerException();
    String name = k.isEmpty()
        ? "get"
        : "get" + Character.toUpperCase(k.charAt(0)) + k.substring(1);
    boolean isClz = obj instanceof Class;
    Object r = LuaJavaClass.get()
        .doMethod(
            L,
            0, // Not used
            0,
            null,
            isClz ? (Class<?>) obj : obj.getClass(),
            isClz,
            name,
            obj);
    return r == VOID ? obj : r;
  }

  /**
   * Gets the object length.
   */
  static int length(Object obj, boolean array, int err) {
    return (obj != null && array) ? Array.getLength(obj) : err;
  }

  /** Load class into table. */
  private static void classForName(Lua L, String name, int idx)
      throws ClassNotFoundException {
    Class<?> value = null;
    String key = null;
    switch (name) {
      case "array":
        value = ARRAY;
        key = name;
        break;
      default:
        break;
    }
    if (value == null) {
      value = PRIMITIVES.get(name);
      key = name;
      if (value == null) {
        value = Class.forName(name);
        key = value.getSimpleName();
        if (!Modifier.isPublic(value.getModifiers())) {
          throw new ClassNotFoundException(name);
        }
        if (key == null) key = ""; // Protection
      }
    }
    L.pushObject(key);
    L.pushObject(value);
    L.setTable(idx);
  }

  /**
   * The implementation of the import.
   *
   * @see Import
   */
  static void loadClass(Lua L, int pidx, int pcount) throws Exception {
    if (pcount < 1 || pcount > 2) {
      throw new Exception("Number of bad parameters");
    }
    int type = L.type(pidx);
    if (type != LuaC.LUA_TTABLE && type != LuaC.LUA_TSTRING) {
      throw new Exception("Must be a table or string");
    }
    if (pcount == 1) {
      L.pushGlobalTable();
    }
    if (!L.isTable(pidx + 1)) {
      throw new Exception("Must be a table");
    }
    if (type == LuaC.LUA_TTABLE) {
      for (Object v : createList(L, pidx, null)) {
        classForName(L, (String) v, pidx + 1);
      }
      return;
    }
    classForName(L, L.toString(pidx), pidx + 1);
  }

  /** Array class */
  static final Class<?> ARRAY = new Object() {}.getClass();

  static Object newArray(Lua L, int pidx, int pcount) throws Exception {
    if (pcount < 2) {
      throw new Exception("Number of bad parameters");
    }
    Class<?> tar = LuaConverter.toObject(L, pidx, Class.class);
    if (tar == null) {
      throw new Exception("No target class");
    }
    int[] dimensions = new int[pcount - 1];
    for (int i = 0; i < dimensions.length; i++) {
      if (!L.isInteger(pidx + i + 1)) {
        throw new Exception("Must be an integer");
      }
      dimensions[i] = (int) L.toInteger(pidx + i + 1);
    }
    return Array.newInstance(tar, dimensions);
  }

  static final Object VOID = new Object();
  static final Map<String, Class<?>> PRIMITIVES;

  static {
    Map<String, Class<?>> m = new HashMap<>();
    m.put("void", Void.TYPE);
    m.put("boolean", Boolean.TYPE);
    m.put("byte", Byte.TYPE);
    m.put("short", Short.TYPE);
    m.put("int", Integer.TYPE);
    m.put("long", Long.TYPE);
    m.put("float", Float.TYPE);
    m.put("double", Double.TYPE);
    m.put("char", Character.TYPE);
    PRIMITIVES = Collections.unmodifiableMap(m);
  }

  static Class<?> toWrapper(Class<?> c) {
    if (c.isPrimitive()) {
      if (c == Void.TYPE) return Void.class;
      if (c == Boolean.TYPE) return Boolean.class;
      if (c == Byte.TYPE) return Byte.class;
      if (c == Short.TYPE) return Short.class;
      if (c == Integer.TYPE) return Integer.class;
      if (c == Long.TYPE) return Long.class;
      if (c == Float.TYPE) return Float.class;
      if (c == Double.TYPE) return Double.class;
      if (c == Character.TYPE) return Character.class;
    }
    return c;
  }

  static <K, V> Map<K, V> lru(int max) {
    return new java.util.LinkedHashMap<K, V>(0, .75F, true) {
      @Override
      protected boolean removeEldestEntry(Map.Entry<K, V> e) {
        return size() > max;
      }
    };
  }

  /**
   * Generic implementation.
   *
   * @see JavaObjectMap
   */
  static JavaObjectMap jom() {
    return new JavaObjectMap() {
      private final CPointerMap<Object> objectMap = new CPointerMap<>(true);
      private final Map<Object, Long> idMap = new java.util.IdentityHashMap<>();
      private long objectId = 0;

      @Override
      protected void clear() {
        objectMap.clear();
        idMap.clear();
      }

      @Override
      protected int size() {
        return objectMap.size();
      }

      @Override
      protected Object get(long id) {
        if (id == 0) return null;
        return objectMap.get(id);
      }

      @Override
      protected long put(Object obj) {
        if (obj == null) return 0;
        Long id = idMap.get(obj);
        if (id != null) {
          return id;
        }
        ++objectId;
        if (objectId == 0) throw new Error();
        objectMap.put(objectId, obj);
        idMap.put(obj, objectId);
        return objectId;
      }

      @Override
      protected Object remove(long id) {
        if (id == 0) return null;
        Object obj = objectMap.remove(id);
        idMap.remove(obj);
        return obj;
      }
    };
  }

  /**
   * @see LuaCleaner
   */
  static LuaCleaner cleaner(Lua L) {
    return new LuaCleaner() {
      private final ReferenceQueue<Object> queue = new ReferenceQueue<>();
      private final Map<PhantomReference<Object>, Runnable> map =
          new HashMap<>();

      @Override
      public Lua lua() {
        return L;
      }

      @Override
      public Object register(Object obj, Runnable action) {
        if (obj == null || action == null) {
          return null;
        }
        PhantomReference<Object> ref = new PhantomReference<>(obj, queue);
        map.put(ref, action);
        return ref;
      }

      @Override
      public void unregister(Object token, boolean clean) {
        if (token == null) {
          return;
        }
        if (token instanceof PhantomReference) {
          ((PhantomReference<?>) token).clear();
          Runnable action = map.remove(token);
          if (action != null) {
            if (clean) action.run();
          }
        }
      }

      @Override
      public void clean() {
        if (map.isEmpty()) return;
        Object ref;
        while ((ref = queue.poll()) != null) {
          Runnable action = map.remove(ref);
          if (action != null) {
            try {
              action.run();
            } catch (Throwable e) {
            }
          }
        }
      }

      @Override
      protected void clear() {
        for (Runnable action : map.values()) {
          try {
            action.run();
          } catch (Throwable e) {
          }
        }
        map.clear();
        while (queue.poll() != null) {}
      }
    };
  }
}
