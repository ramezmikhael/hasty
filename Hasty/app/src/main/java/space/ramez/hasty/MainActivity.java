package space.ramez.hasty;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    boolean mIsRunning;
    TextView txtState;
    Button btnStartStopService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtState = (TextView) findViewById(R.id.txt_service_state);
        btnStartStopService = (Button) findViewById(R.id.btn_start_stop_service);

        btnStartStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(getBaseContext(), WalkingMonitor.class));
            }
        });

        mIsRunning = isServiceRunning(WalkingMonitor.class);
        setUIRunTexts();

        btnStartStopService.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
//            Intent intent = new Intent(this, TestActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUIRunTexts() {
        txtState.setText(mIsRunning ? R.string.app_running : R.string.app_stopped);
        btnStartStopService.setText(mIsRunning ? R.string.stop : R.string.start);
    }

    private boolean isServiceRunning(Class serviceClass) {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if(service.service.getClassName().equals(serviceClass.getName())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == btnStartStopService.getId()) {
            if(mIsRunning)
                stopService(new Intent(getBaseContext(), WalkingMonitor.class));
            else
                startService(new Intent(getBaseContext(), WalkingMonitor.class));

            mIsRunning = !mIsRunning;
            setUIRunTexts();
        }
    }
}
