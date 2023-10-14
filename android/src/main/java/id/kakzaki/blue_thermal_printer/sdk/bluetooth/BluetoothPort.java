//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package id.kakzaki.blue_thermal_printer.sdk.bluetooth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Build.VERSION;
import android.util.Log;
import id.kakzaki.blue_thermal_printer.sdk.IPrinterPort;
import id.kakzaki.blue_thermal_printer.sdk.util.Utils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class BluetoothPort implements IPrinterPort {
    private static final String TAG = "BluetoothPort";
    private BluetoothDevice mDevice;
    private BluetoothSocket mSocket;
    private BluetoothAdapter mAdapter;
    private ConnectThread mConnectThread;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Context mContext;
    private Handler mHandler;
    private int mState;
    private int readLen;
    private final UUID PRINTER_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private BroadcastReceiver boundDeviceReceiver = new 1(this);

    public BluetoothPort(Context context, BluetoothDevice device, Handler handler) {
        this.mHandler = handler;
        this.mDevice = device;
        this.mAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mState = 103;
        this.mContext = context;
    }

    public BluetoothPort(Context context, String address, Handler handler) {
        this.mHandler = handler;
        this.mAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mDevice = this.mAdapter.getRemoteDevice(address);
        this.mState = 103;
        this.mContext = context;
    }

    public void open() {
        Utils.Log("BluetoothPort", "connect to: " + this.mDevice.getName());
        if (this.mState != 103) {
            this.close();
        }

        if (this.mDevice.getBondState() == 10) {
            Log.i("BluetoothPort", "device.getBondState() is BluetoothDevice.BOND_NONE");
            this.PairOrConnect(true);
        } else if (this.mDevice.getBondState() == 12) {
            this.PairOrConnect(false);
        }

    }

    private void PairOrConnect(boolean pair) {
        if (pair) {
            IntentFilter boundFilter = new IntentFilter("android.bluetooth.device.action.BOND_STATE_CHANGED");
            this.mContext.registerReceiver(this.boundDeviceReceiver, boundFilter);
            boolean success = false;

            try {
                Method e = BluetoothDevice.class.getMethod("createBond");
                success = (Boolean)e.invoke(this.mDevice);
            } catch (IllegalAccessException var5) {
                var5.printStackTrace();
            } catch (IllegalArgumentException var6) {
                var6.printStackTrace();
            } catch (InvocationTargetException var7) {
                var7.printStackTrace();
            } catch (NoSuchMethodException var8) {
                var8.printStackTrace();
            }

            Log.i("BluetoothPort", "createBond is success? : " + success);
        } else {
            this.mConnectThread = new ConnectThread(this, (ConnectThread)null, (1)null);
            this.mConnectThread.start();
        }

    }

    @TargetApi(10)
    private boolean ReTryConnect() {
        Utils.Log("BluetoothPort", "android SDK version is:" + VERSION.SDK_INT);

        try {
            if (VERSION.SDK_INT >= 10) {
                this.mSocket = this.mDevice.createInsecureRfcommSocketToServiceRecord(this.PRINTER_UUID);
            } else {
                Method e = this.mDevice.getClass().getMethod("createRfcommSocket", Integer.TYPE);
                this.mSocket = (BluetoothSocket)e.invoke(this.mDevice, 1);
            }

            this.mSocket.connect();
            return false;
        } catch (Exception var2) {
            Utils.Log("BluetoothPort", "connect failed:");
            var2.printStackTrace();
            return true;
        }
    }

    public void close() {
        Utils.Log("BluetoothPort", "close()");

        try {
            if (this.mSocket != null) {
                this.mSocket.close();
            }
        } catch (IOException var2) {
            Utils.Log("BluetoothPort", "close socket failed");
            var2.printStackTrace();
        }

        this.mConnectThread = null;
        this.mDevice = null;
        this.mSocket = null;
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
            Utils.Log("BluetoothPort", "write error.");
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
            Utils.Log("BluetoothPort", "read error");
            var3.printStackTrace();
        }

        Log.w("BluetoothPort", "read length:" + this.readLen);
        return readBuff;
    }

    public synchronized byte[] read(int timeout) {
        byte[] receiveBytes = null;

        try {
            while((this.readLen = this.inputStream.available()) <= 0) {
                timeout -= 50;
                if (timeout <= 0) {
                    break;
                }

                try {
                    Thread.sleep(50L);
                } catch (InterruptedException var4) {
                    var4.printStackTrace();
                }
            }

            if (this.readLen > 0) {
                receiveBytes = new byte[this.readLen];
                this.inputStream.read(receiveBytes);
            }
        } catch (IOException var5) {
            Utils.Log("BluetoothPort", "read error1");
            var5.printStackTrace();
        }

        return receiveBytes;
    }

    private synchronized void setState(int state) {
        Utils.Log("BluetoothPort", "setState() " + this.mState + " -> " + state);
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
}
