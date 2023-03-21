package com.chanyoung.market;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class email_verification extends AppCompatActivity {

    Button verification_btn;

    Button start;

    EditText input_email;
    EditText input_verification;

    public static final String SERVER_ADDRESS = "http://3.36.34.173/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        input_email = (EditText) findViewById(R.id.find_input_email);
        input_verification = (EditText) findViewById(R.id.find_input_verification);

        verification_btn = (Button) findViewById(R.id.find_verify_button);
        start = (Button) findViewById(R.id.find_start);
        
        verification_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                if(input_email.getText().toString().equals("")) {
                    Toast.makeText(email_verification.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (!input_email.getText().toString().contains("@") || !input_email.getText().toString().contains(".com")) {
                    Toast.makeText(email_verification.this, "이메일 형식을 확인 해주세요", Toast.LENGTH_SHORT).show();
                } else {
                     input_verification.setVisibility(View.VISIBLE);
                     start.setVisibility(View.VISIBLE);
                    Toast.makeText(email_verification.this, "인증번호가 전송 되었습니다.", Toast.LENGTH_SHORT).show();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(SERVER_ADDRESS)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                     Retrofit_email retrofit_email = retrofit.create(Retrofit_email.class);

                     String email = input_email.getText().toString();

                    Call<String> call = retrofit_email.InsertEmail(email);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            if (response.isSuccessful()) {
                                start.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        String authNum = response.body();
                                        
                                        if(input_verification.getText().toString().equals("")) {
                                            Toast.makeText(email_verification.this, "인증번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                                        } else if (input_verification.getText().toString().length() < 6) {
                                            Toast.makeText(email_verification.this, "인증번호를 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                                        } else if (!input_verification.getText().toString().equals(authNum)) {
                                            Toast.makeText(email_verification.this, "인증번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(email_verification.this, "인증이 완료 되었습니다.", Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent(email_verification.this,change_phone.class);

                                            intent.putExtra("email",email);

                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
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

}