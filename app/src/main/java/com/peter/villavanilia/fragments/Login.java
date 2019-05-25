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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import java.util.HashMap;
import java.util.Map;

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
                calApi();
        } else
            Common.showErrorAlert(getActivity(), getString(R.string.error_no_internet_connection));


    }

    private void calApi() {

        alertDialog.show();
        final Gson gson = new Gson();

        JSONObject postparams = new JSONObject();
        try {
            postparams.put("user_email", email);
            postparams.put("user_password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String signin_url = getString(R.string.api) + "LoginUser.php";
        final RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, signin_url, postparams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                alertDialog.dismiss();
                requestQueue.stop();
                String r = response.toString();

                try {

                    LoginData current_user = gson.fromJson(r, LoginData.class);
                    Common.currentUser = current_user;
                    Paper.book("villa_vanilia").write("current_user", current_user);

                    Intent intent = new Intent(getContext(), MainActivity.class);
                    getContext().startActivity(intent);

                } catch (Exception e) {
                    Common.showErrorAlert(getActivity(), getString(R.string.error_please_try_again_later));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                alertDialog.dismiss();
                Common.showErrorAlert(getActivity(), getString(R.string.login_faild));
                requestQueue.stop();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("cache-control", "application/json");
                return params;
            }
        };

        requestQueue.add(stringRequest);

    }

   /* private void calApi() {

        alertDialog.show();

        JSONObject postparams = new JSONObject();
        try {
            postparams.put("user_email", email);
            postparams.put("user_password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String signin_url = getString(R.string.api) + "LoginUser.php";
        final RequestQueue requestQueue = Volley.newRequestQueue(getContext());

         JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, signin_url,postparams, new Response.Listener<JSONObject>() {
             @Override
             public void onResponse(JSONObject response) {

                 alertDialog.dismiss();
                 Log.d("ststst22", "result login: " + response);


                 try {

                     JSONObject customerInfo = response.getJSONObject("user");
                     String jwt = response.getString("jwt");
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



                     Intent intent = new Intent(getContext(), MainActivity.class);
                      getContext().startActivity(intent);
                 } catch (JSONException e) {
                     Common.showErrorAlert(getActivity(), getString(R.string.error_please_try_again_later));
                 }
                 requestQueue.stop();
             }
         }, new Response.ErrorListener() {
             @Override
             public void onErrorResponse(VolleyError error) {
                 alertDialog.dismiss();
                 Common.showErrorAlert(getActivity(),getString(R.string.login_faild));
                  requestQueue.stop();
             }
         }){
             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {
                 Map<String, String>  params = new HashMap<String, String>();
                 params.put("cache-control", "application/json");
                 return params;
             }
         };

          requestQueue.add(stringRequest);

    }*/

    private boolean validate(String email, String password) {

        if (TextUtils.isEmpty(email)) {
            Common.showErrorAlert(getActivity(), getString(R.string.please_enter_email));
            return false;
        } else if (TextUtils.isEmpty(password)) {
            Common.showErrorAlert(getActivity(), getString(R.string.please_enter_password));
            return false;
        }

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

                Log.d("ststst1100", "result login: " + str);

                byte[] outputInBytes = str.getBytes("UTF-8");

                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                Log.d("ststst1188800", "result login: " + str);

                OutputStream OS = httpURLConnection.getOutputStream();
                OS.write(outputInBytes);
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                Log.d("ststst1188111", "result login: " + str);

                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();

                Log.d("ststst1188222", "result login: " + str);
                BufferedReader bufferedReader = null;


                try {

                    InputStream is = httpURLConnection.getInputStream();
                    bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                } catch (IOException e) {
                    Log.d("ststst11888333", "rrrrrorr: " + e.getMessage());
                } finally {
                    bufferedReader.close();
                }
                Log.d("ststst11888333", "result login: " + str);

                String response = "";
                String line = "";
                /*while ((line = bufferedReader.readLine()) != null) {
                    response += line;
                    Log.d("ststst22", "result login: " + line);
                }

                bufferedReader.close();
                IS.close();*/
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

            Log.d("ststst1199", "result login: " + result);

            if (result.isEmpty()) {
                Common.showErrorAlert(getActivity(), getString(R.string.login_faild));
                email_ed.setText("");
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


                    //Intent intent = new Intent(getContext(), MainActivity.class);
                    // getContext().startActivity(intent);
                } catch (JSONException e) {
                    Common.showErrorAlert(getActivity(), getString(R.string.error_please_try_again_later));
                }
            }

        }
    }

}
