package sacco.times.attendance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    MaterialEditText inputEmail, inputPassword;
    //P@55w0Rd
    //admin@timesusacco.co.ke
    FirebaseAuth firebaseAuth;
    ProgressBar progress;
    boolean isAvailable=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        progress=findViewById(R.id.progress);
        firebaseAuth = FirebaseAuth.getInstance();
        inputEmail.clearFocus();
        inputPassword.clearFocus();
        hideKeyboard(this);

        if (firebaseAuth.getCurrentUser()!=null){
            Intent x = new Intent(MainActivity.this, LandingActivity.class);
            x.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(x);
            finish();
        }

    }

    public void login(final View view) {
        hideKeyboard(this);
        String names = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        if (names.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fill in all the values", Toast.LENGTH_SHORT).show();
            return;
        }


        //TODO Login logic
        if (isConnected()) {
            progress.setVisibility(View.VISIBLE);
            firebaseAuth.signInWithEmailAndPassword(names, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    progress.setVisibility(View.INVISIBLE);
                    Intent x = new Intent(MainActivity.this, LandingActivity.class);
                    x.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(x);
                    finish();


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progress.setVisibility(View.INVISIBLE);
                    Snackbar.make(view, "Wrong Username Or Password. Please Try Again", Snackbar.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            });
        } else {
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
    String TAG="NETWORK";


    public  void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public void change_password(View view) {
      startActivity(new Intent(this, ChangePasswordActivity.class));
    }
}
