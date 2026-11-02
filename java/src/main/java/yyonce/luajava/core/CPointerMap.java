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

import java.util.Arrays;

/**
 * For internal use.
 */
class CPointerMap<T> {
  private long[] cptrArray;
  private Object[] objArray;
  private boolean[] usedArray;

  private int size;

  private long cptrCache;
  private T objCache;
  private boolean usedCache;

  public CPointerMap() {
    this(64);
  }

  CPointerMap(boolean big) {
    this(big ? 1024 : 128);
  }

  protected CPointerMap(int cap) {
    if (cap < 0) {
      throw new IllegalArgumentException();
    }
    if (cap < 2) {
      cap = 2;
    }
    cptrArray = new long[cap];
    objArray = new Object[cap];
    usedArray = new boolean[cap];
  }

  protected int hash(long cptr, int length) {
    return Long.hashCode(cptr) & (length - 1);
  }

  protected int hashNext(int hash, int length) {
    return (hash + 1) & (length - 1);
  }

  @SuppressWarnings("unchecked")
  private void resize() {
    if (size > 0) {
      final int length = cptrArray.length;
      if (size >= length - (length >> 2)) { // .75
        final int newSize = length << 1;

        long[] cptrArr = cptrArray;
        Object[] objArr = objArray;
        boolean[] usedArr = usedArray;

        cptrArray = new long[newSize];
        objArray = new Object[newSize];
        usedArray = new boolean[newSize];

        size = 0;
        for (int i = 0; i < cptrArr.length; i++) {
          if (usedArr[i]) {
            put(cptrArr[i], (T) objArr[i]);
          }
        }
      }
    }
  }

  public int size() {
    return size;
  }

  public void clear() {
    Arrays.fill(cptrArray, 0);
    Arrays.fill(objArray, null);
    Arrays.fill(usedArray, false);
    size = 0;

    cptrCache = 0;
    objCache = null;
    usedCache = false;
  }

  public T get(long cptr) {
    if (usedCache && cptrCache == cptr) {
      return objCache;
    }

    int idx = hash(cptr, cptrArray.length);
    while (usedArray[idx]) {
      if (cptrArray[idx] == cptr) {
        @SuppressWarnings("unchecked")
        T obj = (T) objArray[idx];
        // Cache
        cptrCache = cptr;
        objCache = obj;
        usedCache = true;
        return obj;
      }
      idx = hashNext(idx, cptrArray.length);
    }
    return null;
  }

  public T put(long cptr, T obj) {
    if (usedCache && cptrCache == cptr) {
      objCache = obj;
    }

    resize();
    int idx = hash(cptr, cptrArray.length);
    while (usedArray[idx]) {
      if (cptrArray[idx] == cptr) {
        @SuppressWarnings("unchecked")
        T old = (T) objArray[idx];
        objArray[idx] = obj;
        return old;
      }
      idx = hashNext(idx, cptrArray.length);
    }
    putInternal(idx, cptr, obj);
    size++;
    return null;
  }

  public T remove(long cptr) {
    if (size > 0) {
      if (usedCache && cptrCache == cptr) {
        cptrCache = 0;
        objCache = null;
        usedCache = false;
      }

      int idx = hash(cptr, cptrArray.length);
      while (usedArray[idx]) {
        if (cptrArray[idx] == cptr) {
          @SuppressWarnings("unchecked")
          T old = (T) objArray[idx];
          removeInternal(idx);
          size--;
          closeDeletion(idx);
          return old;
        }
        idx = hashNext(idx, cptrArray.length);
      }
    }
    return null;
  }

  private void closeDeletion(int dIdx) {
    final int length = cptrArray.length;
    int idx = hashNext(dIdx, length);
    while (usedArray[idx]) {
      final long cptr = cptrArray[idx];
      final int rIdx = hash(cptr, length);
      if ((idx < rIdx && (rIdx <= dIdx || dIdx <= idx))
          || (rIdx <= dIdx && dIdx <= idx)) {
        putInternal(dIdx, cptr, objArray[idx]);
        removeInternal(idx);
        dIdx = idx;
      }
      idx = hashNext(idx, length);
    }
  }

  private void putInternal(int idx, long cptr, Object obj) {
    cptrArray[idx] = cptr;
    objArray[idx] = obj;
    usedArray[idx] = true;
  }

  private void removeInternal(int idx) {
    cptrArray[idx] = 0;
    objArray[idx] = null;
    usedArray[idx] = false;
  }
}
