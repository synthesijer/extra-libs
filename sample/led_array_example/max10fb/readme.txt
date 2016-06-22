「FPGA電子工作スーパーキット」付録のMAX10-FB基板向けプロジェクト

「FPGA電子工作スーパーキット」
http://www.cqpub.co.jp/hanbai/books/mtr/mtrz201604.htm

●対応OS: Microsoft Windows

●I/O電圧の設定について
このプロジェクトはボードのI/O電圧を3.3Vに設定することを前提にしています。 書籍の標準設定では3.3Vになっています。

●実行方法

JDKのパスは環境変数PATHに登録されているものとします。

事前にコマンドプロンプトで synthesijer_env_20xxxxxx.bat が実行されているものとします。

コマンドプロンプトでプロジェクトフォルダに移動（cd）した後、

make.bat

を実行します。
LEDArrayExample.java ファイルがコンパイルされ、 LEDArrayExample.v などのファイルが出力されます。

この状態で led_array_example\max10fb\max10fb_start.qpf をQuartusの「Open Project」で開き、「Start Compilation」、「Programmer」で「Start」で転送して実行します。
