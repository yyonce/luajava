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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class JavaClass<T> {
  private static final JavaClass<?> CLASS = new JavaClass<>(Class.class);
  private static final Map<?, ?>[] CACHE = new Map<?, ?>[16];
  private static final Function<Class<?>, JavaClass<?>> NEW = JavaClass::new;

  static {
    for (int i = 0; i < CACHE.length; i++) {
      CACHE[i] = LuaJavaApi.lru(4000 / CACHE.length);
    }
  }

  @SuppressWarnings("unchecked")
  private static Map<Class<?>, JavaClass<?>> cache(int idx) {
    return (Map<Class<?>, JavaClass<?>>) CACHE[idx];
  }

  @SuppressWarnings("unchecked")
  public static <T> JavaClass<T> get(Class<T> c) {
    if (c == Class.class) {
      return (JavaClass<T>) CLASS;
    }
    int h = c.hashCode();
    int idx = (h ^ (h >>> 16)) & (CACHE.length - 1);
    synchronized (cache(idx)) {
      return (JavaClass<T>) cache(idx).computeIfAbsent(c, NEW);
    }
  }

  public static void remove(
      Predicate<Class<?>> remove, List<JavaClass<?>> list) {
    for (int i = 0; i < CACHE.length; i++) {
      synchronized (cache(i)) {
        cache(i)
            .entrySet()
            .removeIf(e -> remove.test(e.getKey())
                && (list == null || list.add(e.getValue())));
      }
    }
  }

  private final Object lockClasses = new Object();
  private final Object lockFields = new Object();
  private final Object lockConstructors = new Object();
  private final Object lockMethods = new Object();

  private final Class<T> ref;
  private volatile Object tag;
  private volatile List<Class<?>> classList;
  private volatile List<Field> fieldList;
  private volatile List<Constructor<?>> constructorList;
  private volatile List<Method> methodList;
  private volatile Map<String, Field> fieldMap;
  private volatile Map<String, Field> fieldMapStatic;
  private volatile Map<String, List<Method>> methodMap;
  private volatile Map<String, List<Method>> methodMapStatic;

  private JavaClass(Class<T> c) {
    if (c == null) {
      throw new NullPointerException();
    }
    ref = c;
  }

  public final Class<T> toClass() {
    return ref;
  }

  @SuppressWarnings("unchecked")
  <X> X tag(Supplier<X> init) {
    if (tag == null) {
      synchronized (init) {
        if (tag == null) {
          tag = init.get();
        }
      }
    }
    return (X) tag;
  }

  private <E> List<E> unmodifiableList(E[] arr) {
    return Collections.unmodifiableList(Arrays.asList(arr));
  }

  public List<Class<?>> getClasses() {
    if (classList == null) {
      synchronized (lockClasses) {
        if (classList == null) {
          classList = unmodifiableList(ref.getClasses());
        }
      }
    }
    return classList;
  }

  public List<Field> getFields() {
    if (fieldList == null) {
      synchronized (lockFields) {
        if (fieldList == null) {
          fieldList = unmodifiableList(ref.getFields());
        }
      }
    }
    return fieldList;
  }

  public List<Constructor<?>> getConstructors() {
    if (constructorList == null) {
      synchronized (lockConstructors) {
        if (constructorList == null) {
          constructorList = unmodifiableList(ref.getConstructors());
        }
      }
    }
    return constructorList;
  }

  public List<Method> getMethods() {
    if (methodList == null) {
      synchronized (lockMethods) {
        if (methodList == null) {
          methodList = unmodifiableList(ref.getMethods());
        }
      }
    }
    return methodList;
  }

  public Field getField(String name, boolean onlyStatic) {
    if (fieldMap == null || fieldMapStatic == null) {
      synchronized (lockFields) {
        if (fieldMap == null || fieldMapStatic == null) {
          Map<String, Field> map = new HashMap<>();
          Map<String, Field> mapStatic = new HashMap<>();
          for (Field f : getFields()) {
            String fieldName = f.getName();
            map.put(fieldName, f);
            if (Modifier.isStatic(f.getModifiers())) {
              mapStatic.put(fieldName, f);
            }
          }
          fieldMap = map;
          fieldMapStatic = mapStatic;
        }
      }
    }
    return (onlyStatic ? fieldMapStatic : fieldMap).get(name);
  }

  public List<Method> getMethod(String name, boolean onlyStatic) {
    if (methodMap == null || methodMapStatic == null) {
      synchronized (lockMethods) {
        if (methodMap == null || methodMapStatic == null) {
          Map<String, List<Method>> map = new HashMap<>();
          Map<String, List<Method>> mapStatic = new HashMap<>();
          for (Method m : getMethods()) {
            String methodName = m.getName();
            List<Method> list = map.get(methodName);
            if (list == null) {
              map.put(methodName, list = new ArrayList<>());
            }
            list.add(m);
            if (Modifier.isStatic(m.getModifiers())) {
              List<Method> listStatic = mapStatic.get(methodName);
              if (listStatic == null) {
                mapStatic.put(methodName, listStatic = new ArrayList<>());
              }
              listStatic.add(m);
            }
          }
          map.entrySet()
              .forEach(
                  e -> e.setValue(Collections.unmodifiableList(e.getValue())));
          mapStatic
              .entrySet()
              .forEach(
                  e -> e.setValue(Collections.unmodifiableList(e.getValue())));
          methodMap = map;
          methodMapStatic = mapStatic;
        }
      }
    }
    return (onlyStatic ? methodMapStatic : methodMap)
        .getOrDefault(name, Collections.emptyList());
  }
}
