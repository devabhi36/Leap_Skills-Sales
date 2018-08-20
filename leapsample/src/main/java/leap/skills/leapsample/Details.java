package leap.skills.leapsample;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
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

public class Details extends Activity {
    TextView name_show, phone_show, email_show, purchased, amt_paid;
    public static String name_show4, phone_show4, email_show4, token, stu_id;
    boolean internet = false;
    String purchased1="", amt_paid1;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_details);

        name_show = (TextView) findViewById(R.id.name_show4);
        phone_show = (TextView) findViewById(R.id.phone_show4);
        email_show = (TextView) findViewById(R.id.email_show4);
        purchased = (TextView) findViewById(R.id.purchased_pro);
        amt_paid = (TextView) findViewById(R.id.amount_paid);

        name_show.setText("Name: "+name_show4);
        phone_show.setText("Phone: "+phone_show4);
        email_show.setText("Email: "+email_show4);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCancelable(false);
       // progressDialog.show();

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
            internet = true;
        }
        else{
            internet = false;
            //progressDialog.dismiss();
            Toast.makeText(this, "Please Check your internet connection!", Toast.LENGTH_SHORT).show();
        }

        /*String stu_info_url = "http://jaipur.ap-south-1.elasticbeanstalk.com/student/";
        String fstu_info_url = stu_info_url+""+stu_id;

        RequestQueue requestQueue3 = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest3 = new JsonObjectRequest(Request.Method.GET, fstu_info_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject students_info = response.getJSONObject("student_info");

                    purchased.setText("PURCHASED PRODUCT: "+purchased1);
                    amt_paid.setText("AMOUNT PAID: "+amt_paid1);
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("ERROR", "SOME");
                    progressDialog.dismiss();
                    //Toast.makeText(Intern.this, "SOME ERROR", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.setCancelable(true);
                Toast.makeText(Details.this, error.toString(), Toast.LENGTH_LONG).show();
                if(internet == false) {
                    progressDialog.dismiss();
                    Toast.makeText(Details.this, "Please Check your internet connection!", Toast.LENGTH_LONG).show();
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
                Toast.makeText(Details.this, "Slow Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue3.add(jsonObjectRequest3);*/

        purchased.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(purchased1.equals("CP")){
                    Toast.makeText(Details.this, "Product Selected By You is CP", Toast.LENGTH_SHORT).show();
                } else if(purchased1.equals("CP+")){
                    Toast.makeText(Details.this, "Product Selected By You is CP+", Toast.LENGTH_SHORT).show();
                }
                else if(purchased1.equals(""))
                    Toast.makeText(Details.this, "NO PRODUCT TO SHOW", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
