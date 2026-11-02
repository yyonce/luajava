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
 * Standard implementation.
 */
public class LuaState implements Lua {
  private final Thread thread = Thread.currentThread();
  private final JavaObjectMap javaObjectMap;
  private final LuaCleaner cleaner;
  private long cptr;

  @Override
  public final Thread thread() {
    return thread;
  }

  public final void checkThread() {
    if (Thread.currentThread() != thread) {
      throw new LuaError();
    }
  }

  public LuaState() {
    this(LuaJavaApi.jom());
  }

  public LuaState(JavaObjectMap jom) {
    if (jom == null) {
      throw new NullPointerException();
    }
    javaObjectMap = jom;
    cleaner = LuaJavaApi.cleaner(this);
    cptr = LuaNative.newState();
    onNew();
    LuaFactory.put(this);
  }

  protected void onNew() {
    new Import(this);
  }

  @Override
  public final JavaObjectMap javaObjectMap() {
    return javaObjectMap;
  }

  @Override
  public final LuaCleaner cleaner() {
    return cleaner;
  }

  @Override
  public final long getPointer() {
    cleaner.clean();
    return cptr;
  }

  @Override
  public final boolean isClosed() {
    return cptr == 0;
  }

  @Override
  public final void close() {
    if (!isClosed()) {
      LuaFactory.remove(cptr);
      LuaNative.close(cptr);
      cptr = 0;
      javaObjectMap.clear();
      cleaner.clear();
      onClose();
    }
  }

  protected void onClose() {}

  public LuaState openLibs() {
    LopenLibs();
    pushPrimitive();
    return this;
  }

  protected void pushPrimitive() {
    final boolean raw = true;
    LuaHelper.doGlobalSet(this, raw, "void", Void.TYPE);
    LuaHelper.doGlobalSet(this, raw, "boolean", Boolean.TYPE);
    LuaHelper.doGlobalSet(this, raw, "byte", Byte.TYPE);
    LuaHelper.doGlobalSet(this, raw, "short", Short.TYPE);
    LuaHelper.doGlobalSet(this, raw, "int", Integer.TYPE);
    LuaHelper.doGlobalSet(this, raw, "long", Long.TYPE);
    LuaHelper.doGlobalSet(this, raw, "float", Float.TYPE);
    LuaHelper.doGlobalSet(this, raw, "double", Double.TYPE);
    LuaHelper.doGlobalSet(this, raw, "char", Character.TYPE);
  }

  @Override
  public void pushObject(Object obj) {
    LuaConverter.pushObject(this, obj);
  }

  private void push(Object obj) {
    pushJavaObject(obj);
  }

  @Override
  public Object toObject(int idx) {
    return LuaConverter.toObject(this, idx, null, false);
  }

  @Override
  public void pushJavaObject(Object obj) {
    if (obj == null) {
      pushNil();
      return;
    }
    LuaJavaNative.pushJavaObject(
        cptr, javaObjectMap.put(obj), JavaObjectMap.Type.type(obj));
  }

  @Override
  public Object toJavaObject(int idx) {
    return javaObjectMap.get(LuaJavaNative.toJavaObject(cptr, idx));
  }

  @Override
  public boolean isJavaObject(int idx) {
    return LuaJavaNative.isJavaObject(cptr, idx);
  }
}
