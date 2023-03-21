package com.chanyoung.market;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class change_phone extends AppCompatActivity {

    Button get_verification;
    Button start;

    EditText input_phone;
    EditText input_verification;

    private static final String SERVER_ADDRESS = "http://3.36.34.173/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone);

        input_phone = (EditText) findViewById(R.id.change_input_phone);
        input_verification = (EditText) findViewById(R.id.change_input_verification);

        get_verification = (Button) findViewById(R.id.change_verification_button);
        start = (Button) findViewById(R.id.change_start);

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

        get_verification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (input_phone.getText().toString().equals("")) {
                    Toast.makeText(change_phone.this, "휴대전화 번호를 입력 해주세요.", Toast.LENGTH_SHORT).show();
                } else if (input_phone.getText().toString().length() < 11) {
                    Toast.makeText(change_phone.this, "휴대전화 형식을 확인 해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(change_phone.this, "인증번호가 전송 되었습니다.", Toast.LENGTH_SHORT).show();
                    input_verification.setVisibility(View.VISIBLE);
                    start.setVisibility(View.VISIBLE);

                    String phoneNum = input_phone.getText().toString();

                    String verification = input_verification.getText().toString();

                    Retrofit_phone retrofit_phone = retrofit.create(Retrofit_phone.class);

                    Call<String> call = retrofit_phone.insertPhone(phoneNum);

                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            if(response.isSuccessful()) {
                                if(verification.equals("")) {
                                    Toast.makeText(change_phone.this, "인증번호를 입력 해주세요.", Toast.LENGTH_SHORT).show();
                                } else if (verification.length() < 6 ) {
                                    Toast.makeText(change_phone.this, "인증번호를 전부 입력 해주세요.", Toast.LENGTH_SHORT).show();
                                } else if(!verification.equals(response.body())) {
                                    Toast.makeText(change_phone.this, "인증번호를 확인 해주세요.", Toast.LENGTH_SHORT).show();
                                } else if (verification.equals(response.body())) {
                                    Toast.makeText(change_phone.this, "인증이 완료 되었습니다.", Toast.LENGTH_SHORT).show();

                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            t.getMessage();
                        }
                    });
                }
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(change_phone.this, login.class);
                Intent getIntent = getIntent();

                String email = getIntent.getStringExtra("email");
                String phone = input_phone.getText().toString();
                startActivity(intent);
                finish();

                Retrofit_change_phone retrofit_change_phone = retrofit.create(Retrofit_change_phone.class);

                Call<String> call1 = retrofit_change_phone.updatePhone(phone,email);
                call1.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        System.out.println(response.body());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        System.out.println(t.getMessage());
                    }
                });
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

}