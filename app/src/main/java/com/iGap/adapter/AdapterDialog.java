package com.iGap.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RadioButton;

import com.iGap.R;
import com.iGap.activitys.ActivityRegister;
import com.iGap.module.StructCountry;

import java.util.ArrayList;

public class AdapterDialog extends BaseAdapter implements Filterable {

    Context context;
    ArrayList<StructCountry> countrylist;
    ArrayList<StructCountry> mStringFilterList;
    ValueFilter valueFilter;
    public static int mSelectedVariation = -1;
    public static boolean po = false;

    private RadioButton name_tv;

    public AdapterDialog(Context context, ArrayList<StructCountry> countrylist) {
        this.context = context;
        this.countrylist = countrylist;
        mStringFilterList = countrylist;
    }

    @Override
    public int getCount() {
        return countrylist.size();
    }

    @Override
    public Object getItem(int position) {
        return countrylist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return countrylist.indexOf(getItem(position));
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        convertView = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.rg_adapter_dialog, null);
            name_tv = (RadioButton) convertView.findViewById(R.id.rg_radioButton);
            StructCountry structCountry = countrylist.get(position);
            name_tv.setText(structCountry.getName());
        }


        name_tv.setChecked(countrylist.get(position).getId() == mSelectedVariation);
        name_tv.setTag(position);
        name_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSelectedVariation = (Integer) v.getTag();

                ActivityRegister.positionRadioButton = countrylist.get(position).getId();
                mSelectedVariation = countrylist.get(position).getId();
                notifyDataSetChanged();

//                ActivityRegister.dialogChooseCountry.dismiss();

                ActivityRegister.edtCodeNumber.setText(("+ " + countrylist.get(position).getCountryCode()));
                if (countrylist.get(position).getPhonePattetn() != null) {
                    if (countrylist.get(position).getPhonePattetn().equals(" ")) {
                        ActivityRegister.edtPhoneNumber.setMask("###-###-####");

                    } else {

                        ActivityRegister.edtPhoneNumber.setMask((countrylist.get(position).getPhonePattetn().replace("X", "#").replace(" ", "-")));
                    }

                } else {
                    ActivityRegister.edtPhoneNumber.setMask("###-###-####");
                }
                ActivityRegister.btnChoseCountry.setText((countrylist.get(position).getName()));
                ActivityRegister.isoCode = countrylist.get(position).getAbbreviation();
            }
        });

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<StructCountry> filterList = new ArrayList<StructCountry>();
                for (int i = 0; i < mStringFilterList.size(); i++) {
                    if ((mStringFilterList.get(i).getName().toUpperCase())
                            .contains(constraint.toString().toUpperCase())) {

                        StructCountry structCountry = new StructCountry();
                        structCountry.setId(mStringFilterList.get(i).getId());
                        structCountry.setName(mStringFilterList.get(i).getName());
                        structCountry.setCountryCode(mStringFilterList.get(i).getCountryCode());
                        structCountry.setPhonePattetn(mStringFilterList.get(i).getPhonePattetn());
                        structCountry.setAbbreviation(mStringFilterList.get(i).getAbbreviation());
                        filterList.add(structCountry);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;

            } else {
                results.count = mStringFilterList.size();
                results.values = mStringFilterList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            countrylist = (ArrayList<StructCountry>) results.values;
            notifyDataSetChanged();
        }

    }

}
