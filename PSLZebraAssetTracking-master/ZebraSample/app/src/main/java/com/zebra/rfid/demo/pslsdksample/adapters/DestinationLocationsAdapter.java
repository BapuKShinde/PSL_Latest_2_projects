package com.zebra.rfid.demo.pslsdksample.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.zebra.rfid.demo.pslsdksample.R;
import com.zebra.rfid.demo.pslsdksample.modals.DestinationLocations;

import java.util.List;

public class DestinationLocationsAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<DestinationLocations> lst;

    public DestinationLocationsAdapter(Context context, List<DestinationLocations> lst) {
        this.context = context;
        this.lst = lst;
        inflater = LayoutInflater.from(this.context);
    }
    @Override
    public int getCount() {
        return lst.size();
    }

    @Override
    public Object getItem(int position) {
        return lst.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        DestinationLocationsAdapter.MyViewHolder mViewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.transaction_types_adapter_layout, parent, false);
            mViewHolder = new DestinationLocationsAdapter.MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (DestinationLocationsAdapter.MyViewHolder) convertView.getTag();
        }
        //  SFModal crm = lst.get(position);//change by bapu 5-sep-2018 to desc order
        final DestinationLocations crm = lst.get(position);
        mViewHolder.textName.setText(crm.getLocation_name());



        return convertView;
    }

    private class MyViewHolder {
        TextView textName;
        public MyViewHolder(View item) {
            textName = (TextView) item.findViewById(R.id.textName);
        }
    }
}