//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package id.kakzaki.blue_thermal_printer.sdk.wifi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

class WiFiPort$ConnectThread extends Thread {
    private WiFiPort$ConnectThread(WiFiPort var1, WiFiPort$ConnectThread connectThread) {
        this.this$0 = var1;
    }

    public void run() {
        boolean hasError = true;
        InetSocketAddress mSocketAddress = new InetSocketAddress(WiFiPort.access$100(this.this$0), WiFiPort.access$200(this.this$0));

        try {
            WiFiPort.access$302(this.this$0, new Socket());
            WiFiPort.access$300(this.this$0).setSoTimeout(2000);
            WiFiPort.access$300(this.this$0).connect(mSocketAddress, 3000);
            WiFiPort.access$402(this.this$0, WiFiPort.access$300(this.this$0).getOutputStream());
            WiFiPort.access$502(this.this$0, WiFiPort.access$300(this.this$0).getInputStream());
            hasError = false;
        } catch (SocketException var6) {
            var6.printStackTrace();
        } catch (IOException var7) {
            var7.printStackTrace();
        }

        synchronized(this) {
            WiFiPort.access$602(this.this$0, (WiFiPort$ConnectThread)null);
        }

        if (hasError) {
            WiFiPort.access$700(this.this$0, 102);
            this.this$0.close();
        } else {
            WiFiPort.access$700(this.this$0, 101);
        }

    }
}
