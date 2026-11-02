package yyonce.luajava.core;

/**
 * JNI.
 */
public final class LuaJavaNative {
  private LuaJavaNative() {}

  static {
    System.loadLibrary(Lua.LIBRARY);
  }

  public static native void pushJavaObject(long p, long id, int type);

  public static native long toJavaObject(long p, int idx);

  public static native boolean isJavaObject(long p, int idx);
}
