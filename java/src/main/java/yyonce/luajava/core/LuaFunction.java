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
 * Lua function.
 */
public final class LuaFunction<T> extends LuaObjectImpl implements LuaObject {
  LuaFunction(Lua lua, int idx) {
    super(lua, idx);
  }

  @SuppressWarnings("unchecked")
  @Override
  public T call(Object... args) {
    return (T) super.call(args);
  }
}
