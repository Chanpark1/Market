package com.chanyoung.market;

import static android.content.ContentValues.TAG;

import static com.chanyoung.market.MessageAdapter.userImage;
import static com.chanyoung.market.signup.SERVER_ADDRESS;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
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
import retrofit2.http.PartMap;


public class Chatting extends AppCompatActivity {

    TextView tv_username;
    TextView tv_status;
    TextView tv_title;
    TextView tv_price;
    ImageView post_image;

    ImageButton load_image;
    EditText input_message;
    Button send;
    Button image_btn;

    public String from_authNum;
    public String to_authNum;
    public String post_authNum;

    private RecyclerView recyclerView;
    private List<Message> list = new ArrayList<>();
    private List<post_info> info_list = new ArrayList<>();
    private List<ChatImage> img_list = new ArrayList<>();

    private MessageAdapter adapter;
    private LinearLayoutManager manager;

    boolean hasCamPerm;
    boolean hasWritePerm;

    private Handler mHandler;
    Socket socket;
    public static final String IP = "192.168.219.102";
    public static final int PORT = 4462;
    String read;
    String sendmsg;

    String data;

    public String username;

    public static String isRead = "X";

    public String room_auth = null;

    ArrayList<Uri> uri_list = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        hasCamPerm = checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        hasWritePerm = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        ChattingService service = new ChattingService();

        mHandler = new Handler();

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("data"));
//        LocalBroadcastManager.getInstance(this).registerReceiver(ShutdownReceiver,new IntentFilter(Intent.ACTION_SHUTDOWN));
//        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SHUTDOWN);
//
//        registerReceiver(reboot_receiver,intentFilter);

        tv_username = (TextView) findViewById(R.id.chatting_logo);
        tv_status = (TextView) findViewById(R.id.chatting_post_status);
        tv_title = (TextView) findViewById(R.id.chatting_post_title);
        tv_price = (TextView) findViewById(R.id.chatting_post_price);
        post_image = (ImageView) findViewById(R.id.chatting_post_image);
        recyclerView = (RecyclerView) findViewById(R.id.chatting_rv_field);

        load_image = (ImageButton) findViewById(R.id.chatting_load_image);
        input_message = (EditText) findViewById(R.id.chatting_input_message);
        send = (Button) findViewById(R.id.chatting_send_btn);

        Retrofit_chatting retrofit_chatting = getApiClient().create(Retrofit_chatting.class);

        Bundle extras = getIntent().getExtras();

        if(extras != null) {
            to_authNum = extras.getString("authNum");
            post_authNum = extras.getString("post_authNum");

            String userName = extras.getString("username");
            room_auth = extras.getString("room_auth");

            Intent toService = new Intent(Chatting.this, ChattingService.class);
            toService.putExtra("room_auth", room_auth);
            startService(toService);

            Call<String> get_username = retrofit_chatting.get_username(to_authNum);
            get_username.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if(response.isSuccessful() && response.body() != null) {
                        tv_username.setText(response.body());
                    } else {
                        tv_username.setText(userName);
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });


            System.out.println("받은값임 toAUTH : " + to_authNum+ "\n" +" post_AUTH : " +post_authNum+ "\n"+ "room AUTH : " +room_auth);

//            tv_username.setText(userName);
        } else {
            System.out.println("데이터 없음 ㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎ");
        }

        Call<List<Message>> call = retrofit_chatting.load_message(getAuthNum(),to_authNum, room_auth,post_authNum);
        call.enqueue(new Callback<List<Message>>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if(response.isSuccessful() && response.body() != null) {

                    list = response.body();

                    generateMessageList(list);
//                    int position = list.size() - 1;
                    recyclerView.scrollToPosition(0);

                    for(int i = 0; i < list.size(); i++) {
                        System.out.println("INDEX : " + i + "IDX : " + list.get(i).getIdx());
                    }


                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });


        recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if(!view.canScrollVertically(-1)) {

                    int count = list.size() - 1;

                    if(count > 0) {

                        String idx = list.get(count).getIdx();
                        LoadMoreMessage(retrofit_chatting,getAuthNum(),to_authNum,room_auth,post_authNum,idx);
                    }

                }
            }
        });

        System.out.println("들어가는 값 :::::: TO AUTH : " + to_authNum + "FROM AUTH : "  + getAuthNum());


        Call<List<post_info>> post_info = retrofit_chatting.post_info(post_authNum);
        post_info.enqueue(new Callback<List<com.chanyoung.market.post_info>>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(Call<List<com.chanyoung.market.post_info>> call, Response<List<com.chanyoung.market.post_info>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    info_list = response.body();

                    for(int i = 0; i < info_list.size(); i++) {
                        tv_title.setText(info_list.get(i).getTitle());
                        tv_price.setText(info_list.get(i).getPrice() + "원");
                        tv_status.setText(info_list.get(i).getStatus());
                        post_image.setClipToOutline(true);
                        Glide.with(Chatting.this)
                                .load(info_list.get(i).getPostImage())
                                .into(post_image);


                    }
                }
            }

            @Override
            public void onFailure(Call<List<com.chanyoung.market.post_info>> call, Throwable t) {

            }
        });


//       순서
//        에코서버 ( 자바 인텔리제이 )
//        1) 접속할 포트와 소켓을 열어준다.  ( 무한 반복 )
//        2) 접속한 소캣의 정보를 저장해둔다.
//        3) 저장 한 소캣의 입력 출력 스트림을 열어준다   ( 위의 무한 반복 안에 생성 )
//         메세지 송 수신 관련 스트림.  DataInputStream 과 DataOutputStream 를 사용
//        4) 메세지를 수신할 스레드를 작성한다.
//        5) 수신한 메세지를 처리하는 로직을 짠다  ( ex 방나누기  1:1 채팅 그룹채팅 등을 구분할수 있는 구분자들 )
//        6) 보내고 싶은 상대방 쪽 스트림에다가 데이터를 전송한다.

        // Chatting Activity 입장 시 쓰레드 생성해서 서버에 접속
        new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    InetAddress serverAddr = InetAddress.getByName(IP);
                    socket = new Socket(serverAddr,PORT);
                    service.setSocket(socket);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();



        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendmsg = input_message.getText().toString();
                // ^채팅방과 유저 식별을 위해 "@" 로 구분해서 여러 값을 넣어 보낸다.
                // 에코서버에서 split("@") 함수를 이용해 배열에 담아준다.

                input_message.setText("");

                Call<String> call = retrofit_chatting.upload_chat(sendmsg,getAuthNum(),to_authNum, post_authNum);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.isSuccessful() && response.body() != null) {

                            room_auth = response.body();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        System.out.println(t.getMessage());
                    }
                });
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {

                            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                            out.writeUTF(to_authNum + "@" + sendmsg + "@" + getAuthNum() + "@" + post_authNum + "@" + room_auth);
                            mHandler.post(new msgUpdate(sendmsg));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 777) {
            if(data == null) {
                Toast.makeText(this, "이미지를 선택 해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                if(data.getClipData() == null) {
                    Uri imageUri = data.getData();
                    uri_list.add(imageUri);
                } else {
                    ClipData clipData = data.getClipData();
                    if(clipData.getItemCount() > 10) {
                        Toast.makeText(this, "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        for(int i = 0; i < clipData.getItemCount(); i++) {
                            Uri imageUri = clipData.getItemAt(i).getUri();

                            try {
                                uri_list.add(imageUri);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        ArrayList<MultipartBody.Part> files = new ArrayList<>();

                        for(int i = 0; i < uri_list.size(); i++) {
                            Uri uri = uri_list.get(i);

                            String path = getAbsolutePath(uri);

                            File file = new File(path);

                            RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpeg"),file);

                            String fileName = "photo" + i + ".jpg";

                            MultipartBody.Part filePart = MultipartBody.Part.createFormData("uploaded_file"+i, fileName, fileBody);

                            files.add(filePart);
                        }

                        RequestBody room_body = RequestBody.create(MediaType.parse("text/plain"),room_auth);
                        RequestBody from_body = RequestBody.create(MediaType.parse("text/plain"),getAuthNum());
                        RequestBody to_body = RequestBody.create(MediaType.parse("text/plain"),to_authNum);
                        RequestBody post_body = RequestBody.create(MediaType.parse("text/plain"),post_authNum);
                        RequestBody count_body = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(uri_list.size()));
                        HashMap<String,RequestBody> hashMap = new HashMap<>();

                        hashMap.put("room_auth",room_body);
                        hashMap.put("from_auth",from_body);
                        hashMap.put("to_auth",to_body);
                        hashMap.put("post_auth", post_body);
                        hashMap.put("count", count_body);

                        Retrofit_chatting retrofit_chatting = getApiClient().create(Retrofit_chatting.class);
                        Call<List<ChatImage>> call = retrofit_chatting.chat_image(files,hashMap);

                        call.enqueue(new Callback<List<ChatImage>>() {
                            @Override
                            public void onResponse(Call<List<ChatImage>> call, Response<List<ChatImage>> response) {
                                if(response.isSuccessful() && response.body() != null) {
                                    img_list = response.body();


                                    new Thread() {
                                        @Override
                                        public void run() {
                                            super.run();
                                            try {

                                                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                                                for (int i = 0; i < img_list.size(); i++) {
                                                    String path = response.body().get(i).getImage();
                                                    out.writeUTF(to_authNum + "@" + "image" + "@" + path + "@"  + String.valueOf(img_list.size())+"@"+ getAuthNum() + "@" + post_authNum + "@" + room_auth);
                                                }

                                                mHandler.post(new msgUpdate(sendmsg));

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }.start();

                                }
                            }

                            @Override
                            public void onFailure(Call<List<ChatImage>> call, Throwable t) {
                                Log.d(TAG, t.getMessage());
                            }
                        });

                    }
                }
            }
        }

    }

    public void LoadMoreMessage(Retrofit_chatting retrofit_chatting, String from_authNum, String to_authNum, String room_auth, String post_authNum, String count) {
        recyclerView = (RecyclerView) findViewById(R.id.chatting_rv_field);

        Call<List<Message>> load = retrofit_chatting.load_more_message(from_authNum,to_authNum,room_auth,post_authNum,count);
        load.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    for (int i = 0; i < response.body().size(); i++) {
                        String idx = response.body().get(i).getIdx();
                        String userImage = response.body().get(i).getUserImage();
                        String content = response.body().get(i).getContent();
                        String created = response.body().get(i).getCreated();
                        int viewtype = response.body().get(i).getViewType();
                        String path = response.body().get(i).getPath();
                        list.add(new Message(idx,null,null,created,content,userImage,viewtype,path));
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {

            }
        });


    }
    private String getAbsolutePath(Uri uri) {

        String result;
        Cursor cursor = getContentResolver().query(uri,null,null,null,null);
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

   private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            data = intent.getStringExtra("message");
            String[] filt = data.split("@");
            String stDate = null;

            if(filt[1].equals("image") && filt[4].equals(to_authNum)) {
                recyclerView = (RecyclerView) findViewById(R.id.chatting_rv_field);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    LocalDateTime localDateTime = LocalDateTime.now();
                    stDate = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                }
                list.add(0,new Message(null,null,null,stDate,null,userImage,1,filt[2]));

                adapter = new MessageAdapter(Chatting.this, list);
                manager = new LinearLayoutManager(Chatting.this,LinearLayoutManager.VERTICAL,true);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(manager);

                recyclerView.scrollToPosition(0);

            } else if(filt[2].equals(to_authNum)) {
                recyclerView = (RecyclerView) findViewById(R.id.chatting_rv_field);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    LocalDateTime localDateTime = LocalDateTime.now();
                    stDate = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                }


                list.add(0,new Message(null,null,null,stDate,filt[1],userImage,1,null));

                adapter = new MessageAdapter(Chatting.this, list);
                manager = new LinearLayoutManager(Chatting.this,LinearLayoutManager.VERTICAL,true);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(manager);

                recyclerView.scrollToPosition(0);
            }
            input_message.setText("");

        }
    };


    class msgUpdate implements Runnable {
        private String msg;

        public msgUpdate(String str) {
            this.msg = str;
        }

        @Override
        public void run() {

            recyclerView = (RecyclerView) findViewById(R.id.chatting_rv_field);
            String stDate = null;
            LocalDateTime localDateTime = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                localDateTime = LocalDateTime.now();
                stDate = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }



            list.add(0,new Message(null,null,null,stDate,msg,userImage,0,null));
            System.out.println("리스트 추가됨.");
            adapter = new MessageAdapter(Chatting.this, list);

            manager = new LinearLayoutManager(Chatting.this,LinearLayoutManager.VERTICAL,true);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(manager);

            recyclerView.scrollToPosition(0);

            input_message.setText("");

        }
    }

    private void generateMessageList(List<Message> list) {
        recyclerView = (RecyclerView) findViewById(R.id.chatting_rv_field);

        adapter = new MessageAdapter(Chatting.this, list);
        manager = new LinearLayoutManager(Chatting.this,LinearLayoutManager.VERTICAL,true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
    }


    private Retrofit getApiClient() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_ADDRESS)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        return retrofit;
    }

    public String getAuthNum() {
        SharedPreferences prefs = getSharedPreferences("Username",MODE_PRIVATE);

        Collection<?> col_val = prefs.getAll().values();
        Iterator<?> it_val = col_val.iterator();

        String value = (String) it_val.next();

        try {
            JSONObject jsonObject = new JSONObject(value);

            from_authNum = jsonObject.getString("authNum");
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return from_authNum;
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

    @Override
    protected void onPause() {
        super.onPause();

        Intent service = new Intent(Chatting.this, ChattingService.class);
        service.putExtra("room_auth", "X");
        startService(service);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        stopService(new Intent(Chatting.this,ChattingService.class));
    }

}