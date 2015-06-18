library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.ALL;

entity simple_fifo_32 is
  
  generic (
    WIDTH : integer := 32;
    DEPTH : integer := 8
    );
  
  port (
    clk   : in  std_logic;
    reset : in  std_logic;
    we    : in  std_logic;
    din   : in  std_logic_vector(WIDTH-1 downto 0);
    re    : in  std_logic;
    dout  : out std_logic_vector(WIDTH-1 downto 0);
    empty : out std_logic;
    full  : out std_logic;
    count : out std_logic_vector(31 downto 0)
    );
end simple_fifo_32;

architecture RTL of simple_fifo_32 is

 component simple_fifo
  generic (
    WIDTH : integer := 32;
    DEPTH : integer := 8
    );
  port (
    clk   : in  std_logic;
    reset : in  std_logic;
    we    : in  std_logic;
    din   : in  std_logic_vector(WIDTH-1 downto 0);
    re    : in  std_logic;
    dout  : out std_logic_vector(WIDTH-1 downto 0);
    empty : out std_logic;
    full  : out std_logic;
    count : out std_logic_vector(31 downto 0)
    );
 end component;

begin
  
 U: simple_fifo
  generic map(
    WIDTH => 32,
    DEPTH => 8
    )
  port map(
    clk   => clk,
    reset => reset,
    we    => we,
    din   => din,
    re    => re,
    dout  => dout,
    empty => empty,
    full  => full,
    count => count
    );
  
end RTL;
