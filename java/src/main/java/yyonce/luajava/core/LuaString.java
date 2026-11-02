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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Lua value wrapper class.
 */
public final class LuaString implements LuaValue {
  private final byte[] bytes;
  private int hash;
  private String copy;

  public LuaString(byte... b) {
    bytes = b != null ? b : new byte[0];
  }

  @Override
  public int type() {
    return LuaC.LUA_TSTRING;
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof LuaString
        && Arrays.equals(bytes, ((LuaString) o).bytes);
  }

  @Override
  public int hashCode() {
    if (hash != 0) {
      return hash;
    }
    return hash = Arrays.hashCode(bytes);
  }

  @Override
  public String toString() {
    if (copy != null) {
      return copy;
    }
    return copy = new String(bytes, StandardCharsets.UTF_8);
  }

  public String toString(Charset c) {
    return new String(bytes, c);
  }

  public int length() {
    return bytes.length;
  }

  public byte[] toBytes() {
    return bytes.clone();
  }

  final byte[] bytes() {
    return bytes;
  }
}
