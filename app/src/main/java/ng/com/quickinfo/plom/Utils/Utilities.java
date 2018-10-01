package ng.com.quickinfo.plom.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utilities {
    /*contains basic tools*/
    public static Date stringToDate(String dateString) {
        Date date;
        try {
            date = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);

        } catch (ParseException e) {
            e.printStackTrace();
            //TODO parse in today as default date taken
            date = Calendar.getInstance().getTime();
        }
        return date;
    }

    public static String dateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = dateFormat.format(date);
        return strDate;

    }
}
