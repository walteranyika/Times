package timesu.sacco.app.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by walter on 1/4/19.
 */

public class DateUtils {
    public static Date parseDate (String strDate) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date1 = null;
        try {
            date1 = dateFormat.parse(strDate);
        }
        catch (ParseException e) {
            e.printStackTrace ();
        }
        return date1;
    }

    public static String formatDate(Date date){
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return  dateFormat.format(date);
    }

    public static String formatTime(Date date){
        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        return  dateFormat.format(date);
    }


    public static String formatElapsedTime (long seconds) {

        long hours = TimeUnit.SECONDS.toHours(seconds);
        seconds -= TimeUnit.HOURS.toSeconds (hours);

        long minutes = TimeUnit.SECONDS.toMinutes (seconds);
        seconds -= TimeUnit.MINUTES.toSeconds (minutes);

        return String.format ("%dhr:%dmin", hours, minutes);
    }
}
