package com.peter.villavanilia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;

public class PaymentResult extends AppCompatActivity {

    @BindView(R.id.message)
    TextView message;
    @BindView(R.id.order_num)
    TextView order_num;
    @BindView(R.id.delivery_time)
    TextView delivery_time;
    @OnClick(R.id.done)
    public void done(){startActivity(new Intent(this,MainActivity.class));}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_result);
        ButterKnife.bind(this);
        Paper.init(this);
        Paper.book("villa_vanilia").delete("cart");

        message.setText(getIntent().getStringExtra("message"));
        order_num.setText(getIntent().getStringExtra("order_num"));
        delivery_time.setText(getIntent().getStringExtra("delivery_time"));

    }

    @Override
    public void onBackPressed() {
    }
}
