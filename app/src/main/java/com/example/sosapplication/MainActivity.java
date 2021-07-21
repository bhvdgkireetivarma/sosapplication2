package com.example.sosapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import android.location.Address;
import android.location.Geocoder;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;


public class MainActivity extends AppCompatActivity {
    private double longitude = 0, latitude = 0;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =10 ;
    private static  final int MY_LOCATION=101;
    private static  final int MY_COARSE_LOCATION=201;
    private SensorManager mSensorManager;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    Button stopBtn;
    Button addContacts;
    Button sendmsg;
    public static final String MyPREFERENCES = "PhoneNumber" ;
    public static final String phoneNumber1 = "First";
    public static final String phoneNumber2 = "Second";
    public static final String phoneNumber3= "Third";
     String fullAddress;
    String[] phoneNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean hasPermissionWrite = (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionWrite) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        }
        boolean hasPermissionLocation = (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionLocation) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_LOCATION);
        }
        Intent intent=new Intent(this,LockService.class);

        startForegroundService(intent);
        stopBtn=(Button) findViewById(R.id.sosOff);
       sendmsg=(Button)findViewById(R.id.sendmsg);
 addContacts=(Button)findViewById(R.id.buttonsubmit);
 addContacts.setOnClickListener(v -> {
     Intent i = new Intent(getApplicationContext(),AddContacts.class);
     startActivity(i);

 });
 stopBtn.setOnClickListener(v -> {
     Toast.makeText(getApplicationContext(),"services stopped",Toast.LENGTH_SHORT).show();
     stopService(new Intent(getApplicationContext(), LockService.class));
 });


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager).registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        sendmsg.setOnClickListener(V -> {
            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED||
                    ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {

                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        Log.e("fused location provider latitude",String.valueOf(latitude) );
                        Log.e("fused location provider longitude",String.valueOf(longitude) );
                        sendSMS();
                    }
                });
            }
            else
            {
                Log.e("location access","not given" );
            }
        });
    }
    List<Address> addresses;
    private void sendSMS() {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        Log.e("latitude ", String.valueOf(latitude));
        Log.e("longitude ", String.valueOf(longitude));


        try {
            SharedPreferences prefs =this.getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
             phoneNo =new String[3];
            phoneNo[0]=prefs.getString(phoneNumber1," ");
            phoneNo[1]=prefs.getString(phoneNumber2," ");
            phoneNo[2]=prefs.getString(phoneNumber3," ");
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            String area = addresses.get(0).getLocality();
            String city = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();

              fullAddress = address + ", " + area + ", " + city + ", " + country + ", " + postalCode;
            String message = "I am in danger,Help ME! \n I am at " +fullAddress+"\n";
            SharedPreferences sharedpreferences = this.getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("address",address);
            editor.putString("area",area);
            editor.putString("city",city);
            editor.putString("country",country);
            editor.putString("postal",postalCode);
            editor.apply();
            for (int i = 0; i < 3; i++) {
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo[i], null, message, null, null);
                }
                catch(Exception e)
                {
                    //
                }
            }
        } catch (Exception e) {
            //
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case MY_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //reload my activity with permission granted or use the features what required the permission
                    finish();
                    startActivity(getIntent());
                } else
                {
                    Toast.makeText(this, "The app was not allowed to get your phone state. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
            }
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "Permission granted.", Toast.LENGTH_SHORT).show();
                    //reload my activity with permission granted or use the features what required the permission
                    finish();
                    startActivity(getIntent());
                } else
                {
                    Toast.makeText(this, "The app was not allowed to get your location. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
            }
            case MY_COARSE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {

                    finish();
                    startActivity(getIntent());
                } else
                {
                    Toast.makeText(this, "The app was not allowed to get your location. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
            }

        }

    }
    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            if (mAccel > 12) {
                Toast.makeText(getApplicationContext(), "sent Succesful", Toast.LENGTH_SHORT).show();
                try {
                    for(int i=0;i<3;i++) {
                        SmsManager smsManager = SmsManager.getDefault();
                        String msg = "I am in danger,Help me!" +"My Location "+fullAddress;
                        smsManager.sendTextMessage(phoneNo[i], null, msg, null, null);
                    }
                }
                catch (Exception e)
                {
                    //
                }
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onResume() {
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }
    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }





}