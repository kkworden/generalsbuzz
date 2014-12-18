package me.kworden.wlcalendar2.receiver;

import java.util.ArrayList;

import me.kworden.wlcalendar2.MainActivity;
import me.kworden.wlcalendar2.R;
import me.kworden.wlcalendar2.adapter.EventAdapter;
import me.kworden.wlcalendar2.fragment.EventDisplayFragment;
import me.kworden.wlcalendar2.struct.WLEvent;
import me.kworden.wlcalendar2.util.APP;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.ListView;

public class UpdateRowsReceiver extends BroadcastReceiver
{
	private EventDisplayFragment m_fragment;
	
	public UpdateRowsReceiver(EventDisplayFragment p_fragment)
	{
		m_fragment = p_fragment;
	}
	
	@Override
	public void onReceive(final Context p_context, Intent p_intent)
	{
		if(p_intent.getExtras() != null && p_intent.getExtras().containsKey("EVENTS"))
			m_fragment.setEvents((WLEvent[])p_intent.getExtras().getParcelableArray("EVENTS"));
		
		ArrayList<WLEvent> t_events = new ArrayList<WLEvent>();
		for(WLEvent i_event : m_fragment.getEvents())
			t_events.add(i_event);
		
		final ListView t_lv = (ListView)m_fragment.getView().findViewById(R.id.main_events);
		EventAdapter t_event_ad = new EventAdapter((MainActivity)m_fragment.getActivity(), t_events);
		
		t_lv.setAdapter(t_event_ad);
		t_lv.setClickable(true);
		t_lv.setSelectionFromTop(APP.scrollPos[0], APP.scrollPos[1]);
	}
}
