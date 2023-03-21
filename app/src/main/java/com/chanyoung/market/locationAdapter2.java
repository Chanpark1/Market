package com.chanyoung.market;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class locationAdapter2 extends RecyclerView.Adapter<locationAdapter2.CustomViewHolder> {

    private List<locationItem2> list;
    private Context mContext;

    public locationAdapter2(Context mContext, List<locationItem2> list) {
        this.mContext = mContext;
        this.list = list;
    }


    @NonNull
    @Override
    public locationAdapter2.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.signup_location_rv, parent, false);

        CustomViewHolder holder=  new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull locationAdapter2.CustomViewHolder holder, int position) {

        String result = list.get(position).getResult();
        String area = list.get(position).getArea();
        String area4 = list.get(position).getArea4();
        String longitude = list.get(position).getLongitude();
        String latitude = list.get(position).getLatitude();

        holder.address_tv_area.setText(result);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, signup.class);
                if(area4 != null) {
                    intent.putExtra("area",area + " " +area4);
                    intent.putExtra("longitude",longitude);
                    intent.putExtra("latitude",latitude);
                } else {
                    intent.putExtra("area",area);
                    intent.putExtra("longitude",longitude);
                    intent.putExtra("latitude",latitude);
                }


                System.out.println(area);

                ((signup_location)mContext).startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected TextView address_tv_area;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.address_tv_area = (TextView) itemView.findViewById(R.id.address_rv_area1);


        }
    }
}
