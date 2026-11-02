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

public final class LuaHelper {
  private LuaHelper() {
    throw new LuaError();
  }

  /**
   * Open the auxiliary.
   *
   * @return Open successfully returns true.
   */
  public static boolean open(Lua L) {
    return open(L, "*");
  }

  static boolean open(Lua L, String key) {
    L.pushGlobalTable();
    L.pushString("luajava");
    raw(L, true, true, -2);
    L.pushString("open");
    raw(L, true, true, -2);
    L.pushString(key);
    raw(L, false, true, -2);
    boolean ok = L.toBoolean(-1);
    L.pop(4);
    return ok;
  }

  public static Object doFile(Lua L, String path) {
    final int top = L.getTop();
    try {
      checkError(L, L.LdoFile(path));
      return L.getTop() - top > 0 ? L.toObject(-1) : null;
    } finally {
      L.setTop(top);
    }
  }

  public static Object doString(Lua L, String code) {
    final int top = L.getTop();
    try {
      checkError(L, L.LdoString(code));
      return L.getTop() - top > 0 ? L.toObject(-1) : null;
    } finally {
      L.setTop(top);
    }
  }

  /**
   * Call a global object if it can be called.
   *
   * @param check true: non-callable value returns null (no exception);
   *              false: non-callable value is passed to pcall and throws
   */
  public static Object doGlobalCall(
      Lua L, boolean raw, boolean check, Object key, Object... args) {
    final int top = L.getTop();
    try {
      L.pushGlobalTable();
      L.pushObject(key);
      raw(L, raw, true, -2);
      if (check && !callable(L.type(-1))) {
        return null;
      }
      checkError(L, pcall(L, 0, 1, args));
      return L.toObject(-1);
    } finally {
      L.setTop(top);
    }
  }

  public static Object doGlobalGet(Lua L, boolean raw, Object key) {
    L.pushGlobalTable();
    return doGet(L, raw, key);
  }

  public static void doGlobalSet(Lua L, boolean raw, Object key, Object value) {
    L.pushGlobalTable();
    doSet(L, raw, key, value);
  }

  static Object doGet(Lua L, boolean raw, Object key) {
    L.pushObject(key);
    raw(L, raw, true, -2);
    Object obj = L.toObject(-1);
    L.pop(2);
    return obj;
  }

  static void doSet(Lua L, boolean raw, Object key, Object value) {
    L.pushObject(key);
    L.pushObject(value);
    raw(L, raw, false, -3);
    L.pop(1);
  }

  static Object doGetI(Lua L, boolean raw, long idx) {
    rawI(L, raw, true, idx);
    Object obj = L.toObject(-1);
    L.pop(2);
    return obj;
  }

  static void doSetI(Lua L, boolean raw, long idx, Object obj) {
    L.pushObject(obj);
    rawI(L, raw, false, idx);
    L.pop(1);
  }

  private static void raw(Lua L, boolean raw, boolean get, int idx) {
    if (raw) {
      if (get) {
        L.rawGet(idx);
        return;
      }
      L.rawSet(idx);
      return;
    }
    if (get) {
      L.getTable(idx);
      return;
    }
    L.setTable(idx);
  }

  private static void rawI(Lua L, boolean raw, boolean get, long idx) {
    if (raw) {
      if (get) {
        L.rawGetI(-1, idx);
        return;
      }
      L.rawSetI(-2, idx);
      return;
    }
    if (get) {
      L.getI(-1, idx);
      return;
    }
    L.setI(-2, idx);
  }

  /**
   * @return Returns true if the type can be called.
   */
  static boolean callable(int type) {
    return type == LuaC.LUA_TFUNCTION
        || type == LuaC.LUA_TTABLE
        || type == LuaC.LUA_TUSERDATA;
  }

  static Object[] call(LuaObject luaObj, int nret, Object... args) {
    int type = luaObj.type();
    if (!callable(type)) {
      throw new LuaException.Unchecked("Cannot call: " + typeName(type));
    }
    Lua L = luaObj.lua();
    final int top = L.getTop();
    try {
      L.pushObject(luaObj);
      checkError(L, pcall(L, 0, nret, args));
      int ret = L.getTop() - top;
      Object[] r = new Object[nret == LuaC.LUA_MULTRET ? ret : nret];
      for (int i = 0; i < Math.min(ret, r.length); i++) {
        r[i] = L.toObject(top + i + 1);
      }
      return r;
    } finally {
      L.setTop(top);
    }
  }

  private static final Object[] EMPTY = new Object[0];

  private static int pcall(Lua L, int msgh, int nret, Object... args) {
    if (args == null) {
      args = EMPTY;
    }
    for (Object o : args) {
      L.pushObject(o);
    }
    return L.pcall(args.length, nret, msgh);
  }

  private static void checkError(Lua L, int ok) {
    if (ok != LuaC.LUA_OK) {
      throw new LuaException.Unchecked(error(L, -1));
    }
  }

  private static String error(Lua L, int idx) {
    int type = L.type(idx);
    return type == LuaC.LUA_TSTRING ? L.toString(idx) : typeName(type);
  }

  static String typeName(int type) {
    switch (type) {
      case LuaC.LUA_TNONE:
        return "no value";
      case LuaC.LUA_TNIL:
        return "nil";
      case LuaC.LUA_TBOOLEAN:
        return "boolean";
      case LuaC.LUA_TNUMBER:
        return "number";
      case LuaC.LUA_TSTRING:
        return "string";
      case LuaC.LUA_TFUNCTION:
        return "function";
      case LuaC.LUA_TTABLE:
        return "table";
      case LuaC.LUA_TUSERDATA:
      case LuaC.LUA_TLIGHTUSERDATA:
        return "userdata";
      case LuaC.LUA_TTHREAD:
        return "thread";
      default:
        return "unknown";
    }
  }
}
