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

import java.util.List;
import java.util.Map;

public final class LuaConverter {
  private LuaConverter() {
    throw new LuaError();
  }

  public static void pushObject(Lua L, Object obj) {
    if (L == null) throw new NullPointerException();
    if (obj == null) {
      L.pushNil();
      return;
    }
    if (obj instanceof LuaValue) {
      if (obj instanceof LuaBoolean) {
        L.pushBoolean(((LuaBoolean) obj).booleanValue());
        return;
      }
      if (obj instanceof LuaNumber) {
        ((LuaNumber) obj).push(L);
        return;
      }
      if (obj instanceof LuaString) {
        LuaString luaString = (LuaString) obj;
        L.pushLString(luaString.bytes(), luaString.length());
        return;
      }
      // LuaFunction
      // LuaTable
      // LuaThread
      if (obj instanceof LuaObjectImpl) {
        LuaObjectImpl luaObject = (LuaObjectImpl) obj;
        if (luaObject.lua() == L) {
          luaObject.push();
          return;
        }
      }
      L.pushJavaObject(obj);
      return;
    }
    if (obj instanceof Boolean) {
      L.pushBoolean((Boolean) obj);
      return;
    }
    if (pushNumberInternal(L, obj)) return;
    if (obj instanceof String) {
      L.pushString((String) obj);
      return;
    }
    L.pushJavaObject(obj);
  }

  public static <T> T toObject(Lua L, int idx, Class<T> tar) {
    return toObject(L, idx, tar, false);
  }

  public static <T> T toObject(Lua L, int idx, Class<T> tar, boolean wrap) {
    return toObjectInternal(L.type(idx), L, idx, tar, wrap);
  }

  @SuppressWarnings("unchecked")
  static <T> T toObjectInternal(
      int type, Lua L, int idx, Class<T> tar, boolean wrap) {
    if (wrap) {
      switch (type) {
        case LuaC.LUA_TNIL:
          if (tar != null && tar.isPrimitive()) {
            throw newCCE(tar, Object.class);
          }
          return null;
        case LuaC.LUA_TBOOLEAN:
          tryCCE(tar, LuaBoolean.class);
          return (T) LuaBoolean.valueOf(L.toBoolean(idx));
        case LuaC.LUA_TNUMBER:
          tryCCE(tar, LuaNumber.class);
          return (T) LuaNumber.valueOf(L, idx);
        case LuaC.LUA_TSTRING:
          tryCCE(tar, LuaString.class);
          return (T) new LuaString(L.toLString(idx));
        case LuaC.LUA_TFUNCTION:
          tryCCE(tar, LuaFunction.class);
          return (T) new LuaFunction<>(L, idx);
        case LuaC.LUA_TTABLE:
          tryCCE(tar, LuaTable.class);
          return (T) new LuaTable<>(L, idx);
        case LuaC.LUA_TUSERDATA:
        case LuaC.LUA_TLIGHTUSERDATA:
          tryCCE(tar, LuaObject.class);
          return (T) new LuaObjectImpl(L, idx);
        case LuaC.LUA_TTHREAD:
          tryCCE(tar, LuaThread.class);
          return (T) new LuaThread(L, idx);
        default:
          throw new LuaError();
      }
    }
    // No wrap
    try {
      return (T) to(type, L, idx, tar);
    } catch (LuaException e) {
      if (tar == null) throw new LuaError();
      throw new LuaException.Unchecked(e);
    }
  }

  /**
   * A generic conversion, to a specified java type, for any object.
   */
  public static Object toJavaObject(Object obj, Class<?> tar, Object err) {
    if (tar == null) throw new NullPointerException();
    if (obj == null) {
      return obj;
    }
    tar = LuaJavaApi.toWrapper(tar);
    if (obj instanceof LuaValue) {
      if (obj instanceof LuaBoolean) {
        return tar.isAssignableFrom(Boolean.class)
            ? ((LuaBoolean) obj).booleanValue()
            : err;
      }
      if (obj instanceof LuaNumber) {
        LuaNumber luaNumber = (LuaNumber) obj;
        Object nobj = toNumberInternal(
            luaNumber.isInteger(),
            luaNumber.longValue(),
            luaNumber.doubleValue(),
            tar,
            true);
        return nobj != null ? nobj : err;
      }
      if (obj instanceof LuaString) {
        return tar.isAssignableFrom(String.class)
            ? ((LuaString) obj).toString()
            : err;
      }
      return tar.isAssignableFrom(obj.getClass()) ? obj : err;
    }
    if (obj instanceof Boolean) {
      return tar.isAssignableFrom(Boolean.class) ? obj : err;
    }
    if (obj instanceof Number) {
      int tp = isNumber(obj);
      if (tp != 0) {
        Number number = (Number) obj;
        Object nobj = toNumberInternal(
            tp == INTEGER, number.longValue(), number.doubleValue(), tar, true);
        return nobj != null ? nobj : err;
      }
      return tar.isAssignableFrom(obj.getClass()) ? obj : err;
    }
    if (obj instanceof Character) {
      return tar.isAssignableFrom(Character.class) ? obj : err;
    }
    if (obj instanceof String) {
      return tar.isAssignableFrom(String.class) ? obj : err;
    }
    return tar.isAssignableFrom(obj.getClass()) ? obj : err;
  }

  /**
   * Can the Lua type be converted to the target java type.
   */
  static boolean can(int type, Lua L, int idx, Class<?> tar) {
    if (tar == null) throw new NullPointerException();
    switch (type) {
      case LuaC.LUA_TNIL:
        return !tar.isPrimitive();
      case LuaC.LUA_TBOOLEAN:
        return tar == Boolean.TYPE || tar.isAssignableFrom(Boolean.class);
      case LuaC.LUA_TNUMBER:
        return tar == Byte.TYPE
            || tar == Short.TYPE
            || tar == Integer.TYPE
            || tar == Long.TYPE
            || tar == Float.TYPE
            || tar == Double.TYPE
            || tar.isAssignableFrom(Byte.class)
            || tar.isAssignableFrom(Short.class)
            || tar.isAssignableFrom(Integer.class)
            || tar.isAssignableFrom(Long.class)
            || tar.isAssignableFrom(Float.class)
            || tar.isAssignableFrom(Double.class)
            || tar == Character.TYPE
            || tar.isAssignableFrom(Character.class);
      case LuaC.LUA_TSTRING:
        return tar.isAssignableFrom(String.class);
      case LuaC.LUA_TFUNCTION:
        return tar.isAssignableFrom(LuaFunction.class) || tar.isInterface();
      case LuaC.LUA_TTABLE:
        return tar.isAssignableFrom(LuaTable.class)
            || tar.isArray()
            || tar == List.class
            || tar == Map.class // ?
            || tar.isInterface();
      case LuaC.LUA_TUSERDATA:
      case LuaC.LUA_TLIGHTUSERDATA:
        Class<?> clz = LuaObject.class;
        if (L.isJavaObject(idx)) {
          Object jobj = L.toJavaObject(idx);
          if (jobj == null) return true;
          clz = jobj instanceof Class ? (Class<?>) jobj : jobj.getClass();
        }
        return tar.isAssignableFrom(clz);
      case LuaC.LUA_TTHREAD:
        return tar.isAssignableFrom(LuaThread.class);
      default:
        throw new LuaError();
    }
  }

  /**
   * Convert.
   */
  static Object to(int type, Lua L, int idx, Class<?> tar) throws LuaException {
    switch (type) {
      case LuaC.LUA_TNIL:
        if (tar != null && tar.isPrimitive()) {
          throw newCCE(tar, Object.class);
        }
        return null;
      case LuaC.LUA_TBOOLEAN:
        tryCCE(tar, Boolean.class);
        return L.toBoolean(idx);
      case LuaC.LUA_TNUMBER:
        boolean iorn = L.isInteger(idx);
        Object nobj = toNumberInternal(
            iorn,
            iorn ? L.toInteger(idx) : 0,
            iorn ? 0 : L.toNumber(idx),
            tar,
            true);
        if (nobj == null) throw newCCE(tar, iorn ? Long.class : Double.class);
        return nobj;
      case LuaC.LUA_TSTRING:
        tryCCE(tar, String.class);
        return L.toString(idx);
      case LuaC.LUA_TFUNCTION:
        if (tar == null || tar.isAssignableFrom(LuaFunction.class)) {
          return new LuaFunction<>(L, idx);
        }
        if (tar.isInterface()) {
          return new LuaFunction<>(L, idx).createProxy(tar);
        }
        throw newCCE(tar, LuaFunction.class);
      case LuaC.LUA_TTABLE:
        if (tar == null || tar.isAssignableFrom(LuaTable.class)) {
          return new LuaTable<>(L, idx);
        }
        if (tar.isArray()) {
          return LuaJavaApi.createArray(L, idx, tar.getComponentType());
        }
        if (tar == List.class) {
          return LuaJavaApi.createList(L, idx, null);
        }
        if (tar == Map.class) { // ?
          return new LuaTable<>(L, idx);
        }
        if (tar.isInterface()) {
          return new LuaTable<>(L, idx).createProxy(tar);
        }
        throw newCCE(tar, LuaTable.class);
      case LuaC.LUA_TUSERDATA:
      case LuaC.LUA_TLIGHTUSERDATA:
        if (L.isJavaObject(idx)) {
          Object jobj = L.toJavaObject(idx);
          if (jobj == null) return jobj;
          tryCCE(tar, jobj.getClass());
          return jobj;
        }
        tryCCE(tar, LuaObject.class);
        return new LuaObjectImpl(L, idx);
      case LuaC.LUA_TTHREAD:
        tryCCE(tar, LuaThread.class);
        return new LuaThread(L, idx);
      default:
        throw new LuaError();
    }
  }

  private static boolean pushNumberInternal(Lua L, Object obj) {
    if (obj instanceof Number) {
      int tp = isNumber(obj);
      if (tp == INTEGER) {
        L.pushInteger(((Number) obj).longValue());
        return true;
      }
      if (tp == FLOAT) {
        L.pushNumber(((Number) obj).doubleValue());
        return true;
      }
      L.pushJavaObject(obj);
      return true;
    }
    if (obj instanceof Character) {
      L.pushInteger((Character) obj);
      return true;
    }
    // No push
    return false;
  }

  private static Object toNumberInternal(
      boolean iorn, long i, double n, Class<?> tar, boolean checkCharacter) {
    if (tar == null) { // Object.class
      if (iorn) return i;
      return n;
    }
    if (tar == Byte.TYPE || tar == Byte.class) {
      return iorn ? (byte) i : (byte) n;
    }
    if (tar == Short.TYPE || tar == Short.class) {
      return iorn ? (short) i : (short) n;
    }
    if (tar == Integer.TYPE || tar == Integer.class) {
      return iorn ? (int) i : (int) n;
    }
    if (tar == Long.TYPE || tar == Long.class) {
      return iorn ? i : (long) n;
    }
    if (tar == Float.TYPE || tar == Float.class) {
      return iorn ? (float) i : (float) n;
    }
    if (tar == Double.TYPE || tar == Double.class) {
      return iorn ? (double) i : n;
    }
    if (tar.isAssignableFrom(Long.class)
        || tar.isAssignableFrom(Double.class)) {
      if (iorn) return i;
      return n;
    }
    if (checkCharacter && iorn) {
      if (tar == Character.TYPE
          || tar == Character.class
          || tar.isAssignableFrom(Character.class)) {
        return (char) i;
      }
    }
    return null;
  }

  private static final int INTEGER = 1;
  private static final int FLOAT = 2;

  private static int isNumber(Object obj) {
    if (obj instanceof Byte
        || obj instanceof Short
        || obj instanceof Integer
        || obj instanceof Long) {
      return INTEGER;
    }
    if (obj instanceof Float || obj instanceof Double) {
      return FLOAT;
    }
    return 0;
  }

  private static void tryCCE(Class<?> tar, Class<?> clz) {
    if (clz == null) throw new NullPointerException();
    if (tar == null) { // Object.class
      return;
    }
    if (tar == clz
        || LuaJavaApi.toWrapper(tar)
            .isAssignableFrom(LuaJavaApi.toWrapper(clz))) {
      return;
    }
    throw newCCE(tar, clz);
  }

  private static ClassCastException newCCE(Class<?> tar, Class<?> clz) {
    return new ClassCastException(
        "Cannot cast " + clz.getName() + " to " + tar.getName());
  }
}
