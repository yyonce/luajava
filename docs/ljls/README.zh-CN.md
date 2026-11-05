# LuaJava 语义

LuaJava 兼容 Lua 源码，不兼容字节码。
在标准 Lua 之上，它还增加了桥接定义和语法扩展。

[标准语义](#标准语义)<br>
[桥接定义](#桥接定义)<br>
[语法扩展](#语法扩展)<br>
[用法扩展](#用法扩展)

## 标准语义

Lua 原有语法、标准库、元方法的行为不变。
标准 Lua 源码可以直接运行。

## 桥接定义

### 对象

Java 对象在 Lua 中用 `userdata` 表示，
并且有元表实现了元方法：

- `__index`
- `__index` + `__call`，对应 `indexCall`
- `__newindex`
- `__call`
- `__len`
- `__tostring`
- `__eq`
- `__gc`
- `__close`

所有元方法都会转到 [JavaObjectReflector](../../java/src/main/java/yyonce/luajava/core/JavaObjectReflector.java) 触发，
由 [JavaObjectReflectorImpl](../../java/src/main/java/yyonce/luajava/core/JavaObjectReflectorImpl.java) 实现。

### 方法

当通过 `__index` 拿到方法句柄，必须立马调用它（触发 `indexCall`），
保存起来和重复调用行为未定义。

### 类型

Lua `nil` 是 Java `null`。
Lua `boolean` 是 Java `boolean` （包括包装类）。
Lua `number` 是 Java `byte`、`short`、`int`、`long`、`float`、`double`、`char` （包括包装类）。
Lua `string` 是 Java `String`。

## 语法扩展

除了标准 Lua 语法，LuaJava 扩展了一些便捷的语法。

```lua
--[==[
switch

    switch <expr> [do]
      case <expr> [, <expr>]* [then] <block>
      [default [then]] <block>
    end

lambda

    lambda <parlist> : <expr>
    lambda <parlist> : { <block> }

if

    if <expr> [then] <block> end
]==]

local foo = lambda x, y, z : x + y + z
print(foo(1, 2, 3)) -- 6
```

## 用法扩展

参考 [Reference](../reference/)