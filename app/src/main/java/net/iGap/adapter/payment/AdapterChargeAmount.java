package net.iGap.adapter.payment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.model.news.NewsList;

import java.util.ArrayList;
import java.util.List;

public class AdapterChargeAmount extends RecyclerView.Adapter<AdapterChargeAmount.ChargeAmountViewHolder> {

    private int selectedPosition;
    private List<Amount> amountList;

    public AdapterChargeAmount(List<Amount> amountList) {
        this.amountList = amountList;
    }

    @NonNull
    @Override
    public ChargeAmountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_charge_type, parent, false);
        return new ChargeAmountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChargeAmountViewHolder holder, int position) {
        holder.bindAmount(amountList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return amountList.size();
    }

    public class ChargeAmountViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private RadioButton radioButton;

        public ChargeAmountViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.amount);
            radioButton = itemView.findViewById(R.id.radio_amount);

            radioButton.setOnClickListener(v -> itemView.performClick());

            itemView.setOnClickListener(v -> {
                selectedPosition = getAdapterPosition();
                notifyDataSetChanged();

            });
        }

        public void bindAmount(Amount amount, int position) {
            if (selectedPosition == position) {
                radioButton.setChecked(true);
            } else {
                radioButton.setChecked(false);
            }

            textView.setText(amount.getTextAmount());
        }
    }


    public int getSelectedPosition() {
        return selectedPosition;
    }
}
