package com.example.sosapplication;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

public class ScreenReceiver extends BroadcastReceiver{
    boolean mBound = false;
    private double longitude = 0, latitude = 0;
    private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 101;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;




    public static boolean wasScreenOn = true;
    public static int count=0;
    public static long firsttime=0;
    public static long lasttime=0;
    public static SharedPreferences preferences;
    private FusedLocationProviderClient fusedLocationProviderClient;

double mLatitude=0;
double mLongitude=0;
    public static final String MyPREFERENCES = "PhoneNumber" ;
    public static final String phonenumber1 = "First";
    public static final String phonenumber2 = "Second";
    public static final String phonenumber3= "Third";
    public static final String address= "Address";
    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.e("LOB","onReceive");
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {

            count++;
            if(count>=4)
            {
                count=4;
            }
            wasScreenOn = false;
            Log.e("LOB","wasScreenOn"+wasScreenOn);
            Toast.makeText(context, "itsoff", Toast.LENGTH_SHORT).show();


        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            count++;

        Toast.makeText(context, "itson", Toast.LENGTH_SHORT).show();
            Log.e("LOB","wasScreenOn"+wasScreenOn);


        }
        if(count==1)
        {
            firsttime= System.currentTimeMillis();
        }
        else if(count<=3)
        {
            long currentime=System.currentTimeMillis();
            if(currentime-firsttime>=3000)
            {
                count=0;
            }
        }
        if(count==4) {
            lasttime = System.currentTimeMillis();
            if (lasttime - firsttime <= 4000) {
                Toast.makeText(context, "message sent", Toast.LENGTH_SHORT).show();

                SharedPreferences prefs =context.getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
                String[] phoneNo =new String[3];
                phoneNo[0]=prefs.getString(phonenumber1," ");
                phoneNo[1]=prefs.getString(phonenumber2," ");
                phoneNo[2]=prefs.getString(phonenumber3," ");
                String address=prefs.getString("address"," ");
String area=prefs.getString("area"," ");
String city=prefs.getString("city"," ");
String country=prefs.getString("country"," ");
String postal=prefs.getString("postal"," ");

                String p;
                String q;
                try {
                     p = Double.toString(mLatitude);
                  q = Double.toString(mLongitude);
                }
                catch(Exception e){
                    p=" ";
                    q=" ";

                }
                for (int i = 0; i < 3; i++) {

                    String message="I am in danger,Help me!"+"I am at"+area+city+country+postal;
                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(phoneNo[i], null, message, null, null);
                    }
                    catch(Exception e)
                    {
                        //
                    }
                }
            }
            count=0;
        }


    }




    }
