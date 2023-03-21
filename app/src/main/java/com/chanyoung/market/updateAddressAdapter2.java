package com.chanyoung.market;

import static android.content.ContentValues.TAG;

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

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class updateAddressAdapter2 extends RecyclerView.Adapter<updateAddressAdapter2.CustomViewHolder> {

    private List<locationItem2> list;
    private Context mContext;

    public updateAddressAdapter2(Context mContext, List<locationItem2> list) {
        this.mContext = mContext;
        this.list = list;
    }


    @NonNull
    @Override
    public updateAddressAdapter2.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_location_rv, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull updateAddressAdapter2.CustomViewHolder holder, int position) {
        String result = list.get(position).getResult();
        String area = list.get(position).getArea();
        String area4 = list.get(position).getArea4();
        String longitude = list.get(position).getLongitude();
        String latitude = list.get(position).getLatitude();

        holder.tv.setText(result);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getIntent = ((update_set_address)mContext).getIntent();

                String authNum = getIntent.getStringExtra("authNum");

                OkHttpClient client = new OkHttpClient.Builder()
                        .addInterceptor(httpLoggingInterceptor())
                        .build();

                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(SERVER_ADDRESS)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .client(client)
                        .build();
                Retrofit_update_address retrofit_update_address = retrofit.create(Retrofit_update_address.class);

                Call<String> call = retrofit_update_address.updateAddress(authNum,area,area4,longitude,latitude);

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.isSuccessful() && response.body() != null) {
                            System.out.println(response.body());

                            Intent intent = new Intent((update_set_address)mContext,set_address.class);

                            ((update_set_address)mContext).startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        System.out.println(t.getMessage());
                    }
                });


            }
        });
    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }
    private HttpLoggingInterceptor httpLoggingInterceptor() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                android.util.Log.d(TAG,message);
            }
        });
        return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder{

        protected TextView tv;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tv = (TextView) itemView.findViewById(R.id.address_location_tv);
        }
    }
}
