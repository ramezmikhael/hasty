package space.ramez.hasty;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.GregorianCalendar;

/**
 * Created by Ramez on 11/14/2016.
 */
public class WalkingMonitor extends Service implements SensorEventListener {

    private static final String TAG = WalkingMonitor.class.getSimpleName();

    private SensorManager sensorMan;
    private Sensor accelerometer;

    private boolean mIsMonitoring;
    private float[] mGravity;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    private int mCounter;

    // Time management variables
    private GregorianCalendar mCalendar;
    private long mLastStepTime;

    PowerManager mPowerManager;
    PowerManager.WakeLock mWakeLock;

    @Override
    public void onCreate() {
        super.onCreate();

        mCalendar = (GregorianCalendar) GregorianCalendar.getInstance();

        sensorMan = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        mCounter = 0;
        mLastStepTime = GregorianCalendar.getInstance().getTimeInMillis();

        sensorMan.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_UI);

        // Use this partial wake_lock to prevent the service
        // from sleeping when the screen is off
        mPowerManager = (PowerManager) getBaseContext().getSystemService(Context.POWER_SERVICE);

        mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ServiceWakeLock");
        mWakeLock.acquire();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        startMonitoring();

        return(START_NOT_STICKY);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        sensorMan.unregisterListener(this);
        mWakeLock.release();
        stopMonitoring();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = event.values.clone();
            // Shake detection
            float x = mGravity[0];
            float y = mGravity[1];
            float z = mGravity[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float)Math.sqrt(x * x + y * y + z * z);
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            // Make this higher or lower according to how much
            // motion you want to detect
            if (mAccel > 4) {

                // If the phone didn't move for 2 seconds, consider the user at home
                mCounter = mCalendar.getTimeInMillis() - mLastStepTime > Constants.PREF_SENSITIVITY * 1000 ? 0 : mCounter + 1;
                mLastStepTime = mCalendar.getTimeInMillis();

                Log.d(TAG, String.valueOf(mCounter));
                if(mCounter >= Constants.PREF_SENSITIVITY * 10) {

                    notifyByVibrating();
                    notifyByScreenOn();
                    AlertNotification.notify(this);

                    mCounter = 0;
                }
            }
        }

    }

    private void startMonitoring() {
        if(!mIsMonitoring) {
            mIsMonitoring = true;

            final Resources res = this.getResources();

            final NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_stat_monitoring)
                    .setContentTitle(res.getString(R.string.app_name))
                    .setContentText(res.getString(R.string.standby))
                    .setContentIntent(PendingIntent.getActivity( this, 0, new Intent(this, MainActivity.class), 0));

            Notification notification = builder.build();
            notification.flags |= Notification.FLAG_NO_CLEAR;

            startForeground(2, notification);
        }
    }

    private void stopMonitoring() {
        if(mIsMonitoring) {
            stopForeground(true);
        }
    }

    private void notifyByScreenOn() {
        boolean isScreenOn = mPowerManager.isScreenOn();

        if(isScreenOn == false)
        {
            PowerManager.WakeLock wl = mPowerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE, "ScreenWake20Steps");
            wl.acquire(5000);
            PowerManager.WakeLock wl_cpu = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"ScreenWake20StepsCPU");
            wl_cpu.acquire(5000);
        }
    }

    private void notifyByVibrating() {
        if(! Constants.PREF_VIBRATE)
            return;

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{500, 500, 1000}, -1);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // required method
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
