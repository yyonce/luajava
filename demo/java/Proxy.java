import yyonce.luajava.core.*;

public class Proxy {
  public static void main(String[] args) {
    try (Lua L = new LuaState()) {
      LuaHelper.doGlobalSet(L, true, "System", System.class);
      LuaHelper.doString(
          L, "function foo() System.out.println(\"from lua\") end");
      Runnable proxy;
      try {
        proxy = (Runnable) ((LuaObject) LuaHelper.doGlobalGet(L, true, "foo"))
            .createProxy(Runnable.class);
      } catch (LuaException e) {
        throw new LuaException.Unchecked(e);
      }
      proxy.run(); // from lua
    }
  }
}
