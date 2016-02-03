@echo off
echo 正在启动，请稍候...
setlocal EnableDelayedExpansion
set classpath=.;..\*;..\lib\*;..\lib_ext\*
@REM launch echo %classpath%
echo %classpath%
endlocal & set classpath=%classpath%

java -Xms512m -Xmx1024m com.echoman.bootstrap.Bootstrap
echo 启动成功！
pause
