package com.peter.villavanilia;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.peter.villavanilia.common.Common;
import com.peter.villavanilia.model.AddOrder;

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

public class PaymentMethod extends AppCompatActivity {

    @BindView(R.id.radioGroup)
    RadioGroup payment_method_group;
    @BindView(R.id.total_price)
    TextView total_price_txt;
    @OnClick(R.id.payBtn)
    public void cancel() { onBackPressed(); }

    String payment_status_id;
    AddOrder addOrderModel;

    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);
        ButterKnife.bind(this);

        alertDialog = Common.alert(this);

        total_price_txt.setText(String.format("%.3f",Paper.book("villa_vanilia").read("total"))+getString(R.string.kd));

        addOrderModel = getIntent().getParcelableExtra("add_order");

    }

    @OnClick(R.id.payBtn)
    public void checkout() {

        try {
            RadioButton checkRadioButton = payment_method_group.findViewById(payment_method_group.getCheckedRadioButtonId());
            payment_status_id = checkRadioButton.getTag().toString();
        }catch (Exception e){}



       if (payment_status_id == null){
            Common.showErrorAlert(PaymentMethod.this,getString(R.string.please_select_payment_method));
        }else {

            addOrderModel.setPayment_status_id(payment_status_id);

           Gson gson = new Gson();
           String add_order_model_str = gson.toJson(addOrderModel);

           if(Common.isConnectToTheInternet(this)) {
               new AddOrderAPI(this,add_order_model_str).execute();
           }else
               Common.showErrorAlert(this,getString(R.string.error_no_internet_connection));
       }

    }

    private class AddOrderAPI extends AsyncTask<String, Void, String> {

        public JSONObject jsonObject=null;
        public String add_order_model_str;

        AddOrderAPI(Activity activity, String add_order_model_str) {
            this.add_order_model_str = add_order_model_str;
        }

        @Override
        protected void onPreExecute() {
            alertDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String signin_url = getString(R.string.api)+"AddOrder.php";
            try {
                URL url = new URL(signin_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("cache-control", "application/json");
                httpURLConnection.setConnectTimeout(7000);
                httpURLConnection.setReadTimeout(7000);

                byte[] outputInBytes = add_order_model_str.getBytes("UTF-8");

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
                Common.showErrorAlert(PaymentMethod.this,getString(R.string.error_please_try_again_later_));
                return "";
            }
        }

        @Override
        protected void onPostExecute(String result) {
           alertDialog.dismiss();

            try {

                jsonObject = new JSONObject(new String(result));

                String message = jsonObject.getString("message");
                String order_num = jsonObject.getString("order_num");
                String delivery_time = jsonObject.getString("delivery_time");

                Intent i = new Intent(PaymentMethod.this,PaymentResult.class);
                i.putExtra("message",message);
                i.putExtra("order_num",order_num);
                i.putExtra("delivery_time",delivery_time);
                startActivity(i);

            } catch (JSONException e) {
                Common.showErrorAlert(PaymentMethod.this,getString(R.string.error_please_try_again_later_));
            }

        }
    }
}
