package mx.onlinesellers.developermotoappbeta13;

import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class PrevioMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    int track_id;
    ManagerUser database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previo_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            track_id = (int) extras.getLong("ID_TRACK");
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        database = new ManagerUser(this);
        Cursor points = database.getAllPointTrack(track_id);
        double[] lat_add = new double[points.getCount()];
        double[] lon_add = new double[points.getCount()];
        Log.d("LOGMA", ""+track_id);
        Log.d("LOGMA", ""+points.getCount());
        if (points != null) {
            int point_count = 0;
            while(points.moveToNext()) {
                lat_add[point_count] = points.getDouble(0);
                lon_add[point_count] = points.getDouble(1);
                point_count++;
            }
            points.close();
        }
        if(points.getCount()>0){
            LatLng locatioMove = new LatLng(lat_add[0], lon_add[0]);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(locatioMove));
            Log.d("LOGMA", "" + lat_add.toString());
            Log.d("LOGMA", "" + lat_add[0]);
            showRoute(lat_add, lon_add);
        }else{
            Log.d("LOGMA", "Sin puntos");
        }
    }

    public void showRoute(double[] lat_add, double[] lon_add){
        int finalPoint = lat_add.length - 1;
        double last_lat_point = 0.0;
        double last_lon_point = 0.0;
        List<LatLng> points = new ArrayList<LatLng>();
        for(int i = 0; i<lat_add.length; i++){
            Log.d("LOGMA", "Lat:" + lat_add[i]);
            Log.d("LOGMA", "Lon:" + lon_add[i]);
            points.add(new LatLng(lat_add[i], lon_add[i]));
            if(i == 0){
                last_lat_point = lat_add[i];
                last_lon_point = lon_add[i];
                LatLng inicioRecorrido = new LatLng(last_lat_point, last_lon_point);
                mMap.addMarker(new MarkerOptions()
                        .title("Inicio")
                        .snippet("Principio del recorrido")
                        .position(inicioRecorrido));
            }else{
                Polyline poli = mMap.addPolyline(new PolylineOptions().geodesic(true)
                        .add(new LatLng(last_lat_point, last_lon_point))
                        .add(new LatLng(lat_add[i], lon_add[i])));
                last_lat_point = lat_add[i];
                last_lon_point = lon_add[i];
            }
            if(i == finalPoint){
                LatLng inicioRecorrido = new LatLng(last_lat_point, last_lon_point);
                mMap.addMarker(new MarkerOptions()
                        .title("Final")
                        .snippet("Termina el recorrido")
                        .position(inicioRecorrido));
            }

        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng point : points) {
            builder.include(point);
        }
        LatLngBounds bounds = builder.build();
        int padding = 0; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 150, 150, padding);
        mMap.moveCamera(cu);

    }
}
