package sacco.times.attendance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

public class MainActivity extends AppCompatActivity {
    MaterialEditText inputEmail, inputPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputEmail=findViewById(R.id.inputEmail);
        inputPassword=findViewById(R.id.inputPassword);
    }

    public void login(View view) {
        String names= inputEmail.getText().toString().trim();
        String password= inputPassword.getText().toString().trim();
        if (names.isEmpty()|| password.isEmpty()){
            Toast.makeText(this, "Fill in all the values", Toast.LENGTH_SHORT).show();
            return;
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        //TODO Login logic
        if (isConnected()) {
            startActivity(new Intent(this, LandingActivity.class));
            finish();
        }else{
            Toast.makeText(this, "You need Internet Connection. Please Turn on your data or connect to a WIFI", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean isConnected() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
