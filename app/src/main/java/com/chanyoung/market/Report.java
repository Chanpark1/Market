package com.chanyoung.market;

import static android.content.ContentValues.TAG;

import static com.chanyoung.market.signup.SERVER_ADDRESS;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.w3c.dom.Text;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Report extends AppCompatActivity {

    public String post_authNum;
    public String title;

    TextView tv_title;

    CheckBox checkBox1;
    CheckBox checkBox2;
    CheckBox checkBox3;
    CheckBox checkBox4;
    CheckBox checkBox5;

    TextView tv_check1;
    TextView tv_check2;
    TextView tv_check3;
    TextView tv_check4;
    TextView tv_check5;

    EditText editText;

    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        tv_title = (TextView) findViewById(R.id.report_title);

        checkBox1 = (CheckBox) findViewById(R.id.check1);
        checkBox2 = (CheckBox) findViewById(R.id.check2);
        checkBox3 = (CheckBox) findViewById(R.id.check3);
        checkBox4 = (CheckBox) findViewById(R.id.check4);
        checkBox5 = (CheckBox) findViewById(R.id.check5);

        tv_check1 = (TextView) findViewById(R.id.tv_check1);
        tv_check2 = (TextView) findViewById(R.id.tv_check2);
        tv_check3 = (TextView) findViewById(R.id.tv_check3);
        tv_check4 = (TextView) findViewById(R.id.tv_check4);

        editText = (EditText) findViewById(R.id.etc);

        submit = (Button) findViewById(R.id.report_submit);



        post_authNum = getIntent().getStringExtra("post_authNum");
        Retrofit_load_post retrofit_load_post = getApiClient().create(Retrofit_load_post.class);
        Call<String> call = retrofit_load_post.get_post(post_authNum);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful() && response.body() != null) {

                    tv_title.setText(response.body());

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });



    }

    private HttpLoggingInterceptor httpLoggingInterceptor() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d(TAG,message);
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

}