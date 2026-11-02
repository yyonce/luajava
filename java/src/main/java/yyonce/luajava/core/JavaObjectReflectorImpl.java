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

final class JavaObjectReflectorImpl implements JavaObjectReflector {
  /**
   * __index
   * [obj,k]
   */
  @Override
  public int index(
      long lua, long id, int type, String key, int key2, boolean intKey)
      throws Exception {
    Lua L = LuaFactory.get(lua);
    nonNull(L);
    Object obj = L.javaObjectMap().get(id);
    nonNull(obj);
    illegalKey(!intKey && key == null);
    return !intKey
        ? LuaJavaClass.get().get(L, obj, JavaObjectMap.Type.of(type), key)
        : LuaJavaClass.get().getI(L, obj, JavaObjectMap.Type.of(type), key2);
  }

  /**
   * [...]
   */
  @Override
  public int indexCall(long lua, long id, int type, String name)
      throws Exception {
    Lua L = LuaFactory.get(lua);
    nonNull(L);
    Object obj = L.javaObjectMap().get(id);
    nonNull(obj);
    illegalKey(name == null);
    return LuaJavaClass.get()
        .invoke(L, 1, L.getTop(), obj, JavaObjectMap.Type.of(type), name);
  }

  /**
   * __newindex
   * [obj,k,v]
   */
  @Override
  public int newIndex(
      long lua, long id, int type, String key, int key2, boolean intKey)
      throws Exception {
    Lua L = LuaFactory.get(lua);
    nonNull(L);
    Object obj = L.javaObjectMap().get(id);
    nonNull(obj);
    illegalKey(!intKey && key == null);
    int x = !intKey
        ? LuaJavaClass.get().set(L, 3, obj, JavaObjectMap.Type.of(type), key)
        : LuaJavaClass.get().setI(L, 3, obj, JavaObjectMap.Type.of(type), key2);
    return IGNORE;
  }

  /**
   * __call
   * [obj,...]
   */
  @Override
  public int call(long lua, long id, int type) throws Exception {
    Lua L = LuaFactory.get(lua);
    nonNull(L);
    Object obj = L.javaObjectMap().get(id);
    nonNull(obj);
    switch (JavaObjectMap.Type.of(type)) {
      case CLASS:
        return LuaJavaClass.get()
            .newInstance(L, 2, L.getTop() - 1, (Class<?>) obj);
      case OBJECT:
        if (obj instanceof JavaFunction) {
          return ((JavaFunction) obj).call();
        }
        throw newUOE("Cannot call an object");
      case ARRAY:
        if (L.getTop() == 2 && L.isTable(2)) {
          LuaJavaApi.fillArray(L, 2, obj);
          L.setTop(1);
          return 1;
        }
        throw newUOE("Cannot call an array");
      default:
        throw new LuaError();
    }
  }

  /**
   * __len
   */
  @Override
  public int len(long lua, long id, int type) throws Exception {
    Lua L = LuaFactory.get(lua);
    nonNull(L);
    Object obj = L.javaObjectMap().get(id);
    nonNull(obj);
    L.pushInteger(LuaJavaApi.length(obj, JavaObjectMap.Type.ARRAY.is(type), 0));
    return 1;
  }

  /**
   * __tostring
   */
  @Override
  public int toString(long lua, long id, int type) throws Exception {
    Lua L = LuaFactory.get(lua);
    nonNull(L);
    Object obj = L.javaObjectMap().get(id);
    nonNull(obj);
    L.pushString(obj.toString());
    return 1;
  }

  /**
   * __eq
   */
  @Override
  public int eq(long lua, long id, long id2, int type, int type2)
      throws Exception {
    Lua L = LuaFactory.get(lua);
    nonNull(L);
    Object obj = L.javaObjectMap().get(id);
    nonNull(obj);
    Object obj2 = L.javaObjectMap().get(id2);
    nonNull(obj2);
    L.pushBoolean(obj.equals(obj2));
    return 1;
  }

  /**
   * __gc
   */
  @Override
  public int gc(long lua, long id, int type) throws Exception {
    Lua L = LuaFactory.get(lua);
    // Not closed.
    if (L != null) {
      L.javaObjectMap().remove(id);
    }
    return IGNORE;
  }

  /**
   * __close
   */
  @Override
  public int close(long lua, long id, int type) throws Exception {
    Lua L = LuaFactory.get(lua);
    nonNull(L);
    Object obj = L.javaObjectMap().get(id);
    nonNull(obj);
    if (obj instanceof AutoCloseable) {
      ((AutoCloseable) obj).close();
    }
    return IGNORE;
  }

  final void nonNull(Object obj) {
    if (obj == null) {
      throw new LuaError();
    }
  }

  final void illegalKey(boolean b) throws Exception {
    if (b) {
      throw newUOE("Illegal key");
    }
  }

  Exception newUOE(String msg) {
    return new Exception(msg);
  }
}
