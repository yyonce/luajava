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

/**
 * Implementation.
 */
class LuaObjectImpl implements LuaObject {
  protected final Lua L;
  private int type = LuaC.LUA_TNONE;
  private int ref = LuaC.LUA_NOREF;
  private Object tk;

  protected LuaObjectImpl(Lua lua) {
    if (lua == null) {
      throw new NullPointerException();
    }
    L = lua;
  }

  protected LuaObjectImpl(Lua lua, int idx) {
    this(lua);
    ref(idx);
  }

  protected final boolean refing() {
    return ref != LuaC.LUA_NOREF;
  }

  protected final void ref(int idx) {
    if (refing()) throw new LuaError();
    type = L.type(idx);
    L.pushValue(idx);
    ref = L.Lref(LuaC.LUA_REGISTRYINDEX);
    Lua l = L;
    int r = ref;
    tk = L.cleaner().register(this, () -> {
      if (!l.isClosed()) {
        l.Lunref(LuaC.LUA_REGISTRYINDEX, r);
      }
    });
  }

  protected final void unref() {
    if (!refing()) throw new LuaError();
    L.cleaner().unregister(tk, true);
    tk = null;
    type = LuaC.LUA_TNONE;
    ref = LuaC.LUA_NOREF;
  }

  protected final void push() {
    if (!refing()) throw new LuaError();
    L.rawGetI(LuaC.LUA_REGISTRYINDEX, ref);
  }

  @Override
  public final Lua lua() {
    return L;
  }

  @Override
  public final void close() {
    if (!L.isClosed()) {
      if (refing()) {
        unref();
      }
    }
  }

  @Override
  public final int type() {
    return type;
  }

  @Override
  public void set(long idx, Object obj) {
    push();
    LuaHelper.doSetI(L, false, idx, obj);
  }

  @Override
  public Object get(long idx) {
    push();
    return LuaHelper.doGetI(L, false, idx);
  }

  @Override
  public void set(Object key, Object value) {
    push();
    LuaHelper.doSet(L, false, key, value);
  }

  @Override
  public Object get(Object key) {
    push();
    return LuaHelper.doGet(L, false, key);
  }

  @Override
  public Object[] call(int nret, Object... args) {
    return LuaHelper.call(this, nret, args);
  }
}
