package com.peter.villavanilia;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.peter.villavanilia.common.Common;
import com.peter.villavanilia.model.AddOrder;
import com.peter.villavanilia.model.AdditionItems;
import com.peter.villavanilia.model.CartModel;
import com.peter.villavanilia.model.ProductModel;

import java.util.ArrayList;

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
    @BindView(R.id.address)
    EditText address_ed;
    @OnClick(R.id.back_arrow)
    public void back(){onBackPressed();}

    ArrayList<String> dateList ;
    ArrayList<String> timeList ;
    ArrayList<CartModel> cartModel;
    ArrayList<ProductModel> products_info_list;

    String date_str,time_str,driver_notes_str,kitchen_notes_str,cart_notes_str,address;
    AddOrder addOrderModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_time);
        ButterKnife.bind(this);

        if (Common.isArabic)
            back_arrow.setRotation(180);

        address_ed.setText(Common.currentUser.getUser().getUser_address());

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

    @OnClick(R.id.continue_btn)
    public void continue_btn(){

        String user_id = Common.currentUser.getUser().getUser_id();

        driver_notes_str = driver_notes.getText().toString();
        kitchen_notes_str = kitchen_notes.getText().toString();
        cart_notes_str = cart_notes.getText().toString();
        address = address_ed.getText().toString();

        getProductInfo();

        if(validation()){

            addOrderModel = new AddOrder("", user_id,address, driver_notes_str, kitchen_notes_str,
                    cart_notes_str, date_str, time_str,products_info_list);


            Intent i = new Intent(this,PaymentMethod.class);
            i.putExtra("add_order",addOrderModel);
            startActivity(i);
            finish();

           /* Gson gson = new Gson();
            String str = gson.toJson(addOrderModel);

            Log.i("adddd",str);*/

        }
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
        }

        else if(TextUtils.isEmpty(time_str)) {
            Common.showErrorAlert(this,getString(R.string.please_select_time));
            return false;
        }

        else if(TextUtils.isEmpty(address)){
            Common.showErrorAlert(DateTime.this,getString(R.string.please_enter_your_address));
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
               /* Snackbar snackbar = Snackbar.make(rootLayout,time_str,Snackbar.LENGTH_LONG);
                snackbar.show();*/
                break;
            case R.id.date_spinner:
                date_str = dateList.get(position);
               /* snackbar = Snackbar.make(rootLayout,date_str,Snackbar.LENGTH_LONG);
                snackbar.show();*/
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
