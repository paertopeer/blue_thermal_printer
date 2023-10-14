package id.kakzaki.blue_thermal_printer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import id.kakzaki.blue_thermal_printer.sdk.PrinterInstance;

public class PrintfManager {

    public static int ORDINARY = 1, SERIALIZE = 2;

    protected String TAG = "PrintfManager";

    protected Context context;

    protected PrinterInstance mPrinter;


    private PrintfManager() {
    }

    static class PrintfManagerHolder {
        private static PrintfManager instance = new PrintfManager();
    }


    public static PrintfManager getInstance(Context context) {
        if (PrintfManagerHolder.instance.context == null) {
            PrintfManagerHolder.instance.context = context.getApplicationContext();
        }
        return PrintfManagerHolder.instance;
    }

    public void setPrinter(PrinterInstance mPrinter) {
        this.mPrinter = mPrinter;
    }


    public PrinterInstance getPrinter() {
        return mPrinter;
    }





    public void printf(final int width, final int height, final Bitmap bitmap, final Activity activity) {
        System.out.println("testando impressão");
        realPrintfBitmapByLabelView(width,height,bitmap,128,1);

    }

    /**
     * clear Canvas
     */
    public void clearCanvas() {
        mPrinter.sendByteData("CLS\r\n".getBytes());
    }

    /**
     * init Canvas
     *
     * @param w
     * @param h
     */
    public void initCanvas(int w, int h) {
        byte[] data = new StringBuilder().append("SIZE ").append(w + " mm").append(",")
                .append(h + " mm").append("\r\n").toString().getBytes();
        mPrinter.sendByteData(data);
    }

    /**
     * @param x    ：image printf X coordinate
     * @param y    ：image printf Y coordinate
     * @param data ：Picture resources
     */
    public void printfBitmap(int x, int y, Bitmap data, int concentration) {
        //newline
        byte[] crlf = {0x0d, 0x0a};
        StringBuilder BITMAP = new StringBuilder()
                .append("BITMAP ")
                .append(x)
                .append(",")
                .append(y)
                .append(",")
                .append((data.getWidth()) / 8)
                .append(",")
                .append(data.getHeight())
                .append(",1,");
        byte[] bitmapByte = convertToBMW(data, concentration);
        mPrinter.sendByteData(BITMAP.toString().getBytes());
        mPrinter.sendByteData(bitmapByte);
        mPrinter.sendByteData(crlf);
    }


    public void beginPrintf() {
        beginPrintf(1, 1);
    }

    /**
     * begin printf
     *
     * @param sequence    : Sequence
     * @param groupNumber : Group number
     */
    public void beginPrintf(int sequence, int groupNumber) {
        String PRINT = "PRINT " + sequence + "," + groupNumber + "\r\n";
        mPrinter.sendByteData(PRINT.getBytes());
    }


    /**
     * real printf
     *
     * @param concentration
     * @param number
     */
    private boolean realPrintfBitmapByLabelView(int width,int height,Bitmap bitmap, int concentration, int number) {
        try {
            initCanvas(width, height);
            clearCanvas();
            printfBitmap(0, 0, bitmap, concentration);
            beginPrintf(1, number);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            //Util.ToastTextThread(context, context.getString(R.string.printf_error_check));
            return false;
        }
    }

    /**
     * Picture two valued
     * Convert pictures into byte arrays
     * @param bmp
     * @return
     */
    public static byte[] convertToBMW(Bitmap bmp, int concentration) {
        if (concentration <= 0 || concentration >= 255) {
            concentration = 128;
        }
        int width = bmp.getWidth(); // 获取位图的宽
        int height = bmp.getHeight(); // 获取位图的高
        byte[] bytes = new byte[(width) / 8 * height];
        int[] p = new int[8];
        int n = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width / 8; j++) {
                for (int z = 0; z < 8; z++) {
                    int grey = bmp.getPixel(j * 8 + z, i);
                    int red = ((grey & 0x00FF0000) >> 16);
                    int green = ((grey & 0x0000FF00) >> 8);
                    int blue = (grey & 0x000000FF);
                    int gray = (int) (0.29900 * red + 0.58700 * green + 0.11400 * blue); // 灰度转化公式
                    if (gray <= concentration) {
                        gray = 0;
                    } else {
                        gray = 1;
                    }
                    p[z] = gray;
                }
                byte value = (byte) (p[0] * 128 + p[1] * 64 + p[2] * 32 + p[3] * 16 + p[4] * 8 + p[5] * 4 + p[6] * 2 + p[7]);
                bytes[width / 8 * i + j] = value;
            }
        }
        return bytes;
    }


}
