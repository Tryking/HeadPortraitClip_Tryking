package com.tryking.headportraitclip_tryking.clip_widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by Tryking on 2016/4/15.
 */
public class ClipImageLayout extends RelativeLayout {
    private ClipZoomImageView mZoomImageView;
    private ClipBorderView mClipBorderView;

    //此为裁剪区圆形边界距离屏幕边缘的距离，值越大，裁剪区域就越小，反之越大（当然不能小于0）
    private int mHorizontalPadding = 60;

    public ClipImageLayout(Context context) {
        this(context, null);
    }

    public ClipImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mZoomImageView = new ClipZoomImageView(context);
        mClipBorderView = new ClipBorderView(context);

        ViewGroup.LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mZoomImageView, lp);
        this.addView(mClipBorderView, lp);

        //计算padding 的px
        mHorizontalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mHorizontalPadding, getResources().getDisplayMetrics());
        mZoomImageView.setHorizontalPadding(mHorizontalPadding);
        mClipBorderView.setHorizontalPadding(mHorizontalPadding);
    }

    /**
     * 对外公布的设置边距的方法，单位为dp
     *
     * @param horizontalPadding
     */
    public void setHorizontalPadding(int horizontalPadding) {
        this.mHorizontalPadding = horizontalPadding;
    }

    /**
     * 裁剪图片
     *
     * @return
     */
    public Bitmap clip() {
        return mZoomImageView.clip();
    }

    /**
     * 设置图片
     *
     * @param bitmap
     */
    public void setBitmap(Bitmap bitmap) {
        mZoomImageView.setImageBitmap(bitmap);
    }
}
