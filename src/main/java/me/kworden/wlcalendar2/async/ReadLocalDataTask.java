package me.kworden.wlcalendar2.async;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import me.kworden.wlcalendar2.struct.MonthYearParcel;

import android.content.Context;
import android.os.AsyncTask;

public class ReadLocalDataTask extends AsyncTask<MonthYearParcel, Void, ArrayList<String>>
{
	private Context m_context;
	
	public ReadLocalDataTask(Context p_context)
	{
		m_context = p_context;
	}
	
	@Override
	protected ArrayList<String> doInBackground(MonthYearParcel ... p_args)
	{
		ArrayList<String> t_data = new ArrayList<String>();
		
		System.out.println("Reading local data...");
		
		for(MonthYearParcel i_time : p_args)
		{
			try
			{
				t_data.add(loadFile(i_time.getAsFileName()));
			}
			catch(Exception err)
			{
				err.printStackTrace();
			}
		}
		
		return t_data;
	}
	
	@Override
	public void onPostExecute(ArrayList<String> p_data)
	{
		new ParseDataTask(m_context).execute(p_data.toArray(new String[p_data.size()]));
	}
	
	private String loadFile(String p_file_name) throws IOException
	{
		String i_read;
	    StringBuffer t_buffer = new StringBuffer();
		FileInputStream t_fis = m_context.openFileInput(p_file_name);
		BufferedReader t_reader = new BufferedReader(new InputStreamReader(t_fis));
		
	    if(t_fis != null)
	    {                            
	        while((i_read = t_reader.readLine()) != null)
	        {    
	            t_buffer.append(i_read + "\n" );
	        }               
	    }       
		t_fis.close();
		
		return t_buffer.toString();
	}
}
