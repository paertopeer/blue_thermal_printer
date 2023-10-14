//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package id.kakzaki.blue_thermal_printer.sdk.bluetooth;

import id.kakzaki.blue_thermal_printer.sdk.util.Utils;
import java.io.IOException;

class BluetoothPort$ConnectThread extends Thread {
    private BluetoothPort$ConnectThread(BluetoothPort var1, BluetoothPort$ConnectThread connectThread) {
        this.this$0 = var1;
    }

    public void run() {
        boolean hasError = false;
        BluetoothPort.access$600(this.this$0).cancelDiscovery();

        try {
            BluetoothPort.access$702(this.this$0, BluetoothPort.access$000(this.this$0).createRfcommSocketToServiceRecord(BluetoothPort.access$800(this.this$0)));
            BluetoothPort.access$700(this.this$0).connect();
        } catch (IOException var6) {
            Utils.Log("BluetoothPort", "ConnectThread failed. retry.");
            var6.printStackTrace();
            hasError = BluetoothPort.access$900(this.this$0);
        }

        synchronized(this) {
            BluetoothPort.access$1002(this.this$0, (BluetoothPort$ConnectThread)null);
        }

        if (!hasError) {
            try {
                BluetoothPort.access$1102(this.this$0, BluetoothPort.access$700(this.this$0).getInputStream());
                BluetoothPort.access$1202(this.this$0, BluetoothPort.access$700(this.this$0).getOutputStream());
            } catch (IOException var4) {
                hasError = true;
                Utils.Log("BluetoothPort", "Get Stream failed");
                var4.printStackTrace();
            }
        }

        if (hasError) {
            BluetoothPort.access$300(this.this$0, 102);
            this.this$0.close();
        } else {
            BluetoothPort.access$300(this.this$0, 101);
        }

    }
}
