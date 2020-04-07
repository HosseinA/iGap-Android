package net.iGap.adapter.items.cells;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.module.Theme;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemberCell extends FrameLayout {
    private TextView memberNameView;
    private TextView memberStatusView;
    private CircleImageView avatarImage;

    private boolean isRtl = G.isAppRtl;

    public MemberCell(@NonNull Context context) {
        super(context);

        avatarImage = new CircleImageView(getContext());
        avatarImage.setImageResource(R.drawable.ic_cloud_space_blue);
        addView(avatarImage, LayoutCreator.createFrame(45, 45, isRtl ? Gravity.RIGHT : Gravity.LEFT, isRtl ? 0 : 16, 8, isRtl ? 16 : 0, 8));

        memberNameView = new TextView(getContext());
        memberNameView.setTextColor(Theme.getInstance().getTitleTextColor(getContext()));
        memberNameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        memberNameView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.main_font));
        memberNameView.setLines(1);
        memberNameView.setMaxLines(1);
        memberNameView.setSingleLine(true);
        memberNameView.setEllipsize(TextUtils.TruncateAt.END);
        memberNameView.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);
        memberNameView.setText("Abolfazl Abbasi");
        addView(memberNameView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, isRtl ? Gravity.RIGHT : Gravity.LEFT, isRtl ? 0 : 77, 12, isRtl ? 77 : 0, 0));

        memberStatusView = new TextView(getContext());
        memberStatusView.setTextColor(Theme.getInstance().getSubTitleColor(getContext()));
        memberStatusView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        memberStatusView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.main_font));
        memberStatusView.setLines(1);
        memberStatusView.setMaxLines(1);
        memberStatusView.setSingleLine(true);
        memberStatusView.setEllipsize(TextUtils.TruncateAt.END);
        memberStatusView.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);
        memberStatusView.setText("Last Seen Recently");
        addView(memberStatusView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, isRtl ? Gravity.RIGHT : Gravity.LEFT, isRtl ? 0 : 77, 32, isRtl ? 77 : 0, 0));
    }
}
