package leap.skills.leapsample;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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

public class ShowTask extends Activity {
    public static String name, stu_id, phone, email;
    public static String token;
    public static String  member_id;
    TextView name_show, phone_show, email_show, test_show, task, admin_name;
    ToggleButton completed;
    EditText comment_trainer;
    String comment, status;
    Boolean status_result;
    int task_id;
    TableLayout history_sales;
    ProgressDialog progressDialog;
    Button pay, submit_task;
    boolean internet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_show_task);
        name_show = (TextView) findViewById(R.id.name_show);
        phone_show = (TextView) findViewById(R.id.phone_show);
        email_show = (TextView) findViewById(R.id.email_show);
        test_show = (TextView) findViewById(R.id.test_show);
        task = (TextView) findViewById(R.id.task);
        admin_name = (TextView) findViewById(R.id.admin_name);
        completed = (ToggleButton) findViewById(R.id.completed);
        comment_trainer = (EditText) findViewById(R.id.comment_trainer);
        history_sales = (TableLayout) findViewById(R.id.history_sales);
        pay = (Button) findViewById(R.id.pay);
        submit_task = (Button) findViewById(R.id.submit_task);

        name_show.setText("Name: "+ name);
        phone_show.setText("Phone: "+ phone);
        email_show.setText("Email: "+ email);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
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

        student_info();

    }

    public void submit_task(View v){
        progressDialog.show();
        status = completed.getText().toString();
        comment = comment_trainer.getText().toString();
        if(status.equals("NO"))
            status_result = true;
        else if(status.equals("YES"))
            status_result = false;
            String url = "http://jaipur.ap-south-1.elasticbeanstalk.com/lead/";

            Entered entered = new Entered();
            entered.setStu_id(Integer.parseInt(stu_id));
            entered.setTask_id(task_id);
            entered.setMember_id(Integer.parseInt(member_id));
            entered.setComment(comment);
            entered.setStatus(status_result);
        JSONObject params = new JSONObject();
        try {
            params.put("student_id", entered.getStu_id());
            params.put("task_id", entered.getTask_id());
            params.put("member_id", entered.getMember_id());
            params.put("comment", entered.getComment());
            params.put("status", entered.getStatus());
            Log.e("PARAMS: ", params.toString()+"\n\n\n");
        } catch (JSONException e) {
            Log.e("TASK", "NOT SUBMITTED\n\n\n");
            progressDialog.dismiss();
            e.printStackTrace();
            Toast.makeText(ShowTask.this, "Some error trainer to submit", Toast.LENGTH_LONG).show();

        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
            JsonObjectRequest jsonObjectRequest5 = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                    Log.e("RESPONSE: ", response.toString()+"\n\n\n");
                    Toast.makeText(ShowTask.this, "SUCCESSFULLY SUBMITTED", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ShowTask.this, Sales.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("EXIT", true);
                    startActivity(intent);
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.setCancelable(true);
                    Toast.makeText(ShowTask.this, error.toString(), Toast.LENGTH_LONG).show();
                    if(internet == false) {
                        progressDialog.dismiss();
                        Toast.makeText(ShowTask.this, "Please Check your internet connection!", Toast.LENGTH_LONG).show();
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
            requestQueue.add(jsonObjectRequest5);
    }

    public void student_info(){

        Log.e("HISTORY", "HISTORY\n\n\n");

        String arr[] = {"Task", "Assigned By", "Status"};
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
                row[i].addView(tv1, 200, 100);
            }
            //row[i].setOnClickListener((View.OnClickListener) this);
            history_sales.addView(row[i]);
        }

        //CHANGE BELOW
        String stu_info_url = "http://jaipur.ap-south-1.elasticbeanstalk.com/student/";
        String fstu_info_url = stu_info_url+""+stu_id;

        RequestQueue requestQueue3 = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest3 = new JsonObjectRequest(Request.Method.GET, fstu_info_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("Response", response.toString()+"\n\n\n");
                try {
                    JSONObject students_info = response.getJSONObject("student_info");
                            JSONArray tasks = students_info.getJSONArray("tasks");
                    JSONObject task_detail = (JSONObject)tasks.get(0);
                    //CHANGE BELOW
                    String task1 = task_detail.getString("task");
                    String admin_name1 = task_detail.getString("admin");
                    task_id = task_detail.getInt("id");
                    Boolean status1 = task_detail.getBoolean("status");


                    task.setText(task1);
                    admin_name.setText(admin_name1);

                    TableRow row1[] = new TableRow[tasks.length()];
                    for (int j = 0; j<tasks.length(); j++){
                        JSONObject task_detail1 = (JSONObject)tasks.get(j);
                        String task = task_detail1.getString("task");
                        Log.e("task", task+"\n\n\n");
                        String assigned_by_name = task_detail1.getString("admin");
                        Log.e("name", assigned_by_name+"\n\n\n");
                        Boolean status = task_detail1.getBoolean("status");
                        String stausValue = null;
                        if(status == true)
                            stausValue = "Pending";
                        else if(status == false)
                            stausValue = "Completed";
                        Log.e("STATUS", stausValue+"\n\n\n");

                        row1[j] = new TableRow(ShowTask.this);
                        row1[j].setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));


                        TextView tv2 = new TextView(ShowTask.this);
                        tv2.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        tv2.setText(task);
                        tv2.setTextColor(Color.parseColor("#040404"));
                        tv2.setTypeface(font);
                        row1[j].addView(tv2, task.length()*20+20, 100);

                        TextView tv3 = new TextView(ShowTask.this);
                        tv3.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        tv3.setText(assigned_by_name);
                        tv3.setTextColor(Color.parseColor("#040404"));
                        tv3.setTypeface(font);
                        row1[j].addView(tv3, assigned_by_name.length()*20+20, 100);

                        TextView tv4 = new TextView(ShowTask.this);
                        tv4.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        tv4.setText(stausValue);
                        tv4.setTextColor(Color.parseColor("#040404"));
                        tv4.setTypeface(font);
                        row1[j].addView(tv4, stausValue.length()*20+20, 100);

                        history_sales.addView(row1[j]);

                    }

                    if(status1 == false){
                        Log.e("COMPLETED", "YES COMPLETED\n\n\\n");
                        completed.setText("YES");
                        completed.setEnabled(false);
                        completed.setClickable(false);
                        submit_task.setText("COMPLETED");
                        submit_task.setEnabled(false);
                        submit_task.setClickable(false);
                    }
                    else if(status1 == true){

                    }

                    String purchase = students_info.getString("purchase");
                    if(Boolean.parseBoolean(purchase)==true){
                        Log.e("PURCHASED", "YES PURCHASED\n\n\n\n");
                        pay.setText("PAID");
                    }
                    else if(purchase.equals(null)){
                        Log.e("PURCHASED", "NOT PURCHASED\n\n\n\n");
                    }

                    progressDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("ERROR", "SOME");
                    progressDialog.dismiss();
                    Toast.makeText(ShowTask.this, "SOME ERROR TRAINER", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.setCancelable(true);
                Log.e("ERROR IS: ", error.toString());
                Toast.makeText(ShowTask.this, error.toString(), Toast.LENGTH_LONG).show();
                if(internet == false) {
                    progressDialog.dismiss();
                    Toast.makeText(ShowTask.this, "Please Check your internet connection!", Toast.LENGTH_LONG).show();
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
        jsonObjectRequest3.setRetryPolicy(new RetryPolicy() {
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
                progressDialog.dismiss();
                Toast.makeText(ShowTask.this, "Slow Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue3.add(jsonObjectRequest3);

    }

    public void payment(View v){
        String pay_value = pay.getText().toString();
        if(pay_value.equals("PAY")){
            Select_Product.name_show2 = name;
            Select_Product.phone_show2 = phone;
            Select_Product.email_show2 = email;
            Select_Product.token = token;
            Pay.stu_id = stu_id;
            Intent intent = new Intent(this, Select_Product.class);
            startActivity(intent);
        }
        else if(pay_value.equals("PAID")){
            Details.name_show4 = name;
            Details.phone_show4 = phone;
            Details.email_show4 = email;
            Details.token = token;
            Details.stu_id = stu_id;
            Intent intent = new Intent(this, Details.class);
            startActivity(intent);
        }
    }
}
