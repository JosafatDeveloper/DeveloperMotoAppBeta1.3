package mx.onlinesellers.developermotoappbeta13.Servicios;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by dis2 on 13/04/16.
 */
public class MAFUNCIONES extends Service {
    private Context ctx;

    public MAFUNCIONES(){
        super();
        this.ctx = this.getApplicationContext();
    }

    public MAFUNCIONES(Context c) {
        super();
        this.ctx = c;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static int calcularDistancia(int lat_a,int lng_a, int lat_b, int lon_b){
        float Radius = 6371000; //Radio de la tierra
        double lat1 = lat_a / 1E6;
        double lat2 = lat_b / 1E6;
        double lon1 = lng_a / 1E6;
        double lon2 = lon_b / 1E6;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon /2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return (int) (Radius * c);
    }

    public static String printDistancia(int distance){
        if(distance >= 1000){
            int k = distance / 1000;
            int m = distance - (k*1000);
            m = m / 100;
            return String.valueOf(k) + (m>0?("."+String.valueOf(m)):"") + " Km" +(k>1?"s":"");
        } else {
            return String.valueOf(distance) + " metro"+(distance==1?"":"s");
        }
    }

    public static int[] splitToComponentTimes(BigDecimal biggy){
        long longVal = biggy.longValue();
        int hours = (int) longVal / 3600;
        int remainder = (int) longVal - hours * 3600;
        int mins = remainder / 60;
        remainder = remainder - mins * 60;
        int secs = remainder;

        int[] ints = {hours , mins , secs};
        return ints;
    }

    public boolean stringBoolVerificadorLimit(String string, int limitLength){
        if(string.isEmpty() || string == null){
            return false;
        }else if(string.length() < limitLength){
            return false;
        }else{
            return true;
        }
    }

    public static String formatDateTime(Context context, String timeToFormat) {

        String finalDateTime = "";

        SimpleDateFormat iso8601Format = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");

        Date date = null;
        if (timeToFormat != null) {
            try {
                date = iso8601Format.parse(timeToFormat);
            } catch (ParseException e) {
                date = null;
            }

            if (date != null) {
                long when = date.getTime();
                int flags = 0;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_TIME;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_DATE;
                flags |= android.text.format.DateUtils.FORMAT_ABBREV_MONTH;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_YEAR;

                finalDateTime = android.text.format.DateUtils.formatDateTime(context,
                        when + TimeZone.getDefault().getOffset(when), flags);
            }
        }
        return finalDateTime;
    }
}
