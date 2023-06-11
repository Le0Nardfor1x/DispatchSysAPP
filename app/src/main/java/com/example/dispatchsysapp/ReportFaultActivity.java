package com.example.dispatchsysapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.dispatchsysapp.faultDocument.DisplayFaultActivity;
import com.example.dispatchsysapp.faultDocument.FaultDocument;
import com.example.dispatchsysapp.faultDocument.FaultDocumentAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReportFaultActivity extends AppCompatActivity {


    private List<FaultDocument> faultDocuments;

    private TextView position;
    private String token;
    private String message;
    private double eastLongitude;
    private double northLatitude;
    public AMapLocationClient mLocationClient=null;
    public AMapLocationClientOption mLocationOption=null;

    public AMapLocationListener mapLocationListener=new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if(aMapLocation!=null)
            {
                if(aMapLocation.getErrorCode()==0)
                {
                    double latitude=aMapLocation.getLatitude();
                    double Longitude=aMapLocation.getLongitude();
                    eastLongitude = Longitude;
                    northLatitude = latitude;
                    String province=aMapLocation.getProvince();
                    String city=aMapLocation.getCity();
                    String district=aMapLocation.getDistrict();
                    String street = aMapLocation.getStreet();
                    String streetNumber=aMapLocation.getStreetNum();
                    String text="经度: "+Longitude+"\n"
                            +"纬度: "+latitude+"\n"
                            +"详细位置: "+province+city+district+street+streetNumber;
                    position.setText(text);
                }
                else
                {
                    Log.e("AmapError","location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                    position.setText("定位失败");
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_report_fault);

        Intent intent = getIntent();
        token = intent.getStringExtra("token");


        position=findViewById(R.id.current_location_text);
        AMapLocationClient.updatePrivacyShow(getApplicationContext(),true,true);
        AMapLocationClient.updatePrivacyAgree(getApplicationContext(),true);
        try {
            mLocationClient=new AMapLocationClient(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mLocationClient.setLocationListener(mapLocationListener);
        mLocationOption=new AMapLocationClientOption();
        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        if(null!=mLocationClient)
        {
            mLocationClient.setLocationOption(mLocationOption);
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }



        //将经纬度位置信息发送给后端
        EditText faultDescriptionEdit = findViewById(R.id.fault_description_edit);
        Button commitFaultButton = findViewById(R.id.commit_fault_button);
        commitFaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendFaultInfo(faultDescriptionEdit.getText().toString(),eastLongitude,northLatitude);
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(getBaseContext(),message,Toast.LENGTH_SHORT).show();
            }
        });

        //显示故障历史记录
        Button displayFaultDocumentsButton = findViewById(R.id.display_fault_documents_button);
        displayFaultDocumentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFaultDocuments();
                try {
                    Thread.sleep(1000);
                }catch (Exception e){
                    e.printStackTrace();
                }
               Intent intent1 = new Intent(ReportFaultActivity.this,
                       DisplayFaultActivity.class);
               intent1.putParcelableArrayListExtra("myList",
                       new ArrayList<FaultDocument>(faultDocuments));
               startActivity(intent1);
            }
        });

    }


    public void onDestroy()
    {
        super.onDestroy();
        mLocationClient.onDestroy();
    }


    private void sendFaultInfo(String description,double longitude,double latitude){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                String url = "http://125.216.247.120:8081/api/order";
                JSONObject obj = new JSONObject();
                try {
                    obj.put("faultDescribe",description);
                    obj.put("eastLongitude",latitude);
                    obj.put("northLatitude",longitude);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                MediaType type = MediaType.parse("application/json;charset=utf-8");
                RequestBody requestBody = RequestBody.create(type,""+ obj.toString());

                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder().url(url).
                        addHeader("Authorization",token).post(requestBody).build();
                Call call = okHttpClient.newCall(request);
                try {
                    Response response = call.execute();
                    String str = response.body().string();
                    JSONObject jsonObject = new JSONObject(str);
                    message = jsonObject.getString("msg");
                    Log.d("OkHttp",str);
                }catch (Exception e){
                    e.printStackTrace();
                }
                Looper.loop();
            }
        }).start();

    }
    private void getFaultDocuments(){
        faultDocuments = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    String url = "http://111.230.44.64:8081/api/order";
                    Request request = new Request.Builder().url(url).
                            addHeader("Authorization",token).build();
                    Response response = okHttpClient.newCall(request).execute();
                    String str = response.body().string();
                    JSONObject jsonObject = new JSONObject(str);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String id = jsonObject1.getString("code");
                        String description = jsonObject1.getString("faultDescribe");
                        String status = jsonObject1.getString("status");

                        FaultDocument faultDocument = new FaultDocument(id,description,status);
                        faultDocuments.add(faultDocument);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
                Looper.loop();
            }
        }).start();
    }
}