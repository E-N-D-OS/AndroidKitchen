package ru.unpro.apktool.ui.settings;

import android.os.*;
import android.preference.*;
import ru.unpro.apktool.*;

public class SettingsFragment extends PreferenceFragment
 {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
		String settings = getArguments().getString("ad");
        if ("a".equals(settings)) {
            addPreferencesFromResource(R.xml.main_prefs);
        } else if ("s".equals(settings)) {
            addPreferencesFromResource(R.xml.design_prefs);
        }
    }
}
