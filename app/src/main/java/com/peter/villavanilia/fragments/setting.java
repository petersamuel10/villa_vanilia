package com.peter.villavanilia.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.peter.villavanilia.ChangePassword;
import com.peter.villavanilia.MainActivity;
import com.peter.villavanilia.R;
import com.peter.villavanilia.setting.Profile;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;

/**
 * A simple {@link Fragment} subclass.
 */
public class setting extends Fragment {

    @OnClick(R.id.account_information)
    public void info(){
        getContext().startActivity(new Intent(getContext(), Profile.class));
    }

    @OnClick(R.id.change_password)
    public void change_pass(){
        getContext().startActivity(new Intent(getContext(), ChangePassword.class));
    }

    @OnClick(R.id.logout)
    public void logout(){

        Paper.book("villa_vanilia").delete("current_user");
        getContext().startActivity(new Intent(getContext(), MainActivity.class));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_setting, container, false);

        ButterKnife.bind(this,view);

        Paper.init(getContext());

        return view;
    }

}
