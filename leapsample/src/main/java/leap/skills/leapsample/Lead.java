package leap.skills.leapsample;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class Lead extends Activity {
    EditText date, email, ins_name, name, others, phone, time;
    CheckBox alumni, banner_ad, friends, sales_team, social_media;
    DatePickerDialog.OnDateSetListener onDateSetListener;
    TextView tv5, tv6;
    Button submit;
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    String gname, gphone, gemail, gins_name, gdate, gtime, gothers;
    Boolean gsales, gsocial, galumni, gbanner, gfriends;
    String url = "https://boiling-refuge-50245.herokuapp.com/survey/save_survey";
    String url1 = "https://boiling-refuge-50245.herokuapp.com/survey/search?query=9650094700";
    Entered Entered;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_lead);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("We are fetching the data.");
        progressDialog.setCancelable(false);

        initViews();
        final Calendar calendar = Calendar.getInstance();
        tv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Lead.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, onDateSetListener, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int month1 = month+1;
                date.setVisibility(View.VISIBLE);
                date.setInputType(View.AUTOFILL_TYPE_NONE);
                if(month1<10 && dayOfMonth<10)
                    date.setText("0"+dayOfMonth+"-0"+month1+"-"+year);
                else if(month1>9 && dayOfMonth>9)
                    date.setText(dayOfMonth+"-"+month1+"-"+year);
                else if(month1>9 && dayOfMonth<10)
                    date.setText("0"+dayOfMonth+"-"+month1+"-"+year);
                else if(month1<10 && dayOfMonth>9)
                    date.setText(dayOfMonth+"-0"+month1+"-"+year);

            }
        };

        tv6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = calendar.get(Calendar.HOUR);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(Lead.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        time.setVisibility(View.VISIBLE);
                        time.setInputType(View.AUTOFILL_TYPE_NONE);
                        int hourOfDay1 = hourOfDay-12;
                        if(hourOfDay<10 && minute<10)
                            time.setText("0"+hourOfDay+":0"+minute+" AM");
                        else if(hourOfDay<10 && minute>9)
                            time.setText("0"+hourOfDay+":"+minute+" AM");
                        else if(hourOfDay>9 && hourOfDay<12 && minute<10)
                            time.setText(hourOfDay+":0"+minute+" AM");
                        else if(hourOfDay>9 && hourOfDay<12 && minute>9)
                            time.setText(hourOfDay+":"+minute+" AM");
                        if(hourOfDay==12 && minute<10)
                            time.setText(hourOfDay+":0"+minute+" PM");
                        else if(hourOfDay==12 && minute>9)
                            time.setText(hourOfDay+":"+minute+" PM");
                        else if(12<hourOfDay && hourOfDay<22 && minute<10)
                            time.setText("0"+hourOfDay1+":0"+minute+" PM");
                        else if(12<hourOfDay && hourOfDay<22 && minute>9)
                            time.setText("0"+hourOfDay1+":"+minute+" PM");
                        else if(hourOfDay>21 && minute<10)
                            time.setText(hourOfDay1+":0"+minute+" PM");
                        else if(hourOfDay>21 && minute>9)
                            time.setText(hourOfDay1+":"+minute+" PM");
                    }
                }, hour, minute, false);
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.show();
            }
        });

        requestQueue = Volley.newRequestQueue(Lead.this);
        progressDialog = new ProgressDialog(Lead.this);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                get();
                Entered= new Entered();
                Entered.setGname(gname);
                Entered.setGphone(gphone);
                Entered.setGemail(gemail);
                Entered.setGins_name(gins_name);
                Entered.setGdate(gdate);
                Entered.setGtime(gtime);
                Entered.setGsales(gsales);
                Entered.setGsocial(gsocial);
                Entered.setGalumni(galumni);
                Entered.setGfriends(gfriends);
                Entered.setGfriends(gfriends);
                Entered.setGbanner(gbanner);
                Entered.setGothers(gothers);






                JSONObject params = new JSONObject();

                try {
                    params.put("name", Entered.getGname());
                    params.put("phone", Entered.getGphone());
                    params.put("email", Entered.getGemail());
                    params.put("date", Entered.getGdate());
                    params.put("time", Entered.getGtime());
                    params.put("institution_name", Entered.getGins_name());
                    params.put("sales_team", Entered.getGsales());
                    params.put("social_media", Entered.getGsocial());
                    params.put("friends", Entered.getGfriends());
                    params.put("alumni", Entered.getGalumni());
                    params.put("banner_ad", Entered.getGbanner());
                    params.put("others", Entered.getGothers());

                    //Toast.makeText(Lead.this, params.toString(), Toast.LENGTH_SHORT).show();
                    Log.e("data", params.toString()+"\n\n");
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }



                RequestQueue queue  = Volley.newRequestQueue(Lead.this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                progressDialog.dismiss();

                                Log.e("Server: ", response.toString()+"\n\n");

                                Toast.makeText(Lead.this, "Successfully Inserted", Toast.LENGTH_SHORT).show();

                                Intent i = new Intent(Lead.this, Sales.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                i.putExtra("EXIT", true);
                                startActivity(i);
                                finish();

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressDialog.dismiss();

                        Toast.makeText(Lead.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
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
                        Toast.makeText(Lead.this, "Slow Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                });
                queue.add(jsonObjectRequest);
            }
        });
    }

    public void initViews(){
        date = findViewById(R.id.date);
        email = findViewById(R.id.email);
        ins_name = findViewById(R.id.ins_name);
        name = findViewById(R.id.name);
        others = findViewById(R.id.others);
        phone = findViewById(R.id.phone);
        time = findViewById(R.id.time);
        alumni = findViewById(R.id.alumni);
        banner_ad = findViewById(R.id.banner_ad);
        friends = findViewById(R.id.friends);
        sales_team = findViewById(R.id.sales_team);
        social_media = findViewById(R.id.social_media);
        tv5 = findViewById(R.id.tv5);
        tv6 = findViewById(R.id.tv6);
        submit = findViewById(R.id.submit);
    }

    public void get(){

        gname = name.getText().toString().trim();
        gphone = phone.getText().toString().trim();
        gemail = email.getText().toString().trim();
        gins_name = ins_name.getText().toString().trim();
        gdate = date.getText().toString().trim();
        gtime = time.getText().toString().trim();
        if(sales_team.isChecked()==true){
            gsales = true;
        } else gsales = false;

        if(social_media.isChecked()==true){
            gsocial = true;
        } else gsocial = false;

        if(alumni.isChecked()==true){
            galumni = true;
        } else galumni = false;

        if(banner_ad.isChecked()==true){
            gbanner = true;
        } else gbanner = false;

        if(friends.isChecked()==true){
            gfriends = true;
        } else gfriends = false;

        if(others.getText().toString()!=null)
            gothers = others.getText().toString().trim();
        else gothers = "";
    }
}