package me.kworden.wlcalendar2.async;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import me.kworden.wlcalendar2.util.MD5;

import android.content.Context;
import android.os.AsyncTask;

public class StoreDataTask extends AsyncTask<String, Void, Boolean>
{
	private Context m_context;
	
	public StoreDataTask(Context p_context)
	{
		m_context = p_context;
	}
	
	/**
	 * p_args[0] = filename
	 * p_args[1] = data
	 * @return 
	 */
	@Override
	protected Boolean doInBackground(String ... p_args)
	{
		try
		{
			boolean t_update = false;
			
			if(localFileExists(p_args[0]))
			{
				// Is the current local data equal to the data we're passing in? //
				if(!MD5.areSame(loadFile(p_args[0]), p_args[1]))
					t_update = true;
			}
			else
				t_update = true;
			
			if(t_update)
			{
				FileOutputStream t_fos = m_context.openFileOutput(p_args[0], Context.MODE_PRIVATE);
				t_fos.write(p_args[1].getBytes());
				t_fos.flush();
				t_fos.close();
			}
			
			return t_update;
		}
		catch(Exception err)
		{
			err.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	protected void onPostExecute(Boolean p_update)
	{
		super.onPostExecute(p_update);
		
		if(p_update)
			System.out.println("Successfully wrote to file.");
		else
			System.out.println("Did not write out to file. Hashes match.");
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
	            t_buffer.append(i_read + "\n" );
	    }       
		t_fis.close();
		
		return t_buffer.toString();
	}
	
	private boolean localFileExists(String p_file_name)
	{
		return m_context.getFileStreamPath(p_file_name).exists();
	}
}
