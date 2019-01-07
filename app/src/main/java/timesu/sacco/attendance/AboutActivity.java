package timesu.sacco.attendance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Calendar;

import timesu.sacco.attendance.BuildConfig;

public class AboutActivity extends AppCompatActivity {
    TextView txtVersion;
    TextView txtCopyRight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        txtVersion=findViewById(R.id.txtVersion);
        txtCopyRight=findViewById(R.id.txtCopyRight);
        txtVersion.setText("Version "+versionName);
        Calendar c=Calendar.getInstance();
        int y=c.get(Calendar.YEAR);
        txtCopyRight.setText("\u00A9 "+y+" All Rights reserved");
    }
}
