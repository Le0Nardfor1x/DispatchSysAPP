package com.example.dispatchsysapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.example.dispatchsysapp.EngineerMission.DisplayMissionActivity;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EngineerManageActivity extends AppCompatActivity implements AMapLocationListener,LocationSource{
    private TextView position;
    private MapView mapView;

    private String token;
    private String message;
    private double eastLongitude;
    private double northLatitude;
    private AMap aMap = null;
    private LocationSource.OnLocationChangedListener mListener;
    public AMapLocationClient mLocationClient=null;
    public AMapLocationClientOption mLocationOption=null;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_engineer_manage);

        position = findViewById(R.id.engineer_location_text);
        Intent intent = getIntent();
        token = intent.getStringExtra("token");

        initLocation();
        mLocationClient.startLocation();
        initMap(savedInstanceState);


        //将经纬度位置信息发送给后端
        Button commitLocationButton = findViewById(R.id.commit_location_button);
        commitLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //提交位置之后会直接发送一次位置，之后每隔1分钟再发送一次
                sendLocationInfo(eastLongitude,northLatitude);
                timer = new Timer();
                timer.schedule(new MyTimerTask(),0,60*1000);
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(getBaseContext(),message,Toast.LENGTH_SHORT).show();
            }
        });

        //查看任务记录
        Button checkMissionButton = findViewById(R.id.check_mission_button);
        checkMissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(EngineerManageActivity.this,
                        DisplayMissionActivity.class);
                intent1.putExtra("token",token);
                startActivity(intent1);
            }
        });

        //返回登录界面
        Button engineerBackLoginButton = findViewById(R.id.engineer_back_login_button);
        engineerBackLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mLocationClient.onDestroy();
        mapView.onDestroy();
        timer.cancel();
    }

    class MyTimerTask extends TimerTask{
        @Override
        public void run() {
            sendLocationInfo(eastLongitude,northLatitude);
            Log.i("jwd",eastLongitude+" "+northLatitude);
        }
    }

   //激活定位
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mLocationClient == null) {
            mLocationClient.startLocation();//启动定位
        }
    }

   //停止定位
    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    private void initLocation() {
        //初始化定位
        try {
            mLocationClient = new AMapLocationClient(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mLocationClient != null) {
            //设置定位回调监听
            mLocationClient.setLocationListener(this);
            //初始化AMapLocationClientOption对象
            mLocationOption = new AMapLocationClientOption();
            //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //获取最近3s内精度最高的一次定位结果：
            //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。
            // 如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
            mLocationOption.setOnceLocationLatest(true);
            //设置是否返回地址信息（默认返回地址信息）
            mLocationOption.setNeedAddress(true);
            //设置定位请求超时时间，单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
            mLocationOption.setHttpTimeOut(20000);
            //关闭缓存机制，高精度定位会产生缓存。
            mLocationOption.setLocationCacheEnable(false);
            //给定位客户端对象设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
        }
    }
    private void initMap(Bundle savedInstanceState) {
        mapView = findViewById(R.id.map_view);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mapView.onCreate(savedInstanceState);
        //初始化地图控制器对象
        aMap = mapView.getMap();

        // 设置定位监听
        aMap.setLocationSource(this);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if(aMapLocation != null){
            if(aMapLocation.getErrorCode()==0) {
                double latitude=aMapLocation.getLatitude();
                double longitude=aMapLocation.getLongitude();
                double keepLatitude = new BigDecimal(latitude).setScale(6,
                        BigDecimal.ROUND_HALF_UP).doubleValue();
                double keepLongitude = new BigDecimal(longitude).setScale(6,
                        BigDecimal.ROUND_HALF_UP).doubleValue();
                eastLongitude = longitude;
                northLatitude = latitude;
                String province=aMapLocation.getProvince();
                String city=aMapLocation.getCity();
                String district=aMapLocation.getDistrict();
                String street = aMapLocation.getStreet();
                String streetNumber=aMapLocation.getStreetNum();
                String text="经度: "+keepLongitude+"\n"
                        +"纬度: "+keepLatitude+"\n"
                        +"详细位置: "+province+city+district+street+streetNumber;
                position.setText(text);


                //mLocationClient.stopLocation();
                if(mListener!=null){
                    mListener.onLocationChanged(aMapLocation);
                }
            }else {
                Log.e("AmapError","location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
                position.setText("定位失败");
            }
        }
    }

    private void sendLocationInfo(double longitude,double latitude){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                String url = "http://111.230.44.64:8081/api/user/worker/position";

                MediaType type = MediaType.parse("application/json;charset=utf-8");
                RequestBody requestBody = new FormBody.Builder()
                        .add("eastLongitude", String.valueOf(latitude))
                        .add("northLatitude",String.valueOf(longitude))
                        .build();

                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder().url(url).
                        addHeader("Authorization",token).put(requestBody).build();
                Call call = okHttpClient.newCall(request);
                try {
                    Response response = call.execute();
                    String str = response.body().string();
                    JSONObject jsonObject = new JSONObject(str);
                    message = jsonObject.getString("msg");
                    //Log.d("OkHttp",str);
                }catch (Exception e){
                    e.printStackTrace();
                }
                Looper.loop();
            }
        }).start();
    }
}
