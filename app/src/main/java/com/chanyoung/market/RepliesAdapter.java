package com.chanyoung.market;

import static android.content.ContentValues.TAG;
import static com.chanyoung.market.signup.SERVER_ADDRESS;
import static com.chanyoung.market.signup_profile.url;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
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

public class RepliesAdapter extends RecyclerView.Adapter<RepliesAdapter.CustomViewHolder> {

    List<Rep_rep_Items> list;
    Context context;
    public String authNum;
    public RepliesAdapter(Context context, List<Rep_rep_Items> list) {
       this.context = context;
       this.list = list;
    }

    @NonNull
    @Override
    public RepliesAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reply_reply_rv, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RepliesAdapter.CustomViewHolder holder, int position) {
        String replyIdx = list.get(position).getReplyIdx();
        String content = list.get(position).getContent();
        String area = list.get(position).getArea();
        String username = list.get(position).getUsername();
        String like_num = list.get(position).getLike_num();
        String authNum = list.get(position).getAuthNum();
        String post_authNum = list.get(position).getPost_authNum();
        String reply_authNum = list.get(position).getReply_authNum();
        String created = list.get(position).getCreated();
        String image_path = list.get(position).getImage_path();
        String user_path = list.get(position).getUser_path();

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");

        try {
            Date mDate = format.parse(created);

            String stDate = formatTimeString(mDate);
            holder.tv_created.setText(stDate);
            Log.d("Created",created);
            Log.d("mDate", String.valueOf(mDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(image_path != null) {
            holder.iv_reply.setVisibility(View.VISIBLE);

            Glide.with(holder.itemView.getContext())
                    .load(image_path)
                    .into(holder.iv_reply);
        }

        if(user_path != null) {
            Glide.with(holder.itemView.getContext())
                    .load(user_path)
                    .into(holder.imageView);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(url)
                    .into(holder.imageView);
        }
        String forAuth = ((comm_post_read)context).getIntent().getStringExtra("authNum");
        if(forAuth.equals(authNum)) {
            holder.writer.setVisibility(View.VISIBLE);
        }

        holder.tv_content.setText(content);
        holder.tv_area.setText(area);
        holder.tv_username.setText(username);
        TextView tv_reply_num = (TextView) ((comm_post_read)context).findViewById(R.id.comm_read_reply_num);
        Retrofit_load_post retrofit_load_post = getApiClient().create(Retrofit_load_post.class);

        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getAuthNum().equals(authNum)) {
                    holder.menu.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            PopupMenu popup = new PopupMenu(view.getContext(),holder.menu);
                            popup.getMenuInflater().inflate(R.menu.reply_menu_owner, popup.getMenu());

                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem menuItem) {

                                    switch (menuItem.getItemId()) {
                                        case R.id.edit_reply :

                                            Intent intent = new Intent(view.getContext(), edit_replies.class);

                                            intent.putExtra("post_authNum",post_authNum);
                                            intent.putExtra("reply_authNum",reply_authNum);
                                            intent.putExtra("authNum", ((comm_post_read)context).getIntent().getStringExtra("authNum"));
                                            intent.putExtra("replyIdx", replyIdx);
                                            view.getContext().startActivity(intent);
                                            ((comm_post_read)context).finish();

                                        case R.id.delete_reply :
                                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                                            builder.setTitle("댓글 삭제").setMessage("해당 댓글을 삭제할까요?");

                                            builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Call<String> del_reply = retrofit_load_post.delete_replies(post_authNum,reply_authNum,replyIdx);
                                                    del_reply.enqueue(new Callback<String>() {
                                                        @Override
                                                        public void onResponse(Call<String> call, Response<String> response) {
                                                            if(response.isSuccessful() && response.body() != null) {
                                                                list.remove(holder.getAdapterPosition());
                                                                notifyItemRemoved(holder.getAdapterPosition());
                                                                notifyItemRangeChanged(holder.getAdapterPosition(),list.size());

                                                                String num_str = tv_reply_num.getText().toString();
                                                                int num = Integer.parseInt(num_str);
                                                                num -= 1;

                                                                tv_reply_num.setText(String.valueOf(num));
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<String> call, Throwable t) {

                                                        }
                                                    });
                                                }
                                            });

                                            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();;
                                                }
                                            });

                                            AlertDialog dialog = builder.create();
                                            dialog.show();
                                    }

                                    return true;
                                }
                            });

                            popup.show();

                        }
                    });

                } else {
                    holder.menu.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            PopupMenu popup = new PopupMenu(view.getContext(),holder.menu);
                            popup.getMenuInflater().inflate(R.menu.reply_menu_viewer, popup.getMenu());
                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem menuItem) {

                                    switch (menuItem.getItemId()) {
                                        case R.id.read_report :
                                            break;
                                    }
                                    return true;
                                }
                            });
                            popup.show();
                        }
                    });
                }



            }
        });
    }

    private Retrofit getApiClient() {

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

        return retrofit;
    }
    private HttpLoggingInterceptor httpLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d(TAG, message);
            }
        });
        return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    private String getAuthNum() {
        SharedPreferences prefs = ((comm_post_read)context).getSharedPreferences("Username",Context.MODE_PRIVATE);

        Collection<?> col_val = prefs.getAll().values();
        Iterator<?> it_val = col_val.iterator();

        String value = (String) it_val.next();

        try {
            JSONObject jsonObject = new JSONObject(value);
            authNum = jsonObject.getString("authNum");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return authNum;
    }
    public void addItem(Rep_rep_Items items) {
        list.add(items);
    }
    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected ImageView imageView;
        protected ImageView iv_reply;
        protected ImageButton menu;
        protected TextView tv_username;
        protected TextView tv_area;
        protected TextView tv_created;
        protected TextView tv_content;
        protected TextView writer;


        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.reply_reply_profile);
            this.tv_username = (TextView) itemView.findViewById(R.id.reply_reply_username);
            this.tv_area = (TextView) itemView.findViewById(R.id.reply_reply_area);
            this.tv_created = (TextView) itemView.findViewById(R.id.reply_reply_created);
            this.tv_content = (TextView) itemView.findViewById(R.id.reply_reply_content);
            this.iv_reply = (ImageView) itemView.findViewById(R.id.reply_reply_image);
            this.menu = (ImageButton) itemView.findViewById(R.id.reply_reply_menu);
            this.writer = (TextView) itemView.findViewById(R.id.reply_reply_writer_logo);

        }
    }

    public String formatTimeString(Date mDate) {
        long curTime = System.currentTimeMillis();

        long reqTime = mDate.getTime();

        long diffTime = (curTime - reqTime) / 1000;
        String msg = null;


        if (diffTime < postAdapter.TIME_MAXIMUM.SEC) {
            msg = "방금 전";
        } else if ((diffTime /= postAdapter.TIME_MAXIMUM.SEC) < postAdapter.TIME_MAXIMUM.MIN) {
            msg = diffTime + "분 전";
        } else if ((diffTime /= postAdapter.TIME_MAXIMUM.MIN) < postAdapter.TIME_MAXIMUM.HOUR) {
            msg = (diffTime) + "시간 전";
        } else if ((diffTime /= postAdapter.TIME_MAXIMUM.HOUR) < postAdapter.TIME_MAXIMUM.DAY) {
            msg = (diffTime) + "일 전";
        } else if ((diffTime /= postAdapter.TIME_MAXIMUM.DAY) < postAdapter.TIME_MAXIMUM.MONTH ) {
            msg = (diffTime) + "달 전";
        } else {
            msg = (diffTime) + "년 전";
        }
        return msg;
    }
}
