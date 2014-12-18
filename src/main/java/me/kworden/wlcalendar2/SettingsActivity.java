package me.kworden.wlcalendar2;

import me.kworden.wlcalendar2.fragment.SettingsFragment;
import me.kworden.wlcalendar2.util.APP;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;

public class SettingsActivity extends PreferenceActivity
{
	@Override
	protected void onCreate(Bundle p_bundle)
	{
		super.onCreate(p_bundle);
		APP.repull = true;
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_settings);
		getFragmentManager().beginTransaction().replace(R.id.frag_settings_content, new SettingsFragment()).commit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu p_menu)
	{
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem p_item)
	{
		int t_id = p_item.getItemId();
		
		switch(t_id)
		{
			case android.R.id.home:
				this.finish();
				return true;
		}
		return super.onOptionsItemSelected(p_item);
	}
}
