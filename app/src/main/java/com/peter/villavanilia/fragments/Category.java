package com.peter.villavanilia.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.peter.villavanilia.R;
import com.peter.villavanilia.adapter.CategoryAdapter;
import com.peter.villavanilia.common.Common;
import com.peter.villavanilia.model.CategoryModel;
import com.peter.villavanilia.model.Slider;
import com.squareup.picasso.Picasso;

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

/**
 * A simple {@link Fragment} subclass.
 */
public class Category extends Fragment {

    @BindView(R.id.sliderLayout)
    SliderLayout sliderLayout;
    @BindView(R.id.category_rec)
    RecyclerView category_rec;

    AlertDialog alertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        ButterKnife.bind(this,view);

        alertDialog = Common.alert(getActivity());

        if(Common.isConnectToTheInternet(getContext())) {
            new GetCategory(getActivity()).execute();
            new GetSlider(getActivity()).execute();
        }else
            Common.showErrorAlert(getActivity(),getString(R.string.error_no_internet_connection));

        return view;
    }

    private class GetCategory extends AsyncTask<String, Void, String> {

        public JSONObject jsonObject=null;
        public ArrayList<CategoryModel> categoryList;

        GetCategory(Activity activity) {
            categoryList = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            alertDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String signin_url = getResources().getString(R.string.api)+"GetCategory.php";
            try {
                URL url = new URL(signin_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("cache-control", "application/json");
                httpURLConnection.setConnectTimeout(7000);
                httpURLConnection.setReadTimeout(7000);


                String str = "{\"level\": 0}";
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
                Common.showErrorAlert(getActivity(),getString(R.string.error_please_try_again_later));
                return "";
            }
        }

        @Override
        protected void onPostExecute(String result) {
                alertDialog.dismiss();

            try {
                jsonObject = new JSONObject(new String(result));
               // String status = jsonObject.getString("message");
                JSONArray categoryItems = jsonObject.getJSONArray("category_item");

                if (categoryItems.length() != 0) {

                    for (int i = 0; i < categoryItems.length(); i++) {
                        JSONObject jsonObject = categoryItems.getJSONObject(i);

                        String category_id = jsonObject.getString("category_id");
                        String category_icon = jsonObject.getString("category_icon");
                        String category_title_ar = jsonObject.getString("category_title_ar");
                        String category_title_en = jsonObject.getString("category_title_en");

                        CategoryModel categoryItem = new CategoryModel(category_id,category_icon,category_title_ar,category_title_en);
                        categoryList.add(categoryItem);
                    }
                    Log.d("xxx111", String.valueOf(categoryList.size()));
                    category_rec.setLayoutManager(new LinearLayoutManager(getContext()));
                    CategoryAdapter adapter = new CategoryAdapter(categoryList,getContext());
                    category_rec.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {



                }
            } catch (JSONException e) {
                Common.showErrorAlert(getActivity(),getString(R.string.error_please_try_again_later));
            }

        }
    }

    private class GetSlider extends AsyncTask<String, Void, String> {

        public ProgressDialog dialog;
        public JSONObject jsonObject=null;
        public ArrayList<Slider> sliders;
        Picasso picasso;

        GetSlider(Activity activity) {
            this.dialog = new ProgressDialog(activity);
            sliders = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            String signin_url = getResources().getString(R.string.api)+"GetSlider.php";
            try {
                URL url = new URL(signin_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("cache-control", "application/json");
                httpURLConnection.setConnectTimeout(7000);
                httpURLConnection.setReadTimeout(7000);


                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);


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
                Common.showErrorAlert(getActivity(),getString(R.string.error_please_try_again_later));
                return "";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (dialog != null && dialog.isShowing())
                dialog.dismiss();

            Log.d("ststst", "result login: " + result);

            // Toast.makeText(Login.this, "//////////////", Toast.LENGTH_SHORT).show();
            try {
                jsonObject = new JSONObject(new String(result));
                //String status = jsonObject.getString("message");
                JSONArray sliderList = jsonObject.getJSONArray("slider");



                if (sliderList.length() != 0) {

                    for (int i = 0 ; i< sliderList.length() ; i++){
                        JSONObject jsonObject = sliderList.getJSONObject(i);

                        String photos_id = jsonObject.getString("photos_id");
                        String photos_path = jsonObject.getString("photos_path");

                        DefaultSliderView defaultSliderView = new DefaultSliderView(getContext());
                        picasso = Picasso.with(getContext());
                        picasso.load(photos_path);
                        defaultSliderView.setPicasso(picasso);
                        sliderLayout.addSlider(defaultSliderView);

                       /* Slider slider = new Slider(photos_id,photos_path);
                        sliders.add(slider);

                        TextSliderView textSliderView = new TextSliderView(getContext());
                        textSliderView
                                .description(photos_id)
                                .image(photos_path)
                                .setScaleType(BaseSliderView.ScaleType.Fit);
                        sliderLayout.addSlider(textSliderView);*/
                }


                }
            } catch (JSONException e) {
                Common.showErrorAlert(getActivity(),getString(R.string.error_please_try_again_later));
            }

        }
    }

}
