package me.kworden.wlcalendar2.async;

import java.util.ArrayList;

import me.kworden.wlcalendar2.struct.MonthYearParcel;
import me.kworden.wlcalendar2.util.APP;
import me.kworden.wlcalendar2.util.STATUS;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class CheckLocalDataTask extends AsyncTask<MonthYearParcel, Void, STATUS>
{
	private Context m_context;
	private ArrayList<MonthYearParcel> m_files;
	
	public CheckLocalDataTask(Context p_activity)
	{
		m_context = p_activity;
		m_files = new ArrayList<MonthYearParcel>();
	}

	// Asynchronously checks to see if local data exists //
	@Override
	protected STATUS doInBackground(MonthYearParcel ... p_args)
	{
		System.out.println("Checking local data...");
		
		for(MonthYearParcel i_time : p_args)
		{
			// Adds the MonthYearParcels to a instance variable to queue which months should be loaded //
			m_files.add(i_time);

			if(!localFileExists(i_time.getAsFileName()))
			{
				System.out.println(i_time.getAsFileName() + " does not exist. Returning _NOT_FOUND data code for pull.");
				return STATUS.LOCAL_DATA_NOT_FOUND;
			}

			System.out.println("Found file for: " + i_time.getAsFileName());
		}
		
		return STATUS.LOCAL_DATA_FOUND;
	}
	
	@Override
	public void onPostExecute(STATUS p_status)
	{
		if(Integer.parseInt(APP.sharedPreferences.getString("pref_months", "1")) == 0)
		{
			Toast.makeText(m_context, "W-L doesn't exist in the year 3053!", Toast.LENGTH_SHORT).show();
			return;
		}

		// Execute the desired task based on whether or not the local data file exists. m_files.toArray() is used because AsyncTasks can only take arrays //
		if(p_status.equals(STATUS.LOCAL_DATA_FOUND))
		{
			System.out.println("Local data found! Reading.");
			new ReadLocalDataTask(m_context).execute(m_files.toArray(new MonthYearParcel[m_files.size()]));
		}
		else
		{
			System.out.println("No local data found, executing remote request.");
			new RequestRemoteDataTask(m_context).execute(m_files.toArray(new MonthYearParcel[m_files.size()]));
		}
	}
	
	private boolean localFileExists(String p_file_name)
	{
		return m_context.getFileStreamPath(p_file_name).exists();
	}
}
