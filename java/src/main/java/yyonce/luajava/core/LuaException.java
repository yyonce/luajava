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
 * Exception.
 */
public final class LuaException extends Exception {
  private static final long serialVersionUID = 1L;

  public LuaException() {
    super();
  }

  public LuaException(String s) {
    super(s);
  }

  public LuaException(Throwable e) {
    super(e);
  }

  /**
   * Runtime exception.
   */
  public static final class Unchecked extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public Unchecked() {
      super();
    }

    public Unchecked(String s) {
      super(s);
    }

    public Unchecked(Throwable e) {
      super(e);
    }
  }
}
