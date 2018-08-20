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
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.Spinner;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Payi extends Activity {
    TextView name_show, phone_show, email_show, tselected;
    public static String name_show3, phone_show3, email_show3;
    public static String selected, total, token;
    public static String stu_id;
    CheckBox ctotal;
    boolean internet = false;
    Spinner pay_token;
    String payment;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_payi);

        name_show = (TextView) findViewById(R.id.name_show3);
        phone_show = (TextView) findViewById(R.id.phone_show3);
        email_show = (TextView) findViewById(R.id.email_show3);
        tselected = (TextView) findViewById(R.id.selected);
        ctotal = (CheckBox) findViewById(R.id.total);
        pay_token = (Spinner) findViewById(R.id.pay_token);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCancelable(false);

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
            internet = true;
        }
        else{
            internet = false;
            Toast.makeText(this, "Please Check your internet connection!", Toast.LENGTH_SHORT).show();
        }

        name_show.setText("Name: "+name_show3);
        phone_show.setText("Phone: "+phone_show3);
        email_show.setText("Email: "+email_show3);
        tselected.setText(selected);
        ctotal.setText(total);

        tselected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selected.equals("CP")){
                    Toast.makeText(Payi.this, "Product Selected By You is CP", Toast.LENGTH_SHORT).show();
                } else if(selected.equals("CP+")){
                    Toast.makeText(Payi.this, "Product Selected By You is CP+", Toast.LENGTH_SHORT).show();
                }
            }
        });

        pay_token.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    ctotal.setEnabled(true);
                    ctotal.setClickable(true);
                }
                else {
                    ctotal.setChecked(false);
                    ctotal.setEnabled(false);
                    ctotal.setClickable(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void submit_paymenti(View view){
        progressDialog.show();

        if(ctotal.isChecked() == false && pay_token.getSelectedItem().toString().equals("Select")){
            //Toast.makeText(this, "Select a payment mode", Toast.LENGTH_SHORT).show();
        } else if(ctotal.isChecked() == true && pay_token.getSelectedItem().equals("Select")){
            if(selected.equals("CP")){
                payment = "3999";
                //Toast.makeText(this, "amount is: "+ payment, Toast.LENGTH_SHORT).show();
            } else if(selected.equals("CP+")){
                payment = "6499";
                //Toast.makeText(this, "amount is: "+ payment, Toast.LENGTH_SHORT).show();
            }
        } else if(ctotal.isChecked() == false && !pay_token.getSelectedItem().toString().equals("Select")){
            payment = pay_token.getSelectedItem().toString();
            //Toast.makeText(this, "amount is: "+ payment, Toast.LENGTH_SHORT).show();
        }

        String course = null;
        if(selected.equals("CP"))
            course = "cp";
        else if(selected.equals("CP+"))
            course = "cp+";

        String url1 = "http://jaipur.ap-south-1.elasticbeanstalk.com/payment/";

        Entered entered = new Entered();
        entered.setStu_id(Integer.parseInt(stu_id));
        entered.setAmount(Integer.parseInt(payment));
        entered.setCourse_type(course);
        JSONObject params = new JSONObject();
        try {
            params.put("student_id", entered.getStu_id());
            params.put("amount", entered.getAmount());
            params.put("course_type", entered.getCourse_type());
            Log.e("PARAMS: ", params.toString()+"\n\n\n");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest5 = new JsonObjectRequest(Request.Method.POST, url1, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("RESPONSE: ", response.toString()+"\n\n\n");
                Log.e("HELLO", " WORLD");
                progressDialog.dismiss();
                Toast.makeText(Payi.this, "SUCCESSFULLY SUBMITTED BY INTERN", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Payi.this, InternTask.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.setCancelable(true);
                Log.e("ERROR IS: ", error.toString());
                Toast.makeText(Payi.this, error.toString(), Toast.LENGTH_LONG).show();
                if(internet == false) {
                    Toast.makeText(Payi.this, "Please Check your internet connection!", Toast.LENGTH_LONG).show();
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
        jsonObjectRequest5.setRetryPolicy(new RetryPolicy() {
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
                Toast.makeText(Payi.this, "Slow Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest5);
    }
}
