SYNTHESIJER ?= ../../target/synthesijer
SYNTHESIJER_EXT_LIB ?= ../bin

all:
	java -cp $(SYNTHESIJER):$(SYNTHESIJER_EXT_LIB) synthesijer.Main --chaining --vhdl --verilog ../src/synthesijer/lib/wishbone/OpenCoresI2CMaster*.java OpenCoresI2CMasterIface_Test.java

clean:
	-rm -f *.class
	-rm -f *.dot
	-rm -f *.ir
	-rm -f *.tmp_info
	-rm -f *OpenCoresI2CMaster*.txt
	-rm -f *OpenCores*.vhd
	-rm -f *OpenCores*.v
	-rm -f ../src/synthesijer/lib/wishbone/*.class 
