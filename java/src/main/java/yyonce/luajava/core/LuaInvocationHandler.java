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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Internal implementation.
 */
final class LuaInvocationHandler implements InvocationHandler {
  public static Object newProxy(LuaObject luaObj, Class<?>... impls)
      throws LuaException {
    return Proxy.newProxyInstance(
        impls[0].getClassLoader(),
        impls,
        new LuaInvocationHandler(luaObj, impls));
  }

  private final Map<String, LuaObject> map = new HashMap<>();

  private LuaInvocationHandler(LuaObject luaObj, Class<?>... impls)
      throws LuaException {
    final LuaTable<?, ?> luaTable =
        luaObj instanceof LuaTable ? (LuaTable<?, ?>) luaObj : null;

    int abs = 0;
    Method absMethod = null;
    for (Class<?> impl : impls) {
      for (Method m : impl.getMethods()) {
        if (Modifier.isAbstract(m.getModifiers())) {
          abs++;
          absMethod = m;
        }
      }
    }

    // Not a table.
    if (luaTable == null) {
      if (abs != 1) {
        throw new LuaException();
      }
      map.put(absMethod.getName(), luaObj);
      return;
    }

    luaTable.forEach((k, v) -> {
      if (k instanceof String && v instanceof LuaObject) {
        map.put((String) k, (LuaObject) v);
      }
    });
  }

  private LuaObject get(Method method, String methodName) {
    return map.get(methodName);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args)
      throws Throwable {
    String methodName = method.getName();
    Class<?> returnType = method.getReturnType();

    // Intercept
    if (method.getDeclaringClass() == Object.class) {
      switch (methodName) {
        case "equals":
          return proxy == args[0];
        case "hashCode":
          return System.identityHashCode(proxy);
        case "toString":
          StringBuilder sb = new StringBuilder(toString());
          for (Class<?> c : proxy.getClass().getInterfaces()) {
            sb.append(':').append(c.getName());
          }
          return sb.toString();
        default:
          throw new InternalError(); // ?
      }
    }

    LuaObject lo = get(method, methodName);
    if (lo == null) {
      if (method.isDefault()) {
        return Compatible.invokeDefault(proxy, method, args);
      }
    }
    return to(lo != null ? lo.call(args) : null, returnType);
  }

  private Object to(Object obj, Class<?> tar) {
    // Default
    if (obj == null) {
      if (tar == Boolean.TYPE) {
        return false;
      }
      if (tar == Character.TYPE) {
        return (char) 0;
      }
      if (tar != Void.TYPE) {
        if (tar.isPrimitive()) {
          return (byte) 0;
        }
      }
      return null;
    }

    return LuaConverter.toJavaObject(obj, tar, obj);
  }
}
