package com.peter.villavanilia.setting;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
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
import com.peter.villavanilia.R;
import com.peter.villavanilia.common.Common;
import com.peter.villavanilia.model.RegisterModel;

import org.json.JSONArray;
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

public class Profile extends AppCompatActivity {

    @OnClick(R.id.back_arrow)
    public void back() {
        onBackPressed();
    }

    @BindView(R.id.back_arrow)
    ImageView back_arrow;
    @BindView(R.id.email_ed)
    EditText email_ed;
    @BindView(R.id.user_name_ed)
    EditText user_name_ed;
    @BindView(R.id.place_btn)
    Button place_btn;
    @BindView(R.id.area_btn)
    Button area_btn;
    @BindView(R.id.phone_ed)
    EditText phone_ed;
    @BindView(R.id.phone2_ed)
    EditText phone2_ed;
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

    String[] area_arr;
    String email, name, phone, phone1, phone2, area_str, place_str, area_ar, area_en, place_ar, place_en,
            block_str, street_str, avenue_str, building_str, floor_str, apartment_str, address;

    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_profile);
        ButterKnife.bind(this);
        if (Common.isArabic)
            back_arrow.setRotation(180);
        alertDialog = Common.alert(this);
        getArea();

        user_name_ed.setText(Common.currentUser.getUser().getUser_name());
        email_ed.setText(Common.currentUser.getUser().getUser_email());
        phone = Common.currentUser.getUser().getUser_telep();
        address = Common.currentUser.getUser().getUser_address();

        phone1 = phone.substring(0, phone.indexOf("-"));
        phone_ed.setText(phone1);
        phone2 = phone.substring(phone.indexOf("-") + 1, phone.length());
        phone2_ed.setText(phone2);

        area_en = address.substring(0, address.indexOf("-"));
        address = address.substring(address.indexOf("-") + 1);
        area_ar = address.substring(0, address.indexOf("-"));
        area_str = area_en + " - " + area_ar;
        area_btn.setText(area_str);

        address = address.substring(address.indexOf("-") + 1);
        place_en = address.substring(0, address.indexOf("-"));
        address = address.substring(address.indexOf("-") + 1);
        place_ar = address.substring(0, address.indexOf("-"));
        place_str = place_en + " - " + place_ar;
        place_btn.setText(place_str);

        address = address.substring(address.indexOf("-") + 1);
        block_str = address.substring(0, address.indexOf("-"));
        block_ed.setText(block_str);

        address = address.substring(address.indexOf("-") + 1);
        street_str = address.substring(0, address.indexOf("-"));
        street_ed.setText(street_str);

        address = address.substring(address.indexOf("-") + 1);
        avenue_str = address.substring(0, address.indexOf("-"));
        avenue_ed.setText(avenue_str);

        address = address.substring(address.indexOf("-") + 1);
        building_str = address.substring(0, address.indexOf("-"));
        building_ed.setText(building_str);

        address = address.substring(address.indexOf("-") + 1);
        floor_str = address.substring(0, address.indexOf("-"));
        floor_ed.setText(floor_str);

        address = address.substring(address.indexOf("-") + 1);
        apartment_str = address.substring(0);
        apartment_ed.setText(apartment_str);

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
                    Common.showErrorAlert(getParent(), "Error:" + e.getMessage());
                }
                requestQueue.stop();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                alertDialog.dismiss();
                Common.showErrorAlert(getParent(), getString(R.string.error_please_try_again_later));
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


    @OnClick(R.id.updateBtn)
    public void update() {

        name = user_name_ed.getText().toString();
        email = email_ed.getText().toString();
        phone1 = phone_ed.getText().toString();
        phone2 = phone2_ed.getText().toString();
        block_str = block_ed.getText().toString();
        street_str = street_ed.getText().toString();
        avenue_str = avenue_ed.getText().toString();
        building_str = building_ed.getText().toString();
        floor_str = floor_ed.getText().toString();
        apartment_str = apartment_ed.getText().toString();

        if (validate()) {
            address = area_str + "-" + place_str + "-" + block_str + "-" + street_str + "-" + avenue_str + "-" + building_str + "-" + floor_str + "-" + apartment_str;
            phone = phone1 + "-" + phone2;

            RegisterModel registerModel = new RegisterModel(name, email, Common.currentUser.getUser().getUser_password(),
                    phone, address, Common.currentUser.getJwt(), Common.currentUser.getUser().getUser_id());

            if (Common.isConnectToTheInternet(this)) {
                new updateBackgroundTask(this, registerModel).execute();
            } else
                Common.showErrorAlert(this, getString(R.string.error_no_internet_connection));
        }


    }

    private boolean validate() {

        if (TextUtils.isEmpty(name)) {
            Common.showErrorAlert(this, getString(R.string.please_enter_user_name));
            return false;
        } else if (TextUtils.isEmpty(email)) {
            Common.showErrorAlert(this, getString(R.string.please_enter_email));
            return false;
        } else if (TextUtils.isEmpty(place_str)) {
            Common.showErrorAlert(this, getString(R.string.select_place));
            return false;
        } else if (TextUtils.isEmpty(area_str)) {
            Common.showErrorAlert(this, getString(R.string.select_area));
            return false;
        } else if (TextUtils.isEmpty(phone1)) {
            Common.showErrorAlert(this, getString(R.string.please_enter_your_phone));
            return false;
        } else if (TextUtils.isEmpty(street_str)) {
            Common.showErrorAlert(this, getString(R.string.please_enter) + " " + getString(R.string.street));
            return false;
        } else if (TextUtils.isEmpty(building_str)) {
            Common.showErrorAlert(this, getString(R.string.please_enter) + " " + getString(R.string.building));
            return false;
        }


        return true;
    }

    private class updateBackgroundTask extends AsyncTask<String, Void, String> {

        public JSONObject jsonObject = null;
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
            String signin_url = getString(R.string.api) + "UpdateUser.php";
            try {
                URL url = new URL(signin_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("cache-control", "application/json");
                httpURLConnection.setConnectTimeout(7000);
                httpURLConnection.setReadTimeout(7000);

                Gson gson = new Gson();
                String str = gson.toJson(registerModel);

                Log.d("vvvv", str);

                // String str =  "{\"user_name\": \""+name+"\",\"user_email\": \"" + email + "\", \"user_password\": \"" + password + "\"}";
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
                alertDialog.dismiss();
                Common.showErrorAlert(Profile.this, getString(R.string.error_please_try_again_later));
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
                    onBackPressed();
                } else {

                }
            } catch (JSONException e) {
                Common.showErrorAlert(Profile.this, getString(R.string.error_please_try_again_later));
            }

        }
    }

}
