package me.kworden.wlcalendar2.async;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import me.kennyworden.utils.KWDateSuite;
import me.kworden.wlcalendar2.struct.WLEvent;
import me.kworden.wlcalendar2.struct.WLEventClassifier;
import me.kworden.wlcalendar2.struct.WLHeader;
import me.kworden.wlcalendar2.util.APP;
import me.kworden.wlcalendar2.util.BROADCAST;
import me.kworden.wlcalendar2.util.PATTERNS;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

public class ParseDataTask extends AsyncTask<String, Void, ArrayList<WLEvent>>
{
	Context m_context;
	
	public ParseDataTask(Context p_context)
	{
		m_context = p_context;
	}
	
	@Override
	protected ArrayList<WLEvent> doInBackground(String ... p_args)
	{
		// The arraylist of events parsed from the arguments //
		ArrayList<WLEvent> t_events = new ArrayList<WLEvent>();

		// Loop through all arguments (the data for each month) //
		for(String t_data : p_args)
		{
			try
			{
		    	DocumentBuilder t_build = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		    	t_data = t_data.replace("&", "&amp;").replace("&nbsp;", " ");
				Document t_doc = t_build.parse(new ByteArrayInputStream(t_data.getBytes()));
				
				String t_prev_date = "";
				
				// Loop through all td's in the document //
				NodeList t_list = t_doc.getElementsByTagName("td");
				for(int i = 0; i < t_list.getLength(); i++)
				{
					Node i_t_td = t_list.item(i);
					
					Matcher t_match = PATTERNS.EXTRACT_DATE.matcher(i_t_td.getTextContent());
					t_match.find();
					String t_date = t_match.group(0); 

					// Don't read events that are before today //
					if(KWDateSuite.isBefore(t_date, KWDateSuite.getToday()))
					{
						System.out.println("Skipping event with date: " + t_date);
						continue;
					}

					// Private method for ripping through and parsing data //
					WLEvent i_t_event = getEventInfo(i_t_td);

					if(i_t_event == null) continue;

					i_t_event.date = t_date;
					i_t_event.type = WLEventClassifier.getMatchingEvent(getEventStyle(i_t_td), i_t_td.getTextContent());

					// If the app preferences specify not to show this type of event, let's continue //
					if(i_t_event.type == WLEventClassifier.EXAM && !(APP.sharedPreferences.getBoolean("pref_exam", true)))
						continue;
					else if(i_t_event.type == WLEventClassifier.SCHOOL_BOARD && !(APP.sharedPreferences.getBoolean("pref_schoolboard", true)))
						continue;
					else if(i_t_event.type == WLEventClassifier.PTA && !(APP.sharedPreferences.getBoolean("pref_pta", true)))
						continue;
					else if(i_t_event.type == WLEventClassifier.ASSEMBLY && !(APP.sharedPreferences.getBoolean("pref_assembly", true)))
						continue;
					else if(i_t_event.type == WLEventClassifier.BREAK && !(APP.sharedPreferences.getBoolean("pref_break", true)))
						continue;
					else if(i_t_event.type == WLEventClassifier.SPECIAL && !(APP.sharedPreferences.getBoolean("pref_special", true)))
						continue;
					else if(i_t_event.type == WLEventClassifier.IMPORTANT_STUDENT && !(APP.sharedPreferences.getBoolean("pref_importantstudent", true)))
						continue;
					
					if(!t_prev_date.equals(t_date))
					{
						if(t_events.size() > 0)
						{
							//t_events.add(new WLEventLinebreak()); Removed for now.
							t_events.add(new WLHeader(" "));
						}
						
						t_events.add(new WLHeader(KWDateSuite.stripYear(t_date) + " - " + KWDateSuite.getDayOfWeekAb(t_date)));
					}
					
					t_prev_date = t_date;
					t_events.add(i_t_event);
				}
			}
			catch(Exception err)
			{
				err.printStackTrace();
				continue;
			}
			
		}
		
		return t_events;
	}
	
	@Override
	protected void onPostExecute(ArrayList<WLEvent> p_list)
	{
		System.out.println("Pushing out " + p_list.size() + " events to UI.");
		
		Intent t_intent_update = new Intent(BROADCAST.UPDATE_EVENTS);
		t_intent_update.putExtra("EVENTS", p_list.toArray(new WLEvent[p_list.size()]));
		LocalBroadcastManager.getInstance(m_context).sendBroadcast(t_intent_update);
	}
	
	private String getEventStyle(Node p_td)
	{
		// Loop through all attributes of the table definition //
		NamedNodeMap t_map = p_td.getAttributes();
		for(int i = 0; i < t_map.getLength(); i++)
		{
			String t_attr_name = t_map.item(i).getNodeName(),
					t_attr_val = t_map.item(i).getNodeValue();
			
			if(t_attr_name.equals("style"))
				return t_attr_val;
		}
		
		return "background: ";
	}
	
	private WLEvent getEventInfo(Node p_td)
	{
		// Info about the event //
		WLEvent t_event = new WLEvent();
		
		// Loop through all div tags in the table definition //
		NodeList t_div_list = ((Element)p_td).getElementsByTagName("div");
		for(int i = 0; i < t_div_list.getLength(); i++)
		{
			// The div tag //
			Node t_div = t_div_list.item(i);
			
			// Loop through all attributes of the div //
			NamedNodeMap t_map = t_div.getAttributes();
			for(int ii = 0; ii < t_map.getLength(); ii++)
			{
				String t_attr_name = t_map.item(ii).getNodeName(),
						t_attr_val = t_map.item(ii).getNodeValue();
				
				// The details! //
				if(t_attr_name.equalsIgnoreCase("class") && t_attr_val.equalsIgnoreCase("ui-eventlistview-detail"))
				{
					// All span tags in the detail list //
					NodeList t_span_list = ((Element)p_td).getElementsByTagName("span");
					for(int iii = 0; iii < t_span_list.getLength(); iii++)
					{
						Node t_span = t_span_list.item(iii);

						if(iii == 0)
							t_event.location = t_span_list.item(iii).getTextContent().trim().replace("Location: ", "");
						else if(iii == 1)
						{
							NodeList t_info_div_list = ((Element)t_span).getElementsByTagName("div");
							if(t_info_div_list.getLength() > 1)
							{
								t_event.info = "";

								for(int iiii = 0; iiii < t_info_div_list.getLength(); iiii++)
								{
									t_event.info += t_info_div_list.item(iiii).getTextContent().trim() + System.getProperty("line.separator");
									System.out.println("NEW LINE AHAHAHAHA");
								}
							}
							else
								t_event.info = t_span_list.item(iii).getTextContent().trim().replace("<br>", System.getProperty("line.separator"));
						}
					}
				}
			}
		}
		
		// Loop through all anchor tags in the table definition //
		NodeList t_a_list = ((Element)p_td).getElementsByTagName("a");
		for(int i = 0; i < t_a_list.getLength(); i++)
		{
			boolean t_extract = false;
			
			// The anchor tag //
			Node t_a = t_a_list.item(i);
			
			// Loop through all attributes of the anchor //
			NamedNodeMap t_map = t_a.getAttributes();
			for(int ii = 0; ii < t_map.getLength(); ii++)
			{
				String t_attr_name = t_map.item(ii).getNodeName(),
						t_attr_val = t_map.item(ii).getNodeValue();
				
				// The title! //
				if(t_attr_name.equalsIgnoreCase("class") && t_attr_val.equalsIgnoreCase("eventlist-item"))
					t_extract = true;
				else if(t_attr_name.equalsIgnoreCase("title"))
				{
					String t_title = t_attr_val.trim();
					t_event.title = t_title;
					
					if(t_title.equals("W") || t_title.equals("L"))
					{
						return null;
					}
				}
			}
			
			// Get extra data //
			if(t_extract)
			{
				try
				{
					Matcher t_match = PATTERNS.EXTRACT_TIME.matcher(t_a.getTextContent());
					t_match.find();
					t_event.time = t_match.group(0);
				}
				catch(Exception err)
				{
					t_event.time = "";
				}
			}
		}
		return t_event;
	}
	
}
