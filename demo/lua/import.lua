-- Import into the global table
import "java.lang.Object"
import {
  "java.lang.System",
  "java.lang.Class"
}
print(Object)
print(System)
print(Class)

-- Import into another table
local t = {}
import("java.lang.Object", t)
import({
  "java.lang.System",
  "java.lang.Class"
}, t)
print(t.Object)
print(t.System)
print(t.Class)