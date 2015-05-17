package me.kworden.wlcalendar2.struct;

public class WLHeader extends WLEvent
{
	public WLHeader(String p_title)
	{
		title = p_title;
		type = WLEventClassifier.HEADER;
		
		date = null;
		location = null;
		info = null;
		time = null;
	}
}
