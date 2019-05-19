SYNTHESIJER ?= ../../synthesijer/bin
SYNTHESIJER_EXT_LIB ?= ../bin

all:
	java -cp $(SYNTHESIJER):$(SYNTHESIJER_EXT_LIB) synthesijer.Main --chaining --vhdl --verilog ../src/synthesijer/lib/wishbone/OpenCoresI2CMaster*.java OpenCoresI2CMasterIface_Test.java

clean:
	-rm -f *.class
	-rm -f OpenCoresI2CMasterIface_Test*.dot
	-rm -f OpenCoresI2CMasterIface_Test*.ir
	-rm -f OpenCoresI2CMasterIface_Test*.txt
	-rm -f OpenCoresI2CMasterIface_Test*.vhd
	-rm -f OpenCoresI2CMasterIface_Test*.v
	-rm -f ../src/synthesijer/lib/wishbone/*.class 
