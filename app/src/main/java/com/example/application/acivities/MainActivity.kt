package com.example.application.acivities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.LocationSource
import com.amap.api.maps.LocationSource.OnLocationChangedListener
import com.amap.api.maps.model.LatLng
import com.example.application.R
import com.example.application.netwotk.presenters.FirstPresenter
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), LocationSource, AMapLocationListener {
    //判断当前应用是否是debug状态
    var mListener: OnLocationChangedListener? = null
    var mLocationClient: AMapLocationClient? = null
    var mLocationOption: AMapLocationClientOption? = null
    var aMap: AMap? = null
    val empty = emptyList<String>()
    private var isFirstLoc = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        map.onCreate(savedInstanceState);


//        if (!isLocationEnabled()) {
//            LocationDialog().show(supportFragmentManager, "定位权限弹框")
//        }
//        initMapView();
        var first = FirstPresenter();
        first.setUrl("http://t.weather.sojson.com/api/weather/city/101030100")
        first.sendResponse(emptyMap());
    }

    private fun initMapView() {
//        val myStyle = MyLocationStyle() //初始化定位蓝点样式类
//        myStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.location));
//        myStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        // myStyle.interval(2000) //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。单位为毫秒
//        myStyle.anchor(1.0f, 1.0f)
        if (aMap == null) {
            aMap = map.getMap();
//            aMap!!.myLocationStyle = myStyle
            //设置显示定位按钮 并且可以点击
            val settings = aMap!!.uiSettings;
            aMap!!.setLocationSource(this);//设置了定位的监听
            // 是否显示定位按钮
//            settings.setMyLocationButtonEnabled(true);
            aMap!!.isMyLocationEnabled = true;//显示定位层并且可以触发定位,默认是flase
            aMap!!.showIndoorMap(true)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {//未开启定位权限
            //开启定位权限,200是标识码
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                200
            );
        } else {
            location()
            Toast.makeText(this, "已开启定位权限", Toast.LENGTH_LONG).show();
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            200 -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { //用户同意权限,执行我们的操作
                location()
            } else { //用户拒绝之后,当然我们也可以弹出一个窗口,直接跳转到系统设置页面
                Toast.makeText(this@MainActivity, "未开启定位权限,请手动到设置去开启权限", Toast.LENGTH_LONG).show()
            }
            else -> {
            }
        }
    }

    private fun location() {
        //初始化定位
        mLocationClient = AMapLocationClient(this);
        //设置定位回调监听
        mLocationClient!!.setLocationListener(this);
        //初始化定位参数
        mLocationOption = AMapLocationClientOption();
        //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption!!.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption!!.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption!!.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption!!.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption!!.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption!!.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient!!.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient!!.startLocation();
    }

    override fun onDestroy() {
        super.onDestroy()
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        map.onDestroy()
        if (null != mLocationClient) {
            mLocationClient!!.onDestroy();
        }
    }

    override fun onResume() {
        super.onResume()
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        map.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        map.onSaveInstanceState(outState)
    }

    override fun deactivate() {
        mListener = null;
        mLocationClient!!.stopLocation();
        mLocationClient!!.onDestroy();
        mLocationClient = null;
    }

    override fun activate(p0: OnLocationChangedListener?) {
        mListener = p0;
    }

    override fun onLocationChanged(aMapLocation: AMapLocation?) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
                aMapLocation.getLatitude();//获取纬度
                aMapLocation.getLongitude();//获取经度
                aMapLocation.getAccuracy();//获取精度信息
                val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                val date = Date(aMapLocation.getTime());
                df.format(date);//定位时间
                aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                aMapLocation.getCountry();//国家信息
                aMapLocation.getProvince();//省信息
                aMapLocation.getCity();//城市信息
                aMapLocation.getDistrict();//城区信息
                aMapLocation.getStreet();//街道信息
                aMapLocation.getStreetNum();//街道门牌号信息
                aMapLocation.getCityCode();//城市编码
                aMapLocation.getAdCode();//地区编码

                // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                if (isFirstLoc) {
                    //设置缩放级别
                    aMap!!.moveCamera(CameraUpdateFactory.zoomTo(17f));
                    //将地图移动到定位点
                    aMap!!.moveCamera(
                        CameraUpdateFactory.changeLatLng(
                            LatLng(
                                aMapLocation.latitude,
                                aMapLocation.longitude
                            )
                        )
                    );
                    //点击定位按钮 能够将地图的中心移动到定位点
                    mListener!!.onLocationChanged(aMapLocation);
                    //添加图钉
                    //  aMap.addMarker(getMarkerOptions(amapLocation));
                    //获取定位信息
                    val buffer = StringBuffer();
                    buffer.append(
                        aMapLocation.getCountry() + ""
                                + aMapLocation.getProvince() + ""
                                + aMapLocation.getCity() + ""
                                + aMapLocation.getProvince() + ""
                                + aMapLocation.getDistrict() + ""
                                + aMapLocation.getStreet() + ""
                                + aMapLocation.getStreetNum()
                    );
                    Toast.makeText(applicationContext, buffer.toString(), Toast.LENGTH_LONG)
                        .show();
                    isFirstLoc = false;
                }


            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e(
                    "AmapError", "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo()
                );
                Toast.makeText(getApplicationContext(), "定位失败", Toast.LENGTH_LONG).show();
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationMode = 0
        val locationProviders: String
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            locationMode = try {
                Settings.Secure.getInt(
                    this.contentResolver,
                    Settings.Secure.LOCATION_MODE
                )
            } catch (e: Settings.SettingNotFoundException) {
                e.printStackTrace()
                return false
            }
            locationMode != Settings.Secure.LOCATION_MODE_OFF
        } else {
            locationProviders = Settings.Secure.getString(
                this.contentResolver,
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED
            )
            !TextUtils.isEmpty(locationProviders)
        }
    }
}
