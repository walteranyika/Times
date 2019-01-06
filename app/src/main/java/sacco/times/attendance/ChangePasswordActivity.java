package sacco.times.attendance;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.apache.commons.validator.routines.EmailValidator;

public class ChangePasswordActivity extends AppCompatActivity {
    MaterialEditText inputEmail;
    FirebaseAuth firebaseAuth;
    ProgressBar progress;
    TextView txt_change_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        inputEmail = findViewById(R.id.inputEmail);
        progress = findViewById(R.id.progress);
        txt_change_pass = findViewById(R.id.txt_change_pass);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void request_change_password(View view) {
        hideKeyboard(this);
        final String email = inputEmail.getText().toString().trim();
        EmailValidator validator = EmailValidator.getInstance();
        if (email.isEmpty() || !validator.isValid(email)) {
            Toast.makeText(this, "Provide A Valid Email Address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isConnected()) {
            progress.setVisibility(View.VISIBLE);
            firebaseAuth.sendPasswordResetEmail(email).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    progress.setVisibility(View.INVISIBLE);
                    Toast.makeText(ChangePasswordActivity.this, "Failed To Send A Reset Email. Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    txt_change_pass.setText("Email reset link has been sent to " + email);
                    progress.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    public void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
