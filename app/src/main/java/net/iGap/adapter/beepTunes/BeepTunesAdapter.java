package net.iGap.adapter.beepTunes;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.module.api.beepTunes.Album;
import net.iGap.module.api.beepTunes.Datum;

import java.util.ArrayList;
import java.util.List;

public class BeepTunesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Datum> data = new ArrayList<>();
    private static final int ROW = 0;
    private static final int AD = 1;

    private static final String TYPE_AD = "advertisement";
    private static final String TYPE_ROW = "beepTunesCategory";

    public void setData(List<Datum> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder viewHolder;
        switch (i) {
            case ROW:
                View rowViewHolder = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_beeptunes_row, viewGroup, false);
                viewHolder = new RowViewHolder(rowViewHolder);
                break;
            case AD:
                View slideViewHolder = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_beeptunes_slide, viewGroup, false);
                viewHolder = new SlideViewHolder(slideViewHolder);
                break;
            default:
                View defaultViewHolder = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_beeptunes_row, viewGroup, false);
                viewHolder = new RowViewHolder(defaultViewHolder);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        int viewType = viewHolder.getItemViewType();
        switch (viewType) {
            case ROW:
                RowViewHolder holder = (RowViewHolder) viewHolder;
                holder.bindRow(data.get(i).getAlbums());
                holder.headerTv.setText(data.get(i).getInfo().getTitle());
                break;
            case AD:

                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position).getType().equals(TYPE_ROW))
            return ROW;
        else if (data.get(position).getType().equals(TYPE_AD))
            return AD;
        else return 2;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class SlideViewHolder extends RecyclerView.ViewHolder {

        public SlideViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    class RowViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView recyclerView;
        private TextView headerTv;
        private ItemAdapter adapter;

        public RowViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.rv_rowItem);
            headerTv = itemView.findViewById(R.id.tv_rowItem_header);
            recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            adapter = new ItemAdapter();
            recyclerView.setAdapter(adapter);
        }

        void bindRow(List<Album> albums) {
            adapter.setAlbums(albums);
        }
    }
}
