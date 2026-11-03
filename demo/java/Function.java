import yyonce.luajava.core.*;

public class Function {
  public static void main(String[] args) {
    try (Lua L = new LuaState()) {
      LuaFunction<Object> print =
          ((LuaObject) LuaHelper.doGlobalGet(L, true, "print")).to();
      System.out.println(print.call(1, 2, "luajava")); // null
      // Override print
      new JavaFunction(L, "print", true) {
        @Override
        protected int call() {
          int top = L.getTop();
          for (int i = 2; i <= top; i++) {
            System.out.println(L.toObject(i));
          }
          L.pushInteger(top - 1);
          return 1;
        }
      };
      System.out.println(
          LuaHelper.doGlobalCall(L, true, true, "print", 1, 2, "luajava")); // 3
    }
  }
}
