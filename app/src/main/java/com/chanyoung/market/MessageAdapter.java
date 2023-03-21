package com.chanyoung.market;

import static com.chanyoung.market.Chatting.isRead;
import static com.chanyoung.market.signup_profile.url;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.naver.maps.map.internal.util.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Message> list;
    public String authNum;

    public static String userImage;

    public MessageAdapter(Context context, List<Message> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if(viewType == ViewType.RIGHT_CHAT) {
                view = inflater.inflate(R.layout.from_message_rv, parent, false);
                return new RightViewHolder(view);
            } else if (viewType == ViewType.LEFT_CHAT){
                view = inflater.inflate(R.layout.to_message_rv, parent, false);
                return new LeftViewHolder(view);
            } else {
                view = inflater.inflate(R.layout.center_rv, parent, false);
                return new CenterViewHolder(view);
            }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        userImage = list.get(position).getUserImage();
        String content = list.get(position).getContent();
        String created = list.get(position).getCreated();
        String path = list.get(position).getPath();

        ImageView post_imageView = ((Chatting)context).findViewById(R.id.chatting_post_image);
        post_imageView.setClipToOutline(true);

        if(holder instanceof LeftViewHolder) {

            if(userImage != null) {
                Glide.with(holder.itemView.getContext())
                        .load(userImage)
                        .into(((LeftViewHolder)holder).profile_image);
            } else {
                Glide.with(holder.itemView.getContext())
                        .load(url)
                        .into(((LeftViewHolder)holder).profile_image);
            }



            if(path != null) {
                ((LeftViewHolder)holder).to_image.setVisibility(View.VISIBLE);
                ((LeftViewHolder)holder).to_hidden_time.setVisibility(View.VISIBLE);
                ((LeftViewHolder)holder).tv_tomessage.setVisibility(View.GONE);
                ((LeftViewHolder)holder).to_time.setVisibility(View.GONE);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    LocalDateTime dateTime = LocalDateTime.parse(created, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                    String time = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));

                    ((LeftViewHolder)holder).to_hidden_time.setText(time);

                    Glide.with(context)
                            .load(path)
                            .into(((LeftViewHolder)holder).to_image);

                }
            } else {
                ((LeftViewHolder)holder).tv_tomessage.setText(content);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    LocalDateTime dateTime = LocalDateTime.parse(created, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                    String time = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));

                    ((LeftViewHolder)holder).to_time.setText(time);
                }

            }

        } else if(holder instanceof RightViewHolder) {

            ((RightViewHolder)holder).tv_message.setText(content);
            LocalDateTime dateTime = null;


            if(path != null) {
                ((RightViewHolder)holder).from_image.setVisibility(View.VISIBLE);
                ((RightViewHolder)holder).from_hidden_time.setVisibility(View.VISIBLE);
                ((RightViewHolder)holder).tv_message.setVisibility(View.GONE);
                ((RightViewHolder)holder).from_time.setVisibility(View.GONE);

                Glide.with(context)
                        .load(path)
                        .into(((RightViewHolder)holder).from_image);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    dateTime = LocalDateTime.parse(created, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    String time = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
                    ((RightViewHolder)holder).from_hidden_time.setText(time);
                }
            } else {
                if(created != null) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        dateTime = LocalDateTime.parse(created, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        String time = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
                        ((RightViewHolder)holder).from_time.setText(time);
                    }
                }
            }
            }
    }

    public void addItem(Message message) {
        list.add(message);
    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }


    @Override
    public int getItemViewType(int position) {
        return list.get(position).getViewType();
    }

    public class LeftViewHolder extends RecyclerView.ViewHolder {

        protected ImageView profile_image;
        protected TextView tv_tomessage;
        protected TextView to_time;
        protected ImageView to_image;
        protected TextView to_hidden_time;

        public LeftViewHolder(@NonNull View itemView) {
            super(itemView);
            this.profile_image = (ImageView) itemView.findViewById(R.id.message_profile);
            this.tv_tomessage = (TextView) itemView.findViewById(R.id.to_message_tv);
            this.to_time = (TextView) itemView.findViewById(R.id.to_time);
            this.to_image = (ImageView) itemView.findViewById(R.id.to_image);
            this.to_hidden_time = (TextView) itemView.findViewById(R.id.to_hidden_time);

        }
    }

    public class RightViewHolder extends RecyclerView.ViewHolder {

        protected TextView tv_message;
        protected TextView from_time;
        protected ImageView from_image;
        protected TextView from_hidden_time;

        public RightViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tv_message = (TextView) itemView.findViewById(R.id.from_message_tv);
            this.from_time = (TextView) itemView.findViewById(R.id.from_time);
            this.from_image = (ImageView) itemView.findViewById(R.id.from_image);
            this.from_hidden_time = (TextView) itemView.findViewById(R.id.from_hidden_time);
        }
    }

    public class CenterViewHolder extends RecyclerView.ViewHolder {
        protected TextView tv_center;


        public CenterViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tv_center = (TextView) itemView.findViewById(R.id.center_time);
        }
    }

    public String getAuthNum() {
        SharedPreferences prefs = context.getSharedPreferences("Username", Context.MODE_PRIVATE);

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
    public String formatTimeString(String created) {
        String format_now = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            LocalTime now = LocalTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            format_now = now.format(formatter);
        }
        return format_now;
    }

}
