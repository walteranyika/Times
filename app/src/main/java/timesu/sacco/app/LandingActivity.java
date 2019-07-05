package timesu.sacco.app;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

public class LandingActivity extends AppCompatActivity {
    String date="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        getCurrentDate();
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);


    }

    public void today_present(View view) {
        Intent x=new Intent(this, HomeActivity.class);
        startActivity(x);
    }

    public void today_absent(View view) {
        getCurrentDate();
        Intent x=new Intent(this, AbsentActivity.class);
        x.putExtra("date",date);
        startActivity(x);
    }

    public void today_late(View view) {
        getCurrentDate();
        Intent x=new Intent(this, LateActivity.class);
        x.putExtra("date",date);
        startActivity(x);
    }

    public void yesterday_present(View view) {
        Intent x=new Intent(this, HomeActivity.class);
        startActivity(x);
    }

    public void yesterday_absent(View view) {
        getYesterdaysDate();
        Intent x=new Intent(this, AbsentActivity.class);
        x.putExtra("date",date);
        startActivity(x);
    }

    public void yesterday_late(View view) {
        getYesterdaysDate();
        Intent x=new Intent(this, LateActivity.class);
        x.putExtra("date",date);
        startActivity(x);
    }

    private void getCurrentDate() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        String dayStr = String.valueOf(day).length() == 1 ? "0" + day : "" + day;
        String monStr = String.valueOf(month).length() == 1 ? "0" + month : "" + month;

        date = year + "-" + monStr + "-" + dayStr;
    }

    private void getYesterdaysDate() {
        final Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        String dayStr = String.valueOf(day).length() == 1 ? "0" + day : "" + day;
        String monStr = String.valueOf(month).length() == 1 ? "0" + month : "" + month;


        date = year + "-" + monStr + "-" + dayStr;
        Toast.makeText(this, ""+date, Toast.LENGTH_SHORT).show();
    }


    public void about_us(View view) {
        Intent x=new Intent(this, AboutActivity.class);
        startActivity(x);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.landing, menu);



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id==R.id.menu_logout){
            FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
            firebaseAuth.signOut();
            Intent x=new Intent(LandingActivity.this, MainActivity.class);
            x.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(x);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
