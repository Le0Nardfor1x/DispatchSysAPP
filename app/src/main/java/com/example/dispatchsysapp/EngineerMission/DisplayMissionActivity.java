package com.example.dispatchsysapp.EngineerMission;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dispatchsysapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DisplayMissionActivity extends AppCompatActivity {
    private List<EngineerMission> missionList ;
    private String token;

    private String description;
    private String latitude;
    private String longitude;
    private EngineerMission currentMission;
    public  String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_display_mission);

        missionList = new ArrayList<>();
        Intent intent = getIntent();
        token = intent.getStringExtra("token");


        getCurrentMission();
        TextView currentMissionIdText = findViewById(R.id.current_mission_id_text);
        TextView currentMissionDescriptionText = findViewById(R.id.current_mission_description_text);
        TextView currentMissionLocationText = findViewById(R.id.current_mission_location_text);
        currentMissionIdText.setText(currentMission.getId());
        currentMissionDescriptionText.setText(currentMission.getMissionDescription());
        currentMissionLocationText.setText(currentMission.getLocation());


        try {
            Thread.sleep(1000);
        }catch (Exception e){
            e.printStackTrace();
        }


        //返回上一个界面
        Button missionBackEngineerButton = findViewById(R.id.mission_back_engineer_button);
        missionBackEngineerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getCurrentMission() {
        getCurrentMissionInfo();
        try {
            Thread.sleep(1000);
        }catch (Exception e){
            e.printStackTrace();
        }

        getLocationFromLatitudeAndLongitude(Double.parseDouble(latitude),Double.parseDouble(longitude));
        try {
            Thread.sleep(1000);
        }catch (Exception e){
            e.printStackTrace();
        }
        currentMission = new EngineerMission("0",description,longitude,latitude,location);
    }


    private void getCurrentMissionInfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    String url = "http://111.230.44.64:8081/api/order/worker/now";
                    Request request = new Request.Builder().url(url).
                            addHeader("Authorization",token).build();
                    Response response = okHttpClient.newCall(request).execute();
                    String str = response.body().string();
                    JSONObject jsonObject = new JSONObject(str);
                    //Log.i("currentMission",str);
                    JSONObject data = jsonObject.getJSONObject("data");
                    description = data.getString("faultDescribe");
                    longitude = data.getString("eastLongitude");
                    latitude = data.getString("northLatitude");

                }catch (Exception e){
                    e.printStackTrace();
                }
                Looper.loop();
            }
        }).start();
    }


    private void getLocationFromLatitudeAndLongitude(double latitude,double longitude){
        String strLatitude = String.valueOf(latitude);
        String strLongitude = String.valueOf(longitude);
        String key = "fa21569fda915aaa92c22736e7efb6d1";
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    String url = "http://restapi.amap.com/v3/geocode/regeo?output=JSON&location="
                            + strLongitude + "," + strLatitude + "&key=" + key +
                            "&radius=0&extensions=base";
                    Request request = new Request.Builder().url(url).build();
                    Response response = okHttpClient.newCall(request).execute();
                    String str = response.body().string();
                    JSONObject jsonObject = new JSONObject(str);
                    JSONObject geocode = jsonObject.getJSONObject("regeocode");
                    JSONObject addressComponent = geocode.getJSONObject("addressComponent");
                    String province = addressComponent.getString("province");
                    String city = addressComponent.getString("city");
                    String district = addressComponent.getString("district");
                    JSONObject streetNumber = addressComponent.getJSONObject("streetNumber");
                    String street = streetNumber.getString("street");
                    String number = streetNumber.getString("number");
                    location = province+city+district+street+number;
                }catch (Exception e){
                    e.printStackTrace();
                }
                Looper.loop();
            }
        }).start();
    }

}
