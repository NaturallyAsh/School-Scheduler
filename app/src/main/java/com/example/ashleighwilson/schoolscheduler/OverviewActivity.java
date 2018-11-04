package com.example.ashleighwilson.schoolscheduler;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.design.widget.AppBarLayout;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.ashleighwilson.schoolscheduler.adapter.ViewPagerAdapter;
import com.example.ashleighwilson.schoolscheduler.login.SessionManager;

import java.util.ArrayList;

public class OverviewActivity extends AppCompatActivity
{
    private static final String TAG = OverviewActivity.class.getSimpleName();

    static final int STORAGE_PERMS = 175;
    private NavigationView mNavigationView;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    CharSequence tabTitles[] = {"SUBJECTS", "AGENDA", "CALENDER"};
    int numOfTabs = 3;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private NestedScrollView nestedScrollView;
    ViewPagerAdapter adapter;
    AppBarLayout appBarLayout;
    SessionManager session;
    private ImageView backdropIV;
    public static String POSITION = "position";
    TabLayout tabLayout;
    ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate!");

        setContentView(R.layout.activity_main_nav);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        session = new SessionManager(getApplicationContext());
        //call this whenever you want to check user login
        //this will redirect user to LoginActivity if not logged in
        session.checkLogin();

        if (Build.VERSION.SDK_INT > 22)
            checkPermission();
        /*
        HashMap<String, String> user = session.getUserDetails();

        String email = user.get(SessionManager.KEY_EMAIL);
        String password = user.get(SessionManager.KEY_PASS);

        set text from these somewhere..

        Add logout button somewhere...
         */
        if (Build.VERSION.SDK_INT > 22)
        {
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMS);
        }

        appBarLayout = findViewById(R.id.main_appBar);
        collapsingToolbarLayout = findViewById(R.id.overview_collapsingTB);
        collapsingToolbarLayout.setTitleEnabled(false);
        backdropIV = findViewById(R.id.main_backdrop);
        viewPager = findViewById(R.id.viewpager);

        adapter = new ViewPagerAdapter(this, getSupportFragmentManager(), tabTitles, numOfTabs);
        viewPager.setAdapter(adapter);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        mNavigationView = findViewById(R.id.nav_view);
        setupDrawerContent(mNavigationView);

        setImage();

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setOffscreenPageLimit(2);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                viewPager.setCurrentItem(tab.getPosition());
                //toolbar.setTitle(adapter.getPageTitle(tab.getPosition()));
                switch (tab.getPosition())
                {
                    case 0:
                        Glide.with(getApplicationContext())
                                .load(R.drawable.curriculum)
                                //.apply(new RequestOptions().override(600, 400))
                                .into(backdropIV);
                        //Picasso.get().load(R.drawable.curriculum_banner).resize(600, 300).into(backdropIV);
                        break;
                    case 1:
                        Glide.with(getApplicationContext())
                                .load(R.drawable.agenda_drawable)
                                //.apply(new RequestOptions().override(600, 600))
                                .into(backdropIV);
                        //Picasso.get().load(R.drawable.agenda_drawable).resize(600, 300).into(backdropIV);
                        break;
                    case 2:
                        Glide.with(getApplicationContext())
                                .load(R.drawable.calendar_events_drawable)
                                //.apply(new RequestOptions().override(600, 300))
                                .into(backdropIV);
                        //Picasso.get().load(R.drawable.calendar_events_drawable).resize(600, 300).into(backdropIV);
                        break;
                }
                invalidateFragmentMenus(tab.getPosition());
                backdropIV.invalidate();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                final float normalizedPosition = Math.abs(Math.abs(position) - 1);
                page.setScaleX(normalizedPosition / 2 + 0.5f);
                page.setScaleY(normalizedPosition / 2 + 0.5f);
            }
        });
    }

    private void setImage()
    {
        Glide.with(getApplicationContext())
                .load(R.drawable.curriculum)
                //.apply(new RequestOptions().override(600, 100))
                .into(backdropIV);
        //Picasso.get().load(R.drawable.curriculum_banner).resize(600, 300).into(backdropIV);
    }

    private void invalidateFragmentMenus(int position)
    {
        for (int i = 0; i < adapter.getCount(); i++)
        {
            adapter.getItem(i).setHasOptionsMenu(i == position);
        }
        invalidateOptionsMenu();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        //outState.putInt(POSITION, tabLayout.getSelectedTabPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        //viewPager.setCurrentItem(savedInstanceState.getInt(POSITION));
    }

    @Override
    public void onBackPressed()
    {
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //appBarLayout.setVisibility(View.INVISIBLE);
        //getMenuInflater().inflate(R.menu.main_nav, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView)
    {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                selectDrawerItem(menuItem);
                return true;
            }
        });
    }

    public void selectDrawerItem(MenuItem menuItem)
    {
        switch (menuItem.getItemId())
        {
            case R.id.nav_grades:
                Intent gradesIntent = new Intent(this, GradesActivity.class);
                startActivity(gradesIntent);
                break;
            case R.id.nav_notes:
                Intent notesIntent = new Intent(this, NotesActivity.class);
                startActivity(notesIntent);
                break;
            case R.id.logout:
                session.logoutUser();
        }

        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        drawer.closeDrawer(GravityCompat.START);
    }

    private void checkPermission()
    {
        ArrayList<String> arrayPerm = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager
                .PERMISSION_GRANTED)
        {
            arrayPerm.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager
                .PERMISSION_GRANTED)
        {
            arrayPerm.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!arrayPerm.isEmpty())
        {
            String[] permissions = new String[arrayPerm.size()];
            permissions = arrayPerm.toArray(permissions);
            ActivityCompat.requestPermissions(this, permissions, STORAGE_PERMS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults)
    {
        switch (requestCode)
        {
            case STORAGE_PERMS:
            {
                if (grantResults.length > 0)
                {
                    for (int i = 0; i < grantResults.length; i++)
                    {
                        String permission = permissions[i];
                        if (Manifest.permission.READ_EXTERNAL_STORAGE.equals(permission))
                        {
                            if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                            {

                            }
                        }
                        if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission))
                        {
                            if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                            {

                            }
                        }
                    }
                }
            }
        }
    }
}
