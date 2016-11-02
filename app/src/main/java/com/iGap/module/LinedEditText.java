package com.iGap.module;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.EditText;
import com.iGap.G;
import com.iGap.R;

public class LinedEditText extends EditText {

    // the vertical offset scaling factor (10% of the height of the text)
    private static final float VERTICAL_OFFSET_SCALING_FACTOR = 0.1f;
    int initialCount = 0;
    // the paint we will use to draw the lines
    private Paint dashedLinePaint;
    // a reusable rect object
    private Rect reuseableRect;

    public LinedEditText(Context context) {
        super(context);
        init();
        setMaxLines(4);
        setLines(4);
    }

    public LinedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        setMaxLines(4);
        setLines(4);
    }

    public LinedEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        setMaxLines(4);
        setLines(4);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN) private void init() {

        // instantiate the rect
        reuseableRect = new Rect();

        // instantiate the paint
        dashedLinePaint = new Paint();
        dashedLinePaint.setColor(G.context.getResources().getColor(R.color.line_edit_text));
        dashedLinePaint.setStyle(Paint.Style.STROKE);
        initialCount = getMinLines();
        setLines(initialCount);
    }

    @Override protected void onDraw(Canvas canvas) {

        int height = getHeight();
        int lineHeight = getLineHeight() - 1;
        int verticalOffset = (int) (lineHeight * VERTICAL_OFFSET_SCALING_FACTOR);
        int numberOfLines = height / lineHeight * 2;
        if (getLineCount() > numberOfLines) {

            numberOfLines = getLineCount();
        }
        int baseline = getLineBounds(0, reuseableRect);

        // for each line
        for (int i = 0; i < 4; i++) {

            // draw the line
            canvas.drawLine(reuseableRect.left,             // left
                baseline + verticalOffset,      // top
                reuseableRect.right,            // right
                baseline + verticalOffset,      // bottom
                dashedLinePaint);               // paint instance

            // get the baseline for the next line
            baseline += lineHeight;
        }

        super.onDraw(canvas);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);

        if (focused) {
            dashedLinePaint.setColor(G.context.getResources().getColor(R.color.toolbar_background));
        } else {
            dashedLinePaint.setColor(G.context.getResources().getColor(R.color.line_edit_text));
        }
    }
}
