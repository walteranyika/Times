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
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.commons.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;
import timesu.sacco.attendance.adapters.CustomAbsentAdapter;
import timesu.sacco.attendance.models.Absent;
import timesu.sacco.attendance.utils.Urls;

public class AbsentActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    CustomAbsentAdapter mCustomAdapter;
    ArrayList<Absent> mItemsArrayList;
    SpotsDialog mSpotsDialog;

    MenuItem mSpinnerItem1 = null;
    TextView txt_feed_back;
    String date = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absent);

        mRecyclerView = findViewById(R.id.recyclerView);
        mItemsArrayList = new ArrayList<>();
        mCustomAdapter = new CustomAbsentAdapter(this, mItemsArrayList);
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
//                Toast.makeText(HomeActivity.this, year + "-" + month + "-" + day, Toast.LENGTH_SHORT).show();
            }
        }, year, month, day).show();
    }

    private void fetch_records() {

        String url = Urls.BASE_URL+"absent.php";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("date", date);
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            client.setSSLSocketFactory(sf);
        }
        catch (Exception e) {}
        mSpotsDialog.show();
        client.post(url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mSpotsDialog.dismiss();
                Toast.makeText(AbsentActivity.this, "Could Not Fetch The Data. Please Check Your Connection", Toast.LENGTH_SHORT).show();
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
                            String names = obj.getString("names");
                            String date = obj.getString("date");
                            String branch = obj.getString("branch");
                            String department = obj.getString("department");
                            Absent k = new Absent(WordUtils.capitalize(names.toLowerCase()),branch,department,date);
                            mItemsArrayList.add(k);

                        }
                        mCustomAdapter.notifyDataSetChanged();
                        toggleTextView();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mSpotsDialog.dismiss();
            }
        });


    }

    String TAG = "ATTENDANCE_DATA";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.absent, menu);
        mSpinnerItem1 = menu.findItem(R.id.menu_spinner_1);
        View view = mSpinnerItem1.getActionView();
        if (view instanceof Spinner) {
            final Spinner spinner = (Spinner) view;
            final String branches[] = {"All Branches", "Nkubu", "Mitunguu", "Githongo", "Makutano", "Kariene"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(AbsentActivity.this, android.R.layout.simple_spinner_item, branches);
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
    public void toggleTextView(){
        if (mItemsArrayList.size()==0) {
            txt_feed_back.setVisibility(View.VISIBLE);
            txt_feed_back.setText("No Data Was Found For Date " + date);
        }else {
            txt_feed_back.setVisibility(View.GONE);
        }
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
            Intent x=new Intent(AbsentActivity.this, MainActivity.class);
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
