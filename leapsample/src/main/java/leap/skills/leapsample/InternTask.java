package leap.skills.leapsample;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class InternTask extends Activity {

    public static final String PREFS_NAME = "LoginPrefs";
    public static String token, title3;
    ProgressDialog progressDialog;
    boolean internet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_intern_task);

        TextView title = (TextView)findViewById(R.id.name1);
        title.setText("Welcome "+title3);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("We are fetching the data.");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
            internet = true;
        }
        else{
            internet = false;
            progressDialog.dismiss();
            Toast.makeText(this, "Please Check your internet connection!", Toast.LENGTH_SHORT).show();
        }

        final TableLayout tb = (TableLayout) findViewById(R.id.tabi);

        String arr[] = {"S.No.", "Name", "Email", "Phone", "Institution", "Source", "Test", "Date", "Time"};

        final Typeface font = ResourcesCompat.getFont(getApplicationContext(), R.font.worksans_medium);

        TableRow row[] = new TableRow[1];

        TextView tv1 = null;
        for (int i = 0; i < 1; i++) {
            row[i] = new TableRow(this);
            row[i].setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            for (int j = 0; j < arr.length; j++) {
                tv1 = new TextView(this);
                tv1.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                tv1.setText(arr[j]);
                tv1.setTextColor(Color.parseColor("#3E7C62"));
                tv1.setTypeface(font);
                row[i].addView(tv1, 150, 100);
            }
            //row[i].setOnClickListener((View.OnClickListener) this);
            tb.addView(row[i]);
        }

        //CHANGE BELOW URL
        String url = "http://jaipur.ap-south-1.elasticbeanstalk.com/students_sales";

        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray students = response.getJSONArray("students");
                    TableRow row1[] = new TableRow[students.length()];
                    for (int j = 0; j < students.length(); j++) {
                        JSONObject details = (JSONObject) students.get(j);

                        String jsno = details.getString("sno");
                        Log.e("sno", jsno+"\n\n");
                        String jname = details.getString("name");
                        Log.e("name", jname+"\n\n");
                        String jemail = details.getString("email");
                        Log.e("email", jemail+"\n\n");
                        String jphone = details.getString("phone");
                        Log.e("phone", jphone+"\n\n");
                        String jins = details.getString("institution");
                        String jsource = details.getString("source");
                        String jtest = details.getString("test");
                        String jlead = details.getString("lead");
                        String jpurchase = details.getString("purchase");
                        String jdate = details.getString("date");
                        String jtime = details.getString("time");


                        row1[j] = new TableRow(InternTask.this);
                        row1[j].setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));


                        TextView tv2 = new TextView(InternTask.this);
                        tv2.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        tv2.setText(jsno);
                        tv2.setTextColor(Color.parseColor("#040404"));
                        tv2.setTypeface(font);
                        row1[j].addView(tv2, 100, 100);

                        TextView tv3 = new TextView(InternTask.this);
                        tv3.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        tv3.setText(jname);
                        tv3.setTextColor(Color.parseColor("#040404"));
                        tv3.setTypeface(font);
                        row1[j].addView(tv3, jname.length()*20+20, 100);

                        TextView tv4 = new TextView(InternTask.this);
                        tv4.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        tv4.setText(jemail);
                        tv4.setTextColor(Color.parseColor("#040404"));
                        tv4.setTypeface(font);
                        row1[j].addView(tv4, jemail.length()*20+20, 100);


                        TextView tv5 = new TextView(InternTask.this);
                        tv5.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        tv5.setText(jphone);
                        tv5.setTextColor(Color.parseColor("#040404"));
                        tv5.setTypeface(font);
                        row1[j].addView(tv5, jphone.length()*20+20, 100);

                        TextView tv6 = new TextView(InternTask.this);
                        tv6.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        tv6.setText(jins);
                        tv6.setTextColor(Color.parseColor("#040404"));
                        tv6.setTypeface(font);
                        row1[j].addView(tv6, jins.length()*20+20, 100);

                        TextView tv7 = new TextView(InternTask.this);
                        tv7.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        tv7.setText(jsource);
                        tv7.setTextColor(Color.parseColor("#040404"));
                        tv7.setTypeface(font);
                        row1[j].addView(tv7, jsource.length()*20+20, 100);

                        TextView tv8 = new TextView(InternTask.this);
                        tv8.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        if(jtest.equals("null"))
                            tv8.setText("NO");
                        else
                            tv8.setText("YES");
                        tv8.setTextColor(Color.parseColor("#040404"));
                        tv8.setTypeface(font);
                        row1[j].addView(tv8, 200, 100);

                        TextView tv9 = new TextView(InternTask.this);
                        tv9.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        tv9.setText(jdate);
                        tv9.setTextColor(Color.parseColor("#040404"));
                        tv9.setTypeface(font);
                        row1[j].addView(tv9, jdate.length()*20+20, 100);

                        TextView tv10 = new TextView(InternTask.this);
                        tv10.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        tv10.setText(jtime);
                        tv10.setTextColor(Color.parseColor("#040404"));
                        tv10.setTypeface(font);
                        row1[j].addView(tv10, jtime.length()*20+20, 100);


                        row1[j].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (v instanceof TableRow) {
                                    TableRow row = (TableRow) v;
                                    TextView child0 = (TextView) row.getChildAt(0);
                                    TextView child1 = (TextView) row.getChildAt(1);
                                    TextView child2 = (TextView) row.getChildAt(2);
                                    TextView child3 = (TextView) row.getChildAt(3);

                                    //String s = child0.getText() + "\n" + child1.getText() + "\n";
                                    //Toast toast = Toast.makeText(Admin.this, s,
                                    // Toast.LENGTH_SHORT);
                                    // toast.show();
                                    Intern.stu_id = child0.getText().toString();
                                    Intern.name = child1.getText().toString();
                                    Intern.email = child2.getText().toString();
                                    Intern.phone = child3.getText().toString();
                                    Intern.token = token;
                                    Intent intent = new Intent(InternTask.this, Intern.class);
                                    startActivity(intent);
                                }
                            }
                        });
                        tb.addView(row1[j]);

                    }
                    progressDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.setCancelable(true);
                Toast.makeText(InternTask.this, error.toString(), Toast.LENGTH_LONG).show();
                if(internet == false) {
                    progressDialog.dismiss();
                    Toast.makeText(InternTask.this, "Please Check your internet connection!", Toast.LENGTH_LONG).show();
                }
            }
        }){


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params2 = new HashMap<String, String>();
                params2.put("Authentication-Token", token);
                return params2;
            }
        };
        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 30000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 0;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                Toast.makeText(InternTask.this, "Slow Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public void fill_formi(View v1){
        Intent intent = new Intent(this, Leadi.class);
        startActivity(intent);
    }

    public void logout_intern(View v){
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("logged");
        editor.remove("token");
        editor.remove("name");
        editor.remove("id");
        editor.clear();
        editor.commit();
        Intent intent = new Intent(InternTask.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
