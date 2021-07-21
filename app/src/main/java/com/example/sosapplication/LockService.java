package com.example.sosapplication;
import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class LockService extends Service {
    List<Address> addresses;
    double latitude=17.3850,longitude=78.4867;
    Geocoder geocoder;
    public static final String MyPREFERENCES = "PhoneNumber" ;
    public static final String phoneNumber1 = "First";
    public static final String phoneNumber2 = "Second";
    public static final String phoneNumber3= "Third";
    String fullAddress;
    String[] phoneNo;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(receiver, filter);
    }

       @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        stopForeground(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        createNotificationChannel();

Intent intent1=new Intent(this,MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent1,0);
        Notification notification=new NotificationCompat.Builder(this,"hello")
                .setContentTitle("SoS application")
                .setContentText("app is running")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent).build();

     startForeground(1,notification);

     return START_STICKY;
    }

    private void createNotificationChannel()
    {
        NotificationChannel notificationChannel=new NotificationChannel("hello","foreground", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager=getSystemService(NotificationManager.class);
        manager.createNotificationChannel(notificationChannel);
    }


    private void sendSMS() {

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            try {

                fusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(location -> {
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                geocoder = new Geocoder(this, Locale.getDefault());
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
                                    Log.e("message",message);
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

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            } catch (SecurityException unlikely) {
                Log.e("execption", "Lost location permission." + unlikely);
            }
    }
    long count=0,startTime=0,endTime=0;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Toast.makeText(context, "itsoff", Toast.LENGTH_SHORT).show();
            if(count==0)
            {
                startTime=System.currentTimeMillis();
            }
            if(action.equals(Intent.ACTION_SCREEN_OFF)){
                count++;
            }
            else if(action.equals(Intent.ACTION_SCREEN_ON)){
                count++;
            }
            if(count>=4)
            {
                endTime=System.currentTimeMillis();
                if(endTime-startTime<5000)
                {
                    Log.e("Power button pressed 4 times : ",": sms sent");
                    sendSMS();
                    count=0;
                }
            }

            if(System.currentTimeMillis()-startTime>5000)
            {
                startTime=System.currentTimeMillis();
                count=1;
            }
            Log.e("count",String.valueOf(count) );

        }
    };



}
