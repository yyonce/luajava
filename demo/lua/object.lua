import "java.lang.Object"
import "java.lang.String"
import "java.lang.Boolean"
import "java.lang.Byte"
import "java.util.ArrayList"

-- Create an object
print(type(Object())) -- userdata
print(String("luajava") == String(String("luajava"))) -- true

-- Field
print(Boolean.TRUE) -- true
print(Byte.MAX_VALUE) -- 127

-- Method
local list = ArrayList()
list.add("")
list.add("luajava")
list.remove(0)
print(list.get(0)) -- luajava