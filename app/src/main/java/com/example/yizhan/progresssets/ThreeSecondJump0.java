package com.example.yizhan.progresssets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 方框内，3秒跳过
 * Created by yizhan on 2017/12/16.
 */
public class ThreeSecondJump0 extends View {


    //背景色默认透明
    private int mBgColor = 0x00ffffff;

    //屏幕宽高
    private int mWidth;
    private int mHeight;

    private Paint mPaint;

    //圆角矩形内填充的颜色，默认是半透明的黑色
    private int mRectFillColor = 0x32000000;
    //圆角的大小
    private float mCornerX = 5;
    private float mCornerY = 5;

    //字体颜色、大小
    private int mTextColor = Color.WHITE;
    private int mTextSize = 14;
    //数字字体颜色、大小
    private int mNumberTextColor = Color.WHITE;
    private int mNumberTextSize = 12;

    //倒计时相关
    private Timer mTimer;
    private Handler mHandler;
    //默认延迟的时间，单位为s
    private int mDelayTime = 3;

    private boolean isStarting = false;

    //字体与数字之间的间距
    private float mDivider = 15;

    private JumpFinishCallback mCallback;


    public ThreeSecondJump0(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    /**
     * 初始化此view信息
     */
    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        //初始化倒计时相关
        mTimer = new Timer();
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        mDelayTime--;

                        if (mDelayTime == 0) {

                            //结束计时
                            end();

                            if (mCallback != null) {
                                mCallback.onFinish();
                            }
                        }

                        invalidate();
                        break;
                }
            }
        };

        //给当前控件设置一个点击事件
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //担心跳过会点好几次
                synchronized (ThreeSecondJump0.this) {

                    if (isStarting) {
                        //点击了就说明不要等到倒计时完成，现在就给结束了
                        //结束计时
                        end();

                        if (mCallback != null && mDelayTime != 0) {
                            mCallback.onClick();
                            mDelayTime = 0;
                            mCallback = null;
                        }

                        invalidate();
                    }
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

//        Log.i("test", "mDelayTime0 == " + mDelayTime);
        //判空处理
        if (this.mDelayTime < 0) {
            this.mDelayTime = 3;
        }

//        Log.i("test", "mDelayTime1 == " + mDelayTime);

        //控件坐标系移至控件中心位置
        canvas.translate(mWidth / 2, mHeight / 2);

        //先画出透明背景色
        canvas.drawColor(mBgColor);

        //在其上画出圆角矩形，圆角大小由外界决定
        mPaint.setColor(mRectFillColor);
        RectF rectF = new RectF(-mWidth / 2, -mHeight / 2, mWidth / 2, mHeight / 2);
        canvas.drawRoundRect(rectF, mCornerX, mCornerY, mPaint);

        //画出"跳过"两个字
        String text = "跳过";

        //1. 估算出宽度

        //设置画笔
        mPaint.setTextSize(getResources().getDisplayMetrics().scaledDensity * mTextSize);
        mPaint.setTextAlign(Paint.Align.LEFT);

        //估算出字体的宽度
        Rect rect = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), rect);
        float textWidth = rect.right - rect.left;

        //估算出数字的宽度
        float numberWidth = 0f;
        if (isStarting) {
            mPaint.setTextSize(getResources().getDisplayMetrics().scaledDensity * mNumberTextSize);
            mPaint.setTextAlign(Paint.Align.LEFT);

            Rect rect1 = new Rect();
            mPaint.getTextBounds("3", 0, 1, rect1);
            numberWidth = rect1.right - rect1.left;
        }

        //总长度，中间添加间距
        float length = textWidth + mDivider + numberWidth;


        //2. 画出字体，估算出字体和数字整体处于中心位置时，基线与字体左边界交叉点的坐标

        mPaint.setColor(mTextColor);
        mPaint.setTextSize(getResources().getDisplayMetrics().scaledDensity * mTextSize);
        mPaint.setTextAlign(Paint.Align.LEFT);

        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        canvas.drawText(text, -length / 2, (-fontMetrics.top - fontMetrics.bottom) / 2, mPaint);

        //画出倒计时
        if (isStarting) {
            mPaint.setColor(mNumberTextColor);
            mPaint.setTextSize(getResources().getDisplayMetrics().scaledDensity * mNumberTextSize);
            mPaint.setTextAlign(Paint.Align.LEFT);

            Paint.FontMetrics fontMetrics1 = mPaint.getFontMetrics();
            canvas.drawText(mDelayTime + "", -length / 2 + textWidth + mDivider, (-fontMetrics1.top - fontMetrics1.bottom) / 2, mPaint);
        }

    }


    /**
     * 设置圆角矩形中填充的颜色
     */
    public void setFillColor(int fillColor) {
        this.mRectFillColor = fillColor;
        invalidate();
    }

    /**
     * 设置圆角的大小
     */
    public void setCornerXY(float cornerX, float cornerY) {

        //非正常值处理
        if (cornerX <= 0) {
            cornerX = 5;
        }
        if (cornerY <= 0) {
            cornerY = 5;
        }

        this.mCornerX = cornerX;
        this.mCornerY = cornerY;
        invalidate();
    }

    /**
     * 设置"跳过"二字的颜色，默认是白色
     */
    public void setTextColor(int textColor) {
        this.mTextColor = textColor;
        invalidate();
    }

    /**
     * 设置字体的大小，单位为sp，默认为14
     */
    public void setTextSize(int textSize) {

        if (textSize <= 0) {
            textSize = 14;
        }

        this.mTextSize = textSize;
        invalidate();
    }

    /**
     * 延迟时间，单位为s
     */
    public void start(int delayTime, JumpFinishCallback callback) {

        //正在倒计时就不要重复调了
        if (isStarting) {
            return;
        }

        if (mTimer == null || mHandler == null) {
            return;
        }

        this.mDelayTime = delayTime;
        this.mCallback = callback;
        isStarting = true;
        invalidate();

        //开始倒计时
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(0);
            }
        }, 1000, 1000);
    }

    public void end() {

        if (isStarting) {//正在开着的才去结束
            mTimer.cancel();
            isStarting = false;
            mTimer = null;
            mHandler = null;
        }

    }

    /**
     * 设置数字字体颜色
     */
    public void setNumberTextColor(int numberTextColor) {
        this.mNumberTextColor = numberTextColor;
        invalidate();
    }

    /**
     * 设置数字字体大小
     */
    public void setNumberTextSize(int numberTextSize) {
        this.mNumberTextSize = numberTextSize;
        invalidate();
    }

    public void setDivider(float divider) {
        this.mDivider = divider;
        invalidate();
    }


    /**
     * 设置倒计时的回调
     */
    public void setCallback(JumpFinishCallback callback) {
        this.mCallback = callback;
    }


    public static interface JumpFinishCallback {
        /**
         * 跳过倒计时完成时的回调，回调运行在主线程中
         */
        void onFinish();

        /**
         * 点击完成
         */
        void onClick();
    }
}
