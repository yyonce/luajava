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
 * Management of Lua.
 */
public final class LuaFactory {
  private LuaFactory() {}

  private static final ThreadLocal<CPointerMap<Lua>> map =
      ThreadLocal.withInitial(() -> new CPointerMap<>(false));

  public static Lua get(long cptr) {
    return map.get().get(cptr);
  }

  public static long put(Lua L) {
    if (L.isClosed()) {
      throw new LuaError();
    }
    long cptr = L.getPointer();
    map.get().put(cptr, L);
    return cptr;
  }

  public static Lua remove(long cptr) {
    Lua L = map.get().remove(cptr);
    if (L == null) {
      throw new LuaError();
    }
    return L;
  }
}
