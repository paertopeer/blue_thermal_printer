//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package id.kakzaki.blue_thermal_printer.sdk.usb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.util.Log;

class USBPort$1 extends BroadcastReceiver {
    USBPort$1(USBPort this$0) {
        this.this$0 = this$0;
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.w("USBPrinter", "receiver action: " + action);
        if ("com.android.usb.USB_PERMISSION".equals(action)) {
            synchronized(this) {
                USBPort.access$100(this.this$0).unregisterReceiver(USBPort.access$000(this.this$0));
                UsbDevice device = (UsbDevice)intent.getParcelableExtra("device");
                if (intent.getBooleanExtra("permission", false) && USBPort.access$200(this.this$0).equals(device)) {
                    USBPort.access$300(this.this$0);
                } else {
                    USBPort.access$400(this.this$0, 102);
                    Log.e("USBPrinter", "permission denied for device " + device);
                }
            }
        }

    }
}
