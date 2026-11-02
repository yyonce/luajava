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
 * Lua value wrapper class.
 */
public final class LuaBoolean implements LuaValue {
  public static final LuaBoolean TRUE = new LuaBoolean(true);
  public static final LuaBoolean FALSE = new LuaBoolean(false);

  private final boolean value;

  private LuaBoolean(boolean torf) {
    value = torf;
  }

  @Override
  public int type() {
    return LuaC.LUA_TBOOLEAN;
  }

  public boolean booleanValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof LuaBoolean && value == ((LuaBoolean) o).value;
  }

  @Override
  public int hashCode() {
    return Boolean.hashCode(value);
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  public static LuaBoolean valueOf(boolean torf) {
    return torf ? TRUE : FALSE;
  }
}
