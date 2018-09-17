package ru.unpro.apktool.ui.settings;
import android.os.*;
import android.preference.*;
import java.util.*;
import ru.unpro.apktool.*;
import android.app.*;

public class SettingsActivity extends PreferenceActivity
{
	@Override
    public void onBuildHeaders(List<Header> target)
	{
        loadHeadersFromResource(R.xml.ms_prefs, target);
    }
	@Override
	protected boolean isValidFragment(String fragmentName)
	{
		return SettingsFragment.class.getName().equals(fragmentName);}
}
