package com.peter.villavanilia;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.peter.villavanilia.common.Common;
import com.peter.villavanilia.model.LoginData;
import com.peter.villavanilia.model.RegisterModel;

import org.json.JSONArray;
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
    public void back() {
        onBackPressed();
    }

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
    @BindView(R.id.phone2_ed)
    EditText phone2_ed;
    @BindView(R.id.place_btn)
    Button place_btn;
    @BindView(R.id.area_btn)
    Button area_btn;
    @BindView(R.id.block_ed)
    EditText block_ed;
    @BindView(R.id.street_ed)
    EditText street_ed;
    @BindView(R.id.avenue_ed)
    EditText avenue_ed;
    @BindView(R.id.building_ed)
    EditText building_ed;
    @BindView(R.id.floor_ed)
    EditText floor_ed;
    @BindView(R.id.appartment_ed)
    EditText apartment_ed;

    AlertDialog alertDialog;
    String email, name, password, confirm_password, phone, phone2, area_str, place_str,
            block_str, street_str, avenue_str, building_str, floor_str, apartment_str, address;
    String[] area_arr;
    RegisterModel registerModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        alertDialog = Common.alert(this);
        if (Common.isArabic) {
            back_arrow.setRotation(180);
        }

        getArea();
    }

    @OnClick(R.id.registerBtn)
    public void register() {

        name = user_name_rd.getText().toString();
        email = email_ed.getText().toString();
        password = password_ed.getText().toString();
        confirm_password = confirm_password_ed.getText().toString();
        phone = phone_ed.getText().toString();
        phone2 = phone2_ed.getText().toString();
        block_str = block_ed.getText().toString();
        street_str = street_ed.getText().toString();
        avenue_str = avenue_ed.getText().toString();
        building_str = building_ed.getText().toString();
        floor_str = floor_ed.getText().toString();
        apartment_str = apartment_ed.getText().toString();

        if (validate()) {
            address = area_str+"-"+place_str+"-"+block_str+"-"+street_str+"-"+avenue_str+"-"+building_str+"-"+floor_str+"-"+apartment_str;
            phone += "-"+phone2;
            registerModel = new RegisterModel(name, email, password, phone, address);
            if (Common.isConnectToTheInternet(this)) {
                 calApi();
            } else
                Common.showErrorAlert(this, getString(R.string.error_no_internet_connection));
        }
    }

    @OnClick(R.id.place_btn)
    public void place_selection() {
        final String[] places_arr = new String[]{"Work - العمل", "Home - البيت"};

        AlertDialog.Builder place_dialog = new AlertDialog.Builder(this);
        place_dialog.setTitle(getString(R.string.select_place));

        place_dialog.setSingleChoiceItems(places_arr, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                dialog.dismiss();
                place_str = places_arr[position];
                place_btn.setText(place_str);

            }
        });

        place_dialog.create().show();
    }

    @OnClick(R.id.area_btn)
    public void area_selection() {

        AlertDialog.Builder area_dialog = new AlertDialog.Builder(this);
        area_dialog.setTitle(getString(R.string.select_area));

        area_dialog.setSingleChoiceItems(area_arr, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                dialog.dismiss();
                area_str = area_arr[position];
                area_btn.setText(area_str);
            }
        });

        area_dialog.create().show();
    }

    private boolean validate() {

        if (TextUtils.isEmpty(name)) {
            Common.showErrorAlert(Register.this, getString(R.string.please_enter_user_name));
            return false;
        } else if (TextUtils.isEmpty(email)) {
            Common.showErrorAlert(Register.this, getString(R.string.please_enter_email));
            return false;
        } else if (TextUtils.isEmpty(password)) {
            Common.showErrorAlert(Register.this, getString(R.string.please_enter_password));
            return false;
        } else if (TextUtils.isEmpty(confirm_password)) {
            Common.showErrorAlert(Register.this, getString(R.string.please_enter_confirm_password));
            return false;
        } else if (TextUtils.isEmpty(place_str)) {
            Common.showErrorAlert(Register.this, getString(R.string.select_place));
            return false;
        } else if (TextUtils.isEmpty(area_str)) {
            Common.showErrorAlert(Register.this, getString(R.string.select_area));
            return false;
        } else if (TextUtils.isEmpty(phone)) {
            Common.showErrorAlert(Register.this, getString(R.string.please_enter_your_phone));
            return false;
        }else if (TextUtils.isEmpty(street_str)) {
            Common.showErrorAlert(Register.this, getString(R.string.please_enter)+" "+getString(R.string.street));
            return false;
        }else if (TextUtils.isEmpty(building_str)) {
            Common.showErrorAlert(Register.this, getString(R.string.please_enter)+" "+getString(R.string.building));
            return false;
        } else if (!password.equals(confirm_password)) {
            Common.showErrorAlert(Register.this, getString(R.string.error_confirm_password_not_match_new_password));
            return false;
        }


        return true;
    }

    private void getArea() {

        alertDialog.show();
        JSONObject postparams = new JSONObject();

        String signUp_url = getString(R.string.api) + "/GetArea.php";
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, signUp_url, postparams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                alertDialog.dismiss();

                try {
                    JSONArray jsonArray = response.getJSONArray("area_item");
                    area_arr = new String[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        String area_title = object.getString("area_name_eng") + " - " + object.getString("area_name_ar");
                        area_arr[i] = area_title;
                    }
                } catch (JSONException e) {
                    Common.showErrorAlert(Register.this, "Error:" + e.getMessage());
                }
                requestQueue.stop();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                alertDialog.dismiss();
                Common.showErrorAlert(Register.this, getString(R.string.error_please_try_again_later));
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

    private void calApi() {

        final Gson gson = new Gson();
        alertDialog.show();
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

        String signUp_url = getString(R.string.api) + "/CreateUser.php";
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, signUp_url, postparams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String r = response.toString();
                alertDialog.dismiss();

                try {

                    LoginData current_user = gson.fromJson(r, LoginData.class);
                    Common.currentUser = current_user;
                    Paper.book("villa_vanilia").write("current_user", current_user);

                    Intent intent = new Intent(Register.this, MainActivity.class);
                    startActivity(intent);

                } catch (Exception e) {
                    Common.showErrorAlert(Register.this, "rrrrr" + e.getMessage());
                }
                requestQueue.stop();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                alertDialog.dismiss();
                Common.showErrorAlert(Register.this, getString(R.string.email_register_before));
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

}
