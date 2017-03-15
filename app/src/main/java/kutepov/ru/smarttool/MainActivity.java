package kutepov.ru.smarttool;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
    }

    public void onClickButtonSearchDevice(View view) {
        Intent searchDeviceIntent = new Intent(this, SearchDeviceActivity.class);
        startActivity(searchDeviceIntent);
    }
}
