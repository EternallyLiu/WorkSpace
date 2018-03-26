package com.example.administrator.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by Administrator on 2018/3/20.
 */

public class CustomView extends View {

    private int mColor;
    private int mSize;
    private String mText;
    private Paint mPaint;
    private Rect mRect;

    public CustomView(Context context) {
        this(context, null);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取到自定义属性
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.custom_TitleView, defStyleAttr
                , 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.custom_TitleView_titleColor:
                    mColor = a.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.custom_TitleView_titleSize:
                    mSize = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18,
                            getResources().getDisplayMetrics()));
                    break;
                case R.styleable.custom_TitleView_titleText:
                    mText = a.getString(attr);
                    break;
            }
        }
        a.recycle();

        mPaint = new Paint();

        mRect = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            mPaint.setTextSize(mSize);
            mPaint.getTextBounds(mText,0,mText.length(),mRect);
            float textWidth = mRect.width();
            int desired = (int) (getPaddingLeft() + textWidth + getPaddingRight());
            width = desired;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            mPaint.setTextSize(mSize);
            mPaint.getTextBounds(mText,0,mText.length(),mRect);
            float textHeight = mRect.height();
            int desired = (int) (getPaddingTop() + textHeight + getPaddingBottom());
            height = desired;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.BLUE);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);

        mPaint.setColor(mColor);
        canvas.drawText(mText, getWidth() / 2 - mRect.width() / 2, getHeight() / 2 + mRect.height() / 2,
                mPaint);
    }
}
