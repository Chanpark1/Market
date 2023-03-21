package com.chanyoung.market;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Collection;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    Fragment frag_main;
    Fragment frag_community;
    Fragment frag_chatting;
    Fragment frag_profile;
    public static Intent ServiceIntent = null;
    public String authNum;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frag_main = new frag_main();
        frag_community = new frag_community();
        frag_chatting = new frag_chatting();
        frag_profile = new frag_profile();

        bottomNavigationView = findViewById(R.id.main_nav_view);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,frag_main).commitAllowingStateLoss();

        if(ServiceIntent == null) {
            ServiceIntent = new Intent(MainActivity.this,ChattingService.class)
                    .setAction(Intent.ACTION_MAIN)
                    .addCategory(Intent.CATEGORY_LAUNCHER)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(ServiceIntent);
                ServiceIntent = null;

            }
        }

        RebootBroadcastReceiver reboot_receiver = new RebootBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BOOT_COMPLETED);
        registerReceiver(reboot_receiver,intentFilter);
        // 이렇게 된다면 MainActivity 에 진입 할 때마다 startService 가  호출 되는데, 서비스 자체는 중복으로 실행이 되지 않으니 상관 없을듯?
        // 근데 onStartCommand 가 중복 실행 된다는건 계속 에코서버 유저 리스트에 추가가 된 다는 말이기 때문에 에코서버에서 예외처리를 해줘야 한다.

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.home :
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,frag_main).commitAllowingStateLoss();
                        return true;

                    case R.id.community :
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,frag_community).commitAllowingStateLoss();
                        return true;

                    case R.id.chat :
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,frag_chatting).commitAllowingStateLoss();
                        return true;

                    case R.id.profile :
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,frag_profile).commitAllowingStateLoss();
                        return true;
                }
                return false;
            }
        });

    }

    public String getAuthNum() {
        SharedPreferences prefs = getSharedPreferences("Username",MODE_PRIVATE);

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

}