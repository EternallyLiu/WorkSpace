package com.example.administrator.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;

/**
 * Liu Pei
 * mail:lp960822@outlook.com
 */

public class CustomProgressBar extends View {

    public static final float EMPTY_PROGRESS = 0;
    public static final float FULL_PROGRESS = 100;
    /**
     * 进度条最大值
     */
    private float mMaxValue = FULL_PROGRESS;

    /**
     * 当前进度值
     */
    private float mCurrentValue = 0;

    /**
     * 底部圆弧的颜色，默认为Color.WHITE
     */
    private int mFirstColor;

    /**
     * 进度条圆环的颜色
     */
    private int mSecondColor;

    /**
     * 进度条内部圆的颜色
     */
    private int mInsideColor;

    /**
     * 圆环内图标的样式
     */
    private Bitmap mInsideBitmap;

    /**
     * 圆环的宽度（环宽）
     */
    private int mCircleWidth;

    /**
     * 每段间隙大小
     */
    private float mBlockGap = 0.5f;

    /**
     * 画圆环的画笔
     */
    private Paint mCirclePaint;

    /**
     * 画圆环内样式的画笔
     */
    private Paint mInsidePaint;

    /**
     * 是否处于暂停状态
     */
    private boolean isPause = false;

    /**
     * 暂停图标在屏幕中的绘制位置
     */
    private Rect mBitmapDestRect;

    /**
     * 暂停图标的绘制区域
     */
    private Rect mBitmapSrcRect;

    /**
     * 暂停图标的宽度
     */
    private int mPauseWidth = 0;

    /**
     * 暂停图标的高度
     */
    private int mPauseHeight = 0;

    /**
     * 执行状态下透明描边的宽度
     */
    private int mStokeWidth = 15;

    /**
     * 暂停动画的位置标记
     */
    private float mPauseLocation;

    /**
     * 启动动画的标记位置
     */
    private float mStartLocation;

    /**
     * 动画的执行时间
     */
    private final int DEFAULT_ANIMTIME = 200;

    private int mAnimTime = DEFAULT_ANIMTIME;

    /**
     * 暂停时圆环的颜色
     */
    private int mPauseColor;

    /**
     * 记录轨迹的集合
     */
    private ArrayList<Float> mAngleList;

    /**
     * 每段执行区间的初始位置
     */
    private float mLastValue = 0;

    /**
     * 一个执行区间每次绘制的细微间隔位置
     */
    private float mLastProgress = 0;

    /**
     * 本次需要绘制的角度
     */
    private float mNeedMoveAngle = 0;

    /**
     * 是否为继续动画执行
     */
    private boolean isContinue = false;

    /**
     * 缝隙所占的角度
     */
    private float mBlockAngle;

    /**
     * 圆周最大角度
     */
    private static final int ROUND_ANGLE = 270;

    /**
     * 圆环绘制的起始角度
     */
    private static final float START_ANGLE = -90;

    /**
     * 角度最大值
     */
    private float mMaxAngle;

    /**
     * 是否为删除操作
     */
    private boolean isDelete = false;

    /**
     * 是否完成
     */
    private boolean isFinish = false;

    /**
     * 渐变色
     */
    private int mGradientColor;

    /**
     * 是否为首次加载
     */
    private boolean isFirstInit = true;

    /**
     * 中心的坐标值
     */
    private int mCenter;

    /**
     * 圆的半径
     */
    private int mRadius;
    private RectF mOval;

    /**
     * 内部圆里的图标透明度
     */
    private int MAX_ALPHA = 255;
    private int mInsideBitmapAlpha = MAX_ALPHA;


    public CustomProgressBar(Context context) {
        this(context, null);
    }

    public CustomProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomProgressBar,
                defStyleAttr, 0);

        mAngleList = new ArrayList<>();
        int n = ta.getIndexCount();

        for (int i = 0; i < n; i++) {
            int attr = ta.getIndex(i);
            switch (attr) {
                case R.styleable.CustomProgressBar_cirleWidth:
                    mCircleWidth = ta.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            6, getResources().getDisplayMetrics()));//默认宽度6dp

                    break;
                case R.styleable.CustomProgressBar_firstColor:
                    mFirstColor = ta.getColor(attr, Color.WHITE);//默认底色为白色

                    break;
                case R.styleable.CustomProgressBar_secondColor:
                    mSecondColor = ta.getColor(attr, Color.BLACK);//默认填充色为黑色

                    break;
                case R.styleable.CustomProgressBar_baseProgress:
                    mCurrentValue = ta.getFloat(attr, 0);//默认进度为0
                    if (mCurrentValue != 0) {
                        mAngleList.add(changePercentToAngle(mCurrentValue));
                    }
                    mLastValue = mCurrentValue;

                    break;
                case R.styleable.CustomProgressBar_insideColor:
                    mInsideColor = ta.getColor(attr, Color.WHITE);//默认圆环内填充白色
                    mGradientColor = mInsideColor;

                    break;
                case R.styleable.CustomProgressBar_pauseIcon:
                    mInsideBitmap = BitmapFactory.decodeResource(getResources(),
                            ta.getResourceId(attr, 0));

                    break;
                case R.styleable.CustomProgressBar_pauseIconWidth:
                    mPauseWidth = ta.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            mInsideBitmap.getWidth(), getResources().getDisplayMetrics()));//默认为图片自己的宽度

                    break;
                case R.styleable.CustomProgressBar_pauseIconHeight:
                    mPauseHeight = ta.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            mInsideBitmap.getHeight(), getResources().getDisplayMetrics()));//默认为图片自己的高度

                    break;

                case R.styleable.CustomProgressBar_insideStokeWidth:
                    mStokeWidth = ta.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            15, getResources().getDisplayMetrics()));//默认透明环宽度为15dp
                    break;

                case R.styleable.CustomProgressBar_animTime:
                    mAnimTime = ta.getInteger(attr, 230);//默认动画执行230毫秒

                    break;

                case R.styleable.CustomProgressBar_pauseColor:
                    mPauseColor = ta.getColor(attr, Color.BLACK);//默认暂停时圆环颜色为黑色

                    break;

                case R.styleable.CustomProgressBar_blockGap:
                    mBlockGap = ta.getFloat(attr, 0.5f);
                    mBlockAngle = changePercentToAngle(mBlockGap);
                    mMaxAngle = ROUND_ANGLE - mBlockAngle / 2;
                    break;
                default:
                    break;
            }
        }

        ta.recycle();

        mCirclePaint = new Paint();
        //加入抗锯齿和防抖动，方便渐变色使用
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setDither(true);
        mCirclePaint.setStrokeWidth(mCircleWidth);
        //设置方形截面
        mCirclePaint.setStrokeCap(Paint.Cap.SQUARE);

        mInsidePaint = new Paint();
        mInsidePaint.setAntiAlias(true);
        mInsidePaint.setDither(true);

        //圆环内图片的范围
        mBitmapDestRect = new Rect();

        if (mPauseHeight == 0 || mPauseWidth == 0) {
            mBitmapSrcRect = new Rect(0, 0, mInsideBitmap.getWidth(), mInsideBitmap.getHeight());
        } else {
            mBitmapSrcRect = new Rect(0, 0, mPauseWidth, mPauseHeight);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        //选取宽高最小的作为半径
        setMeasuredDimension(Math.min(measureWidth, measureHeight),
                Math.min(measureWidth, measureHeight));

        mCenter = this.getWidth() / 2;
        mRadius = mCenter - mCircleWidth / 2;
        mOval = new RectF(mCenter - mRadius, mCenter - mRadius,
                mCenter + mRadius, mCenter + mRadius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircle(canvas);
        drawInside(canvas);
    }

    /**
     * 绘制进度圆弧
     *
     * @param canvas
     */
    private void drawCircle(Canvas canvas) {
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setColor(mFirstColor);
        canvas.drawCircle(mCenter, mCenter, mRadius, mCirclePaint);

        //每次绘制的起始角度
        float mCurrentAngle = START_ANGLE + mBlockAngle / 2;
        for (int i = 0; i < mAngleList.size(); i++) {
            mCirclePaint.setColor(mPauseColor);
            //每段移动的角度
            float moveAngle = mAngleList.get(i);
            if (mCurrentAngle + moveAngle >= mMaxAngle) {
                canvas.drawArc(mOval, mCurrentAngle, mMaxAngle - mCurrentAngle, false, mCirclePaint);
                mCurrentAngle = mMaxAngle;
            } else {
                canvas.drawArc(mOval, mCurrentAngle, moveAngle, false, mCirclePaint);
                mCurrentAngle += moveAngle;
            }

            //每段之间的分隔区域
            if (!isFinish || i != mAngleList.size() - 1) {
                mCirclePaint.setColor(mFirstColor);
                canvas.drawArc(mOval, mCurrentAngle, mBlockAngle, false, mCirclePaint);
                mCurrentAngle += mBlockAngle;
            }
        }

        if (isPause) {
            //执行缩放动画时不需要执行进度条的绘制
            return;
        }

        //暂停时绘制变色
        if (isPause) {
            mCirclePaint.setColor(mPauseColor);
        } else {
            mCirclePaint.setColor(mSecondColor);
        }

        float acrossAngle = changePercentToAngle(mCurrentValue - mLastProgress);

        //本次从起始位置需要移动的角度
        if (!isDelete) {
            mNeedMoveAngle += acrossAngle;
        }

        if ((mCurrentAngle + mNeedMoveAngle) >= mMaxAngle) {
            float finallyNeed = mMaxAngle - mCurrentAngle;
            mCirclePaint.setColor(mPauseColor);
            if (finallyNeed > 0) {
                isFinish = true;
                canvas.drawArc(mOval, mCurrentAngle, finallyNeed, false, mCirclePaint);
                //存储最后一段的绘制轨迹
                mAngleList.add(finallyNeed);
                finish();
            }
        } else {
            canvas.drawArc(mOval, mCurrentAngle, mNeedMoveAngle, false, mCirclePaint);
        }
    }


    /**
     * 绘制进度条内部样式
     *
     * @param canvas
     */
    private void drawInside(final Canvas canvas) {
        if (isFirstInit) {
            //首次加载
            isPause = true;
            isContinue = false;
            mInsidePaint.setStyle(Paint.Style.FILL);
            mInsidePaint.setColor(mPauseColor);
            canvas.drawCircle(mCenter, mCenter, getWidth() / 2 - mCircleWidth - mStokeWidth + 1, mInsidePaint);
            return;
        }

        if (!isPause || isContinue) {
            //执行态
            mInsidePaint.setStyle(Paint.Style.FILL);
            mInsidePaint.setColor(mGradientColor);
            canvas.drawCircle(mCenter, mCenter, mStartLocation, mInsidePaint);

            mBitmapDestRect.left = mCenter - mInsideBitmap.getWidth() / 2;
            mBitmapDestRect.top = mCenter - mInsideBitmap.getHeight() / 2;
            mBitmapDestRect.right = mCenter + mInsideBitmap.getWidth() / 2;
            mBitmapDestRect.bottom = mCenter + mInsideBitmap.getHeight() / 2;

            mInsidePaint.setAlpha(mInsideBitmapAlpha);
            canvas.drawBitmap(mInsideBitmap, mBitmapSrcRect, mBitmapDestRect, mInsidePaint);
        } else {
            //暂停态
            mInsidePaint.setStyle(Paint.Style.STROKE);
            mInsidePaint.setColor(Color.TRANSPARENT);
            mInsidePaint.setStrokeWidth(mStokeWidth);
            canvas.drawCircle(mCenter, mCenter, mCenter - mCircleWidth - mStokeWidth / 2, mInsidePaint);

            mInsidePaint.setStyle(Paint.Style.FILL);
            mInsidePaint.setColor(mGradientColor);
            canvas.drawCircle(mCenter, mCenter, mPauseLocation, mInsidePaint);
        }

    }

    /**
     * 设置圆环的宽度
     *
     * @param width
     */
    public void setCircleWidth(int width) {
        this.mCircleWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width,
                getResources().getDisplayMetrics());

        mCirclePaint.setStrokeWidth(mCircleWidth);
        invalidate();
    }

    /**
     * 设置进度条的颜色
     *
     * @param color
     */
    public void setCircleColor(int color) {
        this.mFirstColor = color;
        mCirclePaint.setColor(mFirstColor);
        invalidate();
    }

    /**
     * 设置圆环的底色
     *
     * @param color
     */
    public void setBottomColor(int color) {
        this.mSecondColor = color;
        mCirclePaint.setColor(mSecondColor);
        invalidate();
    }

    /**
     * 设置圆环内部的颜色
     *
     * @param color
     */
    public void setInsideColor(int color) {
        this.mInsideColor = color;
        mInsidePaint.setColor(mInsideColor);
        invalidate();
    }

    /**
     * 设置当前进度，无动画
     *
     * @param progress
     */
    public void setProgress(float progress) {

        float percent = progress * 100 / mMaxValue;
        if (percent < EMPTY_PROGRESS) {
            percent = EMPTY_PROGRESS;
        }
        if (percent > FULL_PROGRESS) {
            percent = FULL_PROGRESS;
        }

        this.mLastProgress = mCurrentValue;
        this.mCurrentValue = percent;
        invalidate();
    }

    /**
     * 设置当前进度，可以选择是否使用动画
     *
     * @param progress
     * @param useAnimation
     */
    public void setProgress(float progress, boolean useAnimation) {
        float percent = progress * 100 / mMaxValue;
        if (percent < EMPTY_PROGRESS) {
            percent = EMPTY_PROGRESS;
        }
        if (percent > FULL_PROGRESS) {
            percent = FULL_PROGRESS;
        }
        if (useAnimation) {
            ValueAnimator animator = ValueAnimator.ofFloat(mCurrentValue, percent);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (isPause) {
                        return;
                    }
                    mCurrentValue = (int) animation.getAnimatedValue();
                    invalidate();
                }
            });
            animator.setDuration(100);
            animator.start();
        } else {
            setProgress(progress);
        }
    }

    /**
     * 获取到当前进度
     *
     * @return
     */
    public float getCurrentValue() {
        return mCurrentValue;
    }

    /**
     * 暂停执行
     */
    public void pauseValue() {
        if (mCurrentValue == mMaxValue) {
            return;
        }

        isFirstInit = false;
        isDelete = false;
        isPause = true;
        isFinish = false;

        mAngleList.add(changePercentToAngle(mCurrentValue - mLastValue));
        mLastValue = mCurrentValue;

        //开启动画平滑过渡
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mPauseLocation = getWidth() / 2 - (mCircleWidth + mStokeWidth) * (float) animation.getAnimatedValue();
                mGradientColor = evaluateColor(mFirstColor, mPauseColor, (float) animation.getAnimatedValue());
                mInsideBitmapAlpha = (int) ((1 - (float) animation.getAnimatedValue()) * MAX_ALPHA);
                invalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isContinue = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (progressListener != null) {
                    progressListener.onPause();
                }
                mNeedMoveAngle = 0;
            }
        });
        animator.setDuration(mAnimTime);
        animator.start();
    }

    /**
     * 开始执行
     */
    public void startValue() {
        if (mCurrentValue == mMaxValue) {
            return;
        }

        isFirstInit = false;
        mNeedMoveAngle = 0;
        isDelete = false;
        isFinish = false;

        //开启动画平滑过渡
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mStartLocation = (float) mStokeWidth * (float) animation.getAnimatedValue() + (getWidth() / 2 - mCircleWidth - mStokeWidth + 1);
                mGradientColor = evaluateColor(mPauseColor, mFirstColor, (float) animation.getAnimatedValue());
                mInsideBitmapAlpha = (int) ((float) animation.getAnimatedValue() * MAX_ALPHA);
                invalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isContinue = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (progressListener != null) {
                    progressListener.onContinue();
                }
                isPause = false;
                mNeedMoveAngle = 0;
            }
        });
        animator.setDuration(mAnimTime);
        animator.start();
    }

    public void finish() {
        isPause = true;
        isContinue = false;

        //开启动画平滑过渡
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mPauseLocation = getWidth() / 2 - (mCircleWidth + mStokeWidth) * (float) animation.getAnimatedValue();
                mGradientColor = evaluateColor(mFirstColor, mPauseColor, (float) animation.getAnimatedValue());
                mInsideBitmapAlpha = (int) ((1 - (float) animation.getAnimatedValue()) * MAX_ALPHA);
                invalidate();
            }
        });
    }

    /**
     * 删除指定段落的轨迹
     */
    public void deleteLastValue(int index) {
        if (index < 0 || index >= mAngleList.size()) {
            return;
        }
        isDelete = true;
        mNeedMoveAngle = 0;
        mAngleList.remove(index);
        invalidate();
    }

    /**
     * 获取到轨迹的数量
     *
     * @return
     */
    public int getListSize() {
        return mAngleList.size();
    }

    /**
     * 获取到每段轨迹所占百分比
     */
    public float getPercent(int index) {
        return changeAngleToPercent(mAngleList.get(index));
    }

    /**
     * 将当前进度转为圆环角度
     *
     * @param percent
     * @return
     */
    public float changePercentToAngle(float percent) {
        return percent * 360 / mMaxValue * 1.0f;
    }

    /**
     * 将轨迹角度转换为百分比
     */
    public float changeAngleToPercent(float angle) {
        return angle * mMaxValue / 360.0f;
    }

    /**
     * 实现渐变色
     *
     * @param startValue 起始色
     * @param endValue   结束色
     * @param fraction   比例
     * @return
     */
    private int evaluateColor(int startValue, int endValue, float fraction) {
        if (fraction <= 0) {
            return startValue;
        }
        if (fraction >= 1) {
            return endValue;
        }
        int startInt = startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endInt = endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return ((startA + (int) (fraction * (endA - startA))) << 24) | ((startR + (int) (fraction * (endR - startR))) << 16) | ((startG + (int) (fraction * (endG - startG))) << 8) | ((startB + (int) (fraction * (endB - startB))));
    }

    public CustomProgressListener progressListener;

    public void setCustomProgressListener(CustomProgressListener listener) {
        this.progressListener = listener;
    }

    /**
     * 拍摄执行状态监听器
     */
    interface CustomProgressListener {
        void onPause();

        void onContinue();
    }
}
