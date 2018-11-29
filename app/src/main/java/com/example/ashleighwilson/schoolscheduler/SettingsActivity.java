package com.example.ashleighwilson.schoolscheduler;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.ashleighwilson.schoolscheduler.settings.SettingsFragment;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    private Toolbar toolbar;
    private List<Fragment> backStack = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setTitle("Settings");

        getFragmentManager().beginTransaction().replace(R.id.settings_frag_container,
                new SettingsFragment()).commit();
    }

    public void switchToScreen(String key) {
        SettingsFragment sf = new SettingsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SettingsFragment.XML_NAME, key);
        sf.setArguments(bundle);
        backStack.add(getFragmentManager().findFragmentById(R.id.settings_frag_container));
        replaceFragment(sf);
    }

    private void replaceFragment(Fragment sf) {
        getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in,
                R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(R.id.settings_frag_container, sf)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (backStack.size() > 0) {
            replaceFragment(backStack.remove(backStack.size() - 1));
        } else {
            super.onBackPressed();
        }
    }
}
