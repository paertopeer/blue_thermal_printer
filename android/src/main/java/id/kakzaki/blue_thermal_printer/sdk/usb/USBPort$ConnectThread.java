//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package id.kakzaki.blue_thermal_printer.sdk.usb;

import android.hardware.usb.UsbEndpoint;

class USBPort$ConnectThread extends Thread {
    private USBPort$ConnectThread(USBPort var1, USBPort$ConnectThread connectThread) {
        this.this$0 = var1;
    }

    public void run() {
        boolean hasError = true;
        if (USBPort.access$600(this.this$0).hasPermission(USBPort.access$200(this.this$0))) {
            USBPort.access$702(this.this$0, 1659 == USBPort.access$200(this.this$0).getVendorId() && 8965 == USBPort.access$200(this.this$0).getProductId());

            try {
                USBPort.access$802(this.this$0, USBPort.access$200(this.this$0).getInterface(0));

                for(int e = 0; e < USBPort.access$800(this.this$0).getEndpointCount(); ++e) {
                    UsbEndpoint ep = USBPort.access$800(this.this$0).getEndpoint(e);
                    if (ep.getType() == 2) {
                        if (ep.getDirection() == 0) {
                            USBPort.access$902(this.this$0, ep);
                        } else {
                            USBPort.access$1002(this.this$0, ep);
                        }
                    }
                }

                USBPort.access$1102(this.this$0, USBPort.access$600(this.this$0).openDevice(USBPort.access$200(this.this$0)));
                if (USBPort.access$1100(this.this$0) != null && USBPort.access$1100(this.this$0).claimInterface(USBPort.access$800(this.this$0), true)) {
                    hasError = false;
                }
            } catch (Exception var6) {
                var6.printStackTrace();
            }
        }

        synchronized(this) {
            USBPort.access$1202(this.this$0, (USBPort$ConnectThread)null);
        }

        if (hasError) {
            USBPort.access$400(this.this$0, 102);
            this.this$0.close();
        } else {
            USBPort.access$400(this.this$0, 101);
        }

    }
}
