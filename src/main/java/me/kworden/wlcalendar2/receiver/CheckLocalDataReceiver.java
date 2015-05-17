package me.kworden.wlcalendar2.receiver;

import me.kworden.wlcalendar2.async.CheckLocalDataTask;
import me.kworden.wlcalendar2.struct.MonthYearParcel;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CheckLocalDataReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context p_context, Intent p_intent)
	{
		(new CheckLocalDataTask(p_context)).execute((MonthYearParcel[])p_intent.getParcelableArrayExtra("MONTHS"));
	}
}
