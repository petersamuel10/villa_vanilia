package com.peter.villavanilia;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.peter.villavanilia.adapter.ProductAdapter;
import com.peter.villavanilia.common.Common;
import com.peter.villavanilia.model.ProductModel;

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

public class Products extends AppCompatActivity {

    @BindView(R.id.back_arrow)
    ImageView back_arrow;
    @BindView(R.id.product_rec)
    RecyclerView product_rec;
    @BindView(R.id.category_title)
    TextView category_title;

    @OnClick(R.id.back_arrow)
    public void back() {
        onBackPressed();
    }

    @OnClick(R.id.ic_cart)
    public void cart() {
     Intent i = new Intent(this,MainActivity.class);
     i.putExtra("product_activity","true");
     startActivity(i);
    }

    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        ButterKnife.bind(this);

        alertDialog = Common.alert(this);

        if (Common.isArabic) {
            back_arrow.setRotation(180);
        }
        category_title.setText(getIntent().getStringExtra("category_name"));

        if (Common.isConnectToTheInternet(this)) {
            new GetProducts(this).execute();
        } else
            Common.showErrorAlert(this, getString(R.string.error_no_internet_connection));
    }

    private class GetProducts extends AsyncTask<String, Void, String> {

        public JSONObject jsonObject = null;
        public ArrayList<ProductModel> productList;

        GetProducts(Activity activity) {
            productList = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            alertDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String signin_url = getResources().getString(R.string.api)+"GetProductWhereCategoryID.php";
            try {
                URL url = new URL(signin_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("cache-control", "application/json");
                httpURLConnection.setConnectTimeout(7000);
                httpURLConnection.setReadTimeout(7000);


                String str = "{\"limit\":0," + "\"category_id\": " + getIntent().getExtras().getString("category_id") + "}";
                Log.i("ccc", str);

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
                Common.showErrorAlert(Products.this, getString(R.string.error_please_try_again_later_));
                return "";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            alertDialog.dismiss();

            try {
                jsonObject = new JSONObject(new String(result));
                // String status = jsonObject.getString("message");
                JSONArray productItems = jsonObject.getJSONArray("product_item");

                if (productItems.length() != 0) {

                    for (int i = 0; i < productItems.length(); i++) {
                        JSONObject jsonObject = productItems.getJSONObject(i);

                        String product_id = jsonObject.getString("product_id");
                        String product_title_ar = jsonObject.getString("product_title_ar");
                        String product_title_en = jsonObject.getString("product_title_en");
                        String product_price = jsonObject.getString("product_price");
                        String product_discount = jsonObject.getString("product_discount");
                        String product_img = jsonObject.getString("product_img");

                        ProductModel productItem = new ProductModel(product_id, product_title_ar, product_title_en
                                , product_price, product_discount, product_img);
                        productList.add(productItem);
                    }

                    GridLayoutManager mLayoutManager = new GridLayoutManager(Products.this, 2, LinearLayoutManager.VERTICAL, false);
                    product_rec.setLayoutManager(mLayoutManager);
                    ProductAdapter adapter = new ProductAdapter(productList, Products.this);
                    product_rec.setAdapter(adapter);
                } else {
                    Common.showErrorAlert(Products.this, getString(R.string.error_please_try_again_later_));
                }
            } catch (JSONException e) {
                Common.showErrorAlert(Products.this, e.getMessage());
            }

        }
    }
}
