package net.iGap.module;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ToggleButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.R;
import net.iGap.Theme;

public class CustomToggleButton extends ToggleButton {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomToggleButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public CustomToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CustomToggleButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomToggleButton(Context context) {
        this(context, null);
    }

    private void init() {
        setTextOff("");
        setTextOn("");
        /*if (Theme.isUnderLollipop()) {
            setButtonDrawable(R.drawable.st_switch_button_dark);
        } else {*/

        ContextThemeWrapper wrapper = new ContextThemeWrapper(getContext(), new Theme().getTheme(getContext()));
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.st_switch_button, wrapper.getTheme());
        setButtonDrawable(drawable);
        /*}*/
    }

}
