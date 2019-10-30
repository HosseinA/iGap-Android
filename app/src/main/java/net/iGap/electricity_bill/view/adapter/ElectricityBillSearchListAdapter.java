package net.iGap.electricity_bill.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.electricity_bill.repository.model.BranchData;
import net.iGap.helper.HelperCalander;

import java.util.ArrayList;
import java.util.List;

public class ElectricityBillSearchListAdapter extends RecyclerView.Adapter<ElectricityBillSearchListAdapter.ViewHolder> {

    private List<BranchData> mData;
    private Context context;
    private OnItemClickListener clickListener;

    public ElectricityBillSearchListAdapter(Context context, List<BranchData> data, OnItemClickListener clickListener) {
        this.mData = new ArrayList<>(data);
        this.context = context;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_elec_search_list_cell, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.initView(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView billID, customerName, customerAddress;
        private CardView container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            customerName = itemView.findViewById(R.id.billCustomerName);
            billID = itemView.findViewById(R.id.billID);
            customerAddress = itemView.findViewById(R.id.billCustomerAddress);
            container = itemView.findViewById(R.id.cardHolder);

        }

        void initView(int position) {

            if (HelperCalander.isPersianUnicode) {
                billID.setText(HelperCalander.convertToUnicodeFarsiNumber(mData.get(position).getBillID()));
            }
            else
                billID.setText(mData.get(position).getBillID());
            customerName.setText((mData.get(position).getCustomerName()==null? "":mData.get(position).getCustomerName())
                    + " " + (mData.get(position).getCustomerFamily()==null? "" : mData.get(position).getCustomerFamily()));
            customerAddress.setText(mData.get(position).getServiceAddress());
            container.setOnClickListener(v -> clickListener.onClick(position));

        }
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

}