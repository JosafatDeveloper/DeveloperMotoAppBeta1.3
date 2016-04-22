package mx.onlinesellers.developermotoappbeta13.Servicios;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by dis2 on 12/04/16.
 */
public class MAGPS extends Service implements LocationListener {
    private Context ctx;

    // Public vars
    // Coordenadas
    public double latitud;
    public double longitud;
    public double latitud_last;
    public double longitud_last;
    // Servicios plus
    public double ditancia;
    public boolean activeKMH = true;
    public float addspeed;

    // Loacal vars
    // Activacion
    boolean GPSRecord = false;
    boolean GPSActive;
    // GPS
    Location location;
    LocationManager locationManager;

    public Location useLocation;

    public MAGPS(){
        super();
        this.ctx = this.getApplicationContext();
    }

    public MAGPS(Context c) {
        super();
        this.ctx = c;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public void startLocation(){
        try {
            locationManager = (LocationManager)ctx.getSystemService(LOCATION_SERVICE);
            GPSActive = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (GPSActive) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    } else {
                    }
                }
                Criteria crta = new Criteria();
                crta.setAccuracy(Criteria.ACCURACY_FINE);
                crta.setAltitudeRequired(false);
                crta.setBearingRequired(false);
                crta.setCostAllowed(true);
                crta.setPowerRequirement(Criteria.POWER_LOW);
                String provider = locationManager.getBestProvider(crta, true);
                locationManager.requestLocationUpdates(provider, 1000, 0, this);
                if(locationManager != null){
                    location = locationManager.getLastKnownLocation(provider);
                    useLocation = locationManager.getLastKnownLocation(provider);
                    latitud = location.getLatitude();
                    longitud = location.getLongitude();
                    latitud_last = latitud;
                    longitud_last = longitud;
                }
            }
        } catch (Exception e) {
            Log.d("Error", "Error en el servicio:"+e.toString());
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        useLocation = location;
        addspeed = location.getSpeed();
        if(GPSRecord){
            latitud_last = latitud;
            longitud_last = longitud;
            latitud = location.getLatitude();
            longitud = location.getLongitude();
        }else{
            latitud = location.getLatitude();
            longitud = location.getLongitude();
            latitud_last = latitud;
            longitud_last = longitud;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    public void valocidadKMH(){ activeKMH = true;}
    public void velocidadMPH(){ activeKMH = false;}
    public void recordGPS(){
        GPSRecord = true;
    }
    public void pauseRecordGPS(){
        GPSRecord = false;
    }
    public  void finalizeRecordGPS(){
        GPSRecord = false;
    }

    public float calcularDistancia(double ALatitud, double ALongitud, double BLatitud, double BLongitud){
        float[] results = new float[1];
        Location.distanceBetween(
                ALatitud, ALongitud,
                BLatitud, BLongitud, results);
        return results[0];
    }

    public int calcularVelocidad(){
        if(activeKMH){
            int speed=(int) ((addspeed*3600)/1000);
            return speed;
        }else{
            int speed=(int) (addspeed*2.2369);
            return speed;
        }

    }
}
