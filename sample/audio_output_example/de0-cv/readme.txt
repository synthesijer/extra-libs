DE0-CV向けプロジェクト

コンパイル後、Quartus II Ver.15.0以上 でプロジェクトファイル DE0_CV_start.qpf を開いて「Start Compilation」、「Programmer」で「Start」で転送して実行します。

●GPIOピンについて
このプロジェクトではGPIOピンヘッダ GPIO1を使用しています。
以下のピンを音声出力回路に接続してください。
GPIO1の30番ピン(GND)：音声出力回路のGND
GPIO1の38番ピン(GPIO_1_D33)：音声出力回路のAUDIO_R
GPIO1の40番ピン(GPIO_1_D35)：音声出力回路のAUDIO_L
