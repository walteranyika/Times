package sacco.times.attendance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

public class LandingActivity extends AppCompatActivity {
    String date="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        getCurrentDate();


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


}
