package com.zebra.rfid.demo.pslsdksample.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zebra.rfid.demo.pslsdksample.R;
import com.zebra.rfid.demo.pslsdksample.modals.TransactionTypes;

import java.util.List;

public class TransactionTypesAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<TransactionTypes> lst;

    public TransactionTypesAdapter(Context context, List<TransactionTypes> lst) {
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
        TransactionTypesAdapter.MyViewHolder mViewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.transaction_types_adapter_layout, parent, false);
            mViewHolder = new TransactionTypesAdapter.MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (TransactionTypesAdapter.MyViewHolder) convertView.getTag();
        }
        //  SFModal crm = lst.get(position);//change by bapu 5-sep-2018 to desc order
        final TransactionTypes crm = lst.get(position);
        mViewHolder.textName.setText(crm.getTransaction_description());
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