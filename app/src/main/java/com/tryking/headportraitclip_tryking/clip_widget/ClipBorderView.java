package com.tryking.headportraitclip_tryking.clip_widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by Tryking on 2016/4/15.
 */
public class ClipBorderView extends View {
    /**
     * 水平方向与View的边距
     */
    private int mHorizontalPadding = 0;
    /**
     * 边框的宽度 单位dp
     */
    private int mBorderWidth = 2;
    private Paint mPaintRect;
    private Paint mPaintCircle;
    private Paint mPaintRing;
    private Bitmap mBgBitmap;
    private Canvas mCanvas;//阴影层画布
    private RectF mRectF;//整个屏幕

    public ClipBorderView(Context context) {
        this(context, null);
    }

    public ClipBorderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClipBorderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBorderWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mBorderWidth, getResources().getDisplayMetrics());

        //绘制阴影层
        mPaintRect = new Paint();
        //画笔颜色透明色
//        mPaintRect.setColor(Color.parseColor("#30000000"));
        mPaintRect.setARGB(145, 0, 0, 0);
//        mPaintRect.setAlpha(10);

        //绘制实心圆
        mPaintCircle = new Paint();
        mPaintCircle.setStrokeWidth((getWidth() - 2 * mHorizontalPadding) / 2);//实心圆半径
        mPaintCircle.setARGB(255, 0, 0, 0);
        mPaintCircle.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));//XOR模式：重叠部分被掏空

        //绘制圆环
        mPaintRing = new Paint();
        mPaintRing.setStyle(Paint.Style.STROKE);
        mPaintRing.setAntiAlias(true);//抗锯齿
        mPaintRing.setColor(Color.WHITE);//边框颜色，白色
        mPaintRing.setStrokeWidth(mBorderWidth);//画笔宽度
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBgBitmap == null) {
            mBgBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBgBitmap);
            mRectF = new RectF(0, 0, getWidth(), getHeight());
        }
        //绘制阴影层
        mCanvas.drawRect(mRectF, mPaintRect);
        //绘制实心圆，绘制完后，在mCanvas画布中，mPaintRect和mPaintCircle相交部分即被掏空
        mCanvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - mHorizontalPadding, mPaintCircle);
        //将阴影层画进本View的画布中
        canvas.drawBitmap(mBgBitmap, null, mRectF, new Paint());
        //绘制圆环
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - mHorizontalPadding, mPaintRing);
    }

    /**
     * 设置水平方向与view的边距
     *
     * @param horizontalPadding
     */
    public void setHorizontalPadding(int horizontalPadding) {
        this.mHorizontalPadding = horizontalPadding;
    }
}
