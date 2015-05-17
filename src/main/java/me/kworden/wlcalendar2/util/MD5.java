package me.kworden.wlcalendar2.util;

import java.security.MessageDigest;

public class MD5
{
	public static String getMessageDigest(String p_data)
	{
		try
		{
			StringBuffer hexString = new StringBuffer();
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] hash = md.digest(p_data.getBytes());
	
	        for(int i = 0; i < hash.length; i++)
	        {
	            if((0xff & hash[i]) < 0x10)
	            {
	                hexString.append("0"
	                        + Integer.toHexString((0xFF & hash[i])));
	            }
	            else 
	                hexString.append(Integer.toHexString(0xFF & hash[i]));
	        }
	        
	        return hexString.toString();
		}
		catch(Exception err)
		{
			return "";
		}
    }
	
	public static boolean areSame(String p_data1, String p_data2)
	{
		return getMessageDigest(p_data1).equals(getMessageDigest(p_data2));
	}
}
