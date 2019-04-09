package com.peter.villavanilia;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.peter.villavanilia.common.Common;
import com.peter.villavanilia.model.LoginData;
import com.peter.villavanilia.model.RegisterModel;
import com.peter.villavanilia.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;

public class Register extends AppCompatActivity {

    @BindView(R.id.back_arrow)
    ImageView back_arrow;
    @OnClick(R.id.back_arrow)
    public void back(){onBackPressed();}
    @BindView(R.id.email_ed)
    EditText email_ed;
    @BindView(R.id.user_name_ed)
    EditText user_name_rd;
    @BindView(R.id.password_ed)
    EditText password_ed;
    @BindView(R.id.confirm_password_ed)
    EditText confirm_password_ed;
    @BindView(R.id.phone_ed)
    EditText phone_ed;
    @BindView(R.id.address_ed)
    EditText address_ed;

    String email,name,password,confirm_password,phone,address;
    RegisterModel registerModel;

    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        alertDialog = Common.alert(this);
        if(Common.isArabic){back_arrow.setRotation(180);}

    }

    @OnClick(R.id.registerBtn)
    public void register(){

        name = user_name_rd.getText().toString();
        email = email_ed.getText().toString();
        password = password_ed.getText().toString();
        phone = phone_ed.getText().toString();
        address = address_ed.getText().toString();
        confirm_password = confirm_password_ed.getText().toString();

        if(validate(name,email,password,confirm_password,phone,address)){
            registerModel = new RegisterModel(name,email,password,phone,address);
            if(Common.isConnectToTheInternet(this)) {
                new RegisterBackgroundTask(this).execute();
            }else
                Common.showErrorAlert(this,getString(R.string.error_no_internet_connection));
        }

    }

    private boolean validate(String name, String email, String password, String confirm_password, String phone, String address) {

        if(TextUtils.isEmpty(name)){
            Common.showErrorAlert(Register.this,getString(R.string.please_enter_user_name));
            return false;
        }else if (TextUtils.isEmpty(email)){
            Common.showErrorAlert(Register.this,getString(R.string.please_enter_email));
            return false;
        }else if (TextUtils.isEmpty(password)){
            Common.showErrorAlert(Register.this,getString(R.string.please_enter_password));
            return false;
        }else if (TextUtils.isEmpty(confirm_password)){
            Common.showErrorAlert(Register.this,getString(R.string.please_enter_confirm_password));
            return false;
        }else if (TextUtils.isEmpty(phone)){
            Common.showErrorAlert(Register.this,getString(R.string.please_enter_your_phone));
            return false;
        }else if (TextUtils.isEmpty(address)){
            Common.showErrorAlert(Register.this,getString(R.string.please_enter_your_address));
            return false;
        }else if (!password.equals(confirm_password)){
            Common.showErrorAlert(Register.this,getString(R.string.error_confirm_password_not_match_new_password));
            return false;
        }

    return true;
    }

    private class RegisterBackgroundTask extends AsyncTask<String, Void, String> {

        public JSONObject jsonObject=null;

        RegisterBackgroundTask(Activity activity) {
        }

        @Override
        protected void onPreExecute() {
            alertDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String signin_url = getResources().getString(R.string.api)+"CreateUsr.php";
            try {
                URL url = new URL(signin_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("cache-control", "application/json");
                httpURLConnection.setConnectTimeout(7000);
                httpURLConnection.setReadTimeout(7000);

                Gson gson = new Gson();
                String str = gson.toJson(registerModel);

                byte[] outputInBytes = str.getBytes("UTF-8");

                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream OS = httpURLConnection.getOutputStream();
                OS.write( outputInBytes );
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();

                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));

                String response = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    response += line;
                }

                bufferedReader.close();
                IS.close();
                httpURLConnection.disconnect();
                return response;

            } catch (IOException e) {
                Common.showErrorAlert(Register.this,e.getMessage());
                return "";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            alertDialog.dismiss();

            Log.d("ststst", "result login: " + result);

            if (result.isEmpty()) {
                Common.showErrorAlert(Register.this, getString(R.string.email_register_before));
            } else {
                // Toast.makeText(Login.this, "//////////////", Toast.LENGTH_SHORT).show();
                try {
                    jsonObject = new JSONObject(new String(result));
                    String status = jsonObject.getString("message");

                    if (status.equals("error")) {
                        Common.showErrorAlert(Register.this, status);
                    } else {

                        JSONObject customerInfo = jsonObject.getJSONObject("user");
                        String jwt = jsonObject.getString("jwt");
                        String user_id = customerInfo.getString("user_id");
                        String user_name = customerInfo.getString("user_name");
                        String user_email = customerInfo.getString("user_email");
                        String user_password = customerInfo.getString("user_password");
                        String user_telep = customerInfo.getString("user_telep");
                        String user_address = customerInfo.getString("user_address");
                        String user_token_device = customerInfo.getString("user_token_device");

                        User user = new User(user_id, user_name, user_email, user_password, user_telep, user_address, user_token_device);
                        LoginData current_user = new LoginData(null, jwt, user);

                        Common.currentUser = current_user;

                        Paper.book("villa_vanilia").write("current_user", current_user);


                        Intent intent = new Intent(Register.this, MainActivity.class);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    Common.showErrorAlert(Register.this, getString(R.string.error_please_try_again_later));
                }
            }

        }
    }
}
