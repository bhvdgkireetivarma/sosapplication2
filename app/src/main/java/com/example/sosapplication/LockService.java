package com.example.sosapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
public class LockService extends Service {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    private SensorManager mSensorManager;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    String fullAddress;
    String[] phoneNo;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        createNotificationChannel();

Intent intent1=new Intent(this,MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent1,0);
        Notification notification=new NotificationCompat.Builder(this,"hello")
                .setContentTitle("SoS applicatiom")
                .setContentText("app is running")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent).build();
        final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        final BroadcastReceiver mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
     startForeground(1,notification);


     return START_STICKY;
    }
    public class LocalBinder extends Binder {
        LockService getService() {
            return LockService.this;
        }
    }
    private void createNotificationChannel()
    {

if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
{
    NotificationChannel notificationChannel=new NotificationChannel("hello","foreground", NotificationManager.IMPORTANCE_DEFAULT);
NotificationManager manager=getSystemService(NotificationManager.class);
manager.createNotificationChannel(notificationChannel);
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


    }
