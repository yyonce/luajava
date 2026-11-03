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
 * Special objects that depend on Lua.
 */
public interface LuaObject extends LuaValue, AutoCloseable {
  /**
   * @return Owner.
   */
  Lua lua();

  /**
   * Release, allow gc.
   */
  @Override
  void close();

  void set(long idx, Object obj);

  Object get(long idx);

  void set(Object key, Object value);

  Object get(Object key);

  /**
   * @return The return value of the call, which may have multiple values.
   */
  Object[] call(int nret, Object[] args);

  /**
   * @return A return value.
   */
  default Object call(Object... args) {
    return call(1, args)[0];
  }

  /**
   * @return A java proxy
   */
  default Object createProxy(Class<?>... impls) throws LuaException {
    return LuaInvocationHandler.newProxy(this, impls);
  }
}
