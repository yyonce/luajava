## import

`import (class [, env])`

导入 Java 类到 Lua 环境，返回 `env`。

`class` 可以是字符串或表。
如果 `class` 是字符串，它是要导入的类名；
如果 `class` 是表，`import` 从 1 遍历到 `#class`，每一项必须是字符串。
`env` 必须是表，默认是全局表。

导入的类会放入 `env`，之后可以用类名构造对象或访问静态成员。
如果找不到类会错误。