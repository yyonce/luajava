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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Table implementation.
 */
public final class LuaTable<K, V> extends LuaObjectImpl
    implements LuaObject, Map<K, V> {
  /**
   * Create a new table.
   */
  public LuaTable(Lua lua) {
    super(lua);
    L.createTable(0, 0);
    ref(-1);
    L.pop(1);
  }

  LuaTable(Lua lua, int idx) {
    super(lua, idx);
  }

  public int length() {
    push();
    L.len(-1);
    long length = L.toInteger(-1);
    L.pop(2);
    return (int) length;
  }

  @Override
  public int size() {
    push();
    L.pushNil();
    int count = 0;
    while (L.next(-2) != 0) {
      count++;
      L.pop(1);
    }
    L.pop(1);
    return count;
  }

  @Override
  public boolean isEmpty() {
    push();
    L.pushNil();
    boolean empty = L.next(-2) == 0;
    L.pop(empty ? 1 : 3);
    return empty;
  }

  @SuppressWarnings("unchecked")
  @Override
  public V get(Object key) {
    return (V) super.get(key);
  }

  private V putImpl(Object key, Object value) {
    V old = get(key);
    set(key, value);
    return old;
  }

  @Override
  public V put(K key, V value) {
    return putImpl(key, value);
  }

  @Override
  public V remove(Object key) {
    return putImpl(key, null);
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> map) {
    map.forEach((k, v) -> set(k, v));
  }

  @Override
  public void clear() {
    push();
    L.pushNil();
    while (L.next(-2) != 0) {
      L.pop(1);
      L.pushValue(-1);
      L.pushNil();
      L.setTable(-4);
    }
    L.pop(1);
  }

  @Override
  public boolean containsKey(Object key) {
    push();
    L.pushObject(key);
    boolean b = L.getTable(-2) != LuaC.LUA_TNIL;
    L.pop(2);
    return b;
  }

  @Override
  public boolean containsValue(Object value) {
    if (value == null) return false;
    push();
    L.pushNil();
    boolean b = false;
    while (L.next(-2) != 0) {
      Object o = L.toObject(-1);
      L.pop(1);
      if (b = (o.equals(value))) {
        break;
      }
    }
    L.pop(b ? 2 : 1);
    return b;
  }

  /**
   * Snapshot.
   */
  @Override
  public Set<Map.Entry<K, V>> entrySet() {
    Set<Map.Entry<K, V>> s = new HashSet<>();
    forEach(
        (k, v) -> {
          s.add(new AbstractMap.SimpleEntry<>(k, v));
        },
        null);
    return s;
  }

  /**
   * Snapshot.
   */
  @Override
  public Set<K> keySet() {
    Set<K> s = new HashSet<>();
    forEach((k, v) -> s.add(k), true);
    return s;
  }

  /**
   * Snapshot.
   */
  @Override
  public Collection<V> values() {
    Collection<V> c = new ArrayList<>();
    forEach((k, v) -> c.add(v), false);
    return c;
  }

  @Override
  public void forEach(BiConsumer<? super K, ? super V> action) {
    forEach(action, null);
  }

  @SuppressWarnings("unchecked")
  private void forEach(BiConsumer<? super K, ? super V> action, Boolean type) {
    if (action == null) {
      throw new NullPointerException();
    }
    push();
    L.pushNil();
    while (L.next(-2) != 0) {
      K key = (type == null || type) ? (K) L.toObject(-2) : null;
      V value = (type == null || !type) ? (V) L.toObject(-1) : null;
      L.pop(1);
      try {
        action.accept(key, value);
      } catch (RuntimeException | Error e) {
        L.pop(2);
        throw e;
      }
    }
    L.pop(1);
  }

  @Override
  public V getOrDefault(Object key, V defaultValue) {
    V value = get(key);
    return value != null ? value : defaultValue;
  }
}
