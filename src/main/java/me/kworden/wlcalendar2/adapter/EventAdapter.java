package me.kworden.wlcalendar2.adapter;

import java.util.ArrayList;
import java.util.Locale;

import me.kennyworden.utils.KWDateSuite;
import me.kworden.wlcalendar2.MainActivity;
import me.kworden.wlcalendar2.R;
import me.kworden.wlcalendar2.struct.WLEvent;
import me.kworden.wlcalendar2.struct.WLEventClassifier;
import me.kworden.wlcalendar2.util.APP;
import me.kworden.wlcalendar2.util.BROADCAST;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EventAdapter extends BaseAdapter
{
	private ArrayList<WLEvent> m_events = new ArrayList<WLEvent>();
	private MainActivity m_main_activity;
	
	public EventAdapter(MainActivity p_main_activity, ArrayList<WLEvent> p_events)
	{
		super();
		m_main_activity = p_main_activity;
		m_events = p_events;
	}
	
	public ArrayList<WLEvent> getEvents()
	{
		return m_events;
	}
	
	@Override
	public int getCount()
	{
		return m_events.size();
	}

	@Override
	public Object getItem(int p_pos)
	{
		return m_events.get(p_pos);
	}

	@Override
	public long getItemId(int p_pos)
	{
		return -1;
	}
	
	@Override
	public int getViewTypeCount()
	{
		return 3;
	}

	@Override
	public int getItemViewType(int p_pos)
	{
		// Get the type of event this item is //
		WLEventClassifier t_type = ((WLEvent)this.getItem(p_pos)).type;
		if(t_type.equals(WLEventClassifier.HEADER))
			return 0;
		else if(t_type.equals(WLEventClassifier.LINEBREAK))
			return 1;
		else
			return 2;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public View getView(final int p_pos, View p_convert, final ViewGroup p_parent)
	{
		// Recycle a view, to prevent memory leaks (built-in android funtionality) //
	    View t_view = p_convert;

		// The actual event //
	    final WLEvent t_event = (WLEvent)getItem(p_pos);
	    
	    LayoutInflater t_infl;
        t_infl = LayoutInflater.from(m_main_activity);
        
	    if(t_event == null) return t_view;
	    
	    if(t_event.type != WLEventClassifier.HEADER && t_event.type != WLEventClassifier.LINEBREAK)
	    {
	        ViewHolderRow t_row;

			// ONLY inflate a new view if the ListView cannot provide one for us //
	    	if(t_view == null)
	    	{
		        t_view = t_infl.inflate(R.layout.view_event_row, null);
		        t_row = new ViewHolderRow();
	    	}
	    	else
	    		t_row = (ViewHolderRow)t_view.getTag();


			// Store the ID's of the event row //
			t_row.layout = (RelativeLayout)t_view.findViewById(R.id.event_row);
			t_row.title = (TextView)t_view.findViewById(R.id.event_title);
			t_row.time = (TextView)t_view.findViewById(R.id.event_time);
			t_row.location = (TextView)t_view.findViewById(R.id.event_location);
			t_row.menu = (ImageButton)t_view.findViewById(R.id.event_menu);

			// Set the event info //
			t_row.title.setText(t_event.title);
            t_row.time.setText(t_event.time);
            t_row.location.setText(t_event.location);
            
            t_row.layout.setOnClickListener(new View.OnClickListener()
            {
				@Override
				public void onClick(final View v)
				{
					// Tell the main context to change to show event info //
					m_main_activity.openInfoFragment(t_event.title, t_event.date, t_event.info);

					// Let the app know what the current event we're dealing with is //
					APP.currentEvent = t_event;

					// Change some text for aesthetic appeal //
					((TextView)v.findViewById(R.id.event_title)).setTypeface(null, Typeface.BOLD);
					((TextView)v.findViewById(R.id.event_title)).setTextSize(24);
					v.setBackgroundResource(R.color.white);

					ValueAnimator paddingAnim = ValueAnimator.ofInt(0);
					paddingAnim.setDuration(700);
					paddingAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
					{
						@Override
						public void onAnimationUpdate(ValueAnimator valueAnimator)
						{
							v.setPadding(Integer.parseInt(valueAnimator.getAnimatedValue().toString()), Integer.parseInt(valueAnimator.getAnimatedValue().toString()),
									Integer.parseInt(valueAnimator.getAnimatedValue().toString()), Integer.parseInt(valueAnimator.getAnimatedValue().toString()));
						}
					});

					ValueAnimator positionAnim = ValueAnimator.ofFloat(0);
					positionAnim.setDuration(700);
					positionAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
					{
						@Override
						public void onAnimationUpdate(ValueAnimator valueAnimator)
						{
							v.setY(Float.parseFloat(valueAnimator.getAnimatedValue().toString()));
						}
					});


					// Animate the padding and Y position of a view when it is clicked //
					paddingAnim.start();
					positionAnim.start();
				}
            });

			// Set the menu button functionality for each event //
            t_row.menu.setOnClickListener(new View.OnClickListener()
            {
				@Override
				public void onClick(View p_view)
				{
					// Create a new popup menu, using m_main_activity as the context and p_view for positioning //
					PopupMenu t_menu = new PopupMenu(m_main_activity, p_view);
					t_menu.getMenuInflater().inflate(R.menu.event, t_menu.getMenu());
					t_menu.setOnMenuItemClickListener(new OnMenuItemClickListener()
					{
						@Override
						public boolean onMenuItemClick(MenuItem p_item)
						{
							switch(p_item.getItemId())
							{
								case R.id.action_event_hide:
									if(!t_event.type.getPreference().equals("null"))
									{
										APP.sharedPreferences.edit().putBoolean(t_event.type.getPreference(), false).commit();
										Intent t_intent = new Intent(BROADCAST.CHECK_LOCAL_DATA);
										t_intent.putExtra("MONTHS", APP.dates);
										LocalBroadcastManager.getInstance(m_main_activity).sendBroadcast(t_intent);
										Toast.makeText(m_main_activity, "Similar events hidden.", Toast.LENGTH_SHORT).show();
									}
									break;
								// Send an intent to the default calendar app //
								case R.id.action_event_remind:
									Intent intent = new Intent(Intent.ACTION_EDIT);  
									intent.setType("vnd.android.cursor.item/event");
									intent.putExtra("title", t_event.title + (!t_event.time.equals("") ? " " + t_event.time : "" ));
									intent.putExtra("description", "W-L Event");
									intent.putExtra("allDay", true);
									
									if(!t_event.location.equals("Unspecified"))
										intent.putExtra("eventLocation", t_event.location);
									
									intent.putExtra("beginTime", KWDateSuite.get9OClockOfDay(t_event.date));
									intent.putExtra("endTime", KWDateSuite.get9OClockOfDay(t_event.date) + 3600000);
									m_main_activity.startActivity(intent);
									break;
							}
							return false;
						}
					});
					
					t_menu.show();
				}
            });

			// Draw a specific background for the row based on the type of event it is //
			switch(t_event.type)
			{
				case ASSEMBLY:
					t_row.layout.setBackgroundDrawable(m_main_activity.getResources().getDrawable(R.drawable.style_event_row_assembly));
					break;
				case BREAK:
					t_row.layout.setBackgroundDrawable(m_main_activity.getResources().getDrawable(R.drawable.style_event_row_break));
					break;
				case SPECIAL:
					t_row.layout.setBackgroundDrawable(m_main_activity.getResources().getDrawable(R.drawable.style_event_row_special));
					break;
				case SCHOOL_BOARD:
					t_row.layout.setBackgroundDrawable(m_main_activity.getResources().getDrawable(R.drawable.style_event_row_schoolboard));
					break;
				case IMPORTANT_STUDENT:
					t_row.layout.setBackgroundDrawable(m_main_activity.getResources().getDrawable(R.drawable.style_event_row_importantstudent));
					break;
				case EXAM:
					t_row.layout.setBackgroundDrawable(m_main_activity.getResources().getDrawable(R.drawable.style_event_row_exam));
					break;
				default:
					t_row.layout.setBackgroundDrawable(m_main_activity.getResources().getDrawable(R.drawable.style_event_row_misc));
					break;
			}
			
			int t_padding = (int) m_main_activity.getResources().getDimension(R.dimen.event_row_padding),
				t_padding_r = (int) m_main_activity.getResources().getDimension(R.dimen.event_row_padding_right);
			
			t_row.layout.setPadding(APP.dp(m_main_activity, t_padding), APP.dp(m_main_activity, t_padding), APP.dp(m_main_activity, t_padding_r), APP.dp(m_main_activity, t_padding));
			
			t_view.setTag(t_row);
	    }
	    else if(t_event.type == WLEventClassifier.HEADER)
	    {
	    	// Holds the views for the date header layout. //
	    	ViewHolderHeader t_header;
	        
	    	if(t_view == null)
	    	{
		        t_view = t_infl.inflate(R.layout.view_event_header, null);
		        t_header = new ViewHolderHeader();
	    	}
	    	else
	    		t_header = (ViewHolderHeader)t_view.getTag();
	    	
	    	t_header.title = (TextView)t_view.findViewById(R.id.event_header_title);
	    	t_header.title.setText(t_event.title.toUpperCase(Locale.US));
	    	
			t_view.setTag(t_header);
	    }
	    else if(t_event.type == WLEventClassifier.LINEBREAK)
	    {
	    	if(t_view == null)
		        t_view = t_infl.inflate(R.layout.view_event_linebreak, null);
	    }
	    
	    return t_view;
	}
	
	private class ViewHolderHeader
	{
	    private TextView title;
	}

	private class ViewHolderRow
	{
		private RelativeLayout layout;
		private TextView title, location, time;
		private ImageButton menu;
	}
}