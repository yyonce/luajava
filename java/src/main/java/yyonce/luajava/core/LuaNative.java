package yyonce.luajava.core;

/**
 * JNI.
 */
public final class LuaNative {
  private LuaNative() {}

  // State
  public static native long newState();

  public static native void close(long p);

  public static native long newThread(long p);

  public static native int closeThread(long p, long from);

  // Stack
  public static native int absIndex(long p, int idx);

  public static native int getTop(long p);

  public static native void setTop(long p, int idx);

  public static native void pushValue(long p, int idx);

  public static native void rotate(long p, int idx, int n);

  public static native void copy(long p, int from, int to);

  public static native int checkStack(long p, int n);

  public static native void xmove(long p, long to, int n);

  public static native void pop(long p, int n);

  public static native void insert(long p, int idx);

  public static native void remove(long p, int idx);

  public static native void replace(long p, int idx);

  // Push
  public static native void pushNil(long p);

  public static native void pushBoolean(long p, boolean b);

  public static native void pushNumber(long p, double n);

  public static native void pushInteger(long p, long n);

  public static native void pushLString(long p, byte[] bytes, int len);

  public static native void pushString(long p, String s);

  public static native void pushGlobalTable(long p);

  public static native int pushThread(long p);

  // Type
  public static native int type(long p, int idx);

  public static native String typeName(long p, int tp);

  public static native boolean isNumber(long p, int idx);

  public static native boolean isString(long p, int idx);

  public static native boolean isCFunction(long p, int idx);

  public static native boolean isInteger(long p, int idx);

  public static native boolean isUserdata(long p, int idx);

  public static native boolean isNil(long p, int idx);

  public static native boolean isBoolean(long p, int idx);

  public static native boolean isTable(long p, int idx);

  public static native boolean isFunction(long p, int idx);

  public static native boolean isNone(long p, int idx);

  public static native boolean isNoneOrNil(long p, int idx);

  public static native boolean isLightUserdata(long p, int idx);

  public static native boolean isThread(long p, int idx);

  public static native double toNumber(long p, int idx);

  public static native long toInteger(long p, int idx);

  public static native boolean toBoolean(long p, int idx);

  public static native String toString(long p, int idx);

  public static native byte[] toLString(long p, int idx);

  public static native long rawLen(long p, int idx);

  public static native long toCFunction(long p, int idx);

  public static native long toUserdata(long p, int idx);

  public static native long toThread(long p, int idx);

  public static native long toPointer(long p, int idx);

  // Compare
  public static native boolean rawEqual(long p, int idx1, int idx2);

  public static native boolean compare(long p, int idx1, int idx2, int op);

  // Table
  public static native int getGlobal(long p, String name);

  public static native void setGlobal(long p, String name);

  public static native int getTable(long p, int idx);

  public static native int getField(long p, int idx, String k);

  public static native int getI(long p, int idx, long n);

  public static native int rawGet(long p, int idx);

  public static native int rawGetI(long p, int idx, long n);

  public static native void createTable(long p, int narr, int nrec);

  public static native void setTable(long p, int idx);

  public static native void setField(long p, int idx, String k);

  public static native void setI(long p, int idx, long n);

  public static native void rawSet(long p, int idx);

  public static native void rawSetI(long p, int idx, long n);

  public static native int next(long p, int idx);

  // Metatable
  public static native int getMetatable(long p, int idx);

  public static native int setMetatable(long p, int idx);

  // Call
  public static native void call(long p, int nargs, int nresults);

  public static native int pcall(long p, int nargs, int nresults, int errfunc);

  // Coroutine
  public static native int yield(long p, int nresults);

  public static native int resume(long p, long from, int narg);

  public static native int status(long p);

  public static native boolean isYieldable(long p);

  // Gc
  public static native int gc(long p, int what, int... args);

  // Other
  public static native int error(long p);

  public static native void concat(long p, int n);

  public static native void len(long p, int idx);

  // Load
  public static native int LloadFile(long p, String path, String mode);

  public static native int LloadBuffer(
      long p, byte[] buff, String name, String mode);

  public static native int LloadString(long p, String s);

  public static native int LdoFile(long p, String path);

  public static native int LdoString(long p, String s);

  // Check
  public static native int LargError(long p, int arg, String extramsg);

  public static native int LtypeError(long p, int arg, String tname);

  // Metatable
  public static native int LnewMetatable(long p, String tname);

  public static native void LsetMetatable(long p, String tname);

  public static native int LgetMetatable(long p, String tname);

  public static native int LgetMetaField(long p, int obj, String e);

  public static native int LcallMeta(long p, int obj, String e);

  // Ref
  public static native int Lref(long p, int idx);

  public static native void Lunref(long p, int idx, int ref);

  // String
  public static native byte[] LtoLString(long p, int idx);

  // Lib
  public static native void LopenLibs(long p);

  public static native void LopenSelectedLibs(long p, int load, int preload);

  static {
    System.loadLibrary(Lua.LIBRARY);
  }
}
