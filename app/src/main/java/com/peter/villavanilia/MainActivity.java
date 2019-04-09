package com.peter.villavanilia;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.peter.villavanilia.common.Common;
import com.peter.villavanilia.fragments.AboutApp;
import com.peter.villavanilia.fragments.Cart;
import com.peter.villavanilia.fragments.Category;
import com.peter.villavanilia.fragments.Login;
import com.peter.villavanilia.fragments.Orders;
import com.peter.villavanilia.fragments.setting;

import java.util.Locale;

import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static TextView titleTxt;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    MenuItem mCart;
    static boolean isLogin;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadLocal();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        titleTxt = findViewById(R.id.title);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        toggle.setDrawerIndicatorEnabled(false);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        toggle.setHomeAsUpIndicator(R.drawable.menu_icon);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //init paper
        Paper.init(this);

        setup();

    }

    private void setup() {

        checkAuthontication();
        navigationView.getMenu().getItem(0).setChecked(true);
        // get menu from navigationView
        Menu menu = navigationView.getMenu();

        MenuItem login = menu.findItem(R.id.nav_login);
        MenuItem setting = menu.findItem(R.id.nav_setting);
        MenuItem orders = menu.findItem(R.id.nav_orders);
        MenuItem cart = menu.findItem(R.id.nav_cart);
        MenuItem lan = menu.findItem(R.id.nav_lang);

        if(Common.isArabic)
            lan.setIcon(R.drawable.ic_menu_lang_ar);
        else
            lan.setIcon(R.drawable.ic_menu_lang_en);

        if(isLogin) {
            login.setVisible(false);
            setting.setVisible(true);
            orders.setVisible(true);
            cart.setIcon(R.drawable.ic_menu_cart);
        }else {
            login.setVisible(true);
            setting.setVisible(false);
            orders.setVisible(false);
            cart.setIcon(R.drawable.ic_shopping_login);
        }
        if(getIntent().hasExtra("product_activity")) {
            chooseFragment(new Cart());
            toolbar.setBackgroundResource(R.drawable.header);
            toggle.setHomeAsUpIndicator(R.drawable.white_menu_ic);
            titleTxt.setText(getString(R.string.cart));

        }else
            chooseFragment(new Category());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void setLanguage(String lang)
    {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale= locale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
        Paper.book("villa_vanilia").write("language",lang);

    }

    public void loadLocal()
    {
        Paper.init(this);
        String lan = Paper.book("villa_vanilia").read("language");
        if (!TextUtils.isEmpty(lan)) {
            setLanguage(lan);
            if(lan.contentEquals("ar"))
                Common.isArabic = true;
        }
        else {
            setLanguage("ar");
            Common.isArabic = false;
        }
    }

    private void checkAuthontication() {
        if(Paper.book("villa_vanilia").contains("current_user")) {
             Common.currentUser = Paper.book("villa_vanilia").read("current_user");
             isLogin = true;
        }else
            isLogin = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        mCart = menu.findItem(R.id.action_cart);

        if(getIntent().hasExtra("product_activity"))
            mCart.setIcon(R.drawable.ic_cart);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cart) {
            chooseFragment(new Cart());
            toolbar.setBackgroundResource(R.drawable.header);
            toggle.setHomeAsUpIndicator(R.drawable.white_menu_ic);
            mCart.setIcon(R.drawable.ic_cart);
            titleTxt.setText(getString(R.string.cart));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            chooseFragment(new Category());
            toolbar.setBackgroundColor(getResources().getColor(R.color.grey));
            toggle.setHomeAsUpIndicator(R.drawable.menu_icon);
            mCart.setIcon(R.drawable.ic_cart_green);
        }else if (id == R.id.nav_login) {
            chooseFragment(new Login());
            toolbar.setBackgroundResource(R.drawable.header);
            toggle.setHomeAsUpIndicator(R.drawable.white_menu_ic);
            mCart.setIcon(R.drawable.ic_cart);
        } else if (id == R.id.nav_orders) {
            chooseFragment(new Orders());
            toolbar.setBackgroundResource(R.drawable.header);
            toggle.setHomeAsUpIndicator(R.drawable.white_menu_ic);
            mCart.setIcon(R.drawable.ic_cart);
        } else if (id == R.id.nav_setting) {
            chooseFragment(new setting());
            toolbar.setBackgroundResource(R.drawable.header);
            toggle.setHomeAsUpIndicator(R.drawable.white_menu_ic);
            mCart.setIcon(R.drawable.ic_cart);
        } else if (id == R.id.nav_lang) {
            showLangArelet();
        } else if (id == R.id.nav_cart) {
            chooseFragment(new Cart());
            toolbar.setBackgroundResource(R.drawable.header);
            toggle.setHomeAsUpIndicator(R.drawable.white_menu_ic);
            mCart.setIcon(R.drawable.ic_cart);
        } else if (id == R.id.nav_about) {
            chooseFragment(new AboutApp());
            toolbar.setBackgroundResource(R.drawable.header);
            toggle.setHomeAsUpIndicator(R.drawable.white_menu_ic);
            mCart.setIcon(R.drawable.ic_cart);
        }

        titleTxt.setText(item.getTitle());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showLangArelet() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_lang_dialoge, null);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button arabic = view.findViewById(R.id.ar_lang);
        Button english = view.findViewById(R.id.en_lang);

        arabic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.isArabic = true;
                setNewLanguage("ar");
            }
        });

        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.isArabic = false;
                setNewLanguage("en");

            }
        });

        alertDialog.show();
    }

    private void setNewLanguage(String lan) {

        Paper.book("villa_vanilia").write("language",lan);

        Intent i = getPackageManager()
                .getLaunchIntentForPackage(getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(i);
    }

    private void chooseFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
    }
}
