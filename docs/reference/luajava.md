## luajava

`luajava._VERSION`

A string version number.

---

`luajava.open[key]`

Opens the auxiliary library.

`key` is a string key of the `open` table. `*` opens everything.
Otherwise it must be a Lua standard library name (supported libraries only; unsupported ones raise an error), and the corresponding standard library must exist for the call to succeed.
On success, returns `true` and adds the auxiliary functionality to the corresponding standard library.