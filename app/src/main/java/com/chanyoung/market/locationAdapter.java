package com.chanyoung.market;

import static com.chanyoung.market.signup_location.SERVER_ADDRESS;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class locationAdapter extends RecyclerView.Adapter<locationAdapter.CustomViewHolder> {

    private List<locationItem> list;

    private Context mContext;



    public locationAdapter(Context mContext, List<locationItem> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public locationAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.signup_location_rv, parent, false);

        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull locationAdapter.CustomViewHolder holder, int position) {

        String area1 = list.get(position).getArea1();
        String area2 = list.get(position).getArea2();
        String area3 = list.get(position).getArea3();
        String area4 = list.get(position).getArea4();
        String longitude = list.get(position).getLongitude();
        String latitude = list.get(position).getLatitude();


        if(area4 == null) {
            holder.address_tv_area1.setText(area1 + " " +area2+ " " +area3);
        } else {

            holder.address_tv_area1.setText(area1 + " " +area2+ " " +area3 + " " +area4);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, signup.class);

                if(area4 != null) {
                    intent.putExtra("area",area3 + " " + area4);
                    intent.putExtra("longitude",longitude);
                    intent.putExtra("latitude",latitude);
                } else {
                    intent.putExtra("area",area3);
                    intent.putExtra("longitude",longitude);
                    intent.putExtra("latitude",latitude);
                }


                ((signup_location)mContext).startActivity(intent);

                // retrofit으로 query 문 날려야함
            }
        });

    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView address_tv_area1;

        public CustomViewHolder(@NonNull View view) {
            super(view);

            this.address_tv_area1 = (TextView) view.findViewById(R.id.address_rv_area1);
        }


    }


}
