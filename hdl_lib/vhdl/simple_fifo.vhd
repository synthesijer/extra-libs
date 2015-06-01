library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.ALL;

entity simple_fifo is
  
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
end simple_fifo;

architecture RTL of simple_fifo is

  constant FIFO_SIZE : integer := 2**DEPTH;

  signal head   : signed(31 downto 0);
  signal tail   : signed(31 downto 0);
  signal looped : std_logic;
  
  type MEMORY is array (0 to FIFO_SIZE-1) of std_logic_vector(WIDTH-1 downto 0);
  signal mem : MEMORY;

  attribute ram_style : string;
  attribute ram_style of mem : signal is "distributed";

begin
  
  process (clk)
    
    variable head_tmp   : signed(31 downto 0) := (others => '0');
    variable tail_tmp   : signed(31 downto 0) := (others => '0');
    variable count_tmp  : signed(31 downto 0) := (others => '0');
    variable looped_tmp : std_logic;
    variable full_tmp   : std_logic;
    variable empty_tmp  : std_logic;
    
  begin
            
    if clk'event and clk = '1' then
      
      head_tmp   := head;
      tail_tmp   := tail;
      looped_tmp := looped;
      count_tmp  := (others => '0');
    
      if reset = '1' then
        
        head_tmp   := (others => '0');
        tail_tmp   := (others => '0');
        looped_tmp := '0';
        full_tmp   := '0';
        empty_tmp  := '1';
        
      else
        
        if re = '1' then
          if (looped_tmp = '1') or (head_tmp /= tail_tmp) then
            dout <= mem(to_integer(tail_tmp));
            if tail_tmp = FIFO_SIZE- 1 then
              tail_tmp := (others => '0');
              looped_tmp := '0';
            else
              tail_tmp := tail_tmp + 1;
            end if;
          end if;
        end if;
        
        if we = '1' then
          if (looped_tmp = '0') or (head_tmp /= tail_tmp) then
            mem(to_integer(head_tmp)) <= din;
            if head_tmp = FIFO_SIZE - 1 then
              head_tmp   := (others => '0');
              looped_tmp := '1';
            else
              head_tmp := head_tmp + 1;
            end if;
          end if;
        end if;
        
        if head_tmp = tail_tmp then
          if looped_tmp = '1' then
            full_tmp  := '1';
            empty_tmp := '0';
            count_tmp := to_signed(FIFO_SIZE, count_tmp'length);
          else
            full_tmp  := '0';
            empty_tmp := '1';
            count_tmp := (others => '0');
          end if;
        else
          empty_tmp := '0';
          full_tmp  := '0';
          
          if head_tmp < tail_tmp then
            count_tmp := FIFO_SIZE + head_tmp - tail_tmp;
          else
            count_tmp := head_tmp - tail_tmp;
          end if;
          
        end if;
      end if;
      
      head   <= head_tmp;
      tail   <= tail_tmp;
      looped <= looped_tmp;
      count  <= std_logic_vector(count_tmp);
      full   <= full_tmp;
      empty  <= empty_tmp;
      
    end if;
  end process;
  
 end RTL;
