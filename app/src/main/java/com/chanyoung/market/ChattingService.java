package com.chanyoung.market;

import static android.app.Service.START_STICKY;
import static android.content.ContentValues.TAG;

import static com.chanyoung.market.Chatting.IP;
import static com.chanyoung.market.Chatting.PORT;
import static com.chanyoung.market.signup.SERVER_ADDRESS;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.Provider;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ChattingService extends Service {
// 서비스에서 메서드를 호출할 때 FLAG_ACTIVITY_NEW_TASK 를 추가해줘야 함. 서비스는 화면이 없기 때문에 액티비티를 띄우려면 새로운 태스크를 만들어야 하기 때문.

    String read;
    String sendmsg;
    Socket socket;
    Handler mHandler;

    String data;
    String isRead;
    public String my_authNum;
    public String from_authNum;

    NotificationManager manager;
    Notification notification;

    public String room_auth;

    public String get_time;
    SavedSharedPreferences preferences = new SavedSharedPreferences();
    boolean dup = false;
    int id = 0;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    RebootBroadcastReceiver broadcastReceiver = new RebootBroadcastReceiver();
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Chatting Service" , "onCreate");

        System.out.println("dup :   "  + dup);



        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(ChattingService.this, "111")
                .setContentTitle("")
                .setContentText("")
                .setSmallIcon(R.drawable.ic_baseline_account_circle_24)
                .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE)
                .build();

        startForeground(111, notification);

        Toast.makeText(this,"startForeground Called", Toast.LENGTH_SHORT).show();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mHandler = new Handler();
        Retrofit_chatting retrofit_chatting = getApiClient().create(Retrofit_chatting.class);
        Call<String> call = retrofit_chatting.get_count(getAuthNum());

        Intent s_intent = new Intent(ChattingService.this, MainActivity.class);

        PendingIntent pending = PendingIntent.getActivity(ChattingService.this, (int) System.currentTimeMillis(),s_intent,  PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful() && response.body() != null) {
                    createNotificationChannel();

                    NotificationCompat.Builder builder =
                            new NotificationCompat.Builder(ChattingService.this,"noti")
                                    .setContentTitle("채팅 알림")
                                    .setContentText(response.body() + "개의 메시지가 있습니다.")
                                    .setSmallIcon(R.drawable.ic_baseline_account_circle_24)
                                    .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE)
                                    .setContentIntent(pending)
                                    .setAutoCancel(true);


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel("noti", "chat", NotificationManager.IMPORTANCE_HIGH);

                        channel.enableVibration(true);
                        channel.enableLights(true);
                        channel.setDescription("set");


                        //노티 ID를 개별 값으로, Channel ID 를 같게 해야 알림을 모두 확인할 수 있따.

                        assert manager != null;
                        manager.createNotificationChannel(channel);

                        manager.notify(id, builder.build());
                    }




                } else if (response.body() == null || response.body().equals("0")) {
                    createNotificationChannel();
                    Notification notification = new NotificationCompat.Builder(ChattingService.this, "111")
                            .setContentTitle("")
                            .setContentText("")
                            .setSmallIcon(R.drawable.ic_baseline_account_circle_24)
                            .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE)
                            .build();

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

        Thread main_thread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    // DataOutputStream 을 통해 문자열이 에코서버로 전송된다 AMAZING
                    InetAddress serverAddr = InetAddress.getByName(IP);
                    socket = new Socket(serverAddr,PORT); // -> 채팅 서버에 접속 (연결, 에코서버 IP와 포트번호)
                    // 로컬호스트 IP 주소와 포트번호를 인자로 받아서 소켓을 create 하면 해당 에코 서버의 서버소켓과 자동으로 연결이 된다.
                    // 에코서버 IP와 포트번호를 인자로 받아 새로운 소켓을 생성한다.

                    // ^PrintWriter 를 생성해서 출력 스트림을 달아줘서 텍스트를 보낸다.
                    if(!dup) {
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());


                        out.writeUTF(getAuthNum() + "@" + "auth");
                        System.out.println("Chatting Service writeUTF : " + getAuthNum());

                        dup = true;
                    }
                    DataInputStream input = null;

                    room_auth = intent.getStringExtra("room_auth");
                    System.out.println("서비스에서 받은 room pk 값 입니다 : " + room_auth);
                    input = new DataInputStream(socket.getInputStream());

                    while(input != null) {
                        read = input.readUTF();
                        String[] filt = read.split("@");

                        System.out.println("결과값" + read);

                        if(read != null) {
                            ComponentName componentName = null;
                            String Activity = null;
                            if(filt[0].equals(my_authNum)) {
                                if (filt[1].equals("image")) {
                                    int count = Integer.parseInt(filt[3]);

                                    for (int i = 0; i < count; i++) {
                                        data = read;
                                        sendMessage(data);
                                    }
                                    Intent n_intent = new Intent(ChattingService.this, Chatting.class);
                                    PendingIntent pending = PendingIntent.getActivity(ChattingService.this, (int) System.currentTimeMillis(),n_intent,  PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
                                    manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                    NotificationCompat.Builder builder =
                                            new NotificationCompat.Builder(ChattingService.this,"noti")
                                                    .setSmallIcon(R.drawable.ic_baseline_account_circle_24)
                                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                    .setContentTitle("채팅 알림")
                                                    .setContentIntent(pending)
                                                    .setContentText("사진")
                                                    .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE)
                                                    .setAutoCancel(true);


                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        NotificationChannel channel = new NotificationChannel("noti","chat",NotificationManager.IMPORTANCE_HIGH);

                                        channel.enableVibration(true);
                                        channel.enableLights(true);
                                        channel.setDescription("set");

                                        //노티 ID를 개별 값으로, Channel ID 를 같게 해야 알림을 모두 확인할 수 있따.

                                        assert manager != null;
                                        manager.createNotificationChannel(channel);

                                        manager.notify(id,builder.build());
//                                    startForeground(1234,builder.build());
                                        id++;
                                    }

                                } else {

                                    data = read;
                                    sendMessage(data);

                                    Intent n_intent = new Intent(ChattingService.this, Chatting.class);
                                    ActivityManager acManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                                    List<ActivityManager.RunningTaskInfo> info = acManager.getRunningTasks(1);
                                    if(info.size() != 0) {
                                        componentName = info.get(0).topActivity;
                                        Activity = componentName.getShortClassName().substring(1);

                                        if(!Activity.equals("Chatting") && !filt[4].equals(room_auth)) {
//                                out.writeUTF(to_authNum + "@" + sendmsg + "@" + getAuthNum() + "@" + post_authNum + "@" + room_auth);
                                            n_intent.putExtra("authNum",filt[2]);
                                            n_intent.putExtra("post_authNum",filt[3]);
                                            n_intent.putExtra("room_auth",filt[4]);

                                            System.out.println("서비스에서 보냄  to AUTH : " + filt[2] + " AND  POST : "+ filt[3] + " AND " + filt[4]);
                                            // 1, 2, 3번 어떤 알림을 선택해도 1번 채팅방으로 가버리는 문제 때문에 id는 currentTimeMillis 로 해준다.

                                            PendingIntent pending = PendingIntent.getActivity(ChattingService.this, (int) System.currentTimeMillis(),n_intent,  PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
                                            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                            NotificationCompat.Builder builder =
                                                    new NotificationCompat.Builder(ChattingService.this,"noti")
                                                            .setSmallIcon(R.drawable.ic_baseline_account_circle_24)
                                                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                            .setContentTitle("채팅 알림")
                                                            .setContentIntent(pending)
                                                            .setContentText(filt[1])
                                                            .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE)
                                                            .setAutoCancel(true);


                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                NotificationChannel channel = new NotificationChannel("noti","chat",NotificationManager.IMPORTANCE_HIGH);

                                                channel.enableVibration(true);
                                                channel.enableLights(true);
                                                channel.setDescription("set");

                                                //노티 ID를 개별 값으로, Channel ID 를 같게 해야 알림을 모두 확인할 수 있따.

                                                assert manager != null;
                                                manager.createNotificationChannel(channel);

                                                manager.notify(id,builder.build());
//                                    startForeground(1234,builder.build());
                                                id++;
                                            }
                                        } else if (Activity.equals("Chatting") && !filt[4].equals(room_auth)) {
                                            n_intent.putExtra("authNum",filt[2]);
                                            n_intent.putExtra("post_authNum",filt[3]);
                                            n_intent.putExtra("room_auth",filt[4]);

                                            System.out.println("서비스에서 보냄  to AUTH : " + filt[2] + " AND  POST : "+ filt[3] + " AND " + filt[4]);
                                            // 1, 2, 3번 어떤 알림을 선택해도 1번 채팅방으로 가버리는 문제 때문에 id는 currentTimeMillis 로 해준다.

                                            PendingIntent pending = PendingIntent.getActivity(ChattingService.this, (int) System.currentTimeMillis(),n_intent,  PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
                                            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                            NotificationCompat.Builder builder =
                                                    new NotificationCompat.Builder(ChattingService.this,"noti")
                                                            .setSmallIcon(R.drawable.ic_baseline_account_circle_24)
                                                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                            .setContentTitle("채팅 알림")
                                                            .setContentIntent(pending)
                                                            .setContentText(filt[1])
                                                            .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE)
                                                            .setAutoCancel(true);


                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                NotificationChannel channel = new NotificationChannel("noti","chat",NotificationManager.IMPORTANCE_HIGH);

                                                channel.enableVibration(true);
                                                channel.enableLights(true);
                                                channel.setDescription("set");


                                                //노티 ID를 개별 값으로, Channel ID 를 같게 해야 알림을 모두 확인할 수 있따.

                                                assert manager != null;
                                                manager.createNotificationChannel(channel);

                                                manager.notify(id,builder.build());
//                                    startForeground(1234,builder.build());
                                                id++;
                                            }
                                        } else if (!Activity.equals("Chatting") && room_auth.equals("X")) {
                                            n_intent.putExtra("authNum",filt[2]);
                                            n_intent.putExtra("post_authNum",filt[3]);
                                            n_intent.putExtra("room_auth",filt[4]);

                                            System.out.println("서비스에서 보냄  to AUTH : " + filt[2] + " AND  POST : "+ filt[3] + " AND " + filt[4]);
                                            // 1, 2, 3번 어떤 알림을 선택해도 1번 채팅방으로 가버리는 문제 때문에 id는 currentTimeMillis 로 해준다.

                                            PendingIntent pending = PendingIntent.getActivity(ChattingService.this, (int) System.currentTimeMillis(),n_intent,  PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
                                            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                            NotificationCompat.Builder builder =
                                                    new NotificationCompat.Builder(ChattingService.this,"noti")
                                                            .setSmallIcon(R.drawable.ic_baseline_account_circle_24)
                                                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                            .setContentTitle("채팅 알림")
                                                            .setContentIntent(pending)
                                                            .setContentText(filt[1])
                                                            .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE)
                                                            .setAutoCancel(true);


                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                NotificationChannel channel = new NotificationChannel("noti","chat",NotificationManager.IMPORTANCE_HIGH);

                                                channel.enableVibration(true);
                                                channel.enableLights(true);
                                                channel.setDescription("set");


                                                //노티 ID를 개별 값으로, Channel ID 를 같게 해야 알림을 모두 확인할 수 있따.

                                                assert manager != null;
                                                manager.createNotificationChannel(channel);

                                                manager.notify(id,builder.build());
//                                    startForeground(1234,builder.build());
                                                id++;
                                            }
                                        }
                                    } else if(componentName == null) {
                                        n_intent.putExtra("authNum",filt[2]);
                                        n_intent.putExtra("post_authNum",filt[3]);
                                        n_intent.putExtra("room_auth",filt[4]);

                                        System.out.println("서비스에서 보냄  to AUTH : " + filt[2] + " AND  POST : "+ filt[3] + " AND " + filt[4]);
                                        // 1, 2, 3번 어떤 알림을 선택해도 1번 채팅방으로 가버리는 문제 때문에 id는 currentTimeMillis 로 해준다.

                                        PendingIntent pending = PendingIntent.getActivity(ChattingService.this, (int) System.currentTimeMillis(),n_intent,  PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
                                        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                        NotificationCompat.Builder builder =
                                                new NotificationCompat.Builder(ChattingService.this,"noti")
                                                        .setSmallIcon(R.drawable.ic_baseline_account_circle_24)
                                                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                        .setContentTitle("채팅 알림")
                                                        .setContentIntent(pending)
                                                        .setContentText(filt[1])
                                                        .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE)
                                                        .setAutoCancel(true);


                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            NotificationChannel channel = new NotificationChannel("noti","chat",NotificationManager.IMPORTANCE_HIGH);

                                            channel.enableVibration(true);
                                            channel.enableLights(true);
                                            channel.setDescription("set");


                                            //노티 ID를 개별 값으로, Channel ID 를 같게 해야 알림을 모두 확인할 수 있따.

                                            assert manager != null;
                                            manager.createNotificationChannel(channel);

                                            manager.notify(id,builder.build());
//                                    startForeground(1234,builder.build());
                                            id++;
                                        }
                                    }
                                }






                            }
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };

        main_thread.start();


        registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_SHUTDOWN));


        return START_STICKY;
    }


    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("111","StartChannel",NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }
    }


    public String getAuthNum() {

        SharedPreferences prefs = getSharedPreferences("Username", MODE_PRIVATE);

        Collection<?> col_val = prefs.getAll().values();
        Iterator<?> it_val = col_val.iterator();

        String value = (String) it_val.next();

        try {
            JSONObject jsonObject = new JSONObject(value);

            my_authNum = jsonObject.getString("authNum");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return my_authNum;

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

    private Retrofit getApiClient() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_ADDRESS)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }



    public void sendMessage(String message) {
        Intent intent = new Intent("data");
        intent.putExtra("message",data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }
    public void setSocket(Socket _socket) {
        socket = _socket;
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Chatting Service Destroyed");
        Toast.makeText(this, "서비스 뒤짐ㅋㅋ", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent("restartService");
        sendBroadcast(intent);
        System.out.println("broad 인텐트 보냄");
        System.out.println("서비스 뒤짐서비스 뒤짐서비스 뒤짐서비스 뒤짐서비스 뒤짐서비스 뒤짐서비스 뒤짐서비스 뒤짐서비스 뒤짐서비스 뒤짐서비스 뒤짐서비스 뒤짐서비스 뒤짐서비스 뒤짐서비스 뒤짐서비스 뒤짐서비스 뒤짐서비스 뒤짐");



        unregisterReceiver(broadcastReceiver);
        dup = false;
//        try {
//            socket.close();
//            Log.d("Socket", "Closed");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }



}
