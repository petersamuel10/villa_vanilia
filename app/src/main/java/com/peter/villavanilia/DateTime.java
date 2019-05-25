package com.peter.villavanilia;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.peter.villavanilia.common.Common;
import com.peter.villavanilia.model.AddOrder;
import com.peter.villavanilia.model.AdditionItems;
import com.peter.villavanilia.model.CartModel;
import com.peter.villavanilia.model.ProductModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;

public class DateTime extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @BindView(R.id.back_arrow)
    ImageView back_arrow;
    @BindView(R.id.rootLayout)
    LinearLayout rootLayout;
    @BindView(R.id.date_spinner)
    Spinner date_spinner;
    @BindView(R.id.time_spinner)
    Spinner time_spinner;
    @BindView(R.id.driver_notes)
    EditText driver_notes;
    @BindView(R.id.cart_notes)
    EditText cart_notes;
    @BindView(R.id.kitchen_notes)
    EditText kitchen_notes;
    @BindView(R.id.place_btn)
    Button place_btn;
    @BindView(R.id.area_btn)
    Button area_btn;
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
    @OnClick(R.id.back_arrow)
    public void back(){onBackPressed();}

    ArrayList<String> dateList ;
    ArrayList<String> timeList ;
    ArrayList<CartModel> cartModel;
    ArrayList<ProductModel> products_info_list;

    String[] area_arr;
    String date_str,time_str,driver_notes_str,kitchen_notes_str,cart_notes_str;
    String area_str, place_str, area_ar, area_en, place_ar, place_en,
            block_str, street_str, avenue_str, building_str, floor_str, apartment_str, address;

    AddOrder addOrderModel;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_time);
        ButterKnife.bind(this);
        alertDialog = Common.alert(this);
        if (Common.isArabic)
            back_arrow.setRotation(180);

        getArea();

        address = Common.currentUser.getUser().getUser_address();
        bindAddress();

        dateList = getIntent().getStringArrayListExtra("date");
        timeList = getIntent().getStringArrayListExtra("time");

        ArrayAdapter<String> date_adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, dateList);
        date_adapter.setDropDownViewResource(R.layout.spinner_item);
        date_adapter.notifyDataSetChanged();
        date_spinner.setAdapter(date_adapter);

        ArrayAdapter<String> time_adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, timeList);
        time_adapter.setDropDownViewResource(R.layout.spinner_item);
        time_adapter.notifyDataSetChanged();
        time_spinner.setAdapter(time_adapter);

        date_spinner.setOnItemSelectedListener(this);
        time_spinner.setOnItemSelectedListener(this);
    }

    private void bindAddress() {

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

    @OnClick(R.id.continue_btn)
    public void continue_btn(){

        String user_id = Common.currentUser.getUser().getUser_id();

        driver_notes_str = driver_notes.getText().toString();
        kitchen_notes_str = kitchen_notes.getText().toString();
        cart_notes_str = cart_notes.getText().toString();
        block_str = block_ed.getText().toString();
        street_str = street_ed.getText().toString();
        avenue_str = avenue_ed.getText().toString();
        building_str = building_ed.getText().toString();
        floor_str = floor_ed.getText().toString();
        apartment_str = apartment_ed.getText().toString();

        getProductInfo();

        if(validation()){
            address = area_str + "-" + place_str + "-" + block_str + "-" + street_str + "-" + avenue_str
                    + "-" + building_str + "-" + floor_str + "-" + apartment_str;

            addOrderModel = new AddOrder("", user_id,address, driver_notes_str, kitchen_notes_str,
                    cart_notes_str, date_str, time_str,products_info_list);


            Intent i = new Intent(this,PaymentMethod.class);
            i.putExtra("add_order",addOrderModel);
            startActivity(i);

           /* Gson gson = new Gson();
            String str = gson.toJson(addOrderModel);

            Log.i("adddd",str);*/

        }
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

    private void getProductInfo() {
        products_info_list = new ArrayList<>();

        ProductModel product;
        ArrayList<AdditionItems> productAdditionsList;
        cartModel = Paper.book("villa_vanilia").read("cart");

        for ( CartModel productPerCart:cartModel) {
            
            product = new ProductModel();
            productAdditionsList = new ArrayList<>();
            
            product.setProduct_id(productPerCart.getProduct().getProduct_id());
            product.setProduct_price(productPerCart.getProduct().getProduct_price());

            for (AdditionItems productAddition: productPerCart.getAdditions()){

                // that is to put addition item id only
                AdditionItems additionItems = new AdditionItems();
                additionItems.setAdditions_item_id(productAddition.getAdditions_item_id());
                productAdditionsList.add(additionItems);
            }

            product.setAdditions_items(productAdditionsList);
            products_info_list.add(product);
        }

    }

    private boolean validation() {

        if(TextUtils.isEmpty(date_str)){
            Common.showErrorAlert(this,getString(R.string.please_select_date));
            return false;
        } else if(TextUtils.isEmpty(time_str)) {
            Common.showErrorAlert(this,getString(R.string.please_select_time));
            return false;
        } else if (TextUtils.isEmpty(place_str)) {
            Common.showErrorAlert(this, getString(R.string.select_place));
            return false;
        } else if (TextUtils.isEmpty(area_str)) {
            Common.showErrorAlert(this, getString(R.string.select_area));
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ((TextView) view).setTextColor(Color.BLUE);
        ((TextView) view).setTextSize(18);
        switch (parent.getId()){
            case R.id.time_spinner:
                time_str = timeList.get(position);
                break;
            case R.id.date_spinner:
                date_str = dateList.get(position);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
