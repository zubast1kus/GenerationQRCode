package androidmads.library.qrgenearator;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.util.EnumMap;
import java.util.Map;

@SuppressWarnings("ALL")
public class QRGEncoder {

    private int WHITE = 0xFFFFFFFF;
    private int BLACK = 0xFF000000;
    private int dimension = Integer.MIN_VALUE;
    private String contents = null;
    private String displayContents = null;
    private String title = null;
    private BarcodeFormat format = null;
    private boolean encoded = false;

    public void setColorWhite(int color) {
        this.WHITE = color;
    }

    public void setColorBlack(int color) {
        this.BLACK = color;
    }

    public int getColorWhite() {
        return this.WHITE;
    }

    public int getColorBlack() {
        return this.BLACK;
    }

    public QRGEncoder(String data, String type) {
        encoded = encodeContents(data, null, QRGContents.Type.TEXT);
    }

    public QRGEncoder(String data, String type, int dimension) {
        this.dimension = dimension;
        encoded = encodeContents(data, null, QRGContents.Type.TEXT);
    }

    public QRGEncoder(String data, Bundle bundle, String type, int dimension) {
        this.dimension = dimension;
        encoded = encodeContents(data, bundle, type);
    }

    public String getTitle() {
        return title;
    }

    private boolean encodeContents(String data, Bundle bundle, String type) {
        // Default to QR_CODE if no format given.
        format = BarcodeFormat.QR_CODE;
        if (format == BarcodeFormat.QR_CODE) {
            this.format = BarcodeFormat.QR_CODE;
            encodeQRCodeContents(data, bundle, type);
        } else if (data != null && data.length() > 0) {
            contents = data;
            displayContents = data;
            title = "Text";
        }
        return contents != null && contents.length() > 0;
    }

    private void encodeQRCodeContents(String data, Bundle bundle, String type) {
        if (data != null && data.length() > 0) {
            contents = data;
            displayContents = data;
            title = "Text";
        }
    }

    public Bitmap getBitmap() {
        if (!encoded) return null;
        try {
            Map<EncodeHintType, Object> hints = null;
            String encoding = guessAppropriateEncoding(contents);
            if (encoding != null) {
                hints = new EnumMap<>(EncodeHintType.class);
                hints.put(EncodeHintType.CHARACTER_SET, encoding);
            }
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix result = writer.encode(contents, format, dimension, dimension, hints);
            int width = result.getWidth();
            int height = result.getHeight();
            int[] pixels = new int[width * height];
            // All are 0, or black, by default
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = result.get(x, y) ? getColorBlack() : getColorWhite();
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (Exception ex) {
            return null;
        }
    }

    private String guessAppropriateEncoding(CharSequence contents) {
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

    private String trim(String s) {
        if (s == null) {
            return null;
        }
        String result = s.trim();
        return result.length() == 0 ? null : result;
    }


}
