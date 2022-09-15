package enruta.sistole_gen.clases;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class Utils {
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

    public static boolean isNetworkAvailable(Context context) {
        if(context == null)  return false;


        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {


            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    }  else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)){
                        return true;
                    }
                }
            }
            else {
                try {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        Log.i("update_statut", "Network is available : true");
                        return true;
                    }
                } catch (Exception e) {
                    Log.i("update_statut", "" + e.getMessage());
                }
            }
        }
        Log.i("update_statut","Network is available : FALSE ");
        return false;
    }
}
