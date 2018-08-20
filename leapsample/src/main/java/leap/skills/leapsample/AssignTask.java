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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssignTask extends Activity implements AdapterView.OnItemSelectedListener {
    Spinner select_task, select_trainer;
    ArrayAdapter<String> dataAdapter1, dataAdapter2;
    public static String name, stu_id, admin_id;
    public static String token, admin_name;
    int trainer_id[];
    boolean internet = false;
    String[] trainer2;
    ProgressDialog progressDialog;
    TextView email_assign, name_assign, phone_assign, payment;
    EditText comment_ad;
    Button assign_task;
    String comment_admin;
    TableLayout history;
    String assign_url = "http://jaipur.ap-south-1.elasticbeanstalk.com/task_post/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_assign_task);

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



        Log.e("Stu Id: ", stu_id+"\n\n");
        Log.e("Admin Id: ", admin_id+"\n\n");

        email_assign = (TextView) findViewById(R.id.email_assign);
        name_assign = (TextView) findViewById(R.id.name_assign);
        phone_assign = (TextView) findViewById(R.id.phone_assign);
        payment = (TextView) findViewById(R.id.payment);

        comment_ad = (EditText) findViewById(R.id.comment_ad);
        assign_task = (Button) findViewById(R.id.assign_task);
        history = (TableLayout) findViewById(R.id.history);
        //message.setText(name);
        select_task = (Spinner)findViewById(R.id.select_task);
        select_trainer = (Spinner)findViewById(R.id.select_trainer);

        showHistory();

        select_task.setOnItemSelectedListener(this);


        String task_url = "http://jaipur.ap-south-1.elasticbeanstalk.com/task";


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, task_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("tasks :", response.toString()+"\n\n");
                try {
                    progressDialog.show();
                    JSONArray tasks = response.getJSONArray("tasks");
                    List<String> task = new ArrayList<String>();
                    task.add("Select");
                    for (int i = 0; i<tasks.length(); i++){
                        JSONObject task1 = (JSONObject) tasks.get(i);
                        String task2 = task1.getString("task");
                        Log.e("Task is ", task2+"\n\n");
                        task.add(task2);
                        dataAdapter1 = new ArrayAdapter<String>(AssignTask.this, android.R.layout.simple_spinner_item, task);
                        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        select_task.setAdapter(dataAdapter1);

                    }
                    progressDialog.dismiss();
                    } catch (JSONException e) {
                    progressDialog.setCancelable(true);
                    e.printStackTrace();
                    Toast.makeText(AssignTask.this, "Some error admin1", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.setCancelable(true);
                Toast.makeText(AssignTask.this, error.toString(), Toast.LENGTH_LONG).show();
                if(internet == false) {
                    progressDialog.dismiss();
                    Toast.makeText(AssignTask.this, "Please Check your internet connection!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
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
                progressDialog.setCancelable(true);
                progressDialog.dismiss();
                Toast.makeText(AssignTask.this, "Slow Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);


        select_trainer.setOnItemSelectedListener(this);
        String trainer_url = "http://jaipur.ap-south-1.elasticbeanstalk.com/sales_team";

        RequestQueue requestQueue2 = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest(Request.Method.GET, trainer_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("trainers :", response.toString()+"\n\n");
                try {
                    progressDialog.show();
                    JSONArray trainers = response.getJSONArray("sales_team");
                    List<String> trainer = new ArrayList<String>();
                    trainer.add("Select");
                    trainer_id = new int[trainers.length()];
                    for (int i = 0; i<trainers.length(); i++){
                        JSONObject trainer1 = (JSONObject) trainers.get(i);
                        String trainer2 = trainer1.getString("first_name");
                        int train_id = trainer1.getInt("id");
                        trainer_id[i] = train_id;
                        Log.e("Trainer is ", trainer2+"\n\n");
                        trainer.add(trainer2);
                        dataAdapter2 = new ArrayAdapter<String>(AssignTask.this, android.R.layout.simple_spinner_item, trainer);
                        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        select_trainer.setAdapter(dataAdapter2);
                    }
                    progressDialog.dismiss();

                } catch (JSONException e) {
                    progressDialog.setCancelable(true);
                    e.printStackTrace();
                    Toast.makeText(AssignTask.this, "Some error admin2", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.setCancelable(true);
                Toast.makeText(AssignTask.this, error.toString(), Toast.LENGTH_LONG).show();
                if(internet == false) {
                    progressDialog.dismiss();
                    Toast.makeText(AssignTask.this, "Please Check your internet connection!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params2 = new HashMap<String, String>();
                params2.put("Authentication-Token", token);
                return params2;
            }
        };
        jsonObjectRequest2.setRetryPolicy(new RetryPolicy() {
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
                progressDialog.setCancelable(true);
                progressDialog.dismiss();
                Toast.makeText(AssignTask.this, "Slow Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue2.add(jsonObjectRequest2);


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        /*if(id==0){

        }
        else {
            switch (parent.getId()) {
                case R.id.select_task: {
                    String item = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), "Selected: " + item + "\nPosition is: " + String.valueOf(position) + "\nID is: " + String.valueOf(id), Toast.LENGTH_LONG).show();
                    break;
                }
                case R.id.select_trainer: {
                    String item = parent.getItemAtPosition(position).toString();
                    int id1 = trainer_id[position-1];
                    Toast.makeText(parent.getContext(), "Selected: " + item + "\nPosition is: " + String.valueOf(position)+"\n"+id1, Toast.LENGTH_LONG).show();
                    break;
                }
            }
        }*/
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void assign(View v) {
        progressDialog.show();
        comment_admin = comment_ad.getText().toString();
        String task_item = select_task.getSelectedItem().toString();
        String trainer_item = select_trainer.getSelectedItem().toString();
        if(task_item.equals("Select") || trainer_item.equals("Select")){
            Toast.makeText(this, "Please select both task and trainer", Toast.LENGTH_SHORT).show();
        } else if(!task_item.equals("Select") && !trainer_item.equals("Select")){
            int position = select_trainer.getSelectedItemPosition();
            int id = trainer_id[position-1];

            Entered entered = new Entered();
            entered.setStudent_id(stu_id);
            entered.setTask_ad(task_item);
            entered.setComment_ad(comment_admin);
            entered.setAssigned_to(String.valueOf(id));
            entered.setAssigned_by(Integer.parseInt(admin_id));
            entered.setAssigned_to_name(trainer_item);
            entered.setAssigned_by_name(admin_name);

            JSONObject params = new JSONObject();
            try {
                params.put("student_id", entered.getStudent_id());
                params.put("task", entered.getTask_ad());
                params.put("comment", entered.getComment_ad());
                params.put("assigned_to", entered.getAssigned_to());
                params.put("assigned_by", entered.getAssigned_by());
                params.put("member_name", entered.getAssigned_to_name());
                params.put("admin", entered.getAssigned_by_name());
                Log.e("PARAMS: ", params.toString()+"\n\n\n");
            } catch (JSONException e) {
                Log.e("TASK", "NOT ASSIGNED\n\n\n");
                progressDialog.dismiss();
                e.printStackTrace();
                Toast.makeText(AssignTask.this, "Some error trainer to assign", Toast.LENGTH_LONG).show();
            }
            RequestQueue requestQueue0 = Volley.newRequestQueue(this, new HurlStack());
            JsonObjectRequest jsonObjectRequest0 = new JsonObjectRequest(Request.Method.POST, assign_url, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("Response", response.toString());
                    progressDialog.dismiss();
                    Toast.makeText(AssignTask.this, "Successfully Task Assigned.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AssignTask.this, Admin.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("EXIT", true);
                    startActivity(intent);
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.setCancelable(true);
                    Toast.makeText(AssignTask.this, error.toString(), Toast.LENGTH_LONG).show();
                    if(internet == false) {
                        progressDialog.dismiss();
                        Toast.makeText(AssignTask.this, "Please Check your internet connection!", Toast.LENGTH_LONG).show();
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String > params2 = new HashMap<>();
                    params2.put("Authentication-Token", token);
                    return params2;
                }
            };

            requestQueue0.add(jsonObjectRequest0).setRetryPolicy(new RetryPolicy() {
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
                    progressDialog.setCancelable(true);
                    progressDialog.dismiss();
                    Toast.makeText(AssignTask.this, "Slow Internet Connection", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    public void showHistory(){
        Log.e("message: ", "SHOW HISTORY\n\n\n");

        String arr[] = {"Task", "Assigned To", "Admin Comment", "Status", "Comment"};
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
            history.addView(row[i]);
        }

        String stu_info_url = "http://jaipur.ap-south-1.elasticbeanstalk.com/student/";
        String fstu_info_url = stu_info_url+""+stu_id;
        Log.e("URL IS: ", fstu_info_url+"\n\n\n");
        RequestQueue requestQueue3 = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest3 = new JsonObjectRequest(Request.Method.GET, fstu_info_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("Students :", response.toString()+"\n\n");
                try {
                    JSONObject students_info = response.getJSONObject("student_info");

                    String email_assign1 = students_info.getString("email");
                    email_assign.setText("Email: "+email_assign1);
                    String name_assign1 = students_info.getString("name");
                    name_assign.setText("Name: "+name_assign1);
                    String phone_assign1 = students_info.getString("phone");
                    phone_assign.setText("Phone: "+phone_assign1);

                    String purchase = students_info.getString("purchase");
                    String purchase_value = "NO";
                    if(Boolean.parseBoolean(purchase)==true){
                        Log.e("PURCHASED", "YES PURCHASED\n\n\n\n");
                        purchase_value = "YES";
                    }
                    else if(purchase.equals(null)){
                        Log.e("PURCHASED", "NOT PURCHASED\n\n\n\n");
                    }

                    payment.setText("PURCHASED: "+purchase_value);



                    JSONArray tasks = students_info.getJSONArray("tasks");
                    Log.e("TASK ARRAY: ", tasks.toString()+"\n\n\n\n");
                    TableRow row1[] = new TableRow[tasks.length()];
                    for (int j = 0; j<tasks.length(); j++){
                        JSONObject task_detail = (JSONObject)tasks.get(j);
                        String task = task_detail.getString("task");
                        Log.e("Task", task+"\n\n\n");
                        String assigned_to_name = task_detail.getString("member_name");
                        String comment = task_detail.getString("comment");
                        Log.e("COMMENT", "HELLO");

                        Boolean status = task_detail.getBoolean("status");

                        String team_comment = task_detail.getString("team_comment");


                        String stausValue = null;
                        if(status.booleanValue()==true)
                            stausValue = "Pending";
                        else if(status.booleanValue()!=true)
                            stausValue = "Completed";

                        Log.e(purchase_value, stausValue+"\n\n");

                        row1[j] = new TableRow(AssignTask.this);
                        row1[j].setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                        Log.e("HELLO", "WORLD\n\n");

                        TextView tv2 = new TextView(AssignTask.this);
                        tv2.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        tv2.setText(task);
                        tv2.setTextColor(Color.parseColor("#040404"));
                        tv2.setTypeface(font);
                        row1[j].addView(tv2, task.length()*20+20, 100);
                        Log.e("ROW", "ADDED1\n\n");

                        TextView tv3 = new TextView(AssignTask.this);
                        tv3.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        tv3.setText(assigned_to_name);
                        tv3.setTextColor(Color.parseColor("#040404"));
                        tv3.setTypeface(font);
                        row1[j].addView(tv3, assigned_to_name.length()*20+20, 100);
                        Log.e("ROW", "ADDED2\n\n");

                        TextView tv4 = new TextView(AssignTask.this);
                        tv4.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        tv4.setText(comment);
                        tv4.setTextColor(Color.parseColor("#040404"));
                        tv4.setTypeface(font);
                        row1[j].addView(tv4, comment.length()*20+20, 100);
                        Log.e("ROW", "ADDED3\n\n");

                        TextView tv5 = new TextView(AssignTask.this);
                        tv5.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        tv5.setText(stausValue);
                        tv5.setTextColor(Color.parseColor("#040404"));
                        tv5.setTypeface(font);
                        row1[j].addView(tv5, comment.length()*20+20, 100);
                        Log.e("ROW", "ADDED4\n\n");

                        TextView tv6 = new TextView(AssignTask.this);
                        tv6.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        tv6.setText(team_comment);
                        tv6.setTextColor(Color.parseColor("#040404"));
                        tv6.setTypeface(font);
                        row1[j].addView(tv6, comment.length()*20+20, 100);
                        Log.e("ROW", "ADDED5\n\n");

                        history.addView(row1[j]);
                        Log.e("ROW", "ADDED6\n\n");

                    }
                    progressDialog.dismiss();

                } catch (JSONException e) {
                    progressDialog.setCancelable(true);
                    e.printStackTrace();
                    Log.e("EXCEPTION IS: ", e.toString());
                    Toast.makeText(AssignTask.this, "ERROR ADMIN3", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.setCancelable(true);
                Log.e("Error: ", error.toString());
                Toast.makeText(AssignTask.this, error.toString(), Toast.LENGTH_LONG).show();
                if(internet == false) {
                    progressDialog.dismiss();
                    Toast.makeText(AssignTask.this, "Please Check your internet connection!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
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
                progressDialog.setCancelable(true);
                progressDialog.dismiss();
                Toast.makeText(AssignTask.this, "Slow Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue3.add(jsonObjectRequest3);
    }
}
