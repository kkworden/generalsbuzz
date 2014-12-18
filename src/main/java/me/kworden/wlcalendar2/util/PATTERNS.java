package me.kworden.wlcalendar2.util;

import java.util.regex.Pattern;

public class PATTERNS
{
	public static final Pattern EXTRACT_TIME = Pattern.compile("([0-9]{1,}):([0-9]{2})(\\s*)(AM|PM)((\\s*)-(\\s*)([0-9]{1,}):([0-9]{2})(\\s*)(AM|PM))*"),
			EXTRACT_DATE = Pattern.compile("([0-9]{1,})/([0-9]{1,})/([0-9]{4})"),
			EXTRACT_SCRIPT = Pattern.compile("(<script){1}(.*)(</script>){1}");
}
