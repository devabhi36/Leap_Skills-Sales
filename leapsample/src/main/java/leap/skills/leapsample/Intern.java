package leap.skills.leapsample;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

public class Intern extends Activity {
    public static String name, stu_id, phone, email;
    public static String token;
    public static String  intern_id;
    TextView name_show, phone_show, email_show, test_show, task, admin_name;
    ToggleButton completed;
    EditText comment_intern;
    String comment, status;
    Boolean status_result;
    boolean internet = false;
    int task_id;
    ProgressDialog progressDialog;
    Button pay, submit_task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_intern);

        name_show = (TextView) findViewById(R.id.name_intern);
        phone_show = (TextView) findViewById(R.id.phone_intern);
        email_show = (TextView) findViewById(R.id.email_intern);
        test_show = (TextView) findViewById(R.id.test_intern);
        task = (TextView) findViewById(R.id.task_intern);
        admin_name = (TextView) findViewById(R.id.admin_name1);
        completed = (ToggleButton) findViewById(R.id.is_completed);
        comment_intern = (EditText) findViewById(R.id.comment_intern);
        pay = (Button) findViewById(R.id.pay_intern);
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
            Toast.makeText(this, "Please Check your internet connection!", Toast.LENGTH_SHORT).show();
        }

        student_info();
    }

    public void submit_task_intern(View view){
        progressDialog.show();
        status = completed.getText().toString();
        comment = comment_intern.getText().toString();
        if(status.equals("NO"))
            status_result = true;
        else if(status.equals("YES"))
            status_result = false;

        // CHANGE BELOW URL
        String url = "http://jaipur.ap-south-1.elasticbeanstalk.com/lead/";

        Entered entered = new Entered();
        entered.setStu_id(Integer.parseInt(stu_id));
        entered.setTask_id(task_id);
        entered.setMember_id(Integer.parseInt(intern_id));
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
            Toast.makeText(Intern.this, "Some error trainer to submit", Toast.LENGTH_LONG).show();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest5 = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                Log.e("RESPONSE: ", response.toString()+"\n\n\n");
                Toast.makeText(Intern.this, "SUCCESSFULLY SUBMITTED", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intern.this, InternTask.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.setCancelable(true);
                Toast.makeText(Intern.this, error.toString(), Toast.LENGTH_LONG).show();
                if(internet == false) {
                    progressDialog.dismiss();
                    Toast.makeText(Intern.this, "Please Check your internet connection!", Toast.LENGTH_LONG).show();
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
        requestQueue.add(jsonObjectRequest5).setRetryPolicy(new RetryPolicy() {
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
                Toast.makeText(Intern.this, "Slow Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void student_info(){
        String stu_info_url = "http://jaipur.ap-south-1.elasticbeanstalk.com/student/";
        String fstu_info_url = stu_info_url+""+stu_id;

        RequestQueue requestQueue3 = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest3 = new JsonObjectRequest(Request.Method.GET, fstu_info_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
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
                        Log.e("COMPLETED", "NOT COMPLETED\n\n\\n");
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
                    Log.e("ERROR", e.toString());
                    progressDialog.dismiss();
                    Toast.makeText(Intern.this, "SOME ERROR INTERN", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.setCancelable(true);
                Toast.makeText(Intern.this, error.toString(), Toast.LENGTH_LONG).show();
                if(internet == false) {
                    progressDialog.dismiss();
                    Toast.makeText(Intern.this, "Please Check your internet connection!", Toast.LENGTH_LONG).show();
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
                Toast.makeText(Intern.this, "Slow Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue3.add(jsonObjectRequest3);

    }

    public void paymenti(View v){
        String pay_value = pay.getText().toString();
        if(pay_value.equals("PAY")){
            Select_Producti.name_show2 = name;
            Select_Producti.phone_show2 = phone;
            Select_Producti.email_show2 = email;
            Select_Producti.token = token;
            Payi.stu_id = stu_id;
            Intent intent = new Intent(this, Select_Producti.class);
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
