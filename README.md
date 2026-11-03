# LuaJava

LuaJava bridges Lua and Java in both directions,
letting Lua scripts access Java objects just like native Lua objects, and vice versa.

## Version

Lua 5.5, see the [Lua 5.5 Reference Manual](https://lua.org/manual/5.5).

## Features

The following are just examples; see the documentation for more.

### Bidirectional bridging
Lua to Java (`Class(args)`, `obj.field`, `obj.method(args)`),
Java to Lua (`L.getTop()`, `L.setTop(0)`).

### `long` handles
Java objects are `userdata` in Lua, holding a `long` handle
to avoid the JNI `GlobalRef` limit.

### Function interop
[JavaFunction](java/src/main/java/yyonce/luajava/core/JavaFunction.java) lets Java implement a Lua function callable from Lua;
[LuaFunction](java/src/main/java/yyonce/luajava/core/LuaFunction.java) lets Java call a Lua function directly.

## Requirements

- Java 8+
- Android 8+ (API 26+)

## Examples

[Lua](demo/lua)<br>
[Java](demo/java)

## Documentation

[LuaJava](docs/ljls/)<br>
[All documentation](docs/)

## License

[Java](LICENSE)<br>
[Native](native/LICENSE-PROPRIETARY)