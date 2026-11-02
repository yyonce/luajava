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

/**
 * Bidirectional interoperability between Lua and Java.
 *
 * <p><b>Lua operating on Java</b>: Java objects appear in Lua as userdata
 * holding a {@code long} handle, resolved by {@link JavaObjectMap}.
 * {@code obj.field}, {@code obj.method()} and {@code Class(args)} are
 * forwarded to Java reflection via the metamethods of
 * {@link JavaObjectReflector}; {@link JavaClass} caches reflection data.
 *
 * <p><b>Java operating on Lua</b>: through {@link Lua}'s stack/table/call API
 * ({@code pushInteger}, {@code getTable}, {@code pcall}); through
 * {@link LuaTable}, {@link LuaFunction} and {@link LuaObject} to operate on
 * Lua values; through {@link LuaHelper} to load and execute scripts.
 *
 * <p><b>Entry points</b>: {@link Lua} is the entry point, created via
 * {@code new LuaState()} and released by {@link LuaState#close()}.
 * {@link LuaFactory} registers and looks up {@link Lua} instances per thread;
 * it normally does not need to be called directly. {@link LuaConverter}
 * handles value conversion.
 *
 * <p><b>Resource release</b>: a {@link LuaObject} should be explicitly closed
 * when possible, giving deterministic release timing; when explicit close
 * cannot be guaranteed, {@link LuaCleaner} performs a fallback cleanup after
 * the object is GC'd, with non-deterministic timing and no guarantee of
 * promptness.
 *
 * <p><b>Core classes</b>:
 * <ul>
 *   <li>{@link Lua} / {@link LuaState} / {@link LuaFactory}: Lua state and
 *       thread management;</li>
 *   <li>{@link LuaValue} ({@link LuaBoolean} / {@link LuaNumber} /
 *       {@link LuaString}) / {@link LuaObject} ({@link LuaTable} /
 *       {@link LuaFunction} / {@link LuaThread}): Lua value abstraction;
 *       LuaValue is a pure value independent of Lua state, while LuaObject
 *       depends on Lua state and must be closed (table/function/userdata/thread);</li>
 *   <li>{@link LuaConverter}: Lua value ↔ Java value;</li>
 *   <li>{@link JavaObjectReflector}: Lua metamethod → Java reflection bridge;</li>
 *   <li>{@link LuaJavaClass}: reflection operations (get/set/invoke/newInstance);</li>
 *   <li>{@link JavaClass}: reflection data cache;</li>
 *   <li>{@link JavaObjectMap}: {@code long} handle → Java object;</li>
 *   <li>{@link LuaHelper}: script loading/execution, global calls;</li>
 *   <li>{@link JavaFunction}: a Lua function implemented in Java; subclasses
 *       implement {@code call()} and push return values;</li>
 *   <li>{@link LuaException} (including {@link LuaException.Unchecked}) /
 *       {@link LuaError}: checked/unchecked exceptions; LuaError is a severe error.</li>
 * </ul>
 */
package yyonce.luajava.core;
