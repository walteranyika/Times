package sacco.times.attendance;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import sacco.times.attendance.adapters.CustomAdapter;
import sacco.times.attendance.models.CustomComparator;
import sacco.times.attendance.models.Item;
import sacco.times.attendance.utils.DateUtils;
import sacco.times.attendance.utils.Urls;

public class HomeActivity extends AppCompatActivity     implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView mRecyclerView;
    CustomAdapter mCustomAdapter;
    ArrayList<Item> mItemsArrayList;
    SpotsDialog mSpotsDialog;

    MenuItem mSpinnerBranch = null;
    MenuItem mSpinnerDepartment = null;

    TextView txt_feed_back;
    String date = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mRecyclerView = findViewById(R.id.recyclerView);
        mItemsArrayList = new ArrayList<>();
        mCustomAdapter = new CustomAdapter(this, mItemsArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mCustomAdapter);
        //  mCustomAdapter.getFilter().filter(query);
        mSpotsDialog = new SpotsDialog(this, "Fetching");

        txt_feed_back = findViewById(R.id.txt_feed_back);
        getCurrentDate();
        if (getIntent().getExtras()!=null && getIntent().getExtras().containsKey("date")){
            date=getIntent().getStringExtra("date");
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

        String url =  Urls.BASE_URL+"fetch2.php";
        AsyncHttpClient client = new AsyncHttpClient();
        Log.d(TAG, "fetch_records: "+url);
        RequestParams params = new RequestParams();
        //TODO Pick Current Date
        params.put("date", date);
        mSpotsDialog.show();
        client.post(url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mSpotsDialog.dismiss();
                throwable.printStackTrace();
                Log.d(TAG, "onFailure: "+responseString+ statusCode);
                Toast.makeText(HomeActivity.this, "Could Not Fetch The Data. Please Check Your Connection", Toast.LENGTH_SHORT).show();
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

                } else
                {
                    try {
                        JSONArray jsonArray = new JSONArray(responseString);
                        mItemsArrayList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            String logname = obj.getString("logname");
                            String pin = obj.getString("pin");
                            String branch = obj.getString("branch");
                            String device_branch = obj.getString("device_branch");
                            String device = obj.getString("device");
                            String logtime = obj.getString("logtime");
                            String department = obj.getString("department");
                            String logout = obj.getString("logout");
                            if (logname==null || logname.trim().isEmpty() || branch.trim().isEmpty() || branch==null)
                                continue;
                            Item k = new Item(pin, branch, device_branch, device, WordUtils.capitalize(logname.toLowerCase()), logtime, department,logout);
                            Date startDate = DateUtils.parseDate(logtime);
                            k.setLoginDate(startDate);
                            mItemsArrayList.add(k);


                        }
                        //sort
                        Collections.sort(mItemsArrayList,new CustomComparator());
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    String TAG = "ATTENDANCE_DATA";
    int pos_A=0;
    int pos_B=0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        mSpinnerBranch = menu.findItem(R.id.menu_spinner_1);
        mSpinnerDepartment = menu.findItem(R.id.menu_spinner_2);

        final String deps[] = {"All Dep", "ICT", "FINANCE", "CREDIT", "BS DEV","AUDIT"};
        final String branches[] = {"All Branches", "Nkubu", "Mitunguu", "Githongo", "Makutano", "Kariene"};

        View view2=mSpinnerDepartment.getActionView();
        if (view2 instanceof Spinner){
            final Spinner spinner2 = (Spinner) view2;

            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(HomeActivity.this, android.R.layout.simple_spinner_item, deps);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner2.setAdapter(adapter2);
            spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                   // if (i!=0) {
                      //  mCustomAdapter.getFilter().filter(deps[i]);
                        pos_A=i;
                        String a=pos_A==0?" ":deps[pos_A];
                        String b=pos_B==0?" ":branches[pos_B];
                        String search=a+":"+b;
                        Log.d(TAG, "onItemSelected: "+search);
                         mCustomAdapter.getFilter().filter(search);

         /*          }else{
                        mCustomAdapter.getFilter().filter("");
                        pos_A=0;
                    }*/
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        }

        View view = mSpinnerBranch.getActionView();
        if (view instanceof Spinner) {
            final Spinner spinner = (Spinner) view;

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(HomeActivity.this, android.R.layout.simple_spinner_item, branches);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                   if (i!=0) {
//                        mCustomAdapter.getFilter().filter(branches[i]);
                        pos_B=i;
                        String a=pos_A==0?" ":deps[pos_A];
                        String b=pos_B==0?" ":branches[pos_B];
                        String search=a+":"+b;
                        Log.d(TAG, "onItemSelected: "+search);
                        mCustomAdapter.getFilter().filter(search);
                   /* }else{
                        mCustomAdapter.getFilter().filter("");
                        pos_B=0;
                    }*/
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id==R.id.menu_logout){
            FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
            firebaseAuth.signOut();
            Intent x=new Intent(HomeActivity.this, MainActivity.class);
            x.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(x);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_today_present)
        {
           getCurrentDate();
           fetch_records();
        }
        else if (id == R.id.nav_today_absent)
        {
            getCurrentDate();
            Intent x=new Intent(this, AbsentActivity.class);
            x.putExtra("date",date);
            startActivity(x);
        } else if (id == R.id.nav_today_late) {

            getCurrentDate();
            Intent x=new Intent(this, LateActivity.class);
            x.putExtra("date",date);
            startActivity(x);

        } else if (id == R.id.nav_yester_present) {
            getYesterdaysDate();
            fetch_records();
        } else if (id == R.id.nav_yester_absent) {
            getYesterdaysDate();
            Intent x=new Intent(this, AbsentActivity.class);
            x.putExtra("date",date);
            startActivity(x);

        } else if (id == R.id.nav_yester_late) {
            getYesterdaysDate();
            Intent x=new Intent(this, LateActivity.class);
            x.putExtra("date",date);
            startActivity(x);
        } else if (id == R.id.nav_specific_date) {
            showDatePicker();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void toggleTextView(){
        if (mItemsArrayList.size()==0) {
            txt_feed_back.setVisibility(View.VISIBLE);
            txt_feed_back.setText("No Data Was Found For Date " + date);
        }
        else {
            txt_feed_back.setVisibility(View.GONE);
        }
    }

}

