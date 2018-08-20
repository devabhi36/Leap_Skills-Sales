package leap.skills.leapsample;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    EditText name, no;
    String gname, gno;
    boolean internet = false;
    CheckBox remember;
    TextView show, hide;
    ProgressDialog progressDialog;
    public static final String PREFS_NAME = "LoginPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        name = (EditText) findViewById(R.id.leap_id);
        no = (EditText) findViewById(R.id.dob);
        remember = (CheckBox) findViewById(R.id.remember_me);
        show = (TextView) findViewById(R.id.show);
        hide = (TextView) findViewById(R.id.hide);

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(no.getText().toString().length()>0){
                no.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                else
                    Toast.makeText(MainActivity.this, "PASSWORD FIELD EMPTY", Toast.LENGTH_SHORT).show();

            }
        });


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("LOGGING IN...");
        progressDialog.setCancelable(false);

        SharedPreferences logged = getSharedPreferences(PREFS_NAME, 0);
        String send_token = logged.getString("token", "NO Value");
        String saved_id = logged.getString("id", "No Value Id");
        String saved_name = logged.getString("name", "No Value");
        if (logged.getString("logged", "").toString().equals("ad_logged")) {
            Admin.token = send_token;
            AssignTask.admin_id = saved_id;
            AssignTask.admin_name = saved_name;
            Admin.title1 = saved_name;
            Intent intent = new Intent(this, Admin.class);
            startActivity(intent);
            finish();
        }
        else if(logged.getString("logged", "").toString().equals("sa_logged")){
            Sales.token = send_token;
            ShowTask.member_id = saved_id;
            Sales.title2 = saved_name;
            Intent intent = new Intent(this, Sales.class);
            startActivity(intent);
            finish();
        }
        else if(logged.getString("logged", "").toString().equals("in_logged")){
            InternTask.token = send_token;
            Intern.intern_id = saved_id;
            InternTask.title3 = saved_name;
            Intent intent = new Intent(this, InternTask.class);
            startActivity(intent);
            finish();
        }
    }

    public void ad_login(View v1){


        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
            internet = true;
        }
        else
            internet = false;
        gname = name.getText().toString().trim();
        Log.e("ENTERED ", gname+"\n");
        gno = no.getText().toString().trim();

        if(gname.equals("") || gno.equals("")){
            Toast.makeText(this, "Please fill in all the details.", Toast.LENGTH_SHORT).show();
        }

        else {

            progressDialog.show();

            Entered entered = new Entered();
            entered.setEmail_e(gname);
            entered.setPassword_e(gno);
            Log.e("ENTERED ", "SEND" + "\n");

            //String url = "https://api.androidhive.info/volley/person_array.json";
            String url1 = "http://jaipur.ap-south-1.elasticbeanstalk.com/login";

            JSONObject params = new JSONObject();
            try {
                params.put("email", entered.getEmail_e());
                params.put("password", entered.getPassword_e());
                Log.e("ENTERED ", params + "\n");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url1, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("response- ", response.toString());

                    final String[] id = new String[1];
                    try {
                        JSONObject data = response.getJSONObject("response");
                        final JSONObject user = data.getJSONObject("user");
                        final String token1 = user.getString("authentication_token");


                        //Toast.makeText(getApplicationContext(), response.toString() + "\n\n" + token, Toast.LENGTH_SHORT).show();


                        String url2 = "http://jaipur.ap-south-1.elasticbeanstalk.com/check_role/";

                        progressDialog.setTitle("Successfully Logged In :)");
                        progressDialog.setMessage("Please wait!\nWe are fetching your data.");

                        RequestQueue rq = Volley.newRequestQueue(MainActivity.this);
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url2, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.e("response is- ", response.toString()+"\n\n");
                                try {

                                    JSONObject response1 = new JSONObject(response);
                                    String role = response1.getString("role");
                                    String name = response1.getString("name");
                                    Log.e("ROLE IS: ", role + "\n");
                                    if (role.equals("admin")) {
                                        id[0] = user.getString("id");
                                        Log.e("ADMIN ID IS: ", id[0] + "\n\n");

                                        Admin.token = token1;
                                        AssignTask.admin_id = id[0];
                                        AssignTask.admin_name = name;
                                        Admin.title1 = name;

                                        if (remember.isChecked()) {
                                            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                                            SharedPreferences.Editor editor = settings.edit();
                                            editor.putString("logged", "ad_logged");
                                            editor.putString("token", token1);
                                            editor.putString("id", id[0]);
                                            editor.putString("name", name);
                                            editor.commit();
                                        }

                                        progressDialog.dismiss();

                                        Intent intent = new Intent(MainActivity.this, Admin.class);
                                        startActivity(intent);
                                        finish();
                                    } else if (role.equals("team")) {
                                        id[0] = user.getString("id");

                                        Sales.token = token1;
                                        ShowTask.member_id = id[0];
                                        Sales.title2 = name;

                                        if (remember.isChecked()) {
                                            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                                            SharedPreferences.Editor editor = settings.edit();
                                            editor.putString("logged", "sa_logged");
                                            editor.putString("token", token1);
                                            editor.putString("id", id[0]);
                                            editor.putString("name", name);
                                            editor.commit();
                                        }

                                        progressDialog.dismiss();

                                        Intent intent = new Intent(MainActivity.this, Sales.class);
                                        startActivity(intent);
                                        finish();
                                    } else if (role.equals("intern")) {
                                        id[0] = user.getString("id");

                                        InternTask.token = token1;
                                        Intern.intern_id = id[0];
                                        InternTask.title3 = name;

                                        if (remember.isChecked()) {
                                            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                                            SharedPreferences.Editor editor = settings.edit();
                                            editor.putString("logged", "in_logged");
                                            editor.putString("token", token1);
                                            editor.putString("id", id[0]);
                                            editor.putString("name", name);
                                            editor.commit();
                                        }

                                        progressDialog.dismiss();

                                        Intent intent = new Intent(MainActivity.this, InternTask.class);
                                        startActivity(intent);
                                        finish();
                                } else
                                        Toast.makeText(MainActivity.this, "SOME ERROR HAS BEEN OCCURED PLEASE CHECK", Toast.LENGTH_SHORT).show();


                                } catch (JSONException e) {
                                    progressDialog.dismiss();
                                    Log.e("ERROR" , " ERROR");
                                    Toast.makeText(MainActivity.this, "NO DATA FOUND!", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.setCancelable(true);
                                progressDialog.dismiss();
                                Toast.makeText(MainActivity.this, "Some error occurred :(", Toast.LENGTH_LONG).show();
                            }
                        }) {


                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> params2 = new HashMap<String, String>();
                                params2.put("Authentication-Token", token1);
                                Log.e("HEADER", params2.toString()+"\n\n\n");
                                return params2;
                            }
                        };
                        rq.add(stringRequest);

                    } catch (JSONException e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this,  "Something went wrong :(", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(internet == false) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this,  "Please Check your internet connection!", Toast.LENGTH_LONG).show();
                    } else if(internet == true){
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this,  "Incorrect Username or Password\n OR Slow Connection", Toast.LENGTH_LONG).show();
                    }
                }
            });
            requestQueue.add(jsonObjectRequest);
        }


//        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//
//                try {
//
//                    for (int i = 0; i < response.length(); i++) {
//                        JSONObject person = (JSONObject) response
//                                .get(i);
//
//                        String jname = person.getString("name");
//                        String  jemail = person.getString("email");
//                        JSONObject phone = person
//                                .getJSONObject("phone");
//                        String jhome = phone.getString("home");
//                        String jmobile = phone.getString("mobile");
//
//                        if (gname.equalsIgnoreCase(jname)) {
//                            Log.e("IT IS ", "REACHING HERE");
//                            if (gno.equals(jmobile)) {
//
//                                Log.e("IT IS ", "REACHING HERE ALSO");
//                                String s = "Successful \n"+jname + "\n" + jemail + "\n" + jhome + "\n" + jmobile;
//
//                                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
//                                Log.e("Message ", s+"\n");
//
//                                if(jname.equals("Tommy")){
//
//                                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
//                                    SharedPreferences.Editor editor = settings.edit();
//                                    editor.putString("logged", "in_logged");
//                                    editor.commit();
//
//                                    Intent intent = new Intent(MainActivity.this, Sales.class);
//                                    startActivity(intent);
//                                    finish();
//                                } else if (jname.equals("Ravi Tamada")){
//
//                                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
//                                    SharedPreferences.Editor editor = settings.edit();
//                                    editor.putString("logged", "ad_logged");
//                                    editor.commit();
//
//                                    Intent intent = new Intent(MainActivity.this, Admin.class);
//                                    startActivity(intent);
//                                    finish();
//                                }
//                                break;
//                            } else
//                                Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();
//                        } else
//                            continue;
//                    }
////                        if(jemail != gno){
////                        Toast.makeText(getApplicationContext(), "NO USER FOUND", Toast.LENGTH_SHORT).show();}
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("error is ", error.toString());
//                Toast.makeText(getApplicationContext(),
//                        error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//        requestQueue.add(jsonArrayRequest);
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
               System.exit(1);
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 20000);
    }



//    /*@Override
//    public void onBackPressed() {
//        *//*Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra("EXIT", true);
//        startActivity(intent);*//*
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//        finish();
//        System.exit(1);
//        finishAffinity();
//    }*/
}
