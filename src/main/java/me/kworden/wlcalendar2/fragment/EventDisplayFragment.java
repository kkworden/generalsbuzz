package me.kworden.wlcalendar2.fragment;

import me.kworden.wlcalendar2.MainActivity;
import me.kworden.wlcalendar2.R;
import me.kworden.wlcalendar2.struct.WLEvent;
import me.kworden.wlcalendar2.util.BROADCAST;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class EventDisplayFragment extends Fragment
{
	private WLEvent[] m_events;
	private ListView m_listview;
	private View m_root;
	
	@Override
    public void onSaveInstanceState(Bundle p_state)
	{
		if(m_events != null)
			p_state.putParcelableArray("EVENTS", m_events);
	}
	
	@Override
	public View onCreateView(LayoutInflater p_inflater, ViewGroup p_container, Bundle p_prev_state)
	{
		m_root = p_inflater.inflate(R.layout.fragment_main, p_container, false);
		m_listview = (ListView)m_root.findViewById(R.id.main_events);
		return m_root;
	}

	@Override
    public void onActivityCreated(Bundle p_prev_state)
	{
		super.onActivityCreated(p_prev_state);
		
		if(p_prev_state != null && p_prev_state.containsKey("EVENTS"))
			m_events = (WLEvent[])p_prev_state.getParcelableArray("EVENTS");
	}
	
	public int getIndex()
	{
		try
		{
			return m_listview.getFirstVisiblePosition();
		}
		catch(Exception e){ return 0; }
	}
	
	public int getTop()
	{
		try
		{
			return m_listview.getChildAt(0).getTop();
		}
		catch(Exception e){ return 0; }
	}

	public void setEvents(WLEvent[] p_events)
	{
		m_events = p_events;
	}
	
	public WLEvent[] getEvents()
	{
		return m_events;
	}
}
