SYNTHESIJER ?= ../../synthesijer.jar
SYNTHESIJER_EXT_LIB ?= ../bin

all:
	java -cp $(SYNTHESIJER):$(SYNTHESIJER_EXT_LIB) synthesijer.Main --chaining --vhdl --verilog ../src/synthesijer/lib/wishbone/OpenCoresI2CMaster*.java OpenCoresI2CMasterIface_Test.java

clean:
	-rm -f *.class
	-rm -f OpenCoresI2CMaster*.dot
	-rm -f OpenCoresI2CMaster*.ir
	-rm -f OpenCoresI2CMaster*.txt
	-rm -f OpenCoresI2CMaster*.vhd
	-rm -f OpenCoresI2CMaster*.v
	-rm -f ../src/synthesijer/lib/wishbone/*.class 
