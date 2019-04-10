package com.peter.villavanilia.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.peter.villavanilia.DateTime;
import com.peter.villavanilia.Delete_cart_interface;
import com.peter.villavanilia.MainActivity;
import com.peter.villavanilia.R;
import com.peter.villavanilia.adapter.CartAdapter;
import com.peter.villavanilia.common.Common;
import com.peter.villavanilia.model.CartModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;


public class Cart extends Fragment implements Delete_cart_interface {

    @BindView(R.id.no_data_cart)
    RelativeLayout no_data_cart;
    @BindView(R.id.cartLn)
    RelativeLayout cart_ln;
    @BindView(R.id.total)
    TextView total_txt;
    @BindView(R.id.cart_rec)
    RecyclerView cart_rec;

    ArrayList<CartModel> cartModel;
    CartAdapter cartAdapter;
    ArrayList<String> dateList;
    ArrayList<String> timeList;

    AlertDialog alertDialog;

    float total = 0.0f;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        ButterKnife.bind(this,view);

        alertDialog = Common.alert(getActivity());

        Paper.init(getContext());

        cart_rec.setLayoutManager(new LinearLayoutManager(getContext()));

        if((Paper.book("villa_vanilia").contains("cart"))){
            cart_ln.setVisibility(View.VISIBLE);
            getCartData();
        }else
            no_data_cart.setVisibility(View.VISIBLE);

        return view;
    }

    private void getCartData() {

        cartModel = Paper.book("villa_vanilia").read("cart");
        cartAdapter = new CartAdapter(cartModel);
        cartAdapter.setListener(this);
        cart_rec.setAdapter(cartAdapter);

        total = Paper.book("villa_vanilia").read("total");
        total_txt.setText(String.format("%.3f",total)+" "+getString(R.string.kd));
    }

    @Override
    public void deleteClick(int position) {

        total -= cartModel.get(position).getTotal();
        total_txt.setText(String.format("%.3f",total)+" "+getString(R.string.kd));
        Paper.book("villa_vanilia").write("total",total);

        cartModel.remove(position);
        cartAdapter.notifyItemRemoved(position);
        Paper.book("villa_vanilia").write("cart", cartModel);

        if (cartModel.size() == 0) {
            cart_ln.setVisibility(View.GONE);
            no_data_cart.setVisibility(View.VISIBLE);
            Paper.book("villa_vanilia").delete("cart");
            Paper.book("villa_vanilia").delete("total");
        }
    }

    @OnClick(R.id.payBtn)
    public void payBtn()
    {
        if(!Paper.book("villa_vanilia").contains("current_user")){
            MainActivity.titleTxt.setText(getString(R.string.login));
            FragmentManager manager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.content_frame, new Login());
            transaction.commit();

        }else {
            if (Common.isConnectToTheInternet(getContext())) {
                new GetAvailable(getActivity()).execute();
            } else
                Common.showErrorAlert(getActivity(), getString(R.string.error_no_internet_connection));
        }
    }

    private class GetAvailable extends AsyncTask<String, Void, String> {

        public JSONObject jsonObject=null;

        GetAvailable(Activity activity) {
        }

        @Override
        protected void onPreExecute() {
            alertDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String signin_url = "http://webservice.kall-center.com/api/GetTimeDate.php";
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
           alertDialog.dismiss();

            try {

                jsonObject = new JSONObject(new String(result));
                Log.d("sss",result);
                // String status = jsonObject.getString("message");
                JSONArray available = jsonObject.getJSONArray("available");

                        JSONObject jsonObject = available.getJSONObject(0);
                        String status = jsonObject.getString("status");

                        if(status.equals("0")){

                            dateList = new ArrayList<>();
                            timeList = new ArrayList<>();
                            JSONArray dateJsonArray = jsonObject.getJSONArray("date");
                            JSONArray timeJsonArray = jsonObject.getJSONArray("time");

                            if(dateJsonArray !=null){
                                for(int i = 0; i<dateJsonArray.length();i++){
                                    dateList.add(dateJsonArray.getString(i));
                                }
                            }

                            if(timeJsonArray !=null){
                                for(int i = 0; i<timeJsonArray.length();i++){
                                    timeList.add(timeJsonArray.getString(i));
                                }
                            }

                            goToDateAndTimeActivity();

                        }else if(status.equals("1")){
                            String msg_ar = jsonObject.getString("msg_ar");
                            String msg_eng = jsonObject.getString("msg_eng");

                            if(Common.isArabic)
                                Common.showErrorAlert(getActivity(),msg_ar);
                            else
                                Common.showErrorAlert(getActivity(),msg_eng);
                        }


            } catch (JSONException e) {
                Common.showErrorAlert(getActivity(),getString(R.string.error_please_try_again_later));
            }

        }
    }

    private void goToDateAndTimeActivity() {

        Intent i = new Intent(getContext(), DateTime.class);
        i.putStringArrayListExtra("date",dateList);
        i.putStringArrayListExtra("time",timeList);
        getContext().startActivity(i);

    }
}
