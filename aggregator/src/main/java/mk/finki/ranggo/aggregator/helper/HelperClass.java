package mk.finki.ranggo.aggregator.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Simona on 4/10/2016.
 */
public class HelperClass {

    public static String getToday(){
        Date date = new Date();
        DateFormat formatter = new SimpleDateFormat("YYYY-MM-dd");
        String output = formatter.format(date);
        return output;
    }
}
