//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package id.kakzaki.blue_thermal_printer.sdk.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import id.kakzaki.blue_thermal_printer.sdk.util.Utils;

class BluetoothPort$1 extends BroadcastReceiver {
    BluetoothPort$1(BluetoothPort this$0) {
        this.this$0 = this$0;
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ("android.bluetooth.device.action.BOND_STATE_CHANGED".equals(action)) {
            BluetoothDevice device = (BluetoothDevice)intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
            if (!BluetoothPort.access$000(this.this$0).equals(device)) {
                return;
            }

            switch (device.getBondState()) {
                case 10:
                    BluetoothPort.access$200(this.this$0).unregisterReceiver(BluetoothPort.access$100(this.this$0));
                    BluetoothPort.access$300(this.this$0, 102);
                    Utils.Log("BluetoothPort", "bound cancel");
                    break;
                case 11:
                    Utils.Log("BluetoothPort", "bounding......");
                    break;
                case 12:
                    Utils.Log("BluetoothPort", "bound success");
                    BluetoothPort.access$200(this.this$0).unregisterReceiver(BluetoothPort.access$100(this.this$0));
                    BluetoothPort.access$400(this.this$0, false);
            }
        }

    }
}
