## import

`import (class [, env])`

Imports Java classes into the Lua environment.

`class` can be a string or a table.
If `class` is a string, it is the name of the class to import;
if `class` is a table, `import` iterates from 1 to `#class`, and each item must be a string.
`env` must be a table, and defaults to the global table.

Imported classes are placed into `env`, and can then be used to construct objects or access static members.
An error is raised if a class cannot be found.