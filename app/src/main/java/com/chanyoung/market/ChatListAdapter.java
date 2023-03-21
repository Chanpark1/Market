package com.chanyoung.market;

import static com.chanyoung.market.signup_profile.url;

import android.content.Context;
import android.content.Intent;
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

import org.w3c.dom.Text;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {

    private List<ChatList> list;
    private Context context;

    public ChatListAdapter(Context context, List<ChatList> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ChatListAdapter.ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_rv, parent, false);
        ChatListViewHolder holder = new ChatListViewHolder(view);

        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.ChatListViewHolder holder, int position) {
        String profile_path = list.get(position).getPath();
        String post_path = list.get(position).getPost_path();
        String username = list.get(position).getUsername();
        String content = list.get(position).getContent();
        String area = list.get(position).getArea();
        String to_auth = list.get(position).getTo_auth();
        String post_authNum = list.get(position).getPost_authNum();
        String room_auth = list.get(position).getRoom_auth();

        if(profile_path != null) {
            Glide.with(holder.itemView.getContext())
                    .load(profile_path)
                    .into(holder.profileImage);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(url)
                    .into(holder.profileImage);
        }

        holder.postImage.setClipToOutline(true);

        Glide.with(holder.itemView.getContext())
                .load(post_path)
                .into(holder.postImage);

        holder.username.setText(username);
        holder.content.setText(content);
        holder.area.setText(area);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Chatting.class);

                intent.putExtra("authNum",to_auth);
                intent.putExtra("post_authNum",post_authNum);
                intent.putExtra("username",username);
                intent.putExtra("room_auth",room_auth);

                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    public class ChatListViewHolder extends RecyclerView.ViewHolder {

        protected ImageView profileImage;
        protected ImageView postImage;
        protected TextView username;
        protected TextView area;
        protected TextView content;

        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);

            this.profileImage = itemView.findViewById(R.id.chat_rv_profile);
            this.postImage = itemView.findViewById(R.id.chat_rv_post_image);
            this.username = itemView.findViewById(R.id.chat_rv_username);
            this.area = itemView.findViewById(R.id.chat_rv_area);
            this.content = itemView.findViewById(R.id.chat_rv_content);
        }
    }
}
