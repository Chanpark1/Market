package com.chanyoung.market;

import static android.content.ContentValues.TAG;
import static com.chanyoung.market.set_address.room;
import static com.chanyoung.market.signup.SERVER_ADDRESS;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

public class addressAdapter extends RecyclerView.Adapter<addressAdapter.CustomViewHolder> {

    private List<addressItem> list;
    private Context mContext;
    Button add;

    boolean isSelected1 = true;
    boolean isSelected2 = true;

    SharedPreferences prefs;

    public addressAdapter(Context mContext, List<addressItem> list) {
        this.mContext = mContext;
        this.list = list;
    }


    @NonNull
    @Override
    public addressAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.set_address_rv,parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull addressAdapter.CustomViewHolder holder, int position) {

        int i = holder.getAdapterPosition();

        String area = list.get(position).getArea();
        String idx = list.get(position).getIdx();
        String longitude = list.get(position).getLongitude();
        String latitude = list.get(position).getLatitude();
        add = ((set_address)mContext).findViewById(R.id.add);

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

        holder.tv.setText(area);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println(room);

                if(room < 2) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);


                    dialog.setMessage("동네가 1개만 선택된 상태에서는 삭제 할 수 없습니다. 현재 설정된 동네를 변경하시겠습니까?");
                    dialog.setPositiveButton("변경", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent((set_address)mContext,update_set_address.class);


                            ((set_address)mContext).startActivity(intent);

                        }
                    });
                    dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alertDialog = dialog.create();
                    alertDialog.show();


                } else {

                    list.remove(i);
                    notifyItemRemoved(i);
                    notifyItemRangeChanged(i, list.size());
                    add.setVisibility(View.VISIBLE);
                    room -= 1;

                    Retrofit_remove_set_address i = retrofit.create(Retrofit_remove_set_address.class);
                    Call<String> call = i.insertIdx(idx);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if(response.isSuccessful() && response.body() != null) {
                                System.out.println(response.body());

                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            System.out.println(t.getMessage());
                        }
                    });
                }

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isSelected1) {
                    holder.itemView.setSelected(true);
                    isSelected1 = false;
                    notifyDataSetChanged();
                }else if(holder.itemView.isSelected()){
                    holder.itemView.setSelected(false);
                    isSelected1 = true;
                }

                prefs = mContext.getSharedPreferences("Username",Context.MODE_PRIVATE);

                Collection<?> col_val = prefs.getAll().values();
                Iterator<?> it_val = col_val.iterator();

                while(it_val.hasNext()) {
                    String value = (String) it_val.next();

                    try {
                        JSONObject jsonObject = new JSONObject(value);

                        String authNum = jsonObject.getString("authNum");

                        Retrofit_renew_address retrofit_renew_address = retrofit.create(Retrofit_renew_address.class);
                        Call<String> call = retrofit_renew_address.updateAddress(authNum,area,longitude,latitude);

                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if(response.isSuccessful() && response.body() != null) {
                                    System.out.println(response.body());
                                    Toast.makeText(mContext, "위치 정보가 갱신되었습니다.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(mContext,MainActivity.class);

                                    prefs = mContext.getSharedPreferences("savedState",Context.MODE_PRIVATE);

                                    Collection<?> col_val = prefs.getAll().values();
                                    Iterator<?> it_val = col_val.iterator();
                                    Collection<?> col_key = prefs.getAll().keySet();
                                    Iterator<?> it_key = col_key.iterator();

                                    while(it_val.hasNext() && it_key.hasNext()) {
                                        String value = (String) it_val.next();

                                        try {
                                            JSONObject jsonObject1 = new JSONObject(value);

                                            String progress = jsonObject1.getString("progress");


                                            intent.putExtra("distance", progress);

                                            ((set_address)mContext).startActivity(intent);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
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

    public class CustomViewHolder extends  RecyclerView.ViewHolder{

        protected TextView tv;
        protected Button delete;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv = (TextView) itemView.findViewById(R.id.selected1);
            this.delete = (Button) itemView.findViewById(R.id.delete1);
        }
    }
}