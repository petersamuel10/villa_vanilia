package com.peter.villavanilia.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.peter.villavanilia.R;
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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutApp extends Fragment {

    @BindView(R.id.about_app)
    WebView about_app;

    AlertDialog alertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about_app, container, false);
        ButterKnife.bind(this,view);

        alertDialog = Common.alert(getActivity());

        if(Common.isConnectToTheInternet(getContext())) {
            new GetAboutApp().execute();
        }else
            Common.showErrorAlert(getActivity(),getString(R.string.error_no_internet_connection));

        return view;
    }

    private class GetAboutApp extends AsyncTask<String, Void, String> {

        public JSONObject jsonObject=null;


        @Override
        protected void onPreExecute() {

            alertDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String signin_url = getString(R.string.api)+"GetCMS.php";
            try {
                URL url = new URL(signin_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("cache-control", "application/json");
                httpURLConnection.setConnectTimeout(7000);
                httpURLConnection.setReadTimeout(7000);

                String str = "{\"page_id\" : 1}";

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
                JSONArray jsonObject1 = jsonObject.getJSONArray("page_content");

                JSONObject about_json = jsonObject1.getJSONObject(0);

                String content;

                if(Common.isArabic)
                    content = about_json.getString("pages_contents_ar");
                else
                    content = about_json.getString("pages_contents_en");

                about_app.loadDataWithBaseURL("",content,"text/html","UTF-8","");

            } catch (JSONException e) {
                Common.showErrorAlert(getActivity(),getString(R.string.error_please_try_again_later));
            }

        }
    }

}
