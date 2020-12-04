package com.zebra.pslsdksample.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zebra.pslsdksample.R;
import com.zebra.pslsdksample.modals.SourcePlant;


import java.util.List;

public class SourcePlantAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<SourcePlant> lst;

    public SourcePlantAdapter(Context context, List<SourcePlant> lst) {
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
        SourcePlantAdapter.MyViewHolder mViewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.transaction_types_adapter_layout, parent, false);
            mViewHolder = new SourcePlantAdapter.MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (SourcePlantAdapter.MyViewHolder) convertView.getTag();
        }
        //  SFModal crm = lst.get(position);//change by bapu 5-sep-2018 to desc order
        final SourcePlant crm = lst.get(position);
        mViewHolder.textName.setText(crm.getSp_main_plant());
        //TODO Onlist item delete listener
        // NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // notificationManager.cancel(crm.getEvent_id(),0);


        return convertView;
    }

    private class MyViewHolder {
        TextView textName;
        public MyViewHolder(View item) {
            textName = (TextView) item.findViewById(R.id.textName);
        }
    }
}
