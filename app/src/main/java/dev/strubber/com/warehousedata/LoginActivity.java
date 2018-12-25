package dev.strubber.com.warehousedata;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private Button btn_login;
    private EditText edt_username, edt_password;
    private ProgressDialog progressDialog;
    private String json_response;
    private String login_id, login_personnal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_login = findViewById(R.id.btn_login);
        edt_username = findViewById(R.id.edt_username);
        edt_password = findViewById(R.id.edt_password);

        progressDialog = new ProgressDialog(this);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callLogin();
            }
        });


        //for test
//        edt_username.setText("st772405");
//        edt_password.setText("st772405");

    }

    private void callLogin() {
        String HTTP_URL = "http://hrmsoftware.strubberdata.com/personnel_img/check_login.php?user=" + edt_username.getText().toString() + "&pass=" + edt_password.getText().toString();

        progressDialog.setMessage("กำลัง เข้าสู่ระบบ...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest request = new StringRequest(HTTP_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contains("[]")) {
                    Toast.makeText(LoginActivity.this, "Username/Password Incorrect !!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    json_response = response;
                    new LoginAsync(getApplicationContext()).execute();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Can't connect internet ,please check your network or contact admin.", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }
    private class LoginAsync extends AsyncTask<Void, Void, Void> {

        public Context context;

        public LoginAsync(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                if (json_response != null) {
                    JSONArray jsonArray = null;

                    try {
                        jsonArray = new JSONArray(json_response);
                        JSONObject jsonObject;

                        for (int i = 0; i < jsonArray.length(); i++) {

                            jsonObject = jsonArray.getJSONObject(i);

                            login_id = jsonObject.getString("login_id");
                            login_personnal = jsonObject.getString("login_personnal");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            progressDialog.dismiss();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("LOGIN_ID", login_id);
            intent.putExtra("LOGIN_PERSONAL", login_personnal);
            startActivity(intent);
        }
    }
}
