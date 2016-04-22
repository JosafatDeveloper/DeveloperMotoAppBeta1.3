package mx.onlinesellers.developermotoappbeta13;

import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

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
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

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
                lon_add[point_count] = points.getDouble(0);
                point_count++;
            }
            points.close();
        }
        Log.d("LOGMA", ""+lat_add.toString());
        Log.d("LOGMA", ""+lat_add[0]);


    }
}
