package com.chanyoung.market;

import static android.content.ContentValues.TAG;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.chanyoung.market.comm_post_read.isBitmap;
import static com.chanyoung.market.signup.SERVER_ADDRESS;
import static com.chanyoung.market.signup_profile.url;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.CustomViewHolder> {

    List<ReplyItems> list;
    Context context;
    public static String reply_authNum;
    public String authNum;
    MultipartBody.Part multipartBody;


    public ReplyAdapter( Context context, List<ReplyItems> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ReplyAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reply_rv, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ReplyAdapter.CustomViewHolder holder, int position) {

        String replyIdx = list.get(position).getReplyIdx();
        String content = list.get(position).getContent();
        String area = list.get(position).getArea();
        String username = list.get(position).getUsername();
        String like_num = list.get(position).getLike_num();
        String reply_authNum = list.get(position).getReply_authNum();
        String authNum = list.get(position).getAuthNum();
        String post_authNum = list.get(position).getPost_authNum();
        String created = list.get(position).getCreated();
        String image_path = list.get(position).getImage_path();
        String user_path = list.get(position).getUser_path();



        Retrofit_load_post retrofit_load_post = getApiClient().create(Retrofit_load_post.class);

        Call<String> isThereReplies = retrofit_load_post.isThere(post_authNum,reply_authNum);
        isThereReplies.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if(response.body().equals("O")) {
                        holder.paging.setVisibility(View.VISIBLE);
                        holder.paging.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Call<List<Rep_rep_Items>> rep_rep = retrofit_load_post.load_rep_rep(post_authNum,reply_authNum);

                                rep_rep.enqueue(new Callback<List<Rep_rep_Items>>() {
                                    @Override
                                    public void onResponse(Call<List<Rep_rep_Items>> call, Response<List<Rep_rep_Items>> response) {
                                        if(response.isSuccessful() && response.body() != null) {
                                            holder.rep_rep_list = response.body();
                                            holder.paging.setVisibility(View.GONE);
                                            holder.adapter = new RepliesAdapter(holder.rv.getContext(), holder.rep_rep_list);
                                            holder.manager = new LinearLayoutManager(holder.rv.getContext());

                                            holder.rv.setAdapter(holder.adapter);
                                            holder.rv.setLayoutManager(holder.manager);
                                            for (int i = 0; i < response.body().size(); i++) {
                                                String rows = response.body().get(i).getCount();
                                                int count = Integer.parseInt(rows);
                                                int LeftOver = count - holder.rep_rep_list.size();
                                                if(LeftOver > 0) {
                                                    holder.paging2.setVisibility(View.VISIBLE);
                                                    holder.paging2.setText("-------답글 " + LeftOver + "개 더보기");

                                                    holder.paging2.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            holder.paging2.setVisibility(View.GONE);
                                                            String reply_auth = list.get(holder.getAdapterPosition()).getReply_authNum();
                                                            int position = holder.rep_rep_list.size() - 1;
                                                            String reply_idx = holder.rep_rep_list.get(position).getReplyIdx();
                                                            Call<List<Rep_rep_Items>> call = retrofit_load_post.load_more_replies(post_authNum,reply_auth,reply_idx);
                                                            call.enqueue(new Callback<List<Rep_rep_Items>>() {
                                                                @Override
                                                                public void onResponse(Call<List<Rep_rep_Items>> call, Response<List<Rep_rep_Items>> response) {
                                                                    if(response.isSuccessful() && response.body() != null) {
                                                                        for (int i = 0; i < response.body().size(); i++) {
                                                                            String count = response.body().get(i).getCount();
                                                                            String post_authNum = response.body().get(i).getPost_authNum();
                                                                            String authNum = response.body().get(i).getAuthNum();
                                                                            String content = response.body().get(i).getContent();
                                                                            String image_path = response.body().get(i).getImage_path();
                                                                            String like_num = response.body().get(i).getLike_num();
                                                                            String created = response.body().get(i).getCreated();
                                                                            String user_path = response.body().get(i).getUser_path();
                                                                            String area = response.body().get(i).getArea();
                                                                            String username = response.body().get(i).getUsername();
                                                                            String replyIdx = response.body().get(i).getReplyIdx();
                                                                            String reply_authNum = response.body().get(i).getReply_authNum();

                                                                            holder.adapter.addItem(new Rep_rep_Items(reply_authNum,post_authNum,authNum,content,image_path,like_num,created,user_path,area,username,replyIdx,null));

                                                                            holder.adapter.notifyDataSetChanged();
                                                                            int counts = Integer.parseInt(count);

                                                                            int LeftOvers = counts - holder.rep_rep_list.size();

                                                                            if(LeftOvers > 0) {
                                                                                holder.paging2.setVisibility(View.VISIBLE);
                                                                                holder.paging2.setText("-------답글 " + LeftOvers + "개 더보기");
                                                                            } else {
                                                                                holder.paging2.setVisibility(View.GONE);
                                                                            }



                                                                        }
                                                                    }
                                                                }

                                                                @Override
                                                                public void onFailure(Call<List<Rep_rep_Items>> call, Throwable t) {

                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<List<Rep_rep_Items>> call, Throwable t) {

                                    }
                                });

                            }
                        });


                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");

        try {
            Date mDate = format.parse(created);
            Log.d("ㅆ:바ㅣ란ㅇㄹ니ㅏ르", String.valueOf(mDate));

            String stDate = formatTimeString(mDate);
            holder.tv_created.setText(stDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.iv_reply.setClipToOutline(true);
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
        EditText rep_rep_input = (EditText) ((comm_post_read)context).findViewById(R.id.comm_hidden_input_reply);
        Button rep_rep_submit = (Button) ((comm_post_read)context).findViewById(R.id.comm_hidden_reply_submit);
        ImageView hidden_image_view = (ImageView) ((comm_post_read)context).findViewById(R.id.hidden_reply_image);
        TextView tv_reply_num = (TextView) ((comm_post_read)context).findViewById(R.id.comm_read_reply_num);

        holder.tv_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String idx = list.get(holder.getAdapterPosition()).getReply_authNum();
                LinearLayout origin_input_layout = (LinearLayout) ((comm_post_read)context).findViewById(R.id.bottom_linear);
                origin_input_layout.setVisibility(View.GONE);

                LinearLayout hidden_input_layout = (LinearLayout) ((comm_post_read)context).findViewById(R.id.bottom_hidden_linear);
                LinearLayout hidden_field = (LinearLayout) ((comm_post_read)context).findViewById(R.id.bottom_hidden_reply);
                TextView hidden_tv = (TextView) ((comm_post_read)context).findViewById(R.id.hidden_reply_tv);
                hidden_field.setVisibility(View.VISIBLE);
                hidden_input_layout.setVisibility(View.VISIBLE);

                LinearLayout hidden_image_layout = (LinearLayout) ((comm_post_read)context).findViewById(R.id.bottom_hidden_linear_image);
                ImageView hidden_image_view = (ImageView) ((comm_post_read)context).findViewById(R.id.hidden_reply_image);

                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(INPUT_METHOD_SERVICE);
                EditText input_reply = (EditText) ((comm_post_read)context).findViewById(R.id.comm_hidden_input_reply);

                Retrofit_load_post retrofit_load_post = getApiClient().create(Retrofit_load_post.class);
                Call<String> call = retrofit_load_post.get_username(authNum,post_authNum);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.isSuccessful() && response.body() != null) {
                            input_reply.setHint(response.body() + "님에게 답글 달기");
                            hidden_tv.setText(response.body() + "님에게 답글 남기는 중");
                            imm.toggleSoftInput(0,0);

                            rep_rep_submit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(!isBitmap) {

                                        Bitmap bitmap = ((BitmapDrawable)hidden_image_view.getDrawable()).getBitmap();

                                        Uri uri = getUri(holder.itemView.getContext(),bitmap);

                                        String path = getAbsolutePath(uri);

                                        File file = BitmapToFile(bitmap,path);

                                        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"),file);
                                        multipartBody = MultipartBody.Part.createFormData("uploaded_file",file.getName(),requestBody);

                                    }

                                    String content = rep_rep_input.getText().toString();


                                    HashMap<String,RequestBody> hashMap = new HashMap<>();

                                    RequestBody content_body = RequestBody.create(MediaType.parse("text/plain"),content);
                                    RequestBody auth_body = RequestBody.create(MediaType.parse("text/plain"),getAuthNum());
                                    RequestBody post_body = RequestBody.create(MediaType.parse("text/plain"),post_authNum);
                                    RequestBody rep_auth_body = RequestBody.create(MediaType.parse("text/plain"),idx);
                                    hashMap.put("reply_authNum",rep_auth_body);
                                    hashMap.put("content",content_body);
                                    hashMap.put("authNum",auth_body);
                                    hashMap.put("post_authNum",post_body);

                                    Retrofit_load_post retrofit_load_post = getApiClient().create(Retrofit_load_post.class);
                                    Call<List<Rep_rep_Items>> upload_rep_rep = retrofit_load_post.upload_rep_rep(multipartBody,hashMap);
                                    upload_rep_rep.enqueue(new Callback<List<Rep_rep_Items>>() {
                                        @Override
                                        public void onResponse(Call<List<Rep_rep_Items>> call, Response<List<Rep_rep_Items>> response) {
                                            if(response.isSuccessful() && response.body() != null) {
                                                rep_rep_input.setText("");
                                                holder.rep_rep_list = response.body();

                                                holder.adapter = new RepliesAdapter(view.getContext(), holder.rep_rep_list);
                                                holder.manager = new LinearLayoutManager(holder.rv.getContext());

                                                holder.rv.setAdapter(holder.adapter);
                                                holder.rv.setLayoutManager(holder.manager);

                                                hidden_field.setVisibility(View.GONE);
                                                hidden_input_layout.setVisibility(View.GONE);
                                                origin_input_layout.setVisibility(View.VISIBLE);

                                                hidden_image_layout.setVisibility(View.GONE);
                                                hidden_image_view.setImageResource(0);

                                                String num_str = tv_reply_num.getText().toString();
                                                int num = Integer.parseInt(num_str);

                                                num += 1;

                                                tv_reply_num.setText(String.valueOf(num));

                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<List<Rep_rep_Items>> call, Throwable t) {

                                        }
                                    });


                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d(TAG, t.getMessage());
                    }
                });
            }
        });

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

                                            Intent intent = new Intent(view.getContext(), edit_reply.class);

                                            intent.putExtra("post_authNum",post_authNum);
                                            intent.putExtra("reply_authNum",reply_authNum);
                                            intent.putExtra("authNum", ((comm_post_read)context).getIntent().getStringExtra("authNum"));
                                            view.getContext().startActivity(intent);
                                            ((comm_post_read)context).finish();


                                        case R.id.delete_reply :
                                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                                            builder.setTitle("댓글 삭제").setMessage("해당 댓글을 삭제할까요?");

                                            builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Call<String> del_reply = retrofit_load_post.delete_reply(post_authNum,reply_authNum);
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

    public void addItem(ReplyItems items) {
        list.add(items);
    }

    private String getAbsolutePath(Uri uri) {

        String result;
        Cursor cursor = ((comm_post_read)context).getContentResolver().query(uri,null,null,null,null);
        if(cursor == null) {
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private Uri getUri(Context context, Bitmap bitmap) {


        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(),bitmap,"IMG_"+ Calendar.getInstance().getTime(),null);

        return Uri.parse(path);
    }

    private File BitmapToFile(Bitmap bitmap, String path) {
        File file = new File(path);

        OutputStream stream = null;

        try {
            file.createNewFile();
            stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);

        } catch (Exception e) {
            e.printStackTrace();

        }

        return file;
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
        protected TextView tv_reply;
        protected TextView writer;
        protected TextView paging;
        protected TextView paging2;
        protected ConstraintLayout parent_con;
        protected RecyclerView rv;
        protected RepliesAdapter adapter;
        protected List<Rep_rep_Items> rep_rep_list;
        protected LinearLayoutManager manager;


        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.imageView = (ImageView) itemView.findViewById(R.id.reply_profile);
            this.tv_username = (TextView) itemView.findViewById(R.id.reply_username);
            this.tv_area = (TextView) itemView.findViewById(R.id.reply_area);
            this.tv_created = (TextView) itemView.findViewById(R.id.reply_created);
            this.tv_content = (TextView) itemView.findViewById(R.id.reply_content);
            this.tv_reply = (TextView) itemView.findViewById(R.id.reply_reply);
            this.iv_reply = (ImageView) itemView.findViewById(R.id.reply_image);
            this.menu = (ImageButton) itemView.findViewById(R.id.reply_menu);
            this.writer = (TextView) itemView.findViewById(R.id.writer_logo);
            this.parent_con = (ConstraintLayout) itemView.findViewById(R.id.reply_rv_constraint);
            this.rv = (RecyclerView) itemView.findViewById(R.id.reply_reply_rv);
            this.paging = (TextView) itemView.findViewById(R.id.rep_rep_paging);
            this.paging2 = (TextView) itemView.findViewById(R.id.rep_rep_paging2);
            this.rep_rep_list = new ArrayList<>();
            this.adapter = new RepliesAdapter(itemView.getContext(),rep_rep_list);
            this.manager = new LinearLayoutManager(itemView.getContext());

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
