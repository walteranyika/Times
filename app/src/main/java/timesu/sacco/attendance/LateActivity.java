package timesu.sacco.attendance;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.commons.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;
import timesu.sacco.attendance.adapters.CustomLateAdapter;
import timesu.sacco.attendance.models.CustomComparator;
import timesu.sacco.attendance.models.Item;
import timesu.sacco.attendance.utils.DateUtils;
import timesu.sacco.attendance.utils.Urls;

public class LateActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    CustomLateAdapter mCustomAdapter;
    ArrayList<Item> mItemsArrayList;
    SpotsDialog mSpotsDialog;

    MenuItem mSpinnerItem1 = null;

    TextView txt_feed_back;
    String date = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_late);
        mRecyclerView = findViewById(R.id.recyclerView);
        mItemsArrayList = new ArrayList<>();
        mCustomAdapter = new CustomLateAdapter(this, mItemsArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mCustomAdapter);
        //  mCustomAdapter.getFilter().filter(query);
        mSpotsDialog = new SpotsDialog(this, "Fetching...");

        txt_feed_back = findViewById(R.id.txt_feed_back);
        date=getIntent().getStringExtra("date");
        if (date==null ||date.isEmpty())
        {
            getCurrentDate();
        }
        fetch_records();
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


    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String dayStr = String.valueOf(day).length() == 1 ? "0" + day : "" + day;
                String monStr = String.valueOf(month).length() == 1 ? "0" + month : "" + month;
                date = year + "-" + monStr + "-" + dayStr;
                fetch_records();
            }
        }, year, month, day).show();
    }

    private void fetch_records() {

        String url =  Urls.BASE_URL+"late.php";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("date", date);
        mSpotsDialog.show();
        client.post(url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mSpotsDialog.dismiss();
                Toast.makeText(LateActivity.this, "Could Not Fetch The Data. Please Check Your Connection", Toast.LENGTH_SHORT).show();
                mItemsArrayList.clear();
                toggleTextView();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, "onSuccess: " + responseString);
                if (responseString.contains("No Data Found")) {
                    mItemsArrayList.clear();
                    mCustomAdapter.notifyDataSetChanged();
                    toggleTextView();
                } else {


                    try {
                        JSONArray jsonArray = new JSONArray(responseString);
                        mItemsArrayList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            //pin,  branch, device_branch, device, logname, logtime, department
                            String logname = obj.getString("logname");
                            String pin = obj.getString("pin");
                            String branch = obj.getString("branch");
                            String device_branch = obj.getString("device_branch");
                            String device = obj.getString("device");
                            String logtime = obj.getString("logtime");
                            String department = obj.getString("department");
                            String logout = obj.getString("logtime");
                            if (logname==null || logname.trim().isEmpty() || branch.trim().isEmpty() || branch==null)
                                continue;
                            Item k = new Item(pin, branch, device_branch, device, WordUtils.capitalize(logname.toLowerCase()) , logtime, department,logout);
                            Date startDate = DateUtils.parseDate(logtime);
                            k.setLoginDate(startDate);
                            mItemsArrayList.add(k);

                        }
                        mCustomAdapter.notifyDataSetChanged();
                        Collections.sort(mItemsArrayList,new CustomComparator());
                        toggleTextView();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mSpotsDialog.dismiss();
            }
        });

    }
    public void toggleTextView(){
        if (mItemsArrayList.size()==0) {
            txt_feed_back.setVisibility(View.VISIBLE);
            txt_feed_back.setText("No Data Was Found For Date " + date);
        }else {
            txt_feed_back.setVisibility(View.GONE);
        }
    }


    String TAG = "ATTENDANCE_DATA";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.late, menu);
        mSpinnerItem1 = menu.findItem(R.id.menu_spinner_1);
        View view = mSpinnerItem1.getActionView();
        if (view instanceof Spinner) {
            final Spinner spinner = (Spinner) view;
            final String branches[] = {"All Branches", "Nkubu", "Mitunguu", "Githongo", "Makutano", "Kariene"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(LateActivity.this, android.R.layout.simple_spinner_item, branches);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.d(TAG, "onItemSelected: " + branches[i]);
                    if (i!=0) {
                        mCustomAdapter.getFilter().filter(branches[i]);
                    }else{
                        mCustomAdapter.getFilter().filter("");
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id==R.id.menu_logout){
            FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
            firebaseAuth.signOut();
            Intent x=new Intent(LateActivity.this, MainActivity.class);
            x.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(x);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void pick_date(View view) {
        showDatePicker();
    }
}
