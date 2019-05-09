package net.iGap.libs.bottomNavigation;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.LinearLayout;

import net.iGap.R;
import net.iGap.libs.bottomNavigation.Event.OnItemChangeListener;
import net.iGap.libs.bottomNavigation.Event.OnItemSelected;
import net.iGap.libs.bottomNavigation.Util.Utils;

import java.util.ArrayList;
import java.util.List;

public class BottomNavigation extends LinearLayout implements OnItemSelected {

    public static final String TAG = "BottomNavigation";

    private OnItemChangeListener onItemChangeListener;
    private List<TabItem> tabItems = new ArrayList<>();

    private int defaultItem;
    private int selectedItemPosition = defaultItem;
    private float cornerRadius;
    private int backgroundColor;


    public BottomNavigation(Context context) {
        super(context);
        init(null);
    }

    public BottomNavigation(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BottomNavigation(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
        setElevation(2f);
    }


    private void init(@Nullable AttributeSet attributeSet) {
        parseAttr(attributeSet);
        setupViews();
    }

    private void setupViews() {
        setMinimumHeight(Utils.dpToPx(56));
        setOrientation(HORIZONTAL);
        setWeightSum(5);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setupChildren();
    }

    private void setupChildren() {
        if (getChildCount() > 0) {
            for (int i = 0; i < getChildCount(); i++) {
                if (!(getChildAt(i) instanceof TabItem)) {
                    throw new RuntimeException(TAG + "only accept tab item as child.");
                } else {
                    final TabItem tabItem = (TabItem) getChildAt(i);
                    tabItem.setPosition(i);
                    tabItems.add(tabItem);
                    tabItem.setOnItemSelected(this);
                }
            }
        } else {
            throw new RuntimeException(TAG + " can't be empty!");
        }
    }

    private void parseAttr(AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.BottomNavigation);

            try {
                backgroundColor = typedArray.getColor(R.styleable.BottomNavigation_background_color, getResources().getColor(R.color.background_color));
                defaultItem = typedArray.getInt(R.styleable.BottomNavigation_default_item, 0);
                cornerRadius = typedArray.getInt(R.styleable.BottomNavigation_corner_radius, 0);
            } finally {
                typedArray.recycle();
            }
        }
    }

    public int getDefaultItem() {
        return defaultItem;
    }

    @Override
    public void selectedItem(final int position) {
        if (position != selectedItemPosition) {
            selectedItemPosition = position;
            onSelectedItemChanged();
            if (onItemChangeListener != null) {
                onItemChangeListener.onSelectedItemChanged(tabItems.get(position).getPosition());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void dispatchDraw(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(backgroundColor);
        canvas.drawPath(roundedRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius, true), paint);
        super.dispatchDraw(canvas);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private Path roundedRect(float left, float top, float right, float bottom, float rx, float ry, boolean justTop) {
        Path path = new Path();
        if (rx < 0) rx = 0;
        if (ry < 0) ry = 0;
        float width = right - left;
        float height = bottom - top;
        if (rx > width / 2) rx = width / 2;
        if (ry > height / 2) ry = height / 2;
        float widthMinusCorners = (width - (2 * rx));
        float heightMinusCorners = (height - (2 * ry));
        path.moveTo(right, top + ry);

        path.rQuadTo(0, -ry, -rx, -ry);
        path.rLineTo(-widthMinusCorners, 0);

        path.rQuadTo(-rx, 0, -rx, ry);
        path.rLineTo(0, heightMinusCorners);

        if (justTop) {
            path.rLineTo(0, ry);
            path.rLineTo(width, 0);
            path.rLineTo(0, -ry);
        } else {
            path.rQuadTo(0, ry, rx, ry);
            path.rLineTo(widthMinusCorners, 0);
            path.rQuadTo(rx, 0, rx, -ry);
        }

        path.rLineTo(0, -heightMinusCorners);
        path.close();

        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setConvexPath(path);
            }
        });

        return path;
    }


    public void setCurrentItem(int position) {
        for (int i = 0; i < tabItems.size(); i++) {
            if (tabItems.get(i).getPosition() == position) {
                if (position != selectedItemPosition) {
                    selectedItemPosition = position;
                    onSelectedItemChanged();
                    if (onItemChangeListener != null) {
                        onItemChangeListener.onSelectedItemChanged(tabItems.get(position).getPosition());
                    }
                }
            }
        }
    }

    private void onSelectedItemChanged() {
        for (int i = 0; i < tabItems.size(); i++) {
            if (tabItems.get(i).getPosition() == selectedItemPosition) {
                tabItems.get(i).setSelected(true);
            } else {
                tabItems.get(i).setSelected(false);
            }
        }
    }

    public void setOnItemChangeListener(OnItemChangeListener onItemChangeListener) {
        this.onItemChangeListener = onItemChangeListener;
        onItemChangeListener.onSelectedItemChanged(tabItems.get(defaultItem).getPosition());
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}
