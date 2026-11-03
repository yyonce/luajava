import yyonce.luajava.core.*;

public class Main {
  public static void main(String[] args) {
    try (Lua L = new LuaState()) {
      LuaHelper.doGlobalSet(L, true, 123, "hello");
      System.out.println(LuaHelper.doGlobalGet(L, true, 123)); // hello

      LuaHelper.doString(L, "function foo(a) return a end");
      System.out.println(
          LuaHelper.doGlobalCall(L, true, true, "foo", Main.class)
              == Main.class); // true
    }
  }
}
