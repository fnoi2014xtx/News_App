package com.java.xtxnews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class KeyWordGridView extends GridView {
    public KeyWordGridView(Context paramContext, AttributeSet paramAttributeSet){
        super(paramContext,paramAttributeSet);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
