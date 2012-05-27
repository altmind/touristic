package extensions;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import play.Play;
import play.i18n.Lang;
import play.libs.I18N;
import play.templates.JavaExtensions;

public class CustomExtension extends JavaExtensions {

    public static String getTimeFormat() {
        final String localizedDateFormat = Play.configuration.getProperty("time.format." + Lang.get());
        if (!StringUtils.isEmpty(localizedDateFormat)) {
            return localizedDateFormat;
        }
        final String globalDateFormat = Play.configuration.getProperty("time.format");
        if (!StringUtils.isEmpty(globalDateFormat)) {
            return globalDateFormat;
        }
        // Default value. It's completely arbitrary.
        return "HH:mm:ss";
    }
	
    public static String getDateTimeFormat() {
        final String localizedDateFormat = Play.configuration.getProperty("dateTime.format." + Lang.get());
        if (!StringUtils.isEmpty(localizedDateFormat)) {
            return localizedDateFormat;
        }
        final String globalDateFormat = Play.configuration.getProperty("dateTime.format");
        if (!StringUtils.isEmpty(globalDateFormat)) {
            return globalDateFormat;
        }
        // Default value. It's completely arbitrary.
        return "yyyy-MM-dd HH:mm:ss";
    }
    
    public static String jsEscape(String s) {
    	return s.replace(".", "\\\\.").replace("#", "\\\\#").replace(":", "\\\\:");
    }
    
    public static String formatTime(Date date) {
        // Get the pattern from the configuration
        return new SimpleDateFormat(getTimeFormat()).format(date);
    }
    public static String formatDateTime(Date date) {
        // Get the pattern from the configuration
        return new SimpleDateFormat(getDateTimeFormat()).format(date);
    }
    
}
