set SOURCES=LEDArrayExample.java %SYNTHESIJER_EXTRA_LIB%\src\synthesijer\lib\led\*.java

set CLASSPATH=%SYNTHESIJER%;%SYNTHESIJER_EXTRA_LIB%\bin;.

copy %SYNTHESIJER_EXTRA_LIB%\hdl_lib\verilog\led_array.v .\

java -cp %CLASSPATH% synthesijer.Main --chaining --operation-strength-reduction --verilog %SOURCES%
