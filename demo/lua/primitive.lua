import "void"
import "boolean"
import "byte"
import "short"
import "int"
import "long"
import "float"
import "double"
import "char"
import "java.lang.Void"
import "java.lang.Boolean"
import "java.lang.Byte"
import "java.lang.Short"
import "java.lang.Integer"
import "java.lang.Long"
import "java.lang.Float"
import "java.lang.Double"
import "java.lang.Character"

print(void == Void.TYPE) -- true
print(boolean == Boolean.TYPE) -- true
print(byte == Byte.TYPE) -- true
print(short == Short.TYPE) -- true
print(int == Integer.TYPE) -- true
print(long == Long.TYPE) -- true
print(float == Float.TYPE) -- true
print(double == Double.TYPE) -- true
print(char == Character.TYPE) -- true

print(type(Boolean.valueOf(true))) -- boolean
print(type(Integer.valueOf(123))) -- number
print(type(Double.valueOf(123.4))) -- number
print(type(Character.valueOf(123))) -- number