import "java.lang.CharSequence"
import "java.lang.Runnable"
import "java.lang.Thread"

-- Create an interface
local cs = CharSequence({
  length = function()
    return -1
  end
})
print(CharSequence.isInstance(cs)) -- true
print(cs.length()) -- -1

-- Create a function interface
local runnable = Runnable(function() print(123) end)
runnable.run()
local runnable = Runnable({
  run = function()
    print(123)
  end
})
runnable.run()

-- Parameter interface
local thread = Thread(function() print(123) end)
thread.run()
-- thread.start() -- Danger