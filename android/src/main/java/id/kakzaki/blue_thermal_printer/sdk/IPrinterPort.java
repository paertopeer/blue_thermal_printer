//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package id.kakzaki.blue_thermal_printer.sdk;

public interface IPrinterPort {
    void open();

    void close();

    int write(byte[] var1);

    byte[] read();

    int getState();
}
