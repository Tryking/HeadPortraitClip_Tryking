package com.tryking.headportraitclip_tryking;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2016/4/21.
 */
public class BitmapUtil {
    private static final int UNCONSTRAINED = -1;

    // 获得图像
    private static Bitmap getBitmapByPath(String path, BitmapFactory.Options options, int screenWidth, int screenHeight) throws FileNotFoundException {
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        FileInputStream inputStream = null;
        inputStream = new FileInputStream(file);
        if (options != null) {
            Rect r = getScreenRegion(screenWidth, screenHeight);
            // 取得图片的宽和高
            int w = r.width();
            int h = r.height();
            int maxSize = w > h ? w : h;
            // 计算缩放比例
            int inSimpleSize = computeSampleSize(options, maxSize, w * h);
            // 设置缩放比例
            options.inSampleSize = inSimpleSize;
            options.inJustDecodeBounds = false;
        }

        // 加载压缩后的图片
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    // 获得设置信息
    public static BitmapFactory.Options getOptions(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 只描边，不读取数据
        options.inJustDecodeBounds = true;
        // 加载到内存
        BitmapFactory.decodeFile(path, options);
        return options;
    }

    // 返回加载后的大图片
    public static Bitmap getBitmap(String path, int screenWidth, int screenHeight) throws FileNotFoundException {
        return BitmapUtil.getBitmapByPath(path, BitmapUtil.getOptions(path), screenWidth, screenHeight);
    }

    private static Rect getScreenRegion(int width, int height) {
        return new Rect(0, 0, width, height);
    }

    // 获取需要进行缩放的比例，即options.inSampleSize
    private static int computeSampleSize(BitmapFactory.Options options,
                                         int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);
        return initialSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        // 获得图片的宽和高
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 : (int) Math
                .ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == UNCONSTRAINED) ? 128 : (int) Math
                .min(Math.floor(w / minSideLength),
                        Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            return lowerBound;
        }
        if ((maxNumOfPixels == UNCONSTRAINED)
                && (minSideLength == UNCONSTRAINED)) {
            return 1;
        } else if (minSideLength == UNCONSTRAINED) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * Stores an image on the storage
     *
     * @param image       the image to store.
     * @param pictureFile the file in which it must be stored
     */
    public static void storeImage(Bitmap image, File pictureFile) {
        if (pictureFile == null) {
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("storeImage", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("storeImage", "Error accessing file: " + e.getMessage());
        }
    }
}
