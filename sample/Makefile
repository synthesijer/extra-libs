SYNTHESIJER ?= ../../synthesijer/bin
SYNTHESIJER_EXT_LIB ?= ../bin

all:
	java -cp $(SYNTHESIJER):$(SYNTHESIJER_EXT_LIB) synthesijer.Main --chaining --vhdl --verilog ../src/synthesijer/lib/wishbone/OpenCoresI2CMaster*.java OpenCoresI2CMasterIface_Test.java
