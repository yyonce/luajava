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
 * Lua state entry point, accessible only by the owner thread. Can no longer be used after it is closed.
 *
 * @see LuaState
 */
public interface Lua extends CPointer, LuaC, LuaS, LuaL, AutoCloseable {
  String LIBRARY = "luajava";

  /**
   * @return Owner thread.
   */
  Thread thread();

  /**
   * @return Java object mapping, internal access.
   */
  JavaObjectMap javaObjectMap();

  /**
   * @return Cleaner.
   */
  LuaCleaner cleaner();

  /**
   * @return Native pointer, not fixed.
   */
  @Override
  long getPointer();

  /**
   * @return If closed is true.
   */
  boolean isClosed();

  /**
   * Close.
   */
  @Override
  void close();

  /**
   * Push any object.
   */
  void pushObject(Object obj);

  /**
   * @return Any object.
   */
  Object toObject(int idx);

  /**
   * Push a java object.
   */
  void pushJavaObject(Object obj);

  /**
   * @return A java object.
   */
  Object toJavaObject(int idx);

  /**
   * @return Whether it is a java object.
   */
  boolean isJavaObject(int idx);
}
