package com.peter.villavanilia.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.peter.villavanilia.Delete_cart_interface;
import com.peter.villavanilia.R;
import com.peter.villavanilia.common.Common;
import com.peter.villavanilia.model.AdditionItems;
import com.peter.villavanilia.model.CartModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    ArrayList<CartModel> cartList;
    Context context;
    Delete_cart_interface delete_cart_interface;

    public CartAdapter(ArrayList<CartModel> cartModel) {
        this.cartList = cartModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart,parent,false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.bind(cartList.get(position));
    }

    @Override
    public int getItemCount() {

        return cartList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.product_image)
        ImageView product_image;
        @BindView(R.id.product_name)
        TextView product_name;
        @BindView(R.id.total)
        TextView product_price;
        @BindView(R.id.desc)
        TextView product_additions;

        CartModel cart_product;
        String additions = "";

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void bind(CartModel cartModel){
            cart_product = cartModel;

            if(Common.isArabic) {
                product_name.setText(cart_product.getProduct().getProduct_title_ar());
            }else{
                product_name.setText(cart_product.getProduct().getProduct_title_en());
            }

            for (AdditionItems additionItems :cart_product.getAdditions()) {
                if(Common.isArabic)
                    additions += additionItems.getAdditions_item_ar_name()+", ";
                else
                    additions += additionItems.getAdditions_item_en_name()+", ";
            }

            product_additions.setText(additions);
            product_price.setText(cart_product.getProduct().getProduct_price()+" "+context.getResources().getString(R.string.kd));
            Picasso.with(context).load(context.getResources().getString(R.string.image_link)+cart_product.getProduct().getProduct_img())
                    .placeholder(R.drawable.placeholder).into(product_image);

        }

        @OnClick(R.id.ic_delete)
        public void delete_cart(){
            delete_cart_interface.deleteClick(getAdapterPosition());
        }
    }

    public void setListener(Delete_cart_interface listener) {
        this.delete_cart_interface = listener;
    }
}
