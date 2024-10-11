package cn.fly.verify.demo;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.ServiceState;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import cn.fly.verify.CustomController;
import cn.fly.verify.FlyVerify;

public class OtherActivity extends Activity {
    private CheckBox cbIsLocationDataEnable, cbIsAndroidIdEnable, cbIsOaidEnable, cbIsAdvertisingIdEnable, cbIsWifiDataEnable, cbIsCellLocationDataEnable, cbIsAppListDataEnable, cbIsIpAddressEnable, cbIsPhoneStateDataEnable, cbIsSocietyPlatformDataEnable, cbIsConfigEnable, cbIsDREnable;
    private CheckBox cbGetLocation, cbGetAndroidId, cbGetOaid, cbGetAdvertisingId, cbGetConnectionInfo, cbGetWifiScanResults, cbGetCellLocation, cbGetNeighboringCellInfo;
    private CheckBox cbGetPackageInfos, cbGetIpAddress, cbGetCellIpv4, cbGetCellIpv6, cbGetActiveSubscriptionInfoCount, cbGetActiveSubscriptionInfoList, cbGetSimOperatorName, cbGetSimOperator, cbGetNetworkType, cbGetServiceState;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission("android.permission.ACCESS_FINE_LOCATION") != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"},0);
            }
        }
        setContentView(R.layout.activity_other);
        findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlyVerify.updateCustomController(new CustomController(){
                    public boolean isLocationDataEnable() {
                        return cbIsLocationDataEnable.isChecked();
                    }

                    public boolean isAndroidIdEnable() {
                        return cbIsAndroidIdEnable.isChecked();
                    }

                    public boolean isOaidEnable() {
                        return cbIsOaidEnable.isChecked();
                    }

                    public boolean isAdvertisingIdEnable() {
                        return cbIsAdvertisingIdEnable.isChecked();
                    }

                    public boolean isWifiDataEnable() {
                        return cbIsWifiDataEnable.isChecked();
                    }

                    public boolean isCellLocationDataEnable() {
                        return cbIsCellLocationDataEnable.isChecked();
                    }

                    public boolean isAppListDataEnable() {
                        return cbIsAppListDataEnable.isChecked();
                    }

                    public boolean isIpAddressEnable() {
                        return cbIsIpAddressEnable.isChecked();
                    }

                    public boolean isPhoneStateDataEnable() {
                        return cbIsPhoneStateDataEnable.isChecked();
                    }

                    public boolean isSocietyPlatformDataEnable() {
                        return cbIsSocietyPlatformDataEnable.isChecked();
                    }

                    public boolean isConfigEnable() {
                        return cbIsConfigEnable.isChecked();
                    }

                    public boolean isDREnable() {
                        return cbIsDREnable.isChecked();
                    }

                    public Location getLocation() {
                        if(cbGetLocation.isChecked()){
                            Location location = new Location(LocationManager.GPS_PROVIDER);
                            location.setLatitude(10.00);
                            location.setLongitude(10.01);
                            return location;
                        }else {
                            return null;
                        }
                    }

                    public String getAndroidId() {
                        if(cbGetAndroidId.isChecked()){
                            return "androidId12345";
                        }else {
                            return null;
                        }
                    }

                    public String getOaid() {
                        if(cbGetOaid.isChecked()){
                            return "oaid12345";
                        }else {
                            return null;
                        }
                    }

                    public String getAdvertisingId() {
                        if(cbGetAdvertisingId.isChecked()){
                            return "adid12345";
                        }else {
                            return null;
                        }
                    }

                    public WifiInfo getConnectionInfo() {
                        if(cbGetConnectionInfo.isChecked()){
                            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                            return wifiInfo;
                        }else {
                            return null;
                        }
                    }

                    public List<ScanResult> getWifiScanResults() {
                        if(cbGetWifiScanResults.isChecked()){
                            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                            return wifiManager.getScanResults();
                        }else {
                            return null;
                        }
                    }

                    public CellLocation getCellLocation() {
                        if(cbGetLocation.isChecked()){
                            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                            CellLocation cellLocation = telephonyManager.getCellLocation();
                            return cellLocation;
                        }else {
                            return null;
                        }
                    }

                    public List<NeighboringCellInfo> getNeighboringCellInfo() {
                        if(cbGetNeighboringCellInfo.isChecked()) {
                            NeighboringCellInfo neighboringCellInfo = new NeighboringCellInfo();
                            neighboringCellInfo.setRssi(10);
                            neighboringCellInfo.setCid(12);
                            List<NeighboringCellInfo> list = new ArrayList<>();
                            list.add(neighboringCellInfo);
                            return list;
                        }else{
                            return null;
                        }
                    }

                    public List<PackageInfo> getPackageInfos() {
                        if(cbGetPackageInfos.isChecked()){
                            PackageManager packageManager = getPackageManager();
                            List<PackageInfo> list = packageManager.getInstalledPackages(0);
                            return list;
                        }else {
                            return null;
                        }
                    }

                    public String getIpAddress() {
                        if(cbGetIpAddress.isChecked()){
                            return "10.0.0.1";
                        }else {
                            return null;
                        }
                    }

                    public String getCellIpv4() {
                        if(cbGetCellIpv4.isChecked()){
                            return "10.0.0.1";
                        }else {
                            return null;
                        }
                    }

                    public String getCellIpv6() {
                        if(cbGetCellIpv6.isChecked()){
                            return "AA:BB:CC";
                        }
                        return null;
                    }

                    public int getActiveSubscriptionInfoCount() {
                        if(cbGetActiveSubscriptionInfoCount.isChecked()){
                            return 3;
                        }
                        return -1;
                    }

                    public List<SubscriptionInfo> getActiveSubscriptionInfoList() {
                        if(cbGetActiveSubscriptionInfoList.isChecked()){
                            SubscriptionManager subscriptionManager = ((SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE));
                            return subscriptionManager.getActiveSubscriptionInfoList();
                        }
                        return null;
                    }

                    public String getSimOperatorName() {
                        if(cbGetSimOperatorName.isChecked()){
                            return "CMCC";
                        }
                        return null;
                    }

                    public String getSimOperator() {
                        if(cbGetSimOperator.isChecked()){
                            return "46001";
                        }
                        return null;
                    }

                    public int getNetworkType() {
                        if(cbGetNetworkType.isChecked()){
                            return 8;
                        }
                        return -1;
                    }

                    public ServiceState getServiceState() {
                        if(cbGetServiceState.isChecked()){
                            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                            ServiceState serviceState = telephonyManager.getServiceState();
                            return serviceState;
                        }
                        return null;
                    }
                });
                Toast.makeText(OtherActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
            }
        });
        cbIsLocationDataEnable = (CheckBox)findViewById(R.id.cbIsLocationDataEnable);
        cbIsAndroidIdEnable = (CheckBox)findViewById(R.id.cbIsAndroidIdEnable);
        cbIsOaidEnable = (CheckBox)findViewById(R.id.cbIsOaidEnable);
        cbIsAdvertisingIdEnable = (CheckBox)findViewById(R.id.cbIsAdvertisingIdEnable);
        cbIsWifiDataEnable = (CheckBox)findViewById(R.id.cbIsWifiDataEnable);
        cbIsCellLocationDataEnable = (CheckBox)findViewById(R.id.cbIsCellLocationDataEnable);
        cbIsAppListDataEnable = (CheckBox)findViewById(R.id.cbIsAppListDataEnable);
        cbIsIpAddressEnable = (CheckBox)findViewById(R.id.cbIsIpAddressEnable);
        cbIsPhoneStateDataEnable = (CheckBox)findViewById(R.id.cbIsPhoneStateDataEnable);
        cbIsSocietyPlatformDataEnable = (CheckBox)findViewById(R.id.cbIsSocietyPlatformDataEnable);
        cbIsConfigEnable = (CheckBox)findViewById(R.id.cbIsConfigEnable);
        cbIsDREnable = (CheckBox)findViewById(R.id.cbIsDREnable);

        cbGetLocation = (CheckBox)findViewById(R.id.cbGetLocation);
        cbGetAndroidId = findViewById(R.id.cbGetAndroidId);
        cbGetOaid = findViewById(R.id.cbGetOaid);
        cbGetAdvertisingId = findViewById(R.id.cbGetAdvertisingId);
        cbGetConnectionInfo = findViewById(R.id.cbGetConnectionInfo);
        cbGetConnectionInfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    Log.d("SecPure", "wifiInfo: " + wifiInfo);
                }
            }
        });
        cbGetWifiScanResults = findViewById(R.id.cbGetWifiScanResults);
        cbGetWifiScanResults.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                    List<ScanResult> list = wifiManager.getScanResults();
                    for (int i = 0; i < list.size(); i++) {
                        Log.d("SecPure", "ScanResult: " + list.get(i));
                    }
                }
            }
        });
        cbGetCellLocation = findViewById(R.id.cbGetCellLocation);
        cbGetCellLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    CellLocation cellLocation = telephonyManager.getCellLocation();
                    Log.d("SecPure", "cellLocation: " + cellLocation);
                }
            }
        });
        cbGetNeighboringCellInfo = findViewById(R.id.cbGetNeighboringCellInfo);
        cbGetNeighboringCellInfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                }
            }
        });
        cbGetPackageInfos = findViewById(R.id.cbGetPackageInfos);
        cbGetPackageInfos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    PackageManager packageManager = getPackageManager();
                    List<PackageInfo> list = packageManager.getInstalledPackages(0);
                    for (int i = 0; i < list.size(); i++) {
                        Log.d("SecPure", "PackageInfo: " + list.get(i));
                    }
                }
            }
        });
        cbGetIpAddress = findViewById(R.id.cbGetIpAddress);
        cbGetCellIpv4 = findViewById(R.id.cbGetCellIpv4);
        cbGetCellIpv6 = findViewById(R.id.cbGetCellIpv6);
        cbGetActiveSubscriptionInfoCount = findViewById(R.id.cbGetActiveSubscriptionInfoCount);
        cbGetActiveSubscriptionInfoList = findViewById(R.id.cbGetActiveSubscriptionInfoList);
        cbGetSimOperatorName = findViewById(R.id.cbGetSimOperatorName);
        cbGetSimOperator = findViewById(R.id.cbGetSimOperator);
        cbGetNetworkType = findViewById(R.id.cbGetNetworkType);
        cbGetServiceState = findViewById(R.id.cbGetServiceState);
        cbGetServiceState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    ServiceState serviceState = telephonyManager.getServiceState();
                    Log.d("SecPure", "serviceState: " + serviceState);
                }
            }
        });
    }
}
