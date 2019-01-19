package net.iGap.module;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import net.iGap.G;
import net.iGap.R;
import net.iGap.module.additionalData.ButtonEntity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import static android.view.Gravity.CENTER;
import static android.widget.LinearLayout.HORIZONTAL;
import static net.iGap.adapter.items.chat.ViewMaker.i_Dp;

public class MakeButtons {
    private static LinearLayout childLayout;
    private static HashMap<Integer, JSONArray> buttonList;
    private static Gson gson;


    public static HashMap<Integer, JSONArray> parseData(String json) {

        // childLayout = createLayout();
        // parsedList = parsJson(mMessage.additionalData.additionalData);

        gson = new GsonBuilder().create();

        ArrayList<ButtonEntity> jsonList = new ArrayList<>();

        buttonList = new HashMap<>();

        try {
            //    JSONObject jObject = new JSONObject(mJson);
            JSONArray jsonElements = new JSONArray(json);
            //   JSONArray jsonElements = new JSONArray(mJson);

            //   rows = jsonElements.length();
            for (int i = 0; i < jsonElements.length(); i++) {
                // jsonElements.get(0);
                buttonList.put(i, jsonElements.getJSONArray(i));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return buttonList;
    }

    public static LinearLayout createLayout() {
        LinearLayout linearLayout_179 = new LinearLayout(G.context);
        linearLayout_179.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams layout_937 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout_937.topMargin = 4;
        linearLayout_179.setLayoutParams(layout_937);
        return linearLayout_179;
    }

    public static LinearLayout addButtons(String jsonObject, View.OnClickListener clickListener, int culmn, float wightSum, String lable, String btnName, String imageUrl, int btnId, String value, LinearLayout mainLayout, Integer actionType, Integer additionalType) {
        float weight = wightSum / culmn;
        float weightSum = 0;
        float textWeight = 0f;
        float imageWeight = 0f;
        if (culmn == 1) {
            if (!imageUrl.equals("")) {
                weightSum = 5f;
                textWeight = 3.5f;
                imageWeight = 1.5f;
            } else {
                weightSum = 1f;
                textWeight = 1f;
            }
        } else if (culmn == 2) {
            if (!imageUrl.equals("")) {
                weightSum = .5f;
                textWeight = .33f;
                imageWeight = .16f;
            } else {
                weightSum = .5f;
                textWeight = .5f;
            }
        } else if (culmn == 3) {
            if (!imageUrl.equals("")) {
                weightSum = 3f;
                textWeight = 2f;
                imageWeight = 1f;
            } else {
                weightSum = 3f;
                textWeight = 3f;
            }
        }
        CardView card = new CardView(G.context);

        // Set the CardView layoutParams
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp2), i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp2));
        params.weight = weight;
        card.setLayoutParams(params);

        // Set CardView corner radius
        card.setRadius(16);

        card.setCardElevation(2);

        // Set cardView content padding
        //card.setContentPadding(15, 15, 15, 15);

        // Set a background color for CardView

        if (additionalType == 1) {
            card.setCardBackgroundColor(Color.parseColor("#20000000"));
        }

        if (Build.VERSION.SDK_INT < 21) {
            card.setCardBackgroundColor(Color.parseColor("#cfd8dc"));
        }


        card.setForeground(getSelectedItemDrawable());
        card.setClickable(true);
        // card.setCardElevation(3);


        LinearLayout linearLayout_529 = new LinearLayout(G.context);
        LinearLayout.LayoutParams layout_941 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, i_Dp(R.dimen.dp36));

        layout_941.gravity = Gravity.CENTER_VERTICAL;
        layout_941.setMargins(0, i_Dp(R.dimen.dp4), 0, i_Dp(R.dimen.dp4));
        linearLayout_529.setLayoutParams(layout_941);
        linearLayout_529.setWeightSum(weightSum);

        ImageView img1 = new ImageView(G.context);

        /*img1.setId(1);
        img1.setTag("abc");*/
        if (!imageUrl.equals("")) {
            Picasso.get()
                    .load(imageUrl)
                    .resize(i_Dp(R.dimen.dp32), i_Dp(R.dimen.dp32))

                    .into(img1);
            // img1.setImageResource(R.drawable.icons8_potted_plant_50);
            LinearLayout.LayoutParams layout_738 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);

            layout_738.weight = imageWeight;
            layout_738.setMargins(0, i_Dp(R.dimen.dp2), i_Dp(R.dimen.dp10), i_Dp(R.dimen.dp2));
            layout_738.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;


            img1.setLayoutParams(layout_738);
            linearLayout_529.addView(img1);
        }

        if (lable.trim() != null) {
            TextView btn1 = new TextView(G.context);

            // btn1.setId(R.id.btn1);
            btn1.setEllipsize(TextUtils.TruncateAt.END);
            btn1.setGravity(CENTER);
            btn1.setMaxLines(1);
            btn1.setTypeface(G.typeface_IRANSansMobile);
            btn1.setText(btnName);
            btn1.setTextSize(16);

            LinearLayout.LayoutParams layout_844 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);

            layout_844.weight = textWeight;
            btn1.setLayoutParams(layout_844);

            linearLayout_529.addView(btn1);
        }
        card.addView(linearLayout_529);
        ArrayList<String> actions = new ArrayList<>();
        actions.add(value);
        actions.add(lable);
        actions.add(jsonObject);
        card.setTag(actions);


        card.setId(actionType);

        card.setOnClickListener(clickListener);
        mainLayout.addView(card);
        return mainLayout;


    }

    public static Drawable getSelectedItemDrawable() {
        int[] attrs = new int[]{R.attr.selectableItemBackground};
        TypedArray ta = G.context.getApplicationContext().obtainStyledAttributes(attrs);
        Drawable selectedItemDrawable = ta.getDrawable(0);
        ta.recycle();
        return selectedItemDrawable;
    }
}
