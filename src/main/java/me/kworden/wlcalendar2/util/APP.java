package me.kworden.wlcalendar2.util;

import me.kennyworden.utils.KWDateSuite;
import me.kworden.wlcalendar2.struct.MonthYearParcel;
import me.kworden.wlcalendar2.struct.WLEvent;

import android.content.Context;
import android.content.SharedPreferences;

public class APP
{
	public static WLEvent currentEvent = null;
	public static boolean repull = false;
	public static int scrollPos[] = {0, 0};
	public static SharedPreferences sharedPreferences = null;
	//public static MonthYearParcel[] dates = new MonthYearParcel[]{ new MonthYearParcel(KWDateSuite.getThisMonth(), KWDateSuite.getThisYear()) };
	
	public static int dp(Context p_context, float px)
	{
	    return (int)(px / p_context.getResources().getDisplayMetrics().density);
	}
	
	public static MonthYearParcel[] getDatesToLoad()
	{
		int t_months_ahead = Integer.parseInt(APP.sharedPreferences.getString("pref_months", "2"));
		MonthYearParcel t_dates[] = new MonthYearParcel[t_months_ahead];

		String i_t_month = KWDateSuite.getThisMonth(),
				i_t_year = KWDateSuite.getThisYear();
		
		for(int i = 0; i < t_months_ahead; i++)
		{
			String i_t_date = KWDateSuite.stripDay(KWDateSuite.getMonthsInFuture(i));
			i_t_month = i_t_date.split("/")[0];
			i_t_year = i_t_date.split("/")[1];

			t_dates[i] = new MonthYearParcel(i_t_month, i_t_year);
		}
		
		return t_dates;
	}
}
