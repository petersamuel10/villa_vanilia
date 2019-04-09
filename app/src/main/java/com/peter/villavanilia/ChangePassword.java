package com.peter.villavanilia;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.peter.villavanilia.common.Common;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangePassword extends AppCompatActivity {

    @BindView(R.id.ic_cart)
    ImageView ic_cart;
    @BindView(R.id.back_arrow)
    ImageView back_arrow;
    @OnClick(R.id.back_arrow)
    public void back(){onBackPressed();}
    @BindView(R.id.new_password_ed)
    EditText new_password_ed;
    @BindView(R.id.current_pass_ed)
    EditText current_password_rd;
    @BindView(R.id.confirm_new_password_ed)
    EditText confirm_password_ed;
    @BindView(R.id.changePassBtn)
    Button change_pass_btn;

    String current_password,new_password,confirm_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        ButterKnife.bind(this);

        if(Common.isArabic){back_arrow.setRotation(180);}
    }


    @OnClick(R.id.changePassBtn)
    public void change_pass(){

        current_password = current_password_rd.getText().toString();
        new_password = new_password_ed.getText().toString();
        confirm_password = confirm_password_ed.getText().toString();

        if(validate(current_password,new_password,confirm_password)){

        if(Common.isConnectToTheInternet(this)) {

            /*compositeDisposable.add(Common.getAPI().register()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<LoginData>() {
                        @Override
                        public void accept(LoginData loginData) throws Exception {

                        }
                    }));*/

        }else
            Common.showErrorAlert(this,getString(R.string.error_no_internet_connection));
    }
    }


    private boolean validate(String current_password, String new_password, String confirm_password) {

        if(TextUtils.isEmpty(current_password)) {
            Common.showErrorAlert(this,getString(R.string.please_enter_current_password));
            return false;
        }else if (TextUtils.isEmpty(new_password)){
            Common.showErrorAlert(this,getString(R.string.please_enter_new_password));
            return false;
        }else if (TextUtils.isEmpty(confirm_password)){
            Common.showErrorAlert(this,getString(R.string.please_enter_confirm_password));
            return false;
        }else if (!new_password.equals(this.confirm_password)){
            Common.showErrorAlert(this,getString(R.string.error_confirm_password_not_match_new_password));
            return false;
        }

        return true;
    }
}
