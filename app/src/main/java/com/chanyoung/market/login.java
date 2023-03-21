package com.chanyoung.market;

import static android.content.ContentValues.TAG;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class login extends Activity {
    Button verification;
    Button start;

    EditText input_phone;
    EditText input_verification;

    TextView lost;
    TextView link;

    String conversionTime = "000500";

    static final int SMS_SEND_PERMISSION = 1;

    public static String SERVER_ADDRESS = "http://3.36.34.173/";

    SavedSharedPreferences prefs = new SavedSharedPreferences();
    SavedSharedPreferences prefs2 = new SavedSharedPreferences();

    /*
    OnCreate
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

          /*
        문자 전송 권한 설정
         */
        int permissionCheck = ContextCompat.checkSelfPermission(login.this, Manifest.permission.SEND_SMS);

        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(login.this,Manifest.permission.SEND_SMS)) {
                Toast.makeText(getApplicationContext(), "SMS 권한을 확인해주세요.",Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(login.this, new String[]{Manifest.permission.SEND_SMS}, SMS_SEND_PERMISSION);
        }
        /*
        문자 전송 권한 설정
         */


        lost = (TextView) findViewById(R.id.login_lost);
        link = (TextView) findViewById(R.id.login_find);

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this, email_verification.class);
                startActivity(intent);
            }
        });

        start = (Button) findViewById(R.id.login_start);

        input_phone = (EditText) findViewById(R.id.login_phone);

        input_verification = (EditText) findViewById(R.id.verification_input);

        Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(SERVER_ADDRESS)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Retrofit_verification php = retrofit.create(Retrofit_verification.class);

        verification = (Button) findViewById(R.id.login_verification);
        verification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (input_phone.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(),"전화번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (input_phone.getText().toString().length() < 11 ) {
                    Toast.makeText(getApplicationContext(),"전화번호를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    verification.setText("인증문자 다시 받기");
                    lost.setVisibility(View.GONE);
                    link.setVisibility(View.GONE);
                    start.setVisibility(View.VISIBLE);
                    input_verification.setVisibility(View.VISIBLE);
                    countDown(conversionTime);
                    verification.setClickable(false);
                    Toast.makeText(getApplicationContext(),"인증번호가 전송 됐습니다.",Toast.LENGTH_SHORT).show();

                    String phone = input_phone.getText().toString();

                    Call<String> call = php.InsertPhone(phone);

                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if(response.isSuccessful()) {
                                String result = response.body();
                                System.out.println(response.body());
                                start.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(login.this, MainActivity.class);

                                        if(input_verification.getText().toString().equals("")) {
                                            Toast.makeText(getApplicationContext(), "인증번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                                        } else if (input_verification.getText().toString().length() < 6) {
                                            Toast.makeText(getApplicationContext(),"인증번호 6자리를 모두 입력해주세요.",Toast.LENGTH_SHORT).show();
                                        } else if (verification.getText().toString().equals("인증문자 다시 받기 (00분00초)")) {
                                            Toast.makeText(getApplicationContext(),"인증번호 시간이 만료되었습니다.",Toast.LENGTH_SHORT).show();
                                        }

                                        if (input_verification.getText().toString().equals(result)) {
                                            Toast.makeText(login.this,"인증이 완료 되었습니다.",Toast.LENGTH_SHORT).show();

                                            Retrofit_login retrofit_login = retrofit.create(Retrofit_login.class);

                                            Call<String> call2 = retrofit_login.login(phone);
                                            call2.enqueue(new Callback<String>() {
                                                @Override
                                                public void onResponse(Call<String> call, Response<String> response) {
                                                    System.out.println(response.body());
                                                    if(response.isSuccessful()) {
                                                        if(response.body().equals("O")) {

                                                            Toast.makeText(login.this, "등록된 회원 정보가 없습니다!", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(login.this,signup.class);
                                                            startActivity(intent);
                                                            finish();
                                                            System.out.println(response.body());

                                                        } else {
                                                            Toast.makeText(login.this,"환영합니다!", Toast.LENGTH_SHORT).show();
                                                            System.out.println(response.body());

                                                            long now = System.currentTimeMillis();
                                                            Date mDate = new Date(now);
                                                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                                            String getTime = simpleDateFormat.format(mDate).toString();

                                                            String save_form = "{\"authNum\":\""+response.body()+"\"}";
                                                            String save_form_progress = "{\"progress\":\""+ 5 +"\"}";
                                                            prefs2.clearSavedState(login.this,"savedState");
                                                            prefs2.save_state(login.this, "savedState",save_form_progress);



                                                            prefs.clearUserName(login.this,"Username");
                                                            prefs.setUserName(login.this,getTime,save_form);


                                                            startActivity(intent);
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<String> call, Throwable t) {
                                                    t.getMessage();
                                                    System.out.println(t.getMessage());
                                                }
                                            });

//                                            startActivity(intent);
//                                            finish();
                                        } else {
                                            Toast.makeText(login.this,"인증번호를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                            } else {
                                System.out.println(response.body());
                                Log.d(TAG,response + "실패");
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            t.printStackTrace();
                            Log.d(TAG,"onFailure : " + t.getMessage());
                        }
                    });


                }
            }
        });


    }
    /*
    OnCreate
     */

    public void countDown(String time) {

        long conversionTime = 0;

        verification = (Button) findViewById(R.id.login_verification);
        // 1000 단위가 1초
        // 60000 단위가 1분
        // 60000 * 3600 = 1시간

        String getHour = time.substring(0, 2);
        String getMin = time.substring(2, 4);
        String getSecond = time.substring(4, 6);

        // "00"이 아니고, 첫번째 자리가 0 이면 제거
        if (getHour.substring(0, 1) == "0") {
            getHour = getHour.substring(1, 2);
        }

        if (getMin.substring(0, 1) == "0") {
            getMin = getMin.substring(1, 2);
        }

        if (getSecond.substring(0, 1) == "0") {
            getSecond = getSecond.substring(1, 2);
        }

        // 변환시간
        conversionTime = Long.valueOf(getHour) * 1000 * 3600 + Long.valueOf(getMin) * 60 * 1000 + Long.valueOf(getSecond) * 1000;

        // 첫번쨰 인자 : 원하는 시간 (예를들어 30초면 30 x 1000(주기))
        // 두번쨰 인자 : 주기( 1000 = 1초)
        new CountDownTimer(conversionTime, 1000) {

            // 특정 시간마다 뷰 변경
            public void onTick(long millisUntilFinished) {

                // 시간단위
                String hour = String.valueOf(millisUntilFinished / (60 * 60 * 1000));

                // 분단위
                long getMin = millisUntilFinished - (millisUntilFinished / (60 * 60 * 1000)) ;
                String min = String.valueOf(getMin / (60 * 1000)); // 몫

                // 초단위
                String second = String.valueOf((getMin % (60 * 1000)) / 1000); // 나머지

                // 밀리세컨드 단위
                String millis = String.valueOf((getMin % (60 * 1000)) % 1000); // 몫

                // 시간이 한자리면 0을 붙인다
                if (hour.length() == 1) {
                    hour = "0" + hour;
                }

                // 분이 한자리면 0을 붙인다
                if (min.length() == 1) {
                    min = "0" + min;
                }

                // 초가 한자리면 0을 붙인다
                if (second.length() == 1) {
                    second = "0" + second;
                }

                verification.setText("인증문자 다시 받기"+ " (" + min + "분" + second + "초" + ")");
            }

            // 제한시간 종료시
            public void onFinish() {

                // TODO : 타이머가 모두 종료될때 어떤 이벤트를 진행할지
                verification.setClickable(true);

            }
        }.start();

    }



}