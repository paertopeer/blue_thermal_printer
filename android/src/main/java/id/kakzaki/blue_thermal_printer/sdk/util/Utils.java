//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package id.kakzaki.blue_thermal_printer.sdk.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;
import com.android.print.sdk.PrinterInstance;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils {
    public Utils() {
    }

    public static Bitmap compressBitmap(Bitmap srcBitmap, int maxLength) {
        Bitmap destBitmap = null;

        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            byte[] srcBytes = bitmap2Bytes(srcBitmap);
            BitmapFactory.decodeByteArray(srcBytes, 0, srcBytes.length, opts);
            int srcWidth = opts.outWidth;
            int srcHeight = opts.outHeight;
            boolean destWidth = false;
            boolean destHeight = false;
            double ratio;
            int destWidth1;
            int destHeight1;
            if (srcWidth > srcHeight) {
                ratio = (double)(srcWidth / maxLength);
                destWidth1 = maxLength;
                destHeight1 = (int)((double)srcHeight / ratio);
            } else {
                ratio = (double)(srcHeight / maxLength);
                destHeight1 = maxLength;
                destWidth1 = (int)((double)srcWidth / ratio);
            }

            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            newOpts.inSampleSize = (int)ratio + 1;
            newOpts.inJustDecodeBounds = false;
            newOpts.outHeight = destHeight1;
            newOpts.outWidth = destWidth1;
            destBitmap = BitmapFactory.decodeByteArray(srcBytes, 0, srcBytes.length, newOpts);
        } catch (Exception var14) {
        }

        return destBitmap;
    }

    public static byte[] readStream(InputStream inStream) throws Exception {
        byte[] buffer = new byte[1024];
        boolean len = true;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        int len1;
        while((len1 = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len1);
        }

        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;
    }

    public static Bitmap getImageFromBytes(byte[] bytes, BitmapFactory.Options opts) {
        return bytes != null ? (opts != null ? BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts) : BitmapFactory.decodeByteArray(bytes, 0, bytes.length)) : null;
    }

    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = (float)w / (float)width;
        float scaleHeight = (float)h / (float)height;
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return newBmp;
    }

    public static byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static File saveFileFromBytes(byte[] b, String outputFile) {
        BufferedOutputStream stream = null;
        File file = null;

        try {
            file = new File(outputFile);
            FileOutputStream e = new FileOutputStream(file);
            stream = new BufferedOutputStream(e);
            stream.write(b);
        } catch (Exception var13) {
            var13.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException var12) {
                    var12.printStackTrace();
                }
            }

        }

        return file;
    }

    public static int printBitmap2File(Bitmap bitmap, String filePath) {
        File file;
        if (filePath.endsWith(".png")) {
            file = new File(filePath);
        } else {
            file = new File(filePath + ".png");
        }

        try {
            FileOutputStream e = new FileOutputStream(file);
            bitmap.compress(CompressFormat.PNG, 100, e);
            e.close();
            return 0;
        } catch (Exception var4) {
            var4.printStackTrace();
            return -1;
        }
    }

    public static byte[] bitmap2PrinterBytes(Bitmap bitmap, int left) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        byte[] imgbuf = new byte[(width / 8 + left + 4) * height];
        byte[] bitbuf = new byte[width / 8];
        int[] p = new int[8];
        int s = 0;
        System.out.println("+++++++++++++++ Total Bytes: " + (width / 8 + 4) * height);

        for(int y = 0; y < height; ++y) {
            int n;
            for(n = 0; n < width / 8; ++n) {
                int value;
                for(value = 0; value < 8; ++value) {
                    if (bitmap.getPixel(n * 8 + value, y) == -1) {
                        p[value] = 0;
                    } else {
                        p[value] = 1;
                    }
                }

                value = p[0] * 128 + p[1] * 64 + p[2] * 32 + p[3] * 16 + p[4] * 8 + p[5] * 4 + p[6] * 2 + p[7];
                bitbuf[n] = (byte)value;
            }

            if (y != 0) {
                ++s;
                imgbuf[s] = 22;
            } else {
                imgbuf[s] = 22;
            }

            ++s;
            imgbuf[s] = (byte)(width / 8 + left);

            for(n = 0; n < left; ++n) {
                ++s;
                imgbuf[s] = 0;
            }

            for(n = 0; n < width / 8; ++n) {
                ++s;
                imgbuf[s] = bitbuf[n];
            }

            ++s;
            imgbuf[s] = 21;
            ++s;
            imgbuf[s] = 1;
        }

        return imgbuf;
    }

    public static byte[] bitmap2PrinterBytes_stylus(Bitmap bitmap, int multiple, int left) {
        int height = bitmap.getHeight();
        int width = bitmap.getWidth() + left;
        boolean need_0a = false;
        short maxWidth = 240;
        byte[] imgBuf;
        if (width < maxWidth) {
            imgBuf = new byte[(height / 8 + 1) * (width + 6)];
            need_0a = true;
        } else {
            imgBuf = new byte[(height / 8 + 1) * (width + 5) + 2];
        }

        byte[] tmpBuf = new byte[width + 5];
        int[] p = new int[8];
        int s = 0;
        boolean t = false;
        boolean allZERO = true;

        int sb;
        int i;
        for(sb = 0; sb < height / 8 + 1; ++sb) {
            byte var17 = 0;
            tmpBuf[var17] = 27;
            int var18 = var17 + 1;
            tmpBuf[var18] = 42;
            ++var18;
            tmpBuf[var18] = (byte)multiple;
            ++var18;
            tmpBuf[var18] = (byte)(width % maxWidth);
            ++var18;
            tmpBuf[var18] = (byte)(width / maxWidth > 0 ? 1 : 0);
            allZERO = true;

            int temp;
            for(temp = 0; temp < width; ++temp) {
                for(i = 0; i < 8; ++i) {
                    if (sb * 8 + i < height && temp >= left) {
                        p[i] = bitmap.getPixel(temp - left, sb * 8 + i) == -1 ? 0 : 1;
                    } else {
                        p[i] = 0;
                    }
                }

                i = p[0] * 128 + p[1] * 64 + p[2] * 32 + p[3] * 16 + p[4] * 8 + p[5] * 4 + p[6] * 2 + p[7];
                ++var18;
                tmpBuf[var18] = (byte)i;
                if (i != 0) {
                    allZERO = false;
                }
            }

            if (allZERO) {
                if (s == 0) {
                    imgBuf[s] = 27;
                } else {
                    ++s;
                    imgBuf[s] = 27;
                }

                ++s;
                imgBuf[s] = 74;
                ++s;
                imgBuf[s] = 8;
            } else {
                for(temp = 0; temp < var18 + 1; ++temp) {
                    if (temp == 0 && s == 0) {
                        imgBuf[s] = tmpBuf[temp];
                    } else {
                        ++s;
                        imgBuf[s] = tmpBuf[temp];
                    }
                }

                if (need_0a) {
                    ++s;
                    imgBuf[s] = 10;
                }
            }
        }

        if (!need_0a) {
            ++s;
            imgBuf[s] = 13;
            ++s;
            imgBuf[s] = 10;
        }

        byte[] realBuf = new byte[s + 1];

        for(sb = 0; sb < s + 1; ++sb) {
            realBuf[sb] = imgBuf[sb];
        }

        StringBuffer var19 = new StringBuffer();

        for(i = 0; i < realBuf.length; ++i) {
            String var20 = Integer.toHexString(realBuf[i] & 255);
            if (var20.length() == 1) {
                var20 = "0" + var20;
            }

            var19.append(var20 + " ");
            if (i != 0 && i % 100 == 0 || i == realBuf.length - 1) {
                Log.e("12345", var19.toString());
                var19 = new StringBuffer();
            }
        }

        return realBuf;
    }

    public static int getStringCharacterLength(String line) {
        int length = 0;

        for(int j = 0; j < line.length(); ++j) {
            if (line.charAt(j) > 256) {
                length += 2;
            } else {
                ++length;
            }
        }

        return length;
    }

    public static int getSubLength(String line, int width) {
        int length = 0;

        for(int j = 0; j < line.length(); ++j) {
            if (line.charAt(j) > 256) {
                length += 2;
            } else {
                ++length;
            }

            if (length > width) {
                int temp = line.substring(0, j - 1).lastIndexOf(" ");
                if (temp != -1) {
                    return temp;
                }

                return j - 1 == 0 ? 1 : j - 1;
            }
        }

        return line.length();
    }

    public static boolean isNum(byte temp) {
        return temp >= 48 && temp <= 57;
    }

    public static void Log(String tag, String msg) {
        if (PrinterInstance.DEBUG) {
            Log.i(tag, msg);
        }

    }
}
