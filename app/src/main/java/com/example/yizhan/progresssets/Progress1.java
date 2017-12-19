package com.example.yizhan.progresssets;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by yizhan on 2017/12/18.
 */

public class Progress1 extends View {

    private Paint mPaint;
    private int mWidth;
    private int mHeight;

    private int mBgColor = Color.TRANSPARENT;

    private int mPadding = 10;
    private ValueAnimator mValueAnimator;
    private float mCurrentValue;

    public Progress1(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        //初始化动画
        mValueAnimator = ValueAnimator.ofFloat(0, 1);
        mValueAnimator.setDuration(1200);
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimator.setRepeatMode(ValueAnimator.RESTART);
        mValueAnimator.setInterpolator(null);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //画透明背景
        canvas.drawColor(mBgColor);

        //控件坐标系移到控件的中心
        canvas.translate(mWidth / 2, mHeight / 2);

        //画图片
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.loading);
        //图片的宽高是一样的，所以只获取一个宽就可以了
        int bitmapWH = bitmap.getWidth();
        //找出控件宽高较小的
        int temp = mWidth > mHeight ? mHeight : mWidth;
        int tempContent = temp - mPadding;

        //控件的较小边比图片的宽或高大多少倍
        float s = tempContent * 1.0f / bitmapWH;


        Matrix matrix = new Matrix();
        matrix.postTranslate(-bitmapWH / 2, -bitmapWH / 2);
        matrix.postScale(s, s);
        matrix.postRotate(360 * mCurrentValue);
        canvas.drawBitmap(bitmap, matrix, mPaint);
    }

    public void show() {
        mValueAnimator.start();
    }

    public void hide() {
        mValueAnimator.end();
    }

}
