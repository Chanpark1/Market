package com.chanyoung.market;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class signup extends AppCompatActivity {

    Button verification;
    Button start;

    EditText input_phone;
    EditText input_verification;

    TextView lost;
    TextView link;

    String conversionTime = "000500";

    public static final String SERVER_ADDRESS = "http://3.36.34.173/";
    SavedSharedPreferences prefs = new SavedSharedPreferences();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        
        lost = (TextView) findViewById(R.id.signup_lost);
        link = (TextView) findViewById(R.id.signup_find);

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(signup.this,email_verification.class);
                startActivity(i);
                finish();
            }
        });
        start = (Button) findViewById(R.id.signup_start);
        input_verification = (EditText) findViewById(R.id.signup_verification_input);

        input_phone = (EditText) findViewById(R.id.signup_phone);

        verification = (Button) findViewById(R.id.signup_verification);

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

        Retrofit_verification php = retrofit.create(Retrofit_verification.class);

        verification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(input_phone.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(),"전화번호를 입력 해주세요.",Toast.LENGTH_SHORT).show();
                } else if (input_phone.getText().toString().length() < 11) {
                    Toast.makeText(signup.this, "전화번호를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    verification.setText("인증문자 다시 받기");
                    lost.setVisibility(View.GONE);
                    link.setVisibility(View.GONE);
                    start.setVisibility(View.VISIBLE);
                    input_verification.setVisibility(View.VISIBLE);
                    countDown(conversionTime);
                    Toast.makeText(signup.this, "인증번호가 전송 됐습니다.", Toast.LENGTH_SHORT).show();
                    String phone = input_phone.getText().toString();
                    Call<String> call = php.InsertPhone(phone);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if(response.isSuccessful()) {
                                String result = response.body();
                                System.out.println("인증번호 : " + response.body());


                                start.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(signup.this,signup_profile.class);
                                        if(input_verification.getText().toString().equals("")) {
                                            Toast.makeText(getApplicationContext(), "인증번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                                        } else if (input_verification.getText().toString().length() < 6) {
                                            Toast.makeText(getApplicationContext(),"인증번호 6자리를 모두 입력해주세요.",Toast.LENGTH_SHORT).show();
                                        } else if (verification.getText().toString().equals("인증문자 다시 받기 (00분00초)")) {
                                            Toast.makeText(getApplicationContext(),"인증번호 시간이 만료되었습니다.",Toast.LENGTH_SHORT).show();
                                        }


                                        if (input_verification.getText().toString().equals(result)) {
                                            Toast.makeText(signup.this, "인증이 완료 되었습니다.", Toast.LENGTH_SHORT).show();

                                            String phone = input_phone.getText().toString();

                                            Retrofit_login retrofit_login =  retrofit.create(Retrofit_login.class);

                                            Call<String> call1 = retrofit_login.login(phone);
                                            call1.enqueue(new Callback<String>() {
                                                @Override
                                                public void onResponse(Call<String> call, Response<String> response) {
                                                    if(response.isSuccessful() && response.body() != null) {
                                                       if(response.body().equals("O")) {
                                                            Toast.makeText(signup.this, "가입된 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                                                            System.out.println(response.body());

                                                            Intent getIntent = getIntent();
                                                            intent.putExtra("phone",phone);
                                                            intent.putExtra("area",getIntent.getStringExtra("area"));
                                                            intent.putExtra("longitude",getIntent.getStringExtra("longitude"));
                                                            intent.putExtra("latitude",getIntent.getStringExtra("latitude"));
                                                            startActivity(intent);
                                                            finish();
                                                        } else {

                                                               Toast.makeText(signup.this, "이미 가입 된 회원입니다.", Toast.LENGTH_SHORT).show();
                                                               System.out.println(response.body());
                                                               Intent intent1 = new Intent(signup.this,MainActivity.class);

                                                           long now = System.currentTimeMillis();
                                                           Date mDate = new Date(now);
                                                           SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                                           String getTime = simpleDateFormat.format(mDate).toString();

                                                           String save_form = "{\"authNum\":\""+response.body()+"\"}";


                                                           prefs.clearUserName(signup.this,"Username");
                                                           prefs.setUserName(signup.this,getTime,save_form);


                                                               startActivity(intent1);
                                                               finish();
                                                       }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<String> call, Throwable t) {
                                                    System.out.println(response.body());

                                                }
                                            });

                                            intent.putExtra("phone",phone);

                                        } else {
                                            Toast.makeText(signup.this, "인증번호를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(signup.this, "인증번호 전송 실패", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
                }
            }
        });


    }

    public void countDown(String time) {

        long conversionTime = 0;

        verification = (Button) findViewById(R.id.signup_verification);
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


                }


            }.start();



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

    public void StopCountDown(String time) {

        long conversionTime = 0;

        verification = (Button) findViewById(R.id.signup_verification);
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


            }


        }.cancel();



    }


}

