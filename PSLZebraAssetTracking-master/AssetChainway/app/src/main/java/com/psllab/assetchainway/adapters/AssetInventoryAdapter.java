package com.psllab.assetchainway.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.psllab.assetchainway.R;
import com.psllab.assetchainway.modals.AssetMaster;

import java.util.List;

public class AssetInventoryAdapter extends RecyclerView.Adapter<AssetInventoryAdapter.ViewHolder> {

    private Context context;
    private List<AssetMaster> personUtils;

    public AssetInventoryAdapter(Context context, List<AssetMaster> personUtils) {
        this.context = context;
        this.personUtils = personUtils;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.asset_inventory_adapter, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setTag(personUtils.get(position));

        AssetMaster pu = personUtils.get(position);

        int pos = position+1;

        holder.textAssetSRNO.setText(String.valueOf(pos));
        holder.textAssetID.setText(pu.getName());
        holder.textAssetType.setText(pu.getCategoryName());



    }

    @Override
    public int getItemCount() {
        return personUtils.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textAssetSRNO,textAssetID,textAssetType;


        public ViewHolder(View itemView) {
            super(itemView);

            textAssetSRNO = (TextView) itemView.findViewById(R.id.textSRNO);
            textAssetID = (TextView) itemView.findViewById(R.id.textAssetID);
            textAssetType = (TextView) itemView.findViewById(R.id.textAssetType);



        }
    }

}
