package ng.com.quickinfo.plom.Utils;
import android.arch.persistence.room.TypeConverter;

import java.util.Date;

public class DateConverter {
    /*class converts date for the entity manager
    room database*/

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}