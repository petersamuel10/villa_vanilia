package com.peter.villavanilia.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.gson.Gson;
import com.peter.villavanilia.MainActivity;
import com.peter.villavanilia.R;
import com.peter.villavanilia.Register;
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

public class Login extends Fragment {

    @BindView(R.id.email_ed)
    EditText email_ed;
    @BindView(R.id.password_ed)
    EditText password_ed;

    @OnClick(R.id.forget_password)
    public void forget_password() {
        ///////////////////
    }

    @OnClick(R.id.create_new_user)
    public void register() {
        getContext().startActivity(new Intent(getContext(), Register.class));
    }

    String email, password;
    AlertDialog alertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);

        alertDialog = Common.alert(getActivity());

        Paper.init(getContext());

        return view;
    }

    @OnClick(R.id.loginBtn)
    public void login() {

        email = email_ed.getText().toString();
        password = password_ed.getText().toString();
        if (Common.isConnectToTheInternet(getContext())) {
            if (validate(email, password))
                new LoginBackgroundTask(getActivity()).execute();
        } else
            Common.showErrorAlert(getActivity(), getString(R.string.error_no_internet_connection));


    }

    private boolean validate(String email, String password) {

        if (TextUtils.isEmpty(email)) {
            Common.showErrorAlert(getActivity(), getString(R.string.please_enter_email));
            return false;
        } else if (TextUtils.isEmpty(password)) {
            Common.showErrorAlert(getActivity(), getString(R.string.please_enter_password));
            return false;
        } else
            return true;
    }

    private class LoginBackgroundTask extends AsyncTask<String, Void, String> {

        public JSONObject jsonObject = null;

        LoginBackgroundTask(Activity activity) {

        }

        @Override
        protected void onPreExecute() {
            alertDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String signin_url = getString(R.string.api) + "LoginUser.php";
            try {
                URL url = new URL(signin_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("cache-control", "application/json");
                httpURLConnection.setConnectTimeout(7000);
                httpURLConnection.setReadTimeout(7000);


                RegisterModel registerModel = new RegisterModel(email, password);

                Gson gson = new Gson();
                String str = gson.toJson(registerModel);

                byte[] outputInBytes = str.getBytes("UTF-8");

                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream OS = httpURLConnection.getOutputStream();
                OS.write(outputInBytes);
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
                Common.showErrorAlert(getActivity(), getString(R.string.error_please_try_again_later));
                return "";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            alertDialog.dismiss();

            Log.d("ststst", "result login: " + result);

            if (result.isEmpty()) {
                Common.showErrorAlert(getActivity(), getString(R.string.login_faild));
            } else {
                try {

                    jsonObject = new JSONObject(new String(result));

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

                    Gson gson = new Gson();
                    String str = gson.toJson(user);

                    Log.i("bbbn", str);


                    Intent intent = new Intent(getContext(), MainActivity.class);
                    getContext().startActivity(intent);
                } catch (JSONException e) {
                    Common.showErrorAlert(getActivity(), getString(R.string.error_please_try_again_later));
                }
            }

        }
    }

}
