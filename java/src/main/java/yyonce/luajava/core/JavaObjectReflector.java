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
 * Lua operations java object unified interface.
 * Modifying it will break the core operation and should not be modified at will.
 */
@Native
public interface JavaObjectReflector {
  /**
   * Globally unique and cached.
   */
  @Native
  JavaObjectReflector INSTANCE = JavaObjectReflectorInternal.create();

  /**
   * Just a placeholder.
   */
  int IGNORE = Integer.MIN_VALUE;

  /**
   * __index
   *
   * @return -1 for method.
   */
  @Native
  int index(long lua, long id, int type, String key, int key2, boolean intKey)
      throws Exception;

  /**
   * @return -1 for this.
   */
  @Native
  int indexCall(long lua, long id, int type, String name) throws Exception;

  /**
   * __newindex
   */
  @Native
  int newIndex(
      long lua, long id, int type, String key, int key2, boolean intKey)
      throws Exception;

  /**
   * __call
   */
  @Native
  int call(long lua, long id, int type) throws Exception;

  /**
   * __len
   */
  @Native
  int len(long lua, long id, int type) throws Exception;

  /**
   * __tostring
   */
  @Native
  int toString(long lua, long id, int type) throws Exception;

  /**
   * __eq
   */
  @Native
  int eq(long lua, long id, long id2, int type, int type2) throws Exception;

  /**
   * __gc
   */
  @Native
  int gc(long lua, long id, int type) throws Exception;

  /**
   * __close
   */
  @Native
  int close(long lua, long id, int type) throws Exception;
}
