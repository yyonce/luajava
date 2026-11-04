# LuaJava Semantics

LuaJava is source-compatible with Lua, but not bytecode-compatible.
On top of standard Lua, it adds bridging rules and syntax extensions.

[Standard Semantics](#standard-semantics)<br>
[Bridging](#bridging)<br>
[Syntax Extensions](#syntax-extensions)<br>
[Usage Extensions](#usage-extensions)

## Standard Semantics

The behavior of Lua's existing syntax, standard library, and metamethods is unchanged.
Standard Lua source code runs as-is.

## Bridging

### Objects

Java objects are represented as `userdata` in Lua,
with a metatable implementing the following metamethods:

- `__index`
- `__index` + `__call`, corresponding to `indexCall`
- `__newindex`
- `__call`
- `__len`
- `__tostring`
- `__eq`
- `__gc`
- `__close`

All metamethods are dispatched to [JavaObjectReflector](../../java/src/main/java/yyonce/luajava/core/JavaObjectReflector.java)
and implemented by [JavaObjectReflectorImpl](../../java/src/main/java/yyonce/luajava/core/JavaObjectReflectorImpl.java).

### Methods

When a method handle is obtained via `__index`, it must be called immediately (triggering `indexCall`).
Saving it and calling it repeatedly has undefined behavior.

### Types

Lua `nil` is Java `null`.
Lua `boolean` is Java `boolean` (or its wrapper class).
Lua `number` is Java `byte`, `short`, `int`, `long`, `float`, `double`, `char` (or their wrapper classes).
Lua `string` is Java `String`.

## Syntax Extensions

Besides standard Lua syntax, LuaJava adds some convenience syntax.

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

## Usage Extensions

See [Reference](../reference/)