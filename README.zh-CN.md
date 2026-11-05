# LuaJava

LuaJava 在 Lua 和 Java 之间做双向桥接，
让 Lua 脚本能像访问 Lua 对象一样访问 Java 对象，反之亦然

## 版本

Lua 5.5，参考 [Lua 5.5 Reference Manual](https://lua.org/manual/5.5)

## 特性

以下仅举例，更多请查看文档

### 双向桥接
Lua 到 Java（`Class(args)`、`obj.field`、`obj.method(args)`），
Java 到 Lua（`L.getTop()`、`L.setTop(0)`）

### `long` 句柄
Java 对象在 Lua 里是 `userdata` 存 `long` 句柄，
避免 JNI `GlobalRef` 上限

### 函数互调
[JavaFunction](java/src/main/java/yyonce/luajava/core/JavaFunction.java) 可以用 Java 实现一个 Lua 函数，让 Lua 直接调用；
[LuaFunction](java/src/main/java/yyonce/luajava/core/LuaFunction.java) 可以让 Java 直接调用 Lua 函数

## 要求

- Java 8+
- Android 8+（API 26+）

## 示例

[Lua](demo/lua)<br>
[Java](demo/java)

## 文档

[LuaJava](docs/ljls/)<br>
[全部文档](docs/)

## 许可证

[Java](LICENSE)<br>
[Native](native/LICENSE-PROPRIETARY)