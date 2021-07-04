package com.example.sosapplication;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;


public class ScreenReceiver extends BroadcastReceiver{


    public static boolean wasScreenOn = true;
    public static int count=0;
    public static long firstTime=0;
    public static long lastTime=0;
    public static final String MyPREFERENCES = "PhoneNumber" ;
    public static final String phoneNumber1 = "First";
    public static final String phoneNumber2 = "Second";
    public static final String phoneNumber3= "Third";
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
            firstTime= System.currentTimeMillis();
        }
        else if(count<=3)
        {
            long currentTime=System.currentTimeMillis();
            if(currentTime-firstTime>=3000)
            {
                count=0;
            }
        }
        if(count==4) {
            lastTime = System.currentTimeMillis();
            if (lastTime - firstTime <= 4000) {
                Toast.makeText(context, "message sent", Toast.LENGTH_SHORT).show();
                SharedPreferences prefs =context.getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
                String[] phoneNo =new String[3];
                phoneNo[0]=prefs.getString(phoneNumber1," ");
                phoneNo[1]=prefs.getString(phoneNumber2," ");
                phoneNo[2]=prefs.getString(phoneNumber3," ");
                String area=prefs.getString("area"," ");
                String city=prefs.getString("city"," ");
                String country=prefs.getString("country"," ");
                String postal=prefs.getString("postal"," ");
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
