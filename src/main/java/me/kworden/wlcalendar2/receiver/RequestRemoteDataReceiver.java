package me.kworden.wlcalendar2.receiver;

import me.kworden.wlcalendar2.async.RequestRemoteDataTask;
import me.kworden.wlcalendar2.struct.MonthYearParcel;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RequestRemoteDataReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context p_context, Intent p_intent)
	{
		(new RequestRemoteDataTask(p_context)).execute((MonthYearParcel[])p_intent.getParcelableArrayExtra("MONTHS"));
	}
}
