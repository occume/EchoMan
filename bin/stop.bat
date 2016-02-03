@echo off
echo ÕıÔÚ¹Ø±Õ£¬ÇëÉÔºò...
setlocal EnableDelayedExpansion
set classpath=.
for %%i in ("..\lib\*.jar") do set classpath=!classpath!;%%i
for %%j in ("..\lib_ext\*.jar") do set classpath=!classpath!;%%j
@REM launch echo %classpath%
endlocal & set classpath=%classpath%

java bootstrap.ShutDown %1 %2
