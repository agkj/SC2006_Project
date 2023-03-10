package com.example.sc2006_project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.internal.AccountType;

import java.util.ArrayList;

public class CarparkLotRecViewAdapter extends RecyclerView.Adapter<CarparkLotRecViewAdapter.ViewHolder>{
    private Context context;
    private ArrayList<CarparkLot> lots = new ArrayList<>();
    private Activity mactivity;
    public static final String GET_LOT_COORDS = "com.example.carparkmapinterface.GET_LOT_COORDS";

    public CarparkLotRecViewAdapter(Context context, Activity mactivity){
        this.context = context;
        this.mactivity = mactivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carpark_lot_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.text.setText(lots.get(position).getName());
        holder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendcoord = new Intent();
                sendcoord.putExtra(GET_LOT_COORDS, lots.get(holder.getAdapterPosition()).getPosition());
                mactivity.setResult(1,sendcoord);
                mactivity.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return lots.size();
    }

    public void setLots(ArrayList<CarparkLot> intake){
        lots = intake;
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView text;
        private RelativeLayout parent;

        public ViewHolder(View itemView){
            super(itemView);
            text = itemView.findViewById(R.id.text);
            parent = itemView.findViewById(R.id.parent);
        }
    }
}

