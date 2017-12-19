package com.example.yizhan.progresssets;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 仿交通银行缓冲框
 * Created by yizhan on 2017/12/18.
 */
public class Progress2 extends View {

    private Paint mPaint;

    //边界的大小
    private float mStrokeWidth = 5;
    private int mWidth;
    private int mHeight;

    /**
     * 所画内容与控件边界的间距
     */
    private float mPadding = 5;

    private int mBgColor = Color.TRANSPARENT;

    private int mCirCleBgColor = 0x10000000;

    private int mCircleColor = Color.BLUE;

    private float ratio = 0.12f;
    private ValueAnimator mValueAnimator;
    private float mCurrentValue = 0f;

    private long mDuration = 2500;

    public Progress2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {

        mPaint = new Paint();
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);

        //初始化动画
        mValueAnimator = ValueAnimator.ofFloat(0, 1);
        mValueAnimator.setDuration(mDuration);
        mValueAnimator.setInterpolator(null);
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimator.setRepeatMode(ValueAnimator.RESTART);
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

        //透明背景
        canvas.drawColor(mBgColor);

        //控件坐标系平移到控件中心
        canvas.translate(mWidth / 2, mHeight / 2);

        //计算出单个圆的半径
        //当所画内容的宽尽量填充控件的宽时单个圆的半径
        float radius0 = (mWidth - mPadding) * 1.0f / 4 - mStrokeWidth;
        //当所画内容的高尽量填充控件的高时单个圆的半径
        float radius1 = (mHeight - mPadding) * 1.0f / 2 - mStrokeWidth;
        //较小值即为合适的半径
        float radius = radius0 > radius1 ? radius1 : radius0;


        //画出背景轨道
        mPaint.setColor(mCirCleBgColor);
        mPaint.setStrokeWidth(mStrokeWidth);

        Path path = new Path();

        RectF rectF = new RectF(0, -radius, 2 * radius, radius);
        //这里虽然可以设置成-360，但设置成-360就会有问题，多了一条线
        path.addArc(rectF, 180, -359.99f);


        RectF rectF1 = new RectF(-2 * radius, -radius, 0, radius);
        path.arcTo(rectF1, 0, 359.99f);

        canvas.drawPath(path, mPaint);

        //画动态的变化
        mPaint.setColor(mCircleColor);

        PathMeasure pathMeasure = new PathMeasure(path, false);
        float length = pathMeasure.getLength();
        float startD = mCurrentValue * length;
        Path pathDst = new Path();
        if (mCurrentValue + ratio <= 1) {
            float stopD = (mCurrentValue + ratio) * length;
            pathMeasure.getSegment(startD, stopD, pathDst, true);
        } else {
            pathMeasure.getSegment(startD, length, pathDst, true);
            Path pathDst0 = new Path();
            pathMeasure.getSegment(0, (mCurrentValue + ratio - 1) * length, pathDst0, true);
            //都结合到path中
            pathDst.addPath(pathDst0);
        }

        canvas.drawPath(pathDst, mPaint);
    }


    public void show() {
        mValueAnimator.start();
    }
}
