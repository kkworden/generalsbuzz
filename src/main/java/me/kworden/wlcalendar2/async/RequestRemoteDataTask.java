package me.kworden.wlcalendar2.async;

import java.io.IOException;
import java.util.ArrayList;

import me.kennyworden.utils.KWHttpSuite;
import me.kworden.wlcalendar2.struct.MonthYearParcel;
import me.kworden.wlcalendar2.util.STATUS;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class RequestRemoteDataTask extends AsyncTask<MonthYearParcel, Void, STATUS>
{
	private Context m_context;
	
	public RequestRemoteDataTask(Context p_context)
	{
		m_context = p_context;
	}
	
	@Override
	protected STATUS doInBackground(MonthYearParcel ... p_args)
	{
		ArrayList<String>t_data = new ArrayList<String>();
		
		System.out.println("Requesting remote data...");
		
		for(MonthYearParcel i_time : p_args)
		{
			try
			{
				String i_t_result = KWHttpSuite.requestGet("http://apsva.us//site/UserControls/Calendar/EventListViewWrapper.aspx?ModuleInstanceID=7850&Month="
					+ i_time.month + "+&Year=" + i_time.year);
				
				i_t_result = i_t_result.replaceAll("(<script){1}(.*)(</script>){1}", "").replace("  ", "");
				
				System.out.println("i_t_result = " + i_t_result);
				
				new StoreDataTask(m_context).execute(i_time.getAsFileName(), i_t_result);
				t_data.add(i_t_result);
			}
			catch(IOException err)
			{
				err.printStackTrace();
				return STATUS.REMOTE_REQUEST_FAILED;
			}
		}
		
		new ParseDataTask(m_context).execute(t_data.toArray(new String[t_data.size()]));
		
		return STATUS.REMOTE_REQUEST_OK;
	}
	
	@Override
	public void onPostExecute(STATUS p_status)
	{
		if(p_status.equals(STATUS.REMOTE_REQUEST_FAILED))
			Toast.makeText(m_context, "Unable to contact apsva.us", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(m_context, "Retrieving data from apsva.us", Toast.LENGTH_SHORT).show();
	}
}
