package com.peter.villavanilia.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.peter.villavanilia.OrderDetails;
import com.peter.villavanilia.R;
import com.peter.villavanilia.common.Common;
import com.peter.villavanilia.model.OrderModel;

import java.util.ArrayList;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    ArrayList<OrderModel> orderList;
    Context context;

    public OrderAdapter(ArrayList<OrderModel> orderModels) {
        this.orderList = orderModels;
    }

    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order,parent,false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, final int position) {

        holder.bind(orderList.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, OrderDetails.class);
                i.putExtra("order_id",orderList.get(position).getOrder_id());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.order_number)
        TextView order_number;
        @BindView(R.id.order_status)
        TextView order_status;
        @BindView(R.id.order_date)
        TextView order_date;
        @BindView(R.id.order_time)
        TextView order_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }


        public void bind(OrderModel orderModel) {

            order_number.setText(orderModel.getOrder_number());
            if(Common.isArabic)
                order_status.setText(orderModel.getOrder_status_name());
            else
                order_status.setText(orderModel.getOrder_status_en_name());

            order_date.setText(orderModel.getOrder_created_at());
            order_time.setText(orderModel.getOrder_time());
        }
    }
}
