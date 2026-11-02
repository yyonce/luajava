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
 * State.
 */
interface LuaS extends CPointer, LuaC {
  default int absIndex(int idx) {
    return LuaNative.absIndex(getPointer(), idx);
  }

  default int getTop() {
    return LuaNative.getTop(getPointer());
  }

  default void setTop(int idx) {
    LuaNative.setTop(getPointer(), idx);
  }

  default void pushValue(int idx) {
    LuaNative.pushValue(getPointer(), idx);
  }

  default void rotate(int idx, int n) {
    LuaNative.rotate(getPointer(), idx, n);
  }

  default void copy(int from, int to) {
    LuaNative.copy(getPointer(), from, to);
  }

  default int checkStack(int n) {
    return LuaNative.checkStack(getPointer(), n);
  }

  default void xmove(long to, int n) {
    LuaNative.xmove(getPointer(), to, n);
  }

  default void pop(int n) {
    LuaNative.pop(getPointer(), n);
  }

  default void insert(int idx) {
    LuaNative.insert(getPointer(), idx);
  }

  default void remove(int idx) {
    LuaNative.remove(getPointer(), idx);
  }

  default void replace(int idx) {
    LuaNative.replace(getPointer(), idx);
  }

  default void pushNil() {
    LuaNative.pushNil(getPointer());
  }

  default void pushBoolean(boolean b) {
    LuaNative.pushBoolean(getPointer(), b);
  }

  default void pushNumber(double n) {
    LuaNative.pushNumber(getPointer(), n);
  }

  default void pushInteger(long n) {
    LuaNative.pushInteger(getPointer(), n);
  }

  default void pushLString(byte[] bytes, int len) {
    LuaNative.pushLString(getPointer(), bytes, len);
  }

  default void pushString(String s) {
    LuaNative.pushString(getPointer(), s);
  }

  default void pushGlobalTable() {
    LuaNative.pushGlobalTable(getPointer());
  }

  default int pushThread() {
    return LuaNative.pushThread(getPointer());
  }

  default int type(int idx) {
    return LuaNative.type(getPointer(), idx);
  }

  default String typeName(int tp) {
    return LuaNative.typeName(getPointer(), tp);
  }

  default boolean isNumber(int idx) {
    return LuaNative.isNumber(getPointer(), idx);
  }

  default boolean isString(int idx) {
    return LuaNative.isString(getPointer(), idx);
  }

  default boolean isCFunction(int idx) {
    return LuaNative.isCFunction(getPointer(), idx);
  }

  default boolean isInteger(int idx) {
    return LuaNative.isInteger(getPointer(), idx);
  }

  default boolean isUserdata(int idx) {
    return LuaNative.isUserdata(getPointer(), idx);
  }

  default boolean isNil(int idx) {
    return LuaNative.isNil(getPointer(), idx);
  }

  default boolean isBoolean(int idx) {
    return LuaNative.isBoolean(getPointer(), idx);
  }

  default boolean isTable(int idx) {
    return LuaNative.isTable(getPointer(), idx);
  }

  default boolean isFunction(int idx) {
    return LuaNative.isFunction(getPointer(), idx);
  }

  default boolean isNone(int idx) {
    return LuaNative.isNone(getPointer(), idx);
  }

  default boolean isNoneOrNil(int idx) {
    return LuaNative.isNoneOrNil(getPointer(), idx);
  }

  default boolean isLightUserdata(int idx) {
    return LuaNative.isLightUserdata(getPointer(), idx);
  }

  default boolean isThread(int idx) {
    return LuaNative.isThread(getPointer(), idx);
  }

  default double toNumber(int idx) {
    return LuaNative.toNumber(getPointer(), idx);
  }

  default long toInteger(int idx) {
    return LuaNative.toInteger(getPointer(), idx);
  }

  default boolean toBoolean(int idx) {
    return LuaNative.toBoolean(getPointer(), idx);
  }

  default String toString(int idx) {
    return LuaNative.toString(getPointer(), idx);
  }

  default byte[] toLString(int idx) {
    return LuaNative.toLString(getPointer(), idx);
  }

  default long rawLen(int idx) {
    return LuaNative.rawLen(getPointer(), idx);
  }

  default long toCFunction(int idx) {
    return LuaNative.toCFunction(getPointer(), idx);
  }

  default long toUserdata(int idx) {
    return LuaNative.toUserdata(getPointer(), idx);
  }

  default long toThread(int idx) {
    return LuaNative.toThread(getPointer(), idx);
  }

  default long toPointer(int idx) {
    return LuaNative.toPointer(getPointer(), idx);
  }

  default boolean rawEqual(int idx1, int idx2) {
    return LuaNative.rawEqual(getPointer(), idx1, idx2);
  }

  default boolean compare(int idx1, int idx2, int op) {
    return LuaNative.compare(getPointer(), idx1, idx2, op);
  }

  default int getGlobal(String name) {
    return LuaNative.getGlobal(getPointer(), name);
  }

  default void setGlobal(String name) {
    LuaNative.setGlobal(getPointer(), name);
  }

  default int getTable(int idx) {
    return LuaNative.getTable(getPointer(), idx);
  }

  default int getField(int idx, String k) {
    return LuaNative.getField(getPointer(), idx, k);
  }

  default int getI(int idx, long n) {
    return LuaNative.getI(getPointer(), idx, n);
  }

  default int rawGet(int idx) {
    return LuaNative.rawGet(getPointer(), idx);
  }

  default int rawGetI(int idx, long n) {
    return LuaNative.rawGetI(getPointer(), idx, n);
  }

  default void createTable(int narr, int nrec) {
    LuaNative.createTable(getPointer(), narr, nrec);
  }

  default void setTable(int idx) {
    LuaNative.setTable(getPointer(), idx);
  }

  default void setField(int idx, String k) {
    LuaNative.setField(getPointer(), idx, k);
  }

  default void setI(int idx, long n) {
    LuaNative.setI(getPointer(), idx, n);
  }

  default void rawSet(int idx) {
    LuaNative.rawSet(getPointer(), idx);
  }

  default void rawSetI(int idx, long n) {
    LuaNative.rawSetI(getPointer(), idx, n);
  }

  default int next(int idx) {
    return LuaNative.next(getPointer(), idx);
  }

  default int getMetatable(int idx) {
    return LuaNative.getMetatable(getPointer(), idx);
  }

  default int setMetatable(int idx) {
    return LuaNative.setMetatable(getPointer(), idx);
  }

  default void call(int nargs, int nresults) {
    LuaNative.call(getPointer(), nargs, nresults);
  }

  default int pcall(int nargs, int nresults, int errfunc) {
    return LuaNative.pcall(getPointer(), nargs, nresults, errfunc);
  }

  default int yield(int nresults) {
    return LuaNative.yield(getPointer(), nresults);
  }

  default int resume(long from, int narg) {
    return LuaNative.resume(getPointer(), from, narg);
  }

  default int status() {
    return LuaNative.status(getPointer());
  }

  default boolean isYieldable() {
    return LuaNative.isYieldable(getPointer());
  }

  default int gc(int what, int... args) {
    return LuaNative.gc(getPointer(), what, args);
  }

  default int error() {
    return LuaNative.error(getPointer());
  }

  default void concat(int n) {
    LuaNative.concat(getPointer(), n);
  }

  default void len(int idx) {
    LuaNative.len(getPointer(), idx);
  }
}
