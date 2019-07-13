package net.iGap.adapter.items.poll;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.github.mikephil.charting.data.BarEntry;

import net.iGap.R;
import net.iGap.adapter.items.poll.holder.BaseViewHolder;
import net.iGap.adapter.items.poll.holder.Type1ViewHolder;
import net.iGap.adapter.items.poll.holder.Type2ViewHolder;
import net.iGap.adapter.items.poll.holder.Type3ViewHolder;
import net.iGap.adapter.items.poll.holder.Type4ViewHolder;
import net.iGap.adapter.items.poll.holder.Type5ViewHolder;
import net.iGap.adapter.items.poll.holder.Type6ViewHolder;
import net.iGap.adapter.items.poll.holder.Type7ViewHolder;
import net.iGap.adapter.items.poll.holder.TypeChartViewHolder;
import net.iGap.adapter.items.poll.holder.TypeUnknownViewHolder;

import java.util.ArrayList;
import java.util.List;

public class PollAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<PollItem> pollList;
    private String[] labels;
    private ArrayList<BarEntry> barEntries;


    public PollAdapter(Context context, List<PollItem> pollList) {
        this.context = context;
        this.pollList = pollList;
        this.labels = null;
        this.barEntries = null;
    }

    public void setPollList(List<PollItem> pollList) {
        this.pollList = pollList;
        this.labels = null;
        this.barEntries = null;
    }

    public void addChatToEnd(String[] labels, ArrayList<BarEntry> barEntries) {
        this.labels = labels;
        this.barEntries = barEntries;
    }

    public void notifyChangeData() {
        this.notifyDataSetChanged();
        showChart();
    }

    private void showChart() {
        ArrayList<String> labels = new ArrayList<>();
        ArrayList<BarEntry> barValue = new ArrayList<>();
        boolean userPolledBefore = false;
        int i = 0;
        for (PollItem pollItem: getData()) {
            for (PollItemField pollItemField: pollItem.pollItemFields) {
                if (pollItemField.clicked) {
                    userPolledBefore = true;
                }
                if (pollItemField.clickable) {

                    labels.add(pollItemField.label);
                    barValue.add(new BarEntry(i, pollItemField.sum));
                    i++;
                }
            }
        }

        String[] labels2 = new String[labels.size()];
        labels2 = labels.toArray(labels2);

        if (userPolledBefore) {
            addChatToEnd(labels2, barValue);
        }

    }

    public List<PollItem> getData() {
        return this.pollList;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context.getApplicationContext());
        switch (i) {
            case -1:
                return new TypeChartViewHolder(this, layoutInflater.inflate(R.layout.item_poll_chart, viewGroup, false));
            case 1:
                return new Type1ViewHolder(this, layoutInflater.inflate(R.layout.item_poll_1, viewGroup, false));
            case 2:
                return new Type2ViewHolder(this, layoutInflater.inflate(R.layout.item_poll_2, viewGroup, false));
            case 3:
                return new Type3ViewHolder(this, layoutInflater.inflate(R.layout.item_poll_3, viewGroup, false));
            case 4:
                return new Type4ViewHolder(this, layoutInflater.inflate(R.layout.item_poll_4, viewGroup, false));
            case 5:
                return new Type5ViewHolder(this, layoutInflater.inflate(R.layout.item_poll_5, viewGroup, false));
            case 6:
                return new Type6ViewHolder(this, layoutInflater.inflate(R.layout.item_poll_6, viewGroup, false));
            case 7:
                return new Type7ViewHolder(this, layoutInflater.inflate(R.layout.item_poll_7, viewGroup, false));
        }
        return new TypeUnknownViewHolder(this, layoutInflater.inflate(R.layout.item_discovery_unknown, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (pollList.size() == i) {
            float height = Resources.getSystem().getDisplayMetrics().widthPixels * 1.0f * 2 / 3;
            viewHolder.itemView.getLayoutParams().height = Math.round(height);
            ((TypeChartViewHolder) viewHolder).bindView(labels, barEntries);
        } else {
            String[] scales = pollList.get(i).scale.split(":");
            float height = Resources.getSystem().getDisplayMetrics().widthPixels * 1.0f * Integer.parseInt(scales[1]) / Integer.parseInt(scales[0]);
            viewHolder.itemView.getLayoutParams().height = Math.round(height);
            ((BaseViewHolder) viewHolder).bindView(pollList.get(i));
        }
    }

    @Override
    public int getItemCount() {
        if (labels == null) {
            return pollList.size();
        }

        return pollList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (pollList.size() == position) {
            return -1;
        }

        return pollList.get(position).model + 1;
    }

}