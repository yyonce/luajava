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
 * Library.
 */
interface LuaL extends CPointer, LuaC {
  default int LloadFile(String path, String mode) {
    return LuaNative.LloadFile(getPointer(), path, mode);
  }

  default int LloadBuffer(byte[] buff, String name, String mode) {
    return LuaNative.LloadBuffer(getPointer(), buff, name, mode);
  }

  default int LloadString(String s) {
    return LuaNative.LloadString(getPointer(), s);
  }

  default int LdoFile(String path) {
    return LuaNative.LdoFile(getPointer(), path);
  }

  default int LdoString(String s) {
    return LuaNative.LdoString(getPointer(), s);
  }

  default int LargError(int arg, String extramsg) {
    return LuaNative.LargError(getPointer(), arg, extramsg);
  }

  default int LtypeError(int arg, String tname) {
    return LuaNative.LtypeError(getPointer(), arg, tname);
  }

  default int LnewMetatable(String tname) {
    return LuaNative.LnewMetatable(getPointer(), tname);
  }

  default void LsetMetatable(String tname) {
    LuaNative.LsetMetatable(getPointer(), tname);
  }

  default int LgetMetatable(String tname) {
    return LuaNative.LgetMetatable(getPointer(), tname);
  }

  default int LgetMetaField(int obj, String e) {
    return LuaNative.LgetMetaField(getPointer(), obj, e);
  }

  default int LcallMeta(int obj, String e) {
    return LuaNative.LcallMeta(getPointer(), obj, e);
  }

  default int Lref(int idx) {
    return LuaNative.Lref(getPointer(), idx);
  }

  default void Lunref(int idx, int ref) {
    LuaNative.Lunref(getPointer(), idx, ref);
  }

  default byte[] LtoLString(int idx) {
    return LuaNative.LtoLString(getPointer(), idx);
  }

  default void LopenLibs() {
    LuaNative.LopenLibs(getPointer());
  }

  default void LopenSelectedLibs(int load, int preload) {
    LuaNative.LopenSelectedLibs(getPointer(), load, preload);
  }
}
