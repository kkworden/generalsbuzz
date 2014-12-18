package me.kworden.wlcalendar2;

import me.kennyworden.utils.KWDateSuite;
import me.kworden.wlcalendar2.async.RequestRemoteDataTask;
import me.kworden.wlcalendar2.fragment.EventDisplayFragment;
import me.kworden.wlcalendar2.fragment.EventInfoFragment;
import me.kworden.wlcalendar2.receiver.CheckLocalDataReceiver;
import me.kworden.wlcalendar2.receiver.UpdateRowsReceiver;
import me.kworden.wlcalendar2.util.APP;
import me.kworden.wlcalendar2.util.BROADCAST;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

// Extend Activity in higher versions. ActionBarActivity is for support only. //
public class MainActivity extends Activity
{
	private boolean m_inited;
	
	private EventDisplayFragment m_event_display;
	private BroadcastReceiver m_data_check_recv, m_update_recv;
		
	@Override
	protected void onCreate(Bundle p_prev_state)
	{
		super.onCreate(p_prev_state);
		setContentView(R.layout.activity_main);
		
		APP.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		m_event_display = new EventDisplayFragment();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		
		// Unregister the receivers, so that we can free up memory when the app isn't being used. //
		LocalBroadcastManager.getInstance(this).unregisterReceiver(m_data_check_recv);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(m_update_recv);
		
		this.leaveInfoFragment(true);
		
		APP.scrollPos[0] = m_event_display.getIndex();
		APP.scrollPos[1] = m_event_display.getTop();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		// Register the necessary receievers. //
		
		// Invokers: MainActivity.java //
		LocalBroadcastManager.getInstance(this).registerReceiver((m_data_check_recv = new CheckLocalDataReceiver()),
				new IntentFilter(BROADCAST.CHECK_LOCAL_DATA));
		
		// Invokers: ParseDataTask.java //
		LocalBroadcastManager.getInstance(this).registerReceiver((m_update_recv = new UpdateRowsReceiver(m_event_display)),
				new IntentFilter(BROADCAST.UPDATE_EVENTS));

		if(!m_inited)
		{
			getFragmentManager().beginTransaction().replace(R.id.frag_main_content, m_event_display).addToBackStack(null).commit();
			
			// Do an update. //
			Intent t_intent = new Intent(BROADCAST.CHECK_LOCAL_DATA);
			t_intent.putExtra("MONTHS", APP.getDatesToLoad(Integer.parseInt(APP.sharedPreferences.getString("pref_months", "1"))));
			LocalBroadcastManager.getInstance(this).sendBroadcast(t_intent);
			
			m_inited = true;
		}
		else if(APP.repull)
		{
			Intent t_intent = new Intent(BROADCAST.CHECK_LOCAL_DATA);
			t_intent.putExtra("MONTHS", APP.getDatesToLoad(Integer.parseInt(APP.sharedPreferences.getString("pref_months", "1"))));
			LocalBroadcastManager.getInstance(this).sendBroadcast(t_intent);
			APP.repull = false;
		}
		//else
		//	this.updateEvents();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		menu.clear();

		if(getFragmentManager().getBackStackEntryCount() > 1)
			getMenuInflater().inflate(R.menu.event, menu);
		else
			getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}    

	@Override
	public boolean onOptionsItemSelected(MenuItem p_item)
	{
		int t_id = p_item.getItemId();
		switch(t_id)
		{
			case R.id.action_settings:
				startActivity(new Intent(this, SettingsActivity.class));
				return true;
			case R.id.action_update:
				new RequestRemoteDataTask(this).execute(APP.getDatesToLoad(Integer.parseInt(APP.sharedPreferences.getString("pref_months", "1"))));
				return true;
			case android.R.id.home:
				leaveInfoFragment(true);
				return true;
			case R.id.action_event_hide:
				if(APP.currentEvent == null)
					break;

				if(!APP.currentEvent.type.getPreference().equals("null"))
				{
					this.leaveInfoFragment(false);
					APP.sharedPreferences.edit().putBoolean(APP.currentEvent.type.getPreference(), false).commit();
					Intent t_intent = new Intent(BROADCAST.CHECK_LOCAL_DATA);
					t_intent.putExtra("MONTHS", APP.dates);
					LocalBroadcastManager.getInstance(this).sendBroadcast(t_intent);
					Toast.makeText(this, "Similar events hidden.", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.action_event_remind:
				if(APP.currentEvent == null)
					break;

				leaveInfoFragment(false);
				Intent intent = new Intent(Intent.ACTION_EDIT);
				intent.setType("vnd.android.cursor.item/event");
				intent.putExtra("title", APP.currentEvent.title + (!APP.currentEvent.time.equals("") ? " " + APP.currentEvent.time : ""));
				intent.putExtra("description", APP.currentEvent.time);
				intent.putExtra("allDay", true);

				if(!APP.currentEvent.location.equals("Unspecified"))
					intent.putExtra("eventLocation", APP.currentEvent.location);

				intent.putExtra("beginTime", KWDateSuite.get9OClockOfDay(APP.currentEvent.date));
				intent.putExtra("endTime", KWDateSuite.get9OClockOfDay(APP.currentEvent.date) + 3600000);
				this.startActivity(intent);
				break;
				
		}
		return super.onOptionsItemSelected(p_item);
	}
	
	public void openInfoFragment(String p_title, String p_date, String p_info)
	{
		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		APP.scrollPos[0] = m_event_display.getIndex();
		APP.scrollPos[1] = m_event_display.getTop();

		/* Open the fragment with event data */
		EventInfoFragment t_frag = new EventInfoFragment();
		t_frag.setArguments(p_title, p_date, p_info);

		/* Fade in and out when changing fragments */
		getFragmentManager().beginTransaction()
			.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
			.replace(R.id.frag_main_content, t_frag).addToBackStack(null).commit();
	}
	
	public void leaveInfoFragment(boolean p_pause)
	{
		if(getFragmentManager().getBackStackEntryCount() == 1 && !p_pause)
			this.finish();
		else if(getFragmentManager().getBackStackEntryCount() > 1)
		{
			this.getFragmentManager().popBackStack();
			
			this.getActionBar().setDisplayHomeAsUpEnabled(false);
			this.updateEvents();
		}
	}
	
	public boolean onKeyDown(int p_code, KeyEvent p_event)
	{
		if(p_code == KeyEvent.KEYCODE_BACK)
		{
			this.leaveInfoFragment(false);
			return false;
		}

		return super.onKeyDown(p_code, p_event);
	}

	public void updateEvents()
	{
		Intent t_intent = new Intent(BROADCAST.UPDATE_EVENTS);
		LocalBroadcastManager.getInstance(this).sendBroadcast(t_intent);
	}
}
