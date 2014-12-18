package me.kworden.wlcalendar2.struct;

import android.os.Parcel;
import android.os.Parcelable;

public class MonthYearParcel implements Parcelable
{
	public String month, year;
	
	public MonthYearParcel(String p_month, String p_year)
	{
		month = p_month;
		year = p_year;
	}
	
	public String getAsFileName()
	{
		return month + "-" + year + ".raw";
	}
	
	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel p_dest, int p_flags)
	{
		p_dest.writeString(month);
		p_dest.writeString(year);
	}
	
	public static final Parcelable.Creator<MonthYearParcel> CREATOR = new Parcelable.Creator<MonthYearParcel>()
	{
		public MonthYearParcel createFromParcel(Parcel in)
		{
		    return new MonthYearParcel(in);
		}
		
		public MonthYearParcel[] newArray(int size)
		{
		    return new MonthYearParcel[size];
		}
	};
	
	private MonthYearParcel(Parcel p_in)
	{
		month = p_in.readString();
		year = p_in.readString();
	}
}
