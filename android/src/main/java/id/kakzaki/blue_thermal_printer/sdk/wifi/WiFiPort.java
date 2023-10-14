//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package id.kakzaki.blue_thermal_printer.sdk.wifi;

import android.os.Handler;
import android.util.Log;
import id.kakzaki.blue_thermal_printer.sdk.IPrinterPort;
import id.kakzaki.blue_thermal_printer.sdk.util.Utils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class WiFiPort implements IPrinterPort {
    private static String TAG = "WifiPrinter";
    private String address;
    private int port;
    private Socket mSocket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private ConnectThread mConnectThread;
    private Handler mHandler;
    private int mState;
    private int readLen;

    public WiFiPort(String ipAddress, int portNumber, Handler handler) {
        this.address = ipAddress;
        this.port = portNumber;
        this.mHandler = handler;
        this.mState = 103;
    }

    public void open() {
        Utils.Log(TAG, "open connect to: " + this.address);
        if (this.mState != 103) {
            this.close();
        }

        this.mConnectThread = new ConnectThread(this, (ConnectThread)null, (1)null);
        this.mConnectThread.start();
    }

    public void close() {
        try {
            if (this.outputStream != null) {
                this.outputStream.close();
            }

            if (this.inputStream != null) {
                this.inputStream.close();
            }

            if (this.mSocket != null) {
                this.mSocket.close();
            }
        } catch (IOException var2) {
            var2.printStackTrace();
        }

        this.outputStream = null;
        this.inputStream = null;
        this.mSocket = null;
        this.mConnectThread = null;
        if (this.mState != 102) {
            this.setState(103);
        }

    }

    public int write(byte[] data) {
        try {
            if (this.outputStream != null) {
                this.outputStream.write(data);
                this.outputStream.flush();
                return 0;
            } else {
                return -1;
            }
        } catch (IOException var3) {
            var3.printStackTrace();
            return -1;
        }
    }

    public byte[] read() {
        byte[] readBuff = null;

        try {
            if (this.inputStream != null && (this.readLen = this.inputStream.available()) > 0) {
                readBuff = new byte[this.readLen];
                this.inputStream.read(readBuff);
            }
        } catch (IOException var3) {
            var3.printStackTrace();
        }

        Log.w(TAG, "read length:" + this.readLen);
        return readBuff;
    }

    public int read1() {
        int readValue = -1;

        try {
            readValue = this.inputStream.read();
        } catch (IOException var3) {
            var3.printStackTrace();
        }

        return readValue;
    }

    private synchronized void setState(int state) {
        Utils.Log(TAG, "setState() " + this.mState + " -> " + state);
        if (this.mState != state) {
            this.mState = state;
            if (this.mHandler != null) {
                this.mHandler.obtainMessage(this.mState).sendToTarget();
            }
        }

    }

    public int getState() {
        return this.mState;
    }

    public Boolean isServerClose() {
        try {
            this.mSocket.sendUrgentData(255);
            return false;
        } catch (Exception var2) {
            return true;
        }
    }
}
