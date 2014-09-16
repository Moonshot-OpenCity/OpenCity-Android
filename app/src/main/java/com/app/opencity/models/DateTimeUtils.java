package com.app.opencity.models;

/**
 * Created by DAVID Flavien on 15/09/2014.
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils {

    public String calcDiff(String from) {
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {

            Date date1 = simpleDateFormat.parse(from);
            Date date2 = simpleDateFormat.parse(simpleDateFormat.format(new Date()));

            return printDifference(date1, date2);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    //1 minute = 60 seconds
    //1 hour = 60 x 60 = 3600
    //1 day = 3600 x 24 = 86400
    public String printDifference(Date startDate, Date endDate){

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        if (elapsedDays > 0) {
            if (elapsedDays == 1) {
                return elapsedDays + " jour";
            } else {
                return elapsedDays + " jours";
            }
        } else if (elapsedHours > 0) {
            if (elapsedHours == 1) {
                return elapsedHours + " heure";
            } else {
                return elapsedHours + " heures";
            }
        } else {
            if (elapsedMinutes > 1) {
                return elapsedHours + " minutes";
            } else {
                return "1 minute";
            }
        }
    }

}