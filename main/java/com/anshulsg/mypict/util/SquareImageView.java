package com.anshulsg.mypict.util;

import android.content.Context;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class SquareImageView extends AppCompatImageButton{
    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        ViewGroup.LayoutParams params = this.getLayoutParams();
        params.height = width;
        this.setLayoutParams(params);
        this.setMeasuredDimension(width, width);
        super.onLayout(changed, l, t, r, t + width);
    }
}
