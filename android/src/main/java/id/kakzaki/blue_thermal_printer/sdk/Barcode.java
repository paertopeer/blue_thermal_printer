//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package id.kakzaki.blue_thermal_printer.sdk;

import id.kakzaki.blue_thermal_printer.sdk.util.Utils;
import java.io.UnsupportedEncodingException;

public class Barcode {
    private static final String TAG = "Barcode";
    private byte barcodeType;
    private int param1;
    private int param2;
    private int param3;
    private String content;
    private String charsetName = "gbk";

    public Barcode(byte barcodeType) {
        this.barcodeType = barcodeType;
    }

    public Barcode(byte barcodeType, int param1, int param2, int param3) {
        this.barcodeType = barcodeType;
        this.param1 = param1;
        this.param2 = param2;
        this.param3 = param3;
    }

    public Barcode(byte barcodeType, int param1, int param2, int param3, String content) {
        this.barcodeType = barcodeType;
        this.param1 = param1;
        this.param2 = param2;
        this.param3 = param3;
        this.content = content;
    }

    public void setBarcodeParam(byte param1, byte param2, byte param3) {
        this.param1 = param1;
        this.param2 = param2;
        this.param3 = param3;
    }

    public void setBarcodeContent(String content) {
        this.content = content;
    }

    public void setBarcodeContent(String content, String charsetName) {
        this.content = content;
        this.charsetName = charsetName;
    }

    public byte[] getBarcodeData() {
        byte[] realCommand;
        int j;
        switch (this.barcodeType) {
            case 72:
                realCommand = this.getBarcodeCommand1(this.content, this.barcodeType, (byte)this.content.length());
                break;
            case 73:
                byte[] sb = new byte[1024];
                j = 0;
                int temp = this.content.length();
                int tempLength = temp;
                char[] charArray = this.content.toCharArray();
                boolean preHasCodeA = false;
                boolean preHasCodeB = false;
                boolean preHasCodeC = false;
                boolean needCodeC = false;

                for(int i = 0; i < temp; ++i) {
                    byte a = (byte)charArray[i];
                    if (a >= 0 && a <= 31) {
                        if (i == 0 || !preHasCodeA) {
                            sb[j++] = 123;
                            sb[j++] = 65;
                            preHasCodeA = true;
                            preHasCodeB = false;
                            preHasCodeC = false;
                            tempLength += 2;
                        }

                        sb[j++] = a;
                    } else {
                        if (a >= 48 && a <= 57) {
                            if (!preHasCodeC) {
                                for(int b = 1; b < 9; ++b) {
                                    if (i + b == temp || !Utils.isNum((byte)charArray[i + b])) {
                                        needCodeC = false;
                                        break;
                                    }

                                    if (b == 8) {
                                        needCodeC = true;
                                    }
                                }
                            }

                            if (needCodeC) {
                                if (!preHasCodeC) {
                                    sb[j++] = 123;
                                    sb[j++] = 67;
                                    preHasCodeA = false;
                                    preHasCodeB = false;
                                    preHasCodeC = true;
                                    tempLength += 2;
                                }

                                if (i != temp - 1) {
                                    byte var16 = (byte)charArray[i + 1];
                                    if (Utils.isNum(var16)) {
                                        sb[j++] = (byte)((a - 48) * 10 + (var16 - 48));
                                        --tempLength;
                                        ++i;
                                        continue;
                                    }
                                }
                            }
                        }

                        if (!preHasCodeB) {
                            sb[j++] = 123;
                            sb[j++] = 66;
                            preHasCodeA = false;
                            preHasCodeB = true;
                            preHasCodeC = false;
                            tempLength += 2;
                        }

                        sb[j++] = a;
                    }
                }

                realCommand = this.getBarcodeCommand1(new String(sb, 0, tempLength), this.barcodeType, (byte)tempLength);
                break;
            case 100:
            case 101:
            case 102:
                realCommand = this.getBarcodeCommand2(this.content, this.barcodeType, this.param1, this.param2, this.param3);
                break;
            default:
                realCommand = this.getBarcodeCommand1(this.content, this.barcodeType);
        }

        if (PrinterInstance.DEBUG) {
            StringBuffer var14 = new StringBuffer();

            for(j = 0; j < realCommand.length; ++j) {
                String var15 = Integer.toHexString(realCommand[j] & 255);
                if (var15.length() == 1) {
                    var15 = "0" + var15;
                }

                var14.append(var15 + " ");
            }

            Utils.Log("Barcode", "bar code command: " + var14.toString());
        }

        return realCommand;
    }

    private byte[] getBarcodeCommand1(String content, byte... byteArray) {
        byte index = 0;

        byte[] tmpByte;
        try {
            if (this.charsetName != "") {
                tmpByte = content.getBytes(this.charsetName);
            } else {
                tmpByte = content.getBytes();
            }
        } catch (UnsupportedEncodingException var8) {
            var8.printStackTrace();
            return null;
        }

        byte[] command = new byte[tmpByte.length + 13];
        int var8 = index + 1;
        command[index] = 29;
        command[var8++] = 119;
        if (this.param1 >= 2 && this.param1 <= 6) {
            command[var8++] = (byte)this.param1;
        } else {
            command[var8++] = 2;
        }

        command[var8++] = 29;
        command[var8++] = 104;
        if (this.param2 >= 1 && this.param2 <= 255) {
            command[var8++] = (byte)this.param2;
        } else {
            command[var8++] = -94;
        }

        command[var8++] = 29;
        command[var8++] = 72;
        if (this.param3 >= 0 && this.param3 <= 3) {
            command[var8++] = (byte)this.param3;
        } else {
            command[var8++] = 0;
        }

        command[var8++] = 29;
        command[var8++] = 107;

        int j;
        for(j = 0; j < byteArray.length; ++j) {
            command[var8++] = byteArray[j];
        }

        for(j = 0; j < tmpByte.length; ++j) {
            command[var8++] = tmpByte[j];
        }

        return command;
    }

    private byte[] getBarcodeCommand2(String content, byte barcodeType, int param1, int param2, int param3) {
        byte[] tmpByte;
        try {
            if (this.charsetName != "") {
                tmpByte = content.getBytes(this.charsetName);
            } else {
                tmpByte = content.getBytes();
            }
        } catch (UnsupportedEncodingException var8) {
            var8.printStackTrace();
            return null;
        }

        byte[] command = new byte[tmpByte.length + 10];
        command[0] = 29;
        command[1] = 90;
        command[2] = (byte)(barcodeType - 100);
        command[3] = 27;
        command[4] = 90;
        command[5] = (byte)param1;
        command[6] = (byte)param2;
        command[7] = (byte)param3;
        command[8] = (byte)(tmpByte.length % 256);
        command[9] = (byte)(tmpByte.length / 256);
        System.arraycopy(tmpByte, 0, command, 10, tmpByte.length);
        return command;
    }
}
