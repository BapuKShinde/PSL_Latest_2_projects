package com.psllab.assetchainway.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;


import com.psllab.assetchainway.R;
import com.psllab.assetchainway.SearchActivity;
import com.psllab.assetchainway.databases.DatabaseHandler;
import com.psllab.assetchainway.modals.AssetMaster;

import java.util.ArrayList;
import java.util.List;

public class AsstPreSearchAdapter extends RecyclerView.Adapter<AsstPreSearchAdapter.AnimalsViewHolder> implements Filterable {

    private Context context;
    private List<AssetMaster> nameList;
    private List<AssetMaster> filteredNameList;
    private DatabaseHandler db;



    public AsstPreSearchAdapter(Context context, List<AssetMaster> nameList) {
        super();
        this.context = context;
        this.nameList = nameList;
        this.filteredNameList = nameList;
        db = new DatabaseHandler(context);
    }

    @NonNull
    @Override
    public AnimalsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.pre_search_adapter_layout, viewGroup, false);
        return new AnimalsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AnimalsViewHolder holder, final int position) {
        holder.tvName.setText(filteredNameList.get(position).getName());

        // holder.tvName.setText(filteredNameList.get(position).getName());

        holder.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AssetMaster cm = filteredNameList.get(position);

                Intent searchIntent = new Intent(context, SearchActivity.class);
                searchIntent.putExtra("epc",cm.getTagID());
                searchIntent.putExtra("searchname",cm.getName());
                context.startActivity(searchIntent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return filteredNameList.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charSequenceString = constraint.toString();
                if (charSequenceString.isEmpty()) {
                    filteredNameList = nameList;
                } else {
                    List<AssetMaster> filteredList = new ArrayList<>();
                    for (AssetMaster name : nameList) {
                        if (name.getName().toLowerCase().contains(charSequenceString.toLowerCase())) {
                            filteredList.add(name);
                        }
                        filteredNameList = filteredList;
                    }

                }
                FilterResults results = new FilterResults();
                results.values = filteredNameList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredNameList = (List<AssetMaster>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    class AnimalsViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView tvName;
        private CheckBox chkBox;

        AnimalsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
           // chkBox = itemView.findViewById(R.id.chkBox);
        }
    }

}