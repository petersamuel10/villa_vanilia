package com.peter.villavanilia;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.peter.villavanilia.common.Common;
import com.peter.villavanilia.model.AdditionItems;
import com.peter.villavanilia.model.AdditionsModel;
import com.peter.villavanilia.model.CartModel;
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
import io.paperdb.Paper;

public class ProductAdditions extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.additions_layout)
    LinearLayout addition_layout;
    @BindView(R.id.product_title)
    TextView product_title;
    @BindView(R.id.total_price)
    TextView total_txt;

    @OnClick(R.id.cancel_addition)
    public void cancel_additions() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ProductAdditions.this);

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_cart_dialoge, null);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button back_btn = view.findViewById(R.id.back_btn);
        Button keep_cart_btn = view.findViewById(R.id.keep_cart_btn);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        keep_cart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_to_cart();

            }
        });

        alertDialog.show();

    }

    ProductModel current_product;
    ArrayList<AdditionsModel> additionsModels;
    ArrayList<CartModel> cartList = new ArrayList<>();
    ArrayList<AdditionItems> all_additions;
    ArrayList<AdditionItems> addition_cart;
    float allTotal,total;

    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_additions);
        ButterKnife.bind(this);

        alertDialog = Common.alert(this);

        Paper.init(this);

        setData();
    }

    private void setData() {

        additionsModels = new ArrayList<>();
        all_additions = new ArrayList<>();
        addition_cart = new ArrayList<>();
        addition_layout.removeAllViews();

        current_product = getIntent().getExtras().getParcelable("current_product");

        if(Common.isArabic)
            product_title.setText(current_product.getProduct_title_ar());
        else
            product_title.setText(current_product.getProduct_title_en());

        total = Float.valueOf(current_product.getProduct_price());
        total_txt.setText(String.format("%.3f",total)+getString(R.string.kd));

        if(Common.isConnectToTheInternet(this)) {
            new GetProductAdditions(this).execute();
        }else
            Common.showErrorAlert(this,getString(R.string.error_no_internet_connection));

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        AdditionItems additionItem = all_additions.get(buttonView.getId());
        if(isChecked){
            total += Float.valueOf(buttonView.getTag().toString());
            total_txt.setText(String.format("%.3f",total)+getString(R.string.kd));
            addition_cart.add(additionItem);
        }else {
            total -= Float.valueOf(buttonView.getTag().toString());
            total_txt.setText(String.format("%.3f",total)+getString(R.string.kd));
            addition_cart.remove(additionItem);
        }
    }

    private class GetProductAdditions extends AsyncTask<String, Void, String> {

        public JSONObject jsonObject = null;
        public ArrayList<ProductModel> productList;

        GetProductAdditions(Activity activity) {
            productList = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            alertDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String signin_url = getString(R.string.api)+"GetAdditionsWhereProductID.php";
            try {
                URL url = new URL(signin_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("cache-control", "application/json");
                httpURLConnection.setConnectTimeout(7000);
                httpURLConnection.setReadTimeout(7000);


                String str = "{\"product_id\": " +current_product.getProduct_id()+ "}";
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
                Common.showErrorAlert(ProductAdditions.this,getString(R.string.error_please_try_again_later_));
                return "";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            alertDialog.dismiss();

            try {
                jsonObject = new JSONObject(new String(result));
                // String status = jsonObject.getString("message");
                JSONArray productAdditions = jsonObject.getJSONArray("main");

                if (productAdditions.length() != 0) {

                    for (int i = 0; i < productAdditions.length(); i++) {

                        AdditionsModel additionsModel = new AdditionsModel();

                        JSONObject jsonObject = productAdditions.getJSONObject(i);
                        JSONObject addition = jsonObject.getJSONObject("additions");

                        String additions_name_ar = addition.getString("additions_name_ar");
                        String additions_name_eng = addition.getString("additions_name_eng");
                        String additions_type = addition.getString("additions_type");
                        String additions_id = addition.getString("additions_id");

                        additionsModel.setAdditions_name_ar(additions_name_ar);
                        additionsModel.setAdditions_name_eng(additions_name_eng);
                        additionsModel.setAdditions_type(additions_type);
                        additionsModel.setAdditions_id(additions_id);

                        JSONArray additions_items = addition.getJSONArray("additions_items");

                        ArrayList<AdditionItems> additionItemsArrayList = new ArrayList<>();

                        for (int x = 0; x < additions_items.length(); x++) {

                            AdditionItems additionItemsModel = new AdditionItems();

                            JSONObject addition_item = additions_items.getJSONObject(x);

                            String additions_item_ar_name = addition_item.getString("additions_item_ar_name");
                            String additions_item_en_name = addition_item.getString("additions_item_en_name");
                            String additions_item_price = addition_item.getString("additions_item_price");
                            String additions_item_id = addition_item.getString("additions_item_id");

                            additionItemsModel.setAdditions_item_ar_name(additions_item_ar_name);
                            additionItemsModel.setAdditions_item_en_name(additions_item_en_name);
                            additionItemsModel.setAdditions_item_price(additions_item_price);
                            additionItemsModel.setAdditions_item_id(additions_item_id);

                            additionItemsArrayList.add(additionItemsModel);
                        }

                        additionsModel.setAdditions_items(additionItemsArrayList);
                        additionsModels.add(additionsModel);
                        all_additions.addAll(additionItemsArrayList);

                        createLinear(additionsModel);
                    }

                } else {

                }
            } catch (JSONException e) {
                Common.showErrorAlert(ProductAdditions.this,getString(R.string.error_please_try_again_later_));
            }

        }
    }

    public void createLinear(AdditionsModel additionsModel) {

        RelativeLayout.LayoutParams linearParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(linearParam);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundColor(getResources().getColor(R.color.greyDark));

        TextView addition_title = new TextView(this);

        if (Common.isArabic)
            addition_title.setText(additionsModel.getAdditions_name_ar());
        else
            addition_title.setText(additionsModel.getAdditions_name_eng());

        addition_title.setTextColor(getResources().getColor(R.color.black));
        addition_title.setGravity(Gravity.CENTER);
        addition_title.setTextSize(18);

        TextView addition_note = new TextView(this);

        if (additionsModel.getAdditions_type().equals("1"))
            addition_note.setText(getResources().getString(R.string.choose_items_from_list));
        else if (additionsModel.getAdditions_type().equals("0"))
            addition_note.setText(getResources().getString(R.string.choose_one_item_from_list));

        addition_note.setTextColor(getResources().getColor(R.color.black));
        addition_note.setGravity(Gravity.CENTER);
        addition_note.setTextSize(18);

        linearLayout.addView(addition_title);
        linearLayout.addView(addition_note);

        addition_layout.addView(linearLayout);

        if (additionsModel.getAdditions_type().equals("1"))
            createCheckbox(additionsModel.getAdditions_items());
        else if (additionsModel.getAdditions_type().equals("0"))
            createRadioButton(additionsModel.getAdditions_items());

    }

    private void createCheckbox(ArrayList<AdditionItems> additions_items) {

        RelativeLayout.LayoutParams linearParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        RelativeLayout.LayoutParams linear = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(linearParam);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundColor(Color.WHITE);

        for (AdditionItems additionItem : additions_items) {

            TextView title = new TextView(this);
            title.setTextColor(Color.BLACK);
            title.setTextSize(18);
            title.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);


            CheckBox checkBox = new CheckBox(this);
            checkBox.setTextColor(Color.BLACK);
            checkBox.setTextSize(14);
            checkBox.setOnCheckedChangeListener(this);
            checkBox.setId(all_additions.indexOf(additionItem));
            checkBox.setTag(additionItem.getAdditions_item_price());
            checkBox.setLayoutParams(linear);
            checkBox.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            checkBox.setButtonDrawable(R.drawable.radio_button);

            if (Common.isArabic)
                checkBox.setText(additionItem.getAdditions_item_ar_name() +
                        "   (" + additionItem.getAdditions_item_price() + getString(R.string.kd) + " )");

            else
                checkBox.setText(additionItem.getAdditions_item_en_name() +
                        "   (" + additionItem.getAdditions_item_price() + getString(R.string.kd) + " )");

            linearLayout.addView(checkBox);

        }

        addition_layout.addView(linearLayout);

    }

    private void createRadioButton(ArrayList<AdditionItems> additions_items) {

        RelativeLayout.LayoutParams linearParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);

        RelativeLayout.LayoutParams linear = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setLayoutParams(linearParam);
        radioGroup.setOrientation(LinearLayout.VERTICAL);
        radioGroup.setBackgroundColor(Color.WHITE);


        for (AdditionItems additionItem : additions_items) {

            RadioButton radioButton = new RadioButton(this);
            radioButton.setTextColor(Color.BLACK);
            radioButton.setLayoutParams(linear);
            radioButton.setTextSize(14);
            radioButton.setOnCheckedChangeListener(this);
            radioButton.setId(all_additions.indexOf(additionItem));
            radioButton.setTag(additionItem.getAdditions_item_price());
            radioButton.setButtonDrawable(R.drawable.radio_button);
            radioButton.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);

            if (Common.isArabic)
                radioButton.setText(additionItem.getAdditions_item_ar_name() +
                        "   (" + additionItem.getAdditions_item_price() + getString(R.string.kd) + " )");
            else
                radioButton.setText(additionItem.getAdditions_item_en_name() +
                        "   (" + additionItem.getAdditions_item_price() + getString(R.string.kd) + " )");

            radioGroup.addView(radioButton);

        }

        addition_layout.addView(radioGroup);

    }

    private void add_to_cart() {

        if(Paper.book("villa_vanilia").contains("cart")) {
            cartList = Paper.book("villa_vanilia").read("cart");
            allTotal = Paper.book("villa_vanilia").read("total");
            allTotal += total;
        }else
            allTotal += total;

        Paper.book("villa_vanilia").write("total",allTotal);

        cartList.add(new CartModel(current_product,addition_cart,total));

        Paper.book("villa_vanilia").write("cart",cartList);
        finish();
    }
}
