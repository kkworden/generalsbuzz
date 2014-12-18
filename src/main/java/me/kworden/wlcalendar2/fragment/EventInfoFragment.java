package me.kworden.wlcalendar2.fragment;

import me.kworden.wlcalendar2.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class EventInfoFragment extends Fragment
{
	private String m_message, m_date, m_title;

	public EventInfoFragment()
	{
		super();
	}


	public void setArguments(String p_title, String p_date, String p_message)
	{
		if(p_title == null)
			m_title = "NO TITLE";
		else
			m_title = p_title;

		if(p_date == null)
			m_date = "NO DATE";
		else
			m_date = p_date;

		if(p_message == null)
			m_message = "";
		else
			m_message = p_message.replace("&amp;", "&").replace("&nbsp;", " ");
	}
	
	@Override
	public View onCreateView(LayoutInflater p_inflater, ViewGroup p_container, Bundle p_prev_state)
	{
		View t_view = p_inflater.inflate(R.layout.fragment_event_info, p_container, false);
		((TextView)t_view.findViewById(R.id.main_event_title)).setText(m_title);
		((TextView)t_view.findViewById(R.id.main_event_date)).setText(m_date);
		((TextView)t_view.findViewById(R.id.main_event_info)).setText(m_message);
		return t_view;
	}
}
