package net.iGap.adapter.items.popular;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.ImageLoadingService;
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.model.PopularChannel.Category;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterCategoryItem extends RecyclerView.Adapter<AdapterCategoryItem.FragmentGridViewHolder> {
    private List<Category> categoryList;
    private Context context;
    private OnClickedItemEventCallBack onClickedItemEventCallBack;
    public boolean clickable;

    public AdapterCategoryItem(Context context, boolean clickable, List<Category> categoryList) {
        this.context = context;
        this.clickable = clickable;
        this.categoryList = categoryList;

    }


    @NonNull
    @Override
    public FragmentGridViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_popular_channel_category, viewGroup, false);
        return new FragmentGridViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull FragmentGridViewHolder holder, int i) {
        holder.bindChannel(categoryList.get(i));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class FragmentGridViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView channelImageGrid;
        private TextView channelTitleGrid;
        private CardView root;
        private LinearLayout linearLayout;

        public FragmentGridViewHolder(@NonNull View itemView) {
            super(itemView);
            channelImageGrid = itemView.findViewById(R.id.circle_item_popular_rv_grid);
            channelTitleGrid = itemView.findViewById(R.id.tv_item_popular_rv_grid);

            linearLayout = itemView.findViewById(R.id.ll_item_pop_card_category);
            if (G.isDarkTheme) {
                linearLayout.setBackgroundResource(R.drawable.shape_popular_channel_dark_item_them);
            }
            root = itemView.findViewById(R.id.card_item_pop_category);
            Utils.setCardsBackground(root, R.color.white, R.color.gray_6c);
        }

        public void bindChannel(Category category) {
            ImageLoadingService.load(category.getIcon(), channelImageGrid);
            channelTitleGrid.setText(category.getTitle());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickable)
                        onClickedItemEventCallBack.onClickedItem(category);
                }
            });
        }
    }

    public void setOnClickedItemEventCallBack(OnClickedItemEventCallBack onClickedItemEventCallBack) {
        this.onClickedItemEventCallBack = onClickedItemEventCallBack;
    }

    public interface OnClickedItemEventCallBack {
        void onClickedItem(Category category);
    }
}
