import "array"
import "java.lang.String"

-- Create an array
local arr = array(String, 3)
print(#arr) -- 3
arr[0] = "lua"
arr[1] = "java"
arr[2] = nil
print(arr[0] .. arr[1]) -- luajava

-- Create a multi-dimensional array
local arr = array(String, 1, 2, 3)
print(#arr) -- 1
print(#arr[0]) -- 2
print(#arr[0][0]) -- 3

-- Fill array
local arr = array(String, 10)
arr { "", "", "A" }
print(arr[2] == "A") -- true
print(arr[9] == nil) -- true