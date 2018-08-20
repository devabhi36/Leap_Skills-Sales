package leap.skills.leapsample;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class Select_Producti extends Activity {
    TextView cp, cp_plus, name_show, phone_show, email_show;
    public static String name_show2, phone_show2, email_show2, token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_select__producti);

        cp = (TextView) findViewById(R.id.cp);
        cp_plus = (TextView) findViewById(R.id.cp_plus);
        name_show = (TextView) findViewById(R.id.name_show2);
        phone_show = (TextView) findViewById(R.id.phone_show2);
        email_show = (TextView) findViewById(R.id.email_show2);

        name_show.setText("Name: "+name_show2);
        phone_show.setText("Phone: "+phone_show2);
        email_show.setText("Email: "+email_show2);

        Payi.name_show3 = name_show2;
        Payi.phone_show3 = phone_show2;
        Payi.email_show3 = email_show2;
        Payi.token = token;


        cp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Payi.selected = "CP";
                Payi.total = "Total Amount RS. 3999";
                Intent intent = new Intent(Select_Producti.this, Payi.class);
                startActivity(intent);
            }
        });

        cp_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Payi.selected = "CP+";
                Payi.total = "Total Amount RS. 6499";
                Intent intent = new Intent(Select_Producti.this, Payi.class);
                startActivity(intent);
            }
        });
    }
}
