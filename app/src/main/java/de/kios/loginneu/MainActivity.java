package de.kios.loginneu;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private UserLoginTask authTask = null;
    private ProgressBar bar;
    private String antwort;
    private AdView werbung;
    private EditText prompt_name;
    private EditText prompt_mail;
    private EditText prompt_password;
    private CheckBox cb_signup;
    private Button signin;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signin = (Button) findViewById(R.id.button_signin);
        signin.setOnClickListener(this);
        bar = (ProgressBar) this.findViewById(R.id.progressBar);
        antwort = getResources().getString(R.string.alert_standard);
        werbung = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        werbung.loadAd(adRequest);
        prompt_name = (EditText) findViewById(R.id.prompt_username);
        prompt_mail = (EditText) findViewById(R.id.prompt_email);
        prompt_password = (EditText) findViewById(R.id.prompt_password);
        cb_signup = (CheckBox)findViewById(R.id.cb_signup);


    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.button_signin:
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                v.clearFocus();
                attemptLogin();
                break;
        }
    }

    public void attemptLogin() {

        if (authTask != null) {
            return;
        }


        String name = prompt_name.getText().toString();
        String mail = prompt_mail.getText().toString();
        String password = prompt_password.getText().toString();
        String cb = String.valueOf(cb_signup.isChecked());

        if (mail.length()<1)
        {
            Toast.makeText(this, R.string.alert_email, Toast.LENGTH_LONG).show();
            findViewById(R.id.prompt_email).requestFocus();
        }
        else if (password.length()<1)
        {
            Toast.makeText(this, R.string.alert_short_pw, Toast.LENGTH_LONG).show();
            findViewById(R.id.prompt_password).requestFocus();
        }
        else
        {
            authTask = new UserLoginTask(name, mail, password, cb);
            authTask.execute((Void) null);
        }
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean>
    {

        private String nm;
        private String em;
        private String pw;
        private String cb;


        UserLoginTask(String username, String email, String password, String checkbox) {
            nm = username;
            em = email;
            pw = password;
            cb = checkbox;
        }

        @Override
        protected void onPreExecute(){
            signin.setVisibility(View.GONE);
            prompt_name.setVisibility(View.GONE);
            prompt_mail.setVisibility(View.GONE);
            prompt_password.setVisibility(View.GONE);
            cb_signup.setVisibility(View.GONE);
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String urlstring = "http://192.168.17.88/scripts/android.py/anmelden";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, urlstring, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    antwort = response;
                }

            }
                    , new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("HRGL", error.getMessage());
                }
            })
            {
                @Override
                public String getBodyContentType()
                {
                    return "application/x-www-form-urlencoded; charset=UTF-8";
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("nm", nm);
                    params.put("em", em);
                    params.put("pw", pw);
                    params.put("neu", cb);
                    return params;
                }
            };
            queue.add(stringRequest);

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            if (antwort=="false")
            {
                return false;
            }
            return true;


        }

        @Override
        protected void onPostExecute(final Boolean success)
        {
            bar.setVisibility(View.GONE);
            signin.setVisibility(View.VISIBLE);
            prompt_name.setVisibility(View.VISIBLE);
            prompt_mail.setVisibility(View.VISIBLE);
            prompt_password.setVisibility(View.VISIBLE);
            cb_signup.setVisibility(View.VISIBLE);
            int raus = R.string.alert_standard;
            Log.d("HRGL", antwort);
            String username = "";
            if (antwort.substring(0,3).equals("314"))
            {
                username = antwort.substring(4);
                antwort = antwort.substring(0,3);
            }

            Log.d("HRGL", username);

            switch(antwort)
            {
                case "nyr":
                    raus = R.string.alert_nyr;
                    break;
                case "fpw":
                    raus = R.string.alert_fpw;
                    break;
                case "nat":
                    raus = R.string.alert_nat;
                    break;
                case "314":
                    raus = R.string.begruessung;
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                    break;
            }
            Toast.makeText(MainActivity.this, raus, Toast.LENGTH_LONG).show();
            authTask = null;
        }
    }

}
