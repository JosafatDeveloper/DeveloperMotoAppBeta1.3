package mx.onlinesellers.developermotoappbeta13;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import mx.onlinesellers.developermotoappbeta13.Servicios.MAFUNCIONES;

public class PrevioMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    int track_id;
    ManagerUser database;
    boolean goBack = false;
    MAFUNCIONES MAFunciones;

    float velocity_max;
    float velocity_low = 0;
    float rgblimit = 255;

    // TextView
    TextView textview_velmax;
    TextView textview_velpro;
    TextView textview_status;
    TextView textview_start;
    TextView textview_finished;
    TextView textview_distancia;
    TextView textview_timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previo_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            track_id = extras.getInt("ID_TRACK");
            if(extras.getBoolean("goBack")){
                goBack = true;
            }else{
                goBack = false;
            }
        }
        MAFunciones = new MAFUNCIONES(getApplicationContext());
        textview_distancia = (TextView) findViewById(R.id.prev_text_dis);
        textview_timer = (TextView) findViewById(R.id.prev_text_timer);
        textview_start = (TextView) findViewById(R.id.prev_text_start);
        textview_finished = (TextView) findViewById(R.id.prev_text_finished);
        textview_velmax = (TextView) findViewById(R.id.prev_text_velmax);
        textview_velpro = (TextView) findViewById(R.id.prev_text_velpro);
        textview_status = (TextView) findViewById(R.id.prev_text_status);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        database = new ManagerUser(this);
        Cursor track = database.getTrackInfo(track_id);
        if(track != null){
            while (track.moveToNext()){
                textview_start.setText(""+track.getString(track.getColumnIndex(ManagerUser.ColumnRoutesTrack.START_TRACK)));
                textview_finished.setText(""+track.getString(track.getColumnIndex(ManagerUser.ColumnRoutesTrack.STOP_TRACK)));
                textview_timer.setText(""+MAFunciones.stringTimer(track.getDouble(track.getColumnIndex(ManagerUser.ColumnRoutesTrack.TIME_TOTAL))));
                textview_distancia.setText(""+MAFunciones.printDistancia(track.getInt(track.getColumnIndex(ManagerUser.ColumnRoutesTrack.DISTANCIA_TOTAL))));
                velocity_max = track.getFloat(track.getColumnIndex(ManagerUser.ColumnRoutesTrack.MAX_VELOCITY));
                textview_velmax.setText(""+MAFunciones.calcularVelocidad(velocity_max, true));
                textview_velpro.setText(""+MAFunciones.calcularVelocidad(track.getFloat(track.getColumnIndex(ManagerUser.ColumnRoutesTrack.PROMEDIO_VELOCITY)), true));
                int status = track.getInt(track.getColumnIndex(ManagerUser.ColumnRoutesTrack.STATUS_ROUTE_TRACK));
                switch (status){
                    case 0:{
                        textview_status.setText("NO INICIADO");
                        textview_status.setTextColor(0xFFFF8800);
                    }
                    break;
                    case 1:{
                        textview_status.setText("EN PAUSA");
                        textview_status.setTextColor(0xFF0099CC);
                    }
                    break;
                    case 2:{
                        textview_status.setText("FINALIZADO");
                        textview_status.setTextColor(0xFF669900);
                    }
                    break;
                    default:{
                        textview_status.setText("DESCONOCIDO");
                        textview_status.setTextColor(0xFFFF4444);
                    }
                }
            }
        }


        Cursor points = database.getAllPointTrack(track_id);
        double[] lat_add = new double[points.getCount()];
        double[] lon_add = new double[points.getCount()];
        float[] vel_add = new float[points.getCount()];
        //Log.d("LOGMA", ""+track_id);
        //Log.d("LOGMA", ""+points.getCount());
        if (points != null) {
            int point_count = 0;
            while(points.moveToNext()) {
                lat_add[point_count] = points.getDouble(points.getColumnIndex(ManagerUser.ColumnRoutesTrackPoint.GPS_LATITUD));
                lon_add[point_count] = points.getDouble(points.getColumnIndex(ManagerUser.ColumnRoutesTrackPoint.GPS_LONGITUD));
                vel_add[point_count] = points.getFloat(points.getColumnIndex(ManagerUser.ColumnRoutesTrackPoint.GPS_VELOCIDAD));
                point_count++;
            }
            points.close();
        }
        if(points.getCount()>0){
            LatLng locatioMove = new LatLng(lat_add[0], lon_add[0]);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(locatioMove));
            //Log.d("LOGMA", "" + lat_add.toString());
            //Log.d("LOGMA", "" + lat_add[0]);
            showRoute(lat_add, lon_add, vel_add);
        }else{
            Log.d("LOGMA", "Sin puntos");
        }
    }

    public void showRoute(double[] lat_add, double[] lon_add, float[] velpoint){
        int finalPoint = lat_add.length - 1;
        double last_lat_point = 0.0;
        double last_lon_point = 0.0;
        List<LatLng> points = new ArrayList<LatLng>();
        int color_red = 0;
        int color_blue = 0;
        int color_green = 0;
        float resultColor = 0;
        for(int i = 0; i<lat_add.length; i++){
            //Log.d("LOGMA", "Lat:" + lat_add[i]);
            //Log.d("LOGMA", "Lon:" + lon_add[i]);
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
                resultColor = (int) (rgblimit/(velocity_max/velpoint[i]));
                if(resultColor<(rgblimit/2)){
                    // ROJO
                    color_blue = (int) (rgblimit-resultColor);
                    color_green = color_blue-67;
                    color_red = (int) (rgblimit-color_blue);
                }else{
                    // BLUE
                    color_red = (int) resultColor;
                    color_blue = 0;
                    color_green = 0;
                }
                //Log.d("LOGMA", "red:"+color_red+" green:"+color_green+" blue:"+color_blue);
                Polyline poli = mMap.addPolyline(new PolylineOptions().geodesic(true)
                        .add(new LatLng(last_lat_point, last_lon_point))
                        .add(new LatLng(lat_add[i], lon_add[i]))
                        .color(Color.rgb(color_red,color_green,color_blue))
                        .width(10)
                );
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

    @Override
    public void onBackPressed() {
        finish();
        if(goBack){
        }else{
            Intent startInicio = new Intent(this, InicioActivity.class);
            startActivity(startInicio);
        }
    }

}
