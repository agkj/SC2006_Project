package com.example.sc2006_project;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CarparkRecViewAdapter extends RecyclerView.Adapter<CarparkRecViewAdapter.ViewHolder>{
    private ArrayList<Carpark> carparks = new ArrayList<>();
    private Context context;
    public static final String COORDS = "com.example.application.carparkmapinterface.COORDS";
    public static final String ASSET_NAMES = "com.example.application.carparkmapinterface.ASSET_NAMES";
    public static final String LEVELS = "com.example.application.carparkmapinterface.LEVELS";
    public static final String BOUND = "com.example.application.carparkmapinterface.BOUND";


    public CarparkRecViewAdapter(Context context){
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carpark_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int cur_pos = position;
        holder.txtName.setText(carparks.get(cur_pos).getLocation_name());
        holder.txtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendInfo(carparks.get(cur_pos).getLocation_coord(),
                        carparks.get(cur_pos).getAsset_bound(),
                        carparks.get(cur_pos).getAsset_list(),
                        carparks.get(cur_pos).getLevel_list());
            }
        });
    }

    @Override
    public int getItemCount() {
        return carparks.size();
    }

    public void setCarparks(ArrayList<Carpark> carparks) {
        this.carparks = carparks;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView txtName;
        private RelativeLayout parent;
        public ViewHolder(View itemView){
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            parent = itemView.findViewById(R.id.parent);
        }
    }

    public void sendInfo(LatLng position, LatLngBounds bounds, ArrayList<String> asset_names, ArrayList<String> levels){
        Intent intent = new Intent(context, MapActivity.class);
        intent.putExtra(COORDS, position);
        intent.putExtra(BOUND, bounds);
        intent.putExtra(ASSET_NAMES, asset_names);
        intent.putExtra(LEVELS, levels);
        context.startActivity(intent);
    }
}
