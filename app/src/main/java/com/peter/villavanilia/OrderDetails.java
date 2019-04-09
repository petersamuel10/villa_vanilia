package com.peter.villavanilia;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.peter.villavanilia.adapter.OrderDetailsAdapter;
import com.peter.villavanilia.common.Common;

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

public class OrderDetails extends AppCompatActivity {

    @BindView(R.id.back_arrow)
    ImageView back_arrow;
    @BindView(R.id.order_number)
    TextView order_number;
    @BindView(R.id.order_date)
    TextView order_date;
    @BindView(R.id.order_status)
    TextView order_status;
    @BindView(R.id.rec_order_products)
    RecyclerView rec_order_products;
    @OnClick(R.id.back_arrow)
    public void back(){onBackPressed();}

    AlertDialog alertDialog;
    OrderDetailsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);

        alertDialog = Common.alert(this);

        if(Common.isArabic)
            back_arrow.setRotation(180);


        if(Common.isConnectToTheInternet(this)) {
            new GetOrderDetails(this).execute();
        }else
            Common.showErrorAlert(this,getString(R.string.error_no_internet_connection));
    }

    private class GetOrderDetails extends AsyncTask<String, Void, String> {

        public JSONObject jsonObject=null;

        GetOrderDetails(Activity activity) {
        }

        @Override
        protected void onPreExecute() {
            alertDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String signin_url = getString(R.string.api)+"GetOrderByOrderID.php";
            try {
                URL url = new URL(signin_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("cache-control", "application/json");
                httpURLConnection.setConnectTimeout(7000);
                httpURLConnection.setReadTimeout(7000);

                String str = "{\"order_id\":\""+ getIntent().getStringExtra("order_id")+"\"}";
                Log.i("ccc",str);

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
                Common.showErrorAlert(OrderDetails.this,getString(R.string.error_please_try_again_later_));
                return "";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            alertDialog.dismiss();

            try {
                jsonObject = new JSONObject(new String(result));
                String status = jsonObject.getString("message");

                if (status.equals("error")) {

                    // error
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


                    rec_order_products.setLayoutManager(new LinearLayoutManager(OrderDetails.this));
                    //adapter = new OrderDetailsAdapter();
                    rec_order_products.setAdapter(adapter);

                }
            } catch (JSONException e) {
                Common.showErrorAlert(OrderDetails.this,getString(R.string.error_please_try_again_later_));
            }

        }
    }
}
