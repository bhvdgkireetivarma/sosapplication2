package com.example.sosapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {
    boolean mBound = false;
    private double longitude = 0, latitude = 0;
    private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 101;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    private SensorManager mSensorManager;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    Button sendBtn;
    Button stopBtn;
    Button addContacts;
    Button sendmsg;
    public static final String MyPREFERENCES = "PhoneNumber" ;
    public static final String phonenumber1 = "First";
    public static final String phonenumber2 = "Second";
    public static final String phonenumber3= "Third";
     String fullAddress;
    String[] phoneNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendSMSMessage();
        Intent intent=new Intent(this,LockService.class);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            startForegroundService(intent);
        }
        else
        {
startService(intent);
        }


       stopBtn=(Button) findViewById(R.id.sosOff);
       sendmsg=(Button)findViewById(R.id.sendmsg);
 addContacts=(Button)findViewById(R.id.buttonsubmit);
 addContacts.setOnClickListener(new View.OnClickListener() {
     @Override
     public void onClick(View v) {
         Intent i = new Intent(getApplicationContext(),AddContacts.class);
         startActivity(i);

     }
 });
 stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"services stopped",Toast.LENGTH_SHORT).show();
                stopService(new Intent(getApplicationContext(), LockService.class));
            }
        });


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager).registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        //



        //
        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                        {Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_FINE_LOCATION},
                PackageManager.PERMISSION_GRANTED);

        sendmsg.setOnClickListener(V -> {
            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED||
                    ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            Log.e("fused location provider latitude",String.valueOf(latitude) );
                            Log.e("fused location provider longitude",String.valueOf(longitude) );
                            sendSMS();
                        }
                    }
                });
            }
            else
            {
                Log.e("location access","not given" );
            }
        });

        //

    }
    public void openNewActivity()
    {
        Intent intent = new Intent(this,AddContacts.class);
        startActivity(intent);
    }

    protected void sendSMSMessage() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"services started",Toast.LENGTH_SHORT).show();

        }

    }


    //
    List<Address> addresses;
    private void sendSMS() {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        Log.e("latitude ", String.valueOf(latitude));
        Log.e("longitude ", String.valueOf(longitude));


        try {
            SharedPreferences prefs =this.getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
             phoneNo =new String[3];
            phoneNo[0]=prefs.getString(phonenumber1," ");
            phoneNo[1]=prefs.getString(phonenumber2," ");
            phoneNo[2]=prefs.getString(phonenumber3," ");
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
            editor.commit();
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

        }


    }

    //


    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),"services started",Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;

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