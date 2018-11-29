package com.example.ashleighwilson.schoolscheduler.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;

import com.example.ashleighwilson.schoolscheduler.MySchedulerApp;
import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.SettingsActivity;
import com.example.ashleighwilson.schoolscheduler.notes.Constants;
import com.example.ashleighwilson.schoolscheduler.utils.ResourceUtils;

public class SettingsFragment extends PreferenceFragment {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    public final static String XML_NAME = "xmlName";
    private Activity activity;
    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int xmlId = R.xml.settings_main;
        if (getArguments() != null && getArguments().containsKey(XML_NAME)) {
            xmlId = ResourceUtils.getXmlId(MySchedulerApp.getInstance(), ResourceUtils.ResourceIdentifiers.xml,
                    String.valueOf(getArguments().get(XML_NAME)));
        }
        addPreferencesFromResource(xmlId);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        prefs = activity.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        setTitle();
    }

    private void setTitle() {
        String title = getString(R.string.settings);
        if (getArguments() != null && getArguments().containsKey(XML_NAME)) {
            String xmlName = getArguments().getString(XML_NAME);
            if (!TextUtils.isEmpty(xmlName)) {
                int stringResourceId = getActivity().getResources().getIdentifier(xmlName.replace("setting_",
                        "settings_screen_"), "string",
                        getActivity().getPackageName());
                title = stringResourceId != 0 ? getString(stringResourceId) : title;
            }
            Toolbar toolbar = ((Toolbar) getActivity().findViewById(R.id.main_toolbar));
            if (toolbar != null) {
                toolbar.setTitle(title);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        super.onPreferenceTreeClick(preferenceScreen, preference);
        if (preference instanceof PreferenceScreen) {
            ((SettingsActivity) getActivity()).switchToScreen(preference.getKey());
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();

        //Checklists
        final ListPreference checklist = (ListPreference) findPreference("settings_checked_behavior");
        if (checklist != null) {
            int checklistIndex = checklist.findIndexOfValue(prefs.getString("settings_checklist", "0"));
            String checklistString = getResources().getStringArray(R.array.checked_behavior)[checklistIndex];
            checklist.setSummary(checklistString);
            checklist.setOnPreferenceChangeListener(((preference, newValue) -> {
                int checklistIndex1 = checklist.findIndexOfValue(newValue.toString());
                String checklistString1 = getResources().getStringArray(R.array.checked_behavior)
                        [checklistIndex1];
                checklist.setSummary(checklistString1);
                prefs.edit().putString("settings_checked_behavior", newValue.toString()).apply();
                checklist.setValueIndex(checklistIndex1);
                return false;
            }));
        }
    }
}
