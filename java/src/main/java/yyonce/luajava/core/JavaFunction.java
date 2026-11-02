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
 * Java implementation lua function.
 */
public abstract class JavaFunction {
  protected final Lua L;
  protected final String name;

  protected JavaFunction(Lua l, String n, boolean g) {
    if (l == null || n == null) {
      throw new NullPointerException();
    }
    L = l;
    name = n;
    if (g) {
      L.pushJavaObject(this);
      L.setGlobal(name);
    }
  }

  public Lua lua() {
    return L;
  }

  public String name() {
    return name;
  }

  @Override
  public String toString() {
    return name();
  }

  /**
   * @return Number of stacks pushed.
   */
  protected abstract int call() throws Exception;
}
