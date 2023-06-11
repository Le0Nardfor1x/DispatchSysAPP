package com.example.dispatchsysapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private  String token;
    private  int code;
    private  String message;

    private String role;

    private static final  String[] APP_PERMISSIONS = {android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.ACCESS_WIFI_STATE,
            android.Manifest.permission.CHANGE_WIFI_STATE,
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
    };
    private static final int  PERMISSIONS_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        requestPermissions();



        //用户注册
        Button registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        //用户登录
        EditText loginAccountEditText = findViewById(R.id.login_account_edit_text);
        EditText loginPasswordEditText = findViewById(R.id.login_password_edit_text);
        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendLoginRequest(loginAccountEditText.getText().toString(),
                        loginPasswordEditText.getText().toString());
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }



                if(code==200){
                    Toast.makeText(getBaseContext(), "登录成功", Toast.LENGTH_LONG).show();
                    if(role.equals("02")){
                        Intent intent = new Intent(LoginActivity.this,
                                EngineerManageActivity.class);
                        intent.putExtra("token",token);
                        startActivity(intent);
                    }
                    if(role.equals("01")){
                        Intent intent = new Intent(LoginActivity.this,ReportFaultActivity.class);
                        intent.putExtra("token",token);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(getBaseContext(),message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void sendLoginRequest(String username, String password){
        new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                Looper.prepare();
                String url = "http://125.216.247.120:8081/api/user/login";
                JSONObject obj = new JSONObject();
                try {
                    obj.put("userName",username);
                    obj.put("password",password);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                MediaType type = MediaType.parse("application/json;charset=utf-8");
                RequestBody requestBody = RequestBody.create(type,""+ obj.toString());

                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder().url(url).post(requestBody).build();
                Call call = okHttpClient.newCall(request);
                try {
                    Response response = call.execute();
                    String str=response.body().string();
                    Log.d("OkHttp",str);

                    JSONObject jsonObject = new JSONObject(str);
                    int subCode = jsonObject.getInt("code");
                    code = subCode;
                    //Log.d("code",code);

                    if(subCode == 200){
                        String data = jsonObject.getString("data");
                        JSONObject jsonObject1 = new JSONObject(data);
                        String subToken = jsonObject1.getString("token");
                        token = subToken;
                        String subRole = jsonObject1.getString("role");
                        role = subRole;
                    }else{
                        String subMessage = jsonObject.getString("msg");
                        message = subMessage;

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                Looper.loop();
            }
        }).start();
    }


    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, APP_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSIONS_REQUEST_CODE:
                //权限请求失败
                if (grantResults.length == APP_PERMISSIONS.length){
                    for(int result : grantResults){
                        if(result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this, "请求权限被拒绝", Toast.LENGTH_LONG).show();
                            break;
                        }
                    }
                } else {
                    Toast.makeText(this, "已授权", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}