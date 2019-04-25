package com.wan.grace.voicerecord;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by 开发部 on 2018/7/10.
 */

public class WaveView extends View {

    private Path mPath;
    private Paint mPaint;

    /**
     * 频率
     */
    private float frequency = 1.5f;
    /**
     * 振幅
     */
    private float IdleAmplitude = 0.00f;
    /**
     * 波数
     */
    private int waveNumber = 2;
    /**
     * 相移
     */
    private float phaseShift = 0.15f;
    /**
     * 初始的相移
     */
    private float initialPhaseOffset = 0.0f;
    /**
     * 波峰高度
     */
    private float waveHeight;
    /**
     * 波动的垂直位置
     */
    private float waveVerticalPosition = 2;
    /**
     * 波的颜色
     */
    private int waveColor;
    /**
     * 阶段
     */
    private float phase;
    /**
     * 振幅
     */
    private float amplitude;
    /**
     * 震动波峰比例
     */
    private float level = 1.0f;

    ObjectAnimator mAmplitudeAnimator;

    public WaveView(Context context) {
        super(context);
        if (!isInEditMode())
            init(context, null);
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode())
            init(context, attrs);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode())
            init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WaveView);
        frequency = a.getFloat(R.styleable.WaveView_waveFrequency, frequency);
        IdleAmplitude = a.getFloat(R.styleable.WaveView_waveAmplitude, IdleAmplitude);
        phaseShift = a.getFloat(R.styleable.WaveView_wavePhaseShift, phaseShift);
        initialPhaseOffset = a.getFloat(R.styleable.WaveView_waveInitialPhaseOffset, initialPhaseOffset);
        waveHeight = a.getDimension(R.styleable.WaveView_waveHeight, waveHeight);
        waveColor = a.getColor(R.styleable.WaveView_waveColor, waveColor);
        waveVerticalPosition = a.getFloat(R.styleable.WaveView_waveVerticalPosition, waveVerticalPosition);
        waveNumber = a.getInteger(R.styleable.WaveView_waveAmount, waveNumber);

        mPath = new Path();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1);
        mPaint.setColor(waveColor);

        a.recycle();
        initAnimation();
    }

    private void initAnimation() {
        if (mAmplitudeAnimator == null) {
            //不断的改变amplitude属性的值不断的调用setAmplitude，从而不断的重绘
            mAmplitudeAnimator = ObjectAnimator.ofFloat(this, "amplitude", 1f);
            mAmplitudeAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        }
        //如果默认初始化控件时候就需要动画执行，则可以把注释放开
//        if (!mAmplitudeAnimator.isRunning()) {
//            mAmplitudeAnimator.start();
//        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!mAmplitudeAnimator.isRunning()) {
            //当动画不执行时，我们需要绘制一条直线
            canvas.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2, mPaint);
        } else {
            canvas.drawPath(mPath, mPaint);
            updatePath(canvas);
        }
    }

    private void updatePath(Canvas canvas) {
        mPath.reset();
        //每次绘制的相移变化值
        phase += phaseShift;
        //每次绘制的振幅
        amplitude = Math.max(level, IdleAmplitude);
        for (int i = 0; i < waveNumber; i++) {
            //波形图在控件内部高度位置
            float wavePosition = getHeight() / waveVerticalPosition;
            float width = getWidth();
            float mid = width / 2.0f;
            //最大振幅为波峰的最大高度
            float maxAmplitude = waveHeight;
            //第i+1根线的进度信息
            float progress = 1.0f - (float) i / waveNumber / 8;
            //为了区分不同曲线的高度
            float normedAmplitude = (1.5f * progress - 0.5f) * amplitude;
            for (int x = 0; x < width; x++) {
                Log.i("scal",1 / mid * (x - mid)+"");
                float scaling = (float) (-Math.pow(1 / mid * (x - mid), 2) + 1);
                float y = (float) (scaling * maxAmplitude * normedAmplitude * Math.sin(2 *
                        Math.PI * (x / width) * frequency + phase + initialPhaseOffset) + wavePosition);
                if (x == 0) {
                    mPath.moveTo(x, y);
                } else {
                    //需要使每根波线高度区别开来
                    mPath.lineTo(x + i * 15, y);
                }
            }
        }
    }

    public void setAmplitude(float amplitude) {
        this.amplitude = amplitude;
        invalidate();
    }

    public void setWaveHeight(float height) {
        this.waveHeight = height;
        invalidate();
    }

    public float getAmplitude() {
        return this.amplitude;
    }

    public void stopAnimation() {
        if (mAmplitudeAnimator != null) {
            mAmplitudeAnimator.removeAllListeners();
            mAmplitudeAnimator.end();
            mAmplitudeAnimator.cancel();
        }
    }

    public void startAnimation() {
        if (mAmplitudeAnimator != null) {
            mAmplitudeAnimator.start();
        }
    }

    public void setWaveColor(int waveColor) {
        mPaint.setColor(waveColor);
        invalidate();
    }

    public void setStrokeWidth(float strokeWidth) {
        mPaint.setStrokeWidth(strokeWidth);
        invalidate();
    }

    public void setPhaseShift(float phaseShift) {
        this.phaseShift = phaseShift;
        invalidate();
    }
}
