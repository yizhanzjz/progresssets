package com.example.yizhan.progresssets;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 圆形内3秒
 * Created by yizhan on 2017/12/18.
 */

public class ThreeSecondJump1 extends View {

    private Paint mPaint;

    //控件背景色
    private int mBgColor = Color.TRANSPARENT;

    //圆背景色
    private int mCircleBgColor = Color.BLACK;
    private int mWidth;
    private int mHeight;

    private float mCircleRadius = 0;

    private float mStrokeWidth = 5;

    private float mPadding = 5;

    private int mTextSize = 30;
    private int mTextColor = Color.WHITE;

    private int mCircleStrokeColor = Color.RED;
    private ValueAnimator mValueAnimator;
    private long mDelayTime = 5000;

    private float mCurrentValue = 0;
    private boolean isStarting = false;
    private JumpFinishCallback mCallback;

    public ThreeSecondJump1(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {

        //初始化画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        //初始化动画
        mValueAnimator = ValueAnimator.ofFloat(0, 1);
        mValueAnimator.setDuration(mDelayTime);
        mValueAnimator.setInterpolator(null);//线性辩护
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                end();

                invalidate();

                if (mCallback != null) {
                    mCallback.onFinish();
                }
            }
        });


        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStarting) {
                    //结束动画
                    mValueAnimator.end();
                }
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

        canvas.translate(mWidth / 2, mHeight / 2);

        // 画出控件背景色，为透明
        canvas.drawColor(mBgColor);

        //宽高哪个更小
        int temp = mWidth > mHeight ? mHeight : mWidth;
        //更小的那个，一半儿做圆半径
        mCircleRadius = temp / 2 - mStrokeWidth - mPadding;


        //画出圆
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mCircleBgColor);
        canvas.drawCircle(0, 0, mCircleRadius, mPaint);


        //画出文字
        mPaint.setColor(mTextColor);
        float textWidth = 0;
        float textHeight = 0;
        //遍历出合适的textSize
        do {
            mPaint.setTextSize(mTextSize);
            Rect rect = new Rect();
            mPaint.getTextBounds("跳过", 0, 2, rect);
            textWidth = rect.right - rect.left;
            textHeight = rect.bottom - rect.top;

            if (Math.pow(textWidth / 2, 2) + Math.pow(textHeight / 2, 2) > Math.pow(mCircleRadius, 2)) {
                mTextSize -= 0.5;
            } else {
                break;
            }

        } while (true);

        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        canvas.drawText("跳过", -textWidth / 2, (-fontMetrics.top - fontMetrics.bottom) / 2, mPaint);


        if (isStarting) {
            //画出红色边界
            canvas.save();

            canvas.rotate(-90);

            mPaint.setColor(mCircleStrokeColor);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(mStrokeWidth);
            mPaint.setStrokeCap(Paint.Cap.ROUND);

            Path path = new Path();
            path.addCircle(0, 0, mCircleRadius, Path.Direction.CW);

            PathMeasure pathMeasure = new PathMeasure(path, false);
            float length = pathMeasure.getLength();
            Path pathDst = new Path();
            pathMeasure.getSegment(mCurrentValue * length, length, pathDst, true);
            canvas.drawPath(pathDst, mPaint);

            canvas.restore();
        }
    }

    /**
     * 延迟时间，单位为ms
     */
    public void start(int delayTime, JumpFinishCallback callback) {

        //正在倒计时就不要重复调了
        if (isStarting) {
            return;
        }

        this.mDelayTime = delayTime;
        this.mCallback = callback;
        isStarting = true;

        mValueAnimator.setDuration(mDelayTime);
        mValueAnimator.start();
    }

    public void end() {

        if (isStarting) {//正在开着的才去结束
            isStarting = false;
        }

    }

    public static interface JumpFinishCallback {
        /**
         * 跳过倒计时完成时的回调，回调运行在主线程中
         */
        void onFinish();

    }

}
