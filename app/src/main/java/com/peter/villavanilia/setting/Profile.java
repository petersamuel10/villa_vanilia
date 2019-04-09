package com.peter.villavanilia.setting;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.google.gson.Gson;
import com.peter.villavanilia.R;
import com.peter.villavanilia.common.Common;
import com.peter.villavanilia.model.RegisterModel;

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

public class Profile extends AppCompatActivity {

    @OnClick(R.id.back_arrow)
    public void back(){onBackPressed();}
    @BindView(R.id.email_ed)
    EditText email_ed;
    @BindView(R.id.user_name_ed)
    EditText user_name_ed;
    @BindView(R.id.phone_ed)
    EditText phone_ed;
    @BindView(R.id.address_ed)
    EditText address_ed;

    String email,name,phone,address;

    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_profile);
        ButterKnife.bind(this);

        alertDialog = Common.alert(this);

        user_name_ed.setText(Common.currentUser.getUser().getUser_name());
        email_ed.setText(Common.currentUser.getUser().getUser_email());
        phone_ed.setText(Common.currentUser.getUser().getUser_telep());
        address_ed.setText(Common.currentUser.getUser().getUser_address());

    }

    @OnClick(R.id.updateBtn)
    public void register(){

        name = user_name_ed.getText().toString();
        email = email_ed.getText().toString();
        phone = phone_ed.getText().toString();
        address = address_ed.getText().toString();

        if(validate(name,email,phone,address)){

            RegisterModel registerModel = new RegisterModel(name,email,Common.currentUser.getUser().getUser_password(),
                    phone,address,Common.currentUser.getJwt(),Common.currentUser.getUser().getUser_id());

            if(Common.isConnectToTheInternet(this)) {
                new updateBackgroundTask(this,registerModel).execute();
            }else
                Common.showErrorAlert(this,getString(R.string.error_no_internet_connection));
        }


    }

    private boolean validate(String name, String email, String phone, String address) {

        if(TextUtils.isEmpty(name)){
            Common.showErrorAlert(this,getString(R.string.please_enter_user_name));
            return false;
        }else if (TextUtils.isEmpty(email)){
            Common.showErrorAlert(this,getString(R.string.please_enter_email));
            return false;
        }else if (TextUtils.isEmpty(phone)){
            Common.showErrorAlert(this,getString(R.string.please_enter_your_phone));
            return false;
        }else if (TextUtils.isEmpty(address)){
            Common.showErrorAlert(this,getString(R.string.please_enter_your_address));
            return false;
        }

        return true;
    }

    private class updateBackgroundTask extends AsyncTask<String, Void, String> {

        public JSONObject jsonObject=null;
        public RegisterModel registerModel;

        updateBackgroundTask(Activity activity, RegisterModel registerModel) {
            this.registerModel = registerModel;
        }

        @Override
        protected void onPreExecute() {
            alertDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String signin_url = getString(R.string.api)+"UpdateUser.php";
            try {
                URL url = new URL(signin_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("cache-control", "application/json");
                httpURLConnection.setConnectTimeout(7000);
                httpURLConnection.setReadTimeout(7000);

                Gson gson = new Gson();
                String str = gson.toJson(registerModel);

                Log.d("vvvv",str);

               // String str =  "{\"user_name\": \""+name+"\",\"user_email\": \"" + email + "\", \"user_password\": \"" + password + "\"}";
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
                Common.showErrorAlert(Profile.this,getString(R.string.error_please_try_again_later_));
                return "";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            alertDialog.dismiss();

            Log.d("ststst", "result login: " + result);

            try {
                jsonObject = new JSONObject(new String(result));

                if (result.contains("User was updated")) {
                    Common.currentUser.getUser().setUser_name(name);
                    Common.currentUser.getUser().setUser_email(email);
                    Common.currentUser.getUser().setUser_telep(phone);
                    Common.currentUser.getUser().setUser_address(address);
                } else {

                }
            } catch (JSONException e) {
                Common.showErrorAlert(Profile.this,getString(R.string.error_please_try_again_later_));
            }

        }
    }

}
