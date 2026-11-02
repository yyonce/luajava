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
public final class LuaNumber extends Number implements LuaValue {
  private static final long serialVersionUID = 1L;

  private final double number;
  private final long integer;
  private final boolean isInteger;

  private LuaNumber(double n, long i, boolean iorn) {
    number = n;
    integer = i;
    isInteger = iorn;
  }

  @Override
  public int type() {
    return LuaC.LUA_TNUMBER;
  }

  @Override
  public int intValue() {
    return isInteger ? (int) integer : (int) number;
  }

  @Override
  public long longValue() {
    return isInteger ? integer : (long) number;
  }

  @Override
  public float floatValue() {
    return isInteger ? (float) integer : (float) number;
  }

  @Override
  public double doubleValue() {
    return isInteger ? (double) integer : number;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof LuaNumber) {
      LuaNumber n = (LuaNumber) o;
      if (isInteger && n.isInteger) {
        return integer == n.integer;
      }
      if (!isInteger && !n.isInteger) {
        return number == n.number;
      }
      if (isInteger && !n.isInteger) {
        return integer == n.number;
      }
      if (!isInteger && n.isInteger) {
        return number == n.integer;
      }
    }
    return false;
  }

  @Override
  public int hashCode() {
    return isInteger ? Long.hashCode(integer) : Double.hashCode(number);
  }

  @Override
  public String toString() {
    return isInteger ? String.valueOf(integer) : String.valueOf(number);
  }

  public boolean isInteger() {
    return isInteger;
  }

  final void push(Lua L) {
    if (isInteger) {
      L.pushInteger(integer);
      return;
    }
    L.pushNumber(number);
  }

  public static LuaNumber valueOf(double number) {
    return new LuaNumber(number, 0, false);
  }

  public static LuaNumber valueOf(long integer) {
    return new LuaNumber(0, integer, true);
  }

  static final LuaNumber valueOf(Lua L, int idx) {
    return L.isInteger(idx)
        ? valueOf(L.toInteger(idx))
        : valueOf(L.toNumber(idx));
  }
}
