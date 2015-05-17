package me.kworden.wlcalendar2.struct;

import java.util.Locale;

/** This enum entails all the types of possible events.
 *  The enums store classifiers to sift the raw data and parse it as certain  
 *  types of events. The user can then sort their feed based on the event types.
 */
public enum WLEventClassifier
{
	EXAM("pref_exam", new String[]{ "exam", "test", "psat", "assessment", "sat" }),
	SCHOOL_BOARD("pref_schoolboard", new String[]{ "school board meeting", "school board" }),
	PTA("pref_pta", new String[]{ "pta", "parent teacher association", "fundraiser" }),
	ASSEMBLY("pref_assembly", new String[]{ "assembly", "talent show", "audition", "band", "chorus", "orchestra" }),
	BREAK("pref_break", new String[]{ "holiday", "break", "early release", "no school" }),
	SPECIAL("pref_special", new String[]{ "picture", "portrait", "make", "visit", "president" }),
	IMPORTANT_STUDENT("pref_importantstudent", new String[]{ "night", "session", "interim", "report card", "banquet", "rally", "dance", "field trip", "trip" }),
	MISC("null", new String[0]),
	HEADER("null", new String[0]),
	LINEBREAK("null", new String[0]);
	
	private String m_pref_str;
	private String[] m_keywords;
	
	WLEventClassifier(String p_pref_str, String p_keywords[])
	{
		m_pref_str = p_pref_str;
		m_keywords = p_keywords;
	}
	
	public String[] getKeywordClassifiers()
	{
		return m_keywords;
	}
	
	public String getPreference()
	{
		return m_pref_str;
	}
	
	public static WLEventClassifier getMatchingEvent(String p_style_attr, String p_inner_html)
	{
		// Loop through the events to classify it //
		for(WLEventClassifier i_event : WLEventClassifier.values())
		{
			// It is classified as this event ONLY if it satisfies both classifiers //
			boolean i_keyword_match = false;
		
				
			if(i_event.getKeywordClassifiers().length > 0)
			{
				for(String ii_keyword : i_event.getKeywordClassifiers())
				{
					if(p_inner_html.toLowerCase(Locale.US).contains(ii_keyword))
					{
						i_keyword_match = true;
						break;
					}
				}
			}
			else
				i_keyword_match = true;
			
			if(i_keyword_match)
				return i_event;
		}
		
		return MISC;
	}
}
