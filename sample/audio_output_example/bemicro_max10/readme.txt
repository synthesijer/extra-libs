BeMicro Max 10向けプロジェクト

●I/O電圧のジャンパ設定について
このプロジェクトはボードのI/O電圧を3.3Vに設定することを前提にしています。 BeMicro Max 10 Getting Started User Guide のp.5を参照してVCCIO選択ジャンパ (J1,J9)が3.3V側に設定されていることを確認してください。

https://www.arrow.com/en/products/bemicromax10/arrow-development-tools/

コンパイル後、Quartus II Ver.15.0以上 でプロジェクトファイル bemicro_max10_start.qpf を開いて「Start Compilation」、「Programmer」で「Start」で転送して実行します。

●GPIOピンについて
このプロジェクトではGPIOピンヘッダ J5を使用しています。
以下のピンを音声出力回路に接続してください。
GPIO_J5の30番ピン：音声出力回路のGND
GPIO_J5の38番ピン：音声出力回路のAUDIO_R
GPIO_J5の40番ピン：音声出力回路のAUDIO_L
