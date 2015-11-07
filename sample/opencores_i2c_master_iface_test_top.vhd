library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity top is
  port (
    CLK   : in std_logic;
    RESET : in std_logic;
    
    SCL : inout std_logic;
    SDA : inout std_logic;

    GPIO_LED : out std_logic_vector(3 downto 0)
    );
end entity top;

architecture RTL of top is
  
  component OpenCoresI2CMasterIface_Test
    port (
      clk : in std_logic;
      reset : in std_logic;
      iface_obj_arst_i_exp : in std_logic;
      iface_obj_scl_pad_i_exp : in std_logic;
      iface_obj_scl_pad_o_exp : out std_logic;
      iface_obj_scl_padoen_o_exp : out std_logic;
      iface_obj_sda_pad_i_exp : in std_logic;
      iface_obj_sda_pad_o_exp : out std_logic;
      iface_obj_sda_padoen_o_exp : out std_logic;
      debug_in : in signed(32-1 downto 0);
      debug_we : in std_logic;
      debug_out : out signed(32-1 downto 0);
      U_PEAK_in : in signed(32-1 downto 0);
      U_PEAK_we : in std_logic;
      U_PEAK_out : out signed(32-1 downto 0);
      D_PEAK_in : in signed(32-1 downto 0);
      D_PEAK_we : in std_logic;
      D_PEAK_out : out signed(32-1 downto 0);
      L_PEAK_in : in signed(32-1 downto 0);
      L_PEAK_we : in std_logic;
      L_PEAK_out : out signed(32-1 downto 0);
      R_PEAK_in : in signed(32-1 downto 0);
      R_PEAK_we : in std_logic;
      R_PEAK_out : out signed(32-1 downto 0);
      DECIDE_FLAG_in : in std_logic;
      DECIDE_FLAG_we : in std_logic;
      DECIDE_FLAG_out : out std_logic;
      DETECT_L2R_in : in std_logic;
      DETECT_L2R_we : in std_logic;
      DETECT_L2R_out : out std_logic;
      DETECT_R2L_in : in std_logic;
      DETECT_R2L_we : in std_logic;
      DETECT_R2L_out : out std_logic;
      DETECT_U2D_in : in std_logic;
      DETECT_U2D_we : in std_logic;
      DETECT_U2D_out : out std_logic;
      DETECT_D2U_in : in std_logic;
      DETECT_D2U_we : in std_logic;
      DETECT_D2U_out : out std_logic
      );
  end component OpenCoresI2CMasterIface_Test;

  signal scl_pad_i    : std_logic;
  signal scl_pad_o    : std_logic;
  signal scl_padoen_o : std_logic;
  signal sda_pad_i    : std_logic;
  signal sda_pad_o    : std_logic;
  signal sda_padoen_o : std_logic;

  signal clk_counter : unsigned(31 downto 0);

  attribute mark_debug : string;
  attribute keep : string;

  signal debug : signed(31 downto 0);

  signal U_PEAK_out      : signed(32-1 downto 0);
  signal D_PEAK_out      : signed(32-1 downto 0);
  signal L_PEAK_out      : signed(32-1 downto 0);
  signal R_PEAK_out      : signed(32-1 downto 0);
  signal DECIDE_FLAG_out : std_logic;
  signal DETECT_L2R_out  : std_logic;
  signal DETECT_R2L_out  : std_logic;
  signal DETECT_U2D_out  : std_logic;
  signal DETECT_D2U_out  : std_logic;
  
  attribute mark_debug of debug : signal is "true";
  attribute keep of debug : signal is "true";

  attribute mark_debug of U_PEAK_out      : signal is "true";
  attribute mark_debug of D_PEAK_out      : signal is "true";
  attribute mark_debug of L_PEAK_out      : signal is "true";
  attribute mark_debug of R_PEAK_out      : signal is "true";
  attribute mark_debug of DECIDE_FLAG_out : signal is "true";
  attribute mark_debug of DETECT_L2R_out  : signal is "true";
  attribute mark_debug of DETECT_R2L_out  : signal is "true";
  attribute mark_debug of DETECT_U2D_out  : signal is "true";
  attribute mark_debug of DETECT_D2U_out  : signal is "true";
  
  attribute keep of U_PEAK_out      : signal is "true";
  attribute keep of D_PEAK_out      : signal is "true";
  attribute keep of L_PEAK_out      : signal is "true";
  attribute keep of R_PEAK_out      : signal is "true";
  attribute keep of DECIDE_FLAG_out : signal is "true";
  attribute keep of DETECT_L2R_out  : signal is "true";
  attribute keep of DETECT_R2L_out  : signal is "true";
  attribute keep of DETECT_U2D_out  : signal is "true";
  attribute keep of DETECT_D2U_out  : signal is "true";

begin

  U: OpenCoresI2CMasterIface_Test port map(
      clk => CLK,
      reset => RESET,
      iface_obj_arst_i_exp => '1',
      iface_obj_scl_pad_i_exp => scl_pad_i,
      iface_obj_scl_pad_o_exp => scl_pad_o,
      iface_obj_scl_padoen_o_exp => scl_padoen_o,
      iface_obj_sda_pad_i_exp => sda_pad_i,
      iface_obj_sda_pad_o_exp => sda_pad_o,
      iface_obj_sda_padoen_o_exp => sda_padoen_o,
      debug_in => (others => '0'),
      debug_we => '0',
      debug_out => debug,
      U_PEAK_in       => (others => '0'),
      U_PEAK_we       => '0',
      U_PEAK_out      => U_PEAK_out,
      D_PEAK_in       => (others => '0'),
      D_PEAK_we       => '0',
      D_PEAK_out      => D_PEAK_out,
      L_PEAK_in       => (others => '0'),
      L_PEAK_we       => '0',
      L_PEAK_out      => L_PEAK_out,
      R_PEAK_in       => (others => '0'),
      R_PEAK_we       => '0',
      R_PEAK_out      => R_PEAK_out,
      DECIDE_FLAG_in  => '0',
      DECIDE_FLAG_we  => '0',
      DECIDE_FLAG_out => DECIDE_FLAG_out,
      DETECT_L2R_in   => '0',
      DETECT_L2R_we   => '0',
      DETECT_L2R_out  => DETECT_L2R_out,
      DETECT_R2L_in   => '0',
      DETECT_R2L_we   => '0',
      DETECT_R2L_out  => DETECT_R2L_out,
      DETECT_U2D_in   => '0',
      DETECT_U2D_we   => '0',
      DETECT_U2D_out  => DETECT_U2D_out,
      DETECT_D2U_in   => '0',
      DETECT_D2U_we   => '0',
      DETECT_D2U_out  => DETECT_D2U_out
      );

  SCL <= scl_pad_o when scl_padoen_o = '0' else 'Z';
  SDA <= sda_pad_o when sda_padoen_o = '0' else 'Z';
  
  scl_pad_i <= SCL;
  sda_pad_i <= SDA;
  
  GPIO_LED <= std_logic_vector(clk_counter(22 downto 19));
  process(CLK)
  begin
    if CLK'event and CLK = '1' then
      clk_counter <= clk_counter + 1;
    end if;
  end process;
  
end RTL;
