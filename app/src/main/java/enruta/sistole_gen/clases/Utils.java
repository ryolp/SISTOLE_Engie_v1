package enruta.sistole_gen.clases;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {
    public static Date getDateTime() {
        Calendar calendar = Calendar.getInstance();

        return calendar.getTime();
    }

    public static void showMessageLong(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void showMessageShort(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void logMessageLong(Context context, String msg, Throwable t) {
        msg = msg + " : " + t.getMessage();
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        Log.d("CPL", msg);
    }

    public static void logMessageShort(Context context, String msg, Throwable t) {
        msg = msg + " : " + t.getMessage();
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        Log.d("CPL", msg);
    }

    public static String convToDateTimeStr(Date d, String... format){
        String fmt;
        String result;

        if (d == null)
            return "";

        if (format.length == 0)
            fmt = "yyyy-MM-dd";
        else
            fmt = format[0];

        SimpleDateFormat dateFormat = new SimpleDateFormat(fmt);

        result = dateFormat.format(d);

        return result;
    }

    public static Date getFechaAgregarSegundos(int segundos){
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.SECOND, segundos);
        return calendar.getTime();
    }

    public static int convToInt(String value, Integer...defaultValue) {
        int defaultValueAux = 0;

        try {
            if (defaultValue.length>0)
                defaultValueAux = defaultValue[0];

            return Integer.parseInt(value.trim());
        } catch (Exception e)
        {
            return defaultValueAux;
        }
    }
}
