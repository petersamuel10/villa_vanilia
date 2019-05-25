package com.peter.villavanilia;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.peter.villavanilia.adapter.OrderDetailsAdapter;
import com.peter.villavanilia.model.OrderDetailsModel;
import com.peter.villavanilia.common.Common;

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
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderDetails extends AppCompatActivity {

    @BindView(R.id.back_arrow)
    ImageView back_arrow;
    @BindView(R.id.rootView)
    ConstraintLayout rootView;
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
    ArrayList<OrderDetailsModel> orderDetailsList;

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
            orderDetailsList = new ArrayList<>();
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

                String str = "{\"order_id\":\""+ getIntent().getExtras().getString("order_id")+"\"}";
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
                Common.showErrorAlert(OrderDetails.this,getString(R.string.error_please_try_again_later));
                return "";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            alertDialog.dismiss();
            rootView.setVisibility(View.VISIBLE);

            try {
                jsonObject = new JSONObject(new String(result));

                JSONArray order_list = jsonObject.getJSONArray("order_list");
                JSONObject object = order_list.getJSONObject(0);

                JSONObject orders = object.getJSONObject("orders");

                order_number.setText(orders.getString("order_number"));
                order_date.setText(orders.getString("order_created_at"));
                order_status.setText(orders.getString("order_status_en_name"));

                JSONArray product_List = object.getJSONArray("product_List");
                for (int i = 0; i < product_List.length(); i++) {
                    JSONObject object1 = product_List.getJSONObject(i);

                    String product = (object1.getJSONObject("product")).toString();

                    OrderDetailsModel orderDetailsModel =new OrderDetailsModel();
                    Gson gson = new Gson();
                    orderDetailsModel = gson.fromJson(product,OrderDetailsModel.class);

                    orderDetailsList.add(orderDetailsModel);

                }

                rec_order_products.setLayoutManager(new LinearLayoutManager(getParent()));
                adapter = new OrderDetailsAdapter(orderDetailsList);
                rec_order_products.setAdapter(adapter);

            } catch (JSONException e) {
                Common.showErrorAlert(OrderDetails.this, getString(R.string.error_please_try_again_later));
            }

        }
    }
}
