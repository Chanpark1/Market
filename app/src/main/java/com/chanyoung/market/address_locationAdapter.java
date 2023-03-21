package com.chanyoung.market;

import static android.content.ContentValues.TAG;

import static com.chanyoung.market.set_address.room;
import static com.chanyoung.market.signup_location.SERVER_ADDRESS;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class address_locationAdapter extends RecyclerView.Adapter<address_locationAdapter.CustomViewHolder> {

    private List<locationItem> list;
    private Context mContext;
    SharedPreferences prefs;

    public address_locationAdapter(Context mContext, List<locationItem> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public address_locationAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_location_rv, parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull address_locationAdapter.CustomViewHolder holder, int position) {

        String area1 = list.get(position).getArea1();
        String area2 = list.get(position).getArea2();
        String area3 = list.get(position).getArea3();
        String area4 = list.get(position).getArea4();
        String longitude = list.get(position).getLongitude();
        String latitude = list.get(position).getLatitude();

        holder.tv.setText(area1 + " " +area2+ " " +area3 + " " +area4);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                prefs = mContext.getSharedPreferences("Username", Context.MODE_PRIVATE);

                Collection<?> col_val = prefs.getAll().values();
                Iterator<?> it_val = col_val.iterator();
                Collection<?> col_key = prefs.getAll().keySet();
                Iterator<?> it_key = col_key.iterator();

                while(it_val.hasNext() && it_key.hasNext()) {
                    String value = (String) it_val.next();

                    try {
                        JSONObject jsonObject = new JSONObject(value);

                        String authNum = jsonObject.getString("authNum");

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
                        Retrofit_update_location retrofit_update_location = retrofit.create(Retrofit_update_location.class);
                        Call<String> call = retrofit_update_location.insertAddress(authNum,area3,area4,longitude,latitude);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if(response.isSuccessful() && response.body() != null) {
                                    System.out.println(response.body());
                                    room +=1;
                                    System.out.println(room);

                                    Intent intent = new Intent(mContext,set_address.class);
                                    ((set_address_location)mContext).startActivity(intent);

                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                System.out.println(t.getMessage());
                            }
                        });



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }
        });
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


    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView tv;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tv = (TextView) itemView.findViewById(R.id.address_location_tv);
        }
    }
}
