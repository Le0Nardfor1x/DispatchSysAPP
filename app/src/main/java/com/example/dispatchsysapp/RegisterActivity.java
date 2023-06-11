package com.example.dispatchsysapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        EditText registerAccountEdit = findViewById(R.id.register_account_edit);
        EditText registerPasswordEdit = findViewById(R.id.register_password_edit);

        //注册
        Button registerSecondButton = findViewById(R.id.register_second_button);
        registerSecondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRegisterInfo(registerAccountEdit.getText().toString(),
                        registerPasswordEdit.getText().toString());
            }
        });

        //返回登录界面
        Button registerBackLoginButton = findViewById(R.id.register_back_login_button);
        registerBackLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void sendRegisterInfo(String username,String password){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                String url = "http://111.230.44.64:8081/api/user/register";
                JSONObject obj = new JSONObject();
                try {
                    obj.put("username",username);
                    obj.put("password",password);
                    obj.put("phoneNumber",110);
                    obj.put("email","123@qq.com");
                    obj.put("sex",0);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                MediaType type = MediaType.parse("application/json;charset=utf-8");
                RequestBody requestBody = RequestBody.create(type,""+obj.toString());

                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder().url(url).post(requestBody).build();
                Call call = okHttpClient.newCall(request);
                try {
                    Response response = call.execute();
                    String str=response.body().string();
                    //Log.d("str",str);
                }catch (Exception e){
                    e.printStackTrace();
                }

                Looper.loop();
            }
        }).start();
        Toast.makeText(getBaseContext(), "注册成功", Toast.LENGTH_LONG).show();
    }
}
