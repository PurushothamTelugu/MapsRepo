package com.nic.ts.tracking.localdbmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.ts.tracking.R;
import com.nic.ts.tracking.modalobjects.LocationObject;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    ArrayList<LocationObject> dataHolder;

    public MyAdapter(ArrayList<LocationObject> dataHolder) {
        this.dataHolder = dataHolder;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout,parent,false);
       return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.dlatitude.setText(dataHolder.get(position).getLatitude());
        holder.dlongitude.setText(dataHolder.get(position).getLongitude());
        holder.daddress.setText(dataHolder.get(position).getAddress());
        holder.ddatetime.setText(dataHolder.get(position).getDateTime());

    }

    @Override
    public int getItemCount() {
        return dataHolder.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView dlatitude,dlongitude,daddress,ddatetime;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            dlatitude = itemView.findViewById(R.id.latitude_txt);
            dlongitude = itemView.findViewById(R.id.longitude_txt);
            daddress = itemView.findViewById(R.id.address_txt);
            ddatetime = itemView.findViewById(R.id.datetime_txt);

        }
    }

}