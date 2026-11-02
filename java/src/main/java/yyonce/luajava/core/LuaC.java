/*
 * Copyright 2026 yyonce
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package yyonce.luajava.core;

/**
 * Constants.
 */
interface LuaC {
  int LUA_OK = 0;
  int LUA_YIELD = 1;
  int LUA_ERRRUN = 2;
  int LUA_ERRSYNTAX = 3;
  int LUA_ERRMEM = 4;
  int LUA_ERRERR = 5;

  int LUA_TNONE = -1;
  int LUA_TNIL = 0;
  int LUA_TBOOLEAN = 1;
  int LUA_TLIGHTUSERDATA = 2;
  int LUA_TNUMBER = 3;
  int LUA_TSTRING = 4;
  int LUA_TTABLE = 5;
  int LUA_TFUNCTION = 6;
  int LUA_TUSERDATA = 7;
  int LUA_TTHREAD = 8;
  int LUA_NUMTYPES = 9;

  int LUA_MULTRET = -1;
  int LUA_REGISTRYINDEX = -(Integer.MAX_VALUE / 2 + 1000);
  int LUA_RIDX_GLOBALS = 2;
  int LUA_RIDX_MAINTHREAD = 3;
  int LUA_RIDX_LAST = 3;
  int LUA_MINSTACK = 20;

  int LUA_GCSTOP = 0;
  int LUA_GCRESTART = 1;
  int LUA_GCCOLLECT = 2;
  int LUA_GCCOUNT = 3;
  int LUA_GCCOUNTB = 4;
  int LUA_GCSTEP = 5;
  int LUA_GCISRUNNING = 6;
  int LUA_GCGEN = 7;
  int LUA_GCINC = 8;
  int LUA_GCPARAM = 9;
  int LUA_GCPMINORMUL = 0;
  int LUA_GCPMAJORMINOR = 1;
  int LUA_GCPMINORMAJOR = 2;
  int LUA_GCPPAUSE = 3;
  int LUA_GCPSTEPMUL = 4;
  int LUA_GCPSTEPSIZE = 5;
  int LUA_GCPN = 6;

  int LUA_OPEQ = 0;
  int LUA_OPLT = 1;
  int LUA_OPLE = 2;

  // lauxlib.h
  int LUA_ERRFILE = 6;
  int LUA_NOREF = -2;
  int LUA_REFNIL = -1;

  // lualib.h
  int LUA_GLIBK = 1;
  int LUA_LOADLIBK = LUA_GLIBK << 1;
  int LUA_COLIBK = LUA_LOADLIBK << 1;
  int LUA_DBLIBK = LUA_COLIBK << 1;
  int LUA_IOLIBK = LUA_DBLIBK << 1;
  int LUA_MATHLIBK = LUA_IOLIBK << 1;
  int LUA_OSLIBK = LUA_MATHLIBK << 1;
  int LUA_STRLIBK = LUA_OSLIBK << 1;
  int LUA_TABLIBK = LUA_STRLIBK << 1;
  int LUA_UTF8LIBK = LUA_TABLIBK << 1;
}
