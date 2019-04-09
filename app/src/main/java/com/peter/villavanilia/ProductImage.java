package com.peter.villavanilia;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.peter.villavanilia.adapter.ViewPagerAdapter;
import com.peter.villavanilia.common.Common;
import com.peter.villavanilia.model.ProductModel;
import com.peter.villavanilia.model.Slider;

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
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProductImage extends AppCompatActivity {

    @BindView(R.id.product_name)
    TextView product_name;
    @BindView(R.id.sliderLayout)
    ViewPager sliderLayout;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @OnClick(R.id.imageView4)
    public void cancel(){finish();}

    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000; // time in milliseconds between successive task executions.

    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_image);
        ButterKnife.bind(this);

        alertDialog = Common.alert(this);

        product_name.setText(getIntent().getStringExtra("product_name"));

        if(Common.isConnectToTheInternet(this)) {
            new GetProductImage(this).execute();
        }else
            Common.showErrorAlert(this,getString(R.string.error_no_internet_connection));
    }

    private class GetProductImage extends AsyncTask<String, Void, String> {

        public JSONObject jsonObject=null;
        public ArrayList<ProductModel> productList;
        public ArrayList<Slider> sliders;

        ArrayList<String> mylist;

        GetProductImage(Activity activity) {
            productList = new ArrayList<>();
            sliders = new ArrayList<>();
            mylist = new ArrayList<String>();
        }

        @Override
        protected void onPreExecute() {
            alertDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String signin_url = getResources().getString(R.string.api)+"GetGalleryWhereProductID.php";
            try {
                URL url = new URL(signin_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("cache-control", "application/json");
                httpURLConnection.setConnectTimeout(7000);
                httpURLConnection.setReadTimeout(7000);


                String str = "{\"product_id\": "+getIntent().getExtras().getString("product_id")+"}";
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
                Common.showErrorAlert(ProductImage.this,getString(R.string.error_please_try_again_later));
                return "";
            }
        }

        @Override
        protected void onPostExecute(String result) {
           alertDialog.dismiss();

            if(result.contains("no")){
                Common.showErrorAlert(ProductImage.this,getString(R.string.error_please_try_again_later));
            }else {

                try {
                    jsonObject = new JSONObject(new String(result));
                    // String status = jsonObject.getString("message");
                    JSONArray sliderList = jsonObject.getJSONArray("slider");

                    if (sliderList.length() != 0) {

                        for (int i = 0 ; i< sliderList.length() ; i++) {
                            JSONObject jsonObject = sliderList.getJSONObject(i);

                            String photos_id = jsonObject.getString("photos_id");
                            String photos_path = jsonObject.getString("photos_path");

                            mylist.add(photos_path);

                        }

                        ViewPagerAdapter adapter = new ViewPagerAdapter(ProductImage.this, mylist);
                        sliderLayout.setAdapter(adapter);
                        tabLayout.setupWithViewPager(sliderLayout,true);

                        /*After setting the adapter use the timer */
                        final Handler handler = new Handler();
                        final Runnable Update = new Runnable() {
                            public void run() {
                                if (currentPage == mylist.size()) {
                                    currentPage = 0;
                                }
                                sliderLayout.setCurrentItem(currentPage++, true);
                            }
                        };

                        timer = new Timer(); // This will create a new Thread
                        timer.schedule(new TimerTask() { // task to be scheduled
                            @Override
                            public void run() {
                                handler.post(Update);
                            }
                        }, DELAY_MS, PERIOD_MS);

                    }


                } catch (JSONException e) {
                    Common.showErrorAlert(ProductImage.this,getString(R.string.error_please_try_again_later));
                }
            }

        }
    }
}
