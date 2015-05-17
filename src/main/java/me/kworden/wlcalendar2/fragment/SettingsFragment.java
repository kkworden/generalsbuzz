package me.kworden.wlcalendar2.fragment;

import me.kworden.wlcalendar2.R;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment
{
	@Override
	public void onCreate(Bundle p_prev_state)
	{
		super.onCreate(p_prev_state);
		addPreferencesFromResource(R.xml.pref_general);
	}
}
