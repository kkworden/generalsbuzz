package me.kworden.wlcalendar2.struct;

import android.os.Parcel;
import android.os.Parcelable;

public class WLEvent implements Parcelable
{
	public WLEventClassifier type = WLEventClassifier.MISC;
	public String title = "", location = "", time = "", info = "", date = "";
	
	public WLEvent()
	{
		super();
	}
	
	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel p_dest, int p_flags)
	{
		p_dest.writeString(title);
		p_dest.writeString(location);
		p_dest.writeString(time);
		p_dest.writeString(info);
		p_dest.writeString(date);
		p_dest.writeInt(type.ordinal());
	}
	
	public static final Parcelable.Creator<WLEvent> CREATOR = new Parcelable.Creator<WLEvent>()
	{
		public WLEvent createFromParcel(Parcel in)
		{
		    return new WLEvent(in);
		}
		
		public WLEvent[] newArray(int size)
		{
		    return new WLEvent[size];
		}
	};
	
	private WLEvent(Parcel p_in)
	{
		title = p_in.readString();
		location = p_in.readString();
		time = p_in.readString();
		info = p_in.readString();
		date = p_in.readString();
		type = WLEventClassifier.values()[p_in.readInt()];
	}
}
