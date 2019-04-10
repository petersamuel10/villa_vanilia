package com.peter.villavanilia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.peter.villavanilia.common.Common;
import com.peter.villavanilia.model.LoginData;
import com.peter.villavanilia.model.RegisterModel;
import com.peter.villavanilia.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
                calApi();
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

    private void calApi() {

        JSONObject postparams = new JSONObject();
        try {
            postparams.put("user_name", name);
            postparams.put("user_email", email);
            postparams.put("user_password", password);
            postparams.put("user_telep", phone);
            postparams.put("user_address", address);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String signUp_url = getResources().getString(R.string.api)+"CreateUsr.php";
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, signUp_url,postparams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

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


                    Log.d("bbbn", "result login: " + response);
                        Intent intent = new Intent(Register.this, MainActivity.class);
                        startActivity(intent);

                } catch (JSONException e) {
                    Common.showErrorAlert(Register.this, getString(R.string.error_please_try_again_later));
                }
                requestQueue.stop();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Common.showErrorAlert(Register.this, getString(R.string.email_register_before));
                Log.d("bbbn111","result login: " +  error.getMessage());
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

    }

}
