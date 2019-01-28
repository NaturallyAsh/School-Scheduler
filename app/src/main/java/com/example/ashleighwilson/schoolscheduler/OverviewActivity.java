package com.example.ashleighwilson.schoolscheduler;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.support.design.widget.AppBarLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ashleighwilson.schoolscheduler.adapter.ViewPagerAdapter;
import com.example.ashleighwilson.schoolscheduler.login.SessionManager;
import com.example.ashleighwilson.schoolscheduler.timetable.ExtendedCalendarView;
import com.example.ashleighwilson.schoolscheduler.views.CustomViewPager;

import static com.example.ashleighwilson.schoolscheduler.WeekViewFragment.testCal;
import static com.example.ashleighwilson.schoolscheduler.WeekViewFragment.TYPE_WEEK_VIEW;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class OverviewActivity extends AppCompatActivity
{
    private static final String TAG = OverviewActivity.class.getSimpleName();

    private static final int IMG_FILES = 1;
    static final int STORAGE_PERMS = 175;
    private NavigationView mNavigationView;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    CharSequence tabTitles[] = {"SUBJECTS", "AGENDA", "CALENDER"};
    int numOfTabs = 3;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private NestedScrollView nestedScrollView;
    private ExtendedCalendarView extendedCalendarView;
    private WeekViewFragment weekViewFragment;
    ViewPagerAdapter adapter;
    public static AppBarLayout appBarLayout;
    SessionManager session;
    private ImageView backdropIV;
    public static String POSITION = "position";
    TabLayout tabLayout;
    CustomViewPager viewPager;
    View navHeaderView;
    private CircleImageView headerIMV;
    private TextView headerTV;
    private int position;
    public static Toolbar fragToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate!");

        setContentView(R.layout.activity_main_nav);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("School Scheduler");

        fragToolbar = findViewById(R.id.frag_toolbar);

        session = new SessionManager(getApplicationContext());
        //call this whenever you want to check user login
        //this will redirect user to LoginActivity if not logged in
        session.checkLogin();

        if (Build.VERSION.SDK_INT > 22)
            checkPermission();

        HashMap<String, String> user = session.getUserDetails();

        String email = user.get(SessionManager.KEY_EMAIL);
        String password = user.get(SessionManager.KEY_PASS);
        String profileImage = session.getProfileImage();

        //set text from these somewhere..

        //Add logout button somewhere...

        if (Build.VERSION.SDK_INT > 22)
        {
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMS);
        }

        //TODO: LOOK INTO CREATING SEPARATE TAB METHODS FOR ACCESS TO OTHER ACTIVITIES AND FRAGS
        //TODO: ALSO SIS. ALSO SAVE THE PAGE YOU WERE ON BEFORE EXITING THE APP.
        //TODO: MAYBE SHARED PREFS

        appBarLayout = findViewById(R.id.main_appBar);
        collapsingToolbarLayout = findViewById(R.id.overview_collapsingTB);
        collapsingToolbarLayout.setTitleEnabled(false);
        backdropIV = findViewById(R.id.main_backdrop);
        viewPager = findViewById(R.id.viewpager);

        if (adapter == null) {
            adapter = new ViewPagerAdapter(this, getSupportFragmentManager(), tabTitles, numOfTabs);
        }
        viewPager.setAdapter(adapter);
        viewPager.setPagingEnabled(false);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        if (savedInstanceState != null) {
            Log.i(TAG, "SIS is not null");
            position = savedInstanceState.getInt(POSITION);
            viewPager.setCurrentItem(position);
        } else {
            Log.i(TAG, "SIS is null");
        }

        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        mNavigationView = findViewById(R.id.nav_view);
        setupDrawerContent(mNavigationView);

        navHeaderView = mNavigationView.inflateHeaderView(R.layout.nav_header_main_nav);
        headerIMV = navHeaderView.findViewById(R.id.header_imageView);
        headerTV = navHeaderView.findViewById(R.id.header_tv);

        headerTV.setText(email);
        if (profileImage != null) {
            headerIMV.setImageURI(Uri.parse(profileImage));
        }
        setImage();

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setOffscreenPageLimit(3);
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
                                .into(backdropIV);
                        appBarLayout.setExpanded(true);
                        getSupportActionBar().setTitle("School Scheduler");
                        break;
                    case 1:
                        Glide.with(getApplicationContext())
                                .load(R.drawable.agenda_drawable)
                                .into(backdropIV);
                        appBarLayout.setExpanded(true);
                        getSupportActionBar().setTitle("School Scheduler");
                        break;
                    case 2:
                        Glide.with(getApplicationContext())
                                .load(R.drawable.calendar_events_drawable)
                                .into(backdropIV);
                        appBarLayout.setExpanded(false);
                        String name = testCal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) +
                                " " + testCal.get(Calendar.YEAR);
                        getSupportActionBar().setTitle(name);
                        Log.i(TAG, "WV cal: " + testCal.get(Calendar.YEAR) + ", " + testCal.get(Calendar.MONTH));

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

        headerIMV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFiles();
            }
        });
    }

    private void getImageFiles() {
        Intent filesIntent;
        filesIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        filesIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        filesIntent.setType("*/*");
        startActivityForResult(filesIntent, IMG_FILES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1 && intent != null) {
                switch (requestCode) {
                    case IMG_FILES:
                        Uri selectedImage = intent.getData();

                        this.grantUriPermission(this.getPackageName(), selectedImage, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                        this.getContentResolver().takePersistableUriPermission(selectedImage, takeFlags);

                        session.setProfileImage(String.valueOf(selectedImage));

                        Glide.with(getApplicationContext())
                                .load(selectedImage)
                                .apply(new RequestOptions().centerCrop())
                                //.transition(withCrossFade())
                                .into(headerIMV);
                        headerIMV.invalidate();

                        break;
                }
            }
        }
    }

    private void setImage()
    {
        Glide.with(getApplicationContext())
                .load(R.drawable.curriculum)
                //.apply(new RequestOptions().override(600, 100))
                .into(backdropIV);
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
        outState.putInt(POSITION, tabLayout.getSelectedTabPosition());
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
        int count = getFragmentManager().getBackStackEntryCount();

        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        if (count == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
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
            case R.id.nav_timetable:
                Intent intent = new Intent(OverviewActivity.this, WeekViewLayout.class);
                startActivity(intent);
                break;
            case R.id.nav_grades:
                Intent gradesIntent = new Intent(this, GradesActivity.class);
                startActivity(gradesIntent);
                break;
            case R.id.nav_notes:
                Intent notesIntent = new Intent(this, NotesActivity.class);
                startActivity(notesIntent);
                break;
            case R.id.nav_recordings:
                Intent recordIntent = new Intent(this, RecordActivity.class);
                startActivity(recordIntent);
                break;
            case R.id.nav_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.logout:
                session.logoutUser();
                break;
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
