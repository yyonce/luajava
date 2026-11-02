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

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final class Compatible {
  private Compatible() {}

  private static final Method INVOKE_DEFAULT;
  private static final Method PRIVATE_LOOKUP_IN;
  private static final Constructor<MethodHandles.Lookup> LOOKUP;

  static {
    Method invokeDefault = null;
    Method privateLookupIn = null;
    Constructor<MethodHandles.Lookup> lookup = null;
    try {
      invokeDefault = InvocationHandler.class.getMethod(
          "invokeDefault", Object.class, Method.class, Object[].class);
    } catch (NoSuchMethodException jdk16) {
      try {
        lookup = MethodHandles.Lookup.class.getDeclaredConstructor(
            Class.class, int.class);
        lookup.setAccessible(true);
      } catch (Exception jdk8) {
        lookup = null;
        try {
          privateLookupIn = MethodHandles.class.getMethod(
              "privateLookupIn", Class.class, MethodHandles.Lookup.class);
        } catch (NoSuchMethodException jdk9) {
        }
      }
    }
    INVOKE_DEFAULT = invokeDefault;
    PRIVATE_LOOKUP_IN = privateLookupIn;
    LOOKUP = lookup;
  }

  static Object invokeDefault(Object proxy, Method method, Object[] args)
      throws Throwable {
    try {
      if (INVOKE_DEFAULT == null
          && PRIVATE_LOOKUP_IN == null
          && LOOKUP == null) {
        throw new Error("invokeDefault");
      }
      if (INVOKE_DEFAULT != null) {
        return INVOKE_DEFAULT.invoke(null, proxy, method, args);
      }
      Class<?> declaring = method.getDeclaringClass();
      MethodHandles.Lookup lookup = PRIVATE_LOOKUP_IN != null
          ? (MethodHandles.Lookup)
              PRIVATE_LOOKUP_IN.invoke(null, declaring, MethodHandles.lookup())
          : LOOKUP.newInstance(declaring, -1);
      return lookup
          .unreflectSpecial(method, declaring)
          .bindTo(proxy)
          .invokeWithArguments(args);
    } catch (InvocationTargetException e) {
      Throwable c = e.getCause();
      throw c != null ? c : e;
    }
  }
}
