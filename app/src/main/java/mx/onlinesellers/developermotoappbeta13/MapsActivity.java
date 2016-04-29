package mx.onlinesellers.developermotoappbeta13;

import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.app.Activity;
import android.view.Menu;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.math.BigDecimal;
import java.util.Timer;
import java.util.TimerTask;

import mx.onlinesellers.developermotoappbeta13.Servicios.MAFUNCIONES;
import mx.onlinesellers.developermotoappbeta13.Servicios.MAGPS;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    public int distancia = 0; // Distancia en Kilometros
    public Timer timer_clock; // Timer de la duración total
    public double duracion = 0;
    public int secuencia_saveLine = 0;
    public int secuencia_saveServer = 0;
    public int limit_saveline = 2;
    public int limit_saveServer = 10;
    public int distancia_limit_track = 5;
    public Location[] locations;
    public LatLng locationLast;
    public boolean pause_timer; // Timer pause
    public String new_route;
    // Btn activity
    Button btn_activity;
    Button btn_stop;
    TextView text_distancia;
    TextView text_timer;
    TextView text_velocidad;
    TextView text_log;
    // Services
    MAFUNCIONES MAFunciones;
    MAGPS MAGPSManager;

    public int track_id;
    public int track_config_id;
    public int route_id;
    public int cloud_select;
    public int pausea_select;
    public int public_type;

    // Iniciality Parametros
    public float velocidad_maxima = 0;
    public int status_Track = 0;
    public String name_Track;
    public double count_promedio_Track;
    public double plus_promedio_Track;
    public double promedio_vel_Track;

    private ManagerUser dataSource;

    public int id_last_location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        dataSource = new ManagerUser(this);

        // New vars
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            new_route = extras.getString("NAME_TRACK");
            track_id = extras.getInt("TRACK_ID");
            track_config_id = extras.getInt("TRACK_CONFIG_ID");
            route_id = extras.getInt("ROUTE_ID");
            cloud_select = extras.getInt("CLOUD_SELECT");
            pausea_select = extras.getInt("PAUSEA_SELECT");
            public_type = extras.getInt("PUBLIC_TYPE");
        }

        // Load track BD
        Cursor trackIDInfo = dataSource.getTrackInfo(track_id);
        if (trackIDInfo != null) {
            while(trackIDInfo.moveToNext()) {
                velocidad_maxima = trackIDInfo.getFloat(trackIDInfo.getColumnIndex(ManagerUser.ColumnRoutesTrack.MAX_VELOCITY));
                status_Track = trackIDInfo.getInt(trackIDInfo.getColumnIndex(ManagerUser.ColumnRoutesTrack.STATUS_ROUTE_TRACK));
                name_Track = trackIDInfo.getString(trackIDInfo.getColumnIndex(ManagerUser.ColumnRoutesTrack.STATUS_ROUTE_TRACK));
                duracion = (double) trackIDInfo.getInt(trackIDInfo.getColumnIndex(ManagerUser.ColumnRoutesTrack.TIME_TOTAL));
                promedio_vel_Track = (double) trackIDInfo.getFloat(trackIDInfo.getColumnIndex(ManagerUser.ColumnRoutesTrack.PROMEDIO_VELOCITY));
                distancia = trackIDInfo.getInt(trackIDInfo.getColumnIndex(ManagerUser.ColumnRoutesTrack.DISTANCIA_TOTAL));
                if(promedio_vel_Track == 0){
                    count_promedio_Track = 0;
                    plus_promedio_Track = 0;
                }else{
                    count_promedio_Track = 1;
                    plus_promedio_Track = promedio_vel_Track;
                }
            }
            trackIDInfo.close();
        }else{
            finish();
        }


        // Configuracion
        btn_activity = (Button) findViewById(R.id.map_btn_activity);
        btn_activity.setOnClickListener(this);
        btn_activity.setTag(1);
        btn_stop = (Button) findViewById(R.id.map_btn_stop);
        btn_stop.setOnClickListener(this);
        text_distancia = (TextView) findViewById(R.id.map_text_distancia);
        text_timer = (TextView) findViewById(R.id.map_text_timer);
        text_log = (TextView) findViewById(R.id.log);
        text_velocidad = (TextView) findViewById(R.id.map_text_velocidad);
        timer_clock = new Timer();
        // Load Servicios
        MAFunciones = new MAFUNCIONES(getApplicationContext());
        MAGPSManager = new MAGPS(getApplicationContext());
        text_log.setText(new_route);

    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        MAGPSManager.startLocation();
        LatLng locate_init = new LatLng(MAGPSManager.latitud, MAGPSManager.longitud);
        locationLast = new LatLng(MAGPSManager.latitud, MAGPSManager.longitud);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locate_init, 18));
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

    }

    // onClick Event
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.map_btn_activity){
            if((int)btn_activity.getTag() == 1){
                final int sdk = android.os.Build.VERSION.SDK_INT;
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    btn_activity.setBackgroundDrawable( getResources().getDrawable(R.drawable.map_icon_pause) );
                } else {
                    btn_activity.setBackground( getResources().getDrawable(R.drawable.map_icon_pause));
                }
                btn_activity.setTag(2);
                pause_timer = false;
                locationLast = null;
                locationLast = new LatLng(MAGPSManager.latitud, MAGPSManager.longitud);
                runTimer();
                if(status_Track == 0){
                    statusTrackSave(1);
                }
            }else{
                final int sdk = android.os.Build.VERSION.SDK_INT;
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    btn_activity.setBackgroundDrawable( getResources().getDrawable(R.drawable.map_icon_playcircle) );
                } else {
                    btn_activity.setBackground( getResources().getDrawable(R.drawable.map_icon_playcircle));
                }
                btn_activity.setTag(1);
                pause_timer = true;
                stopTimer();
            }
        }else if(v.getId() == R.id.map_btn_stop){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage("¿Seguro que quieres terminar el viaje?");
            builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", then he is allowed to exit from application
                    //finish();
                    statusTrackSave(2);
                    pause_timer = true;
                    stopTimer();
                    Intent intent = new Intent(MapsActivity.this, PrevioMapActivity.class);
                    intent.putExtra("ID_TRACK", track_id);
                    MapsActivity.this.startActivity(intent);
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user select "No", just cancel this dialog and continue with app
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    // render Activity
    public void setDisplayTimer(){
        MapsActivity.this.text_timer.setText(MAFunciones.stringTimer(duracion));
    }

    public void  setDisplayDistance(){
        String string_distancia = MAFUNCIONES.printDistancia(distancia);
        MapsActivity.this.text_distancia.setText(string_distancia);
    }


    // timer Action duracion
    public void runTimer(){
        if(MapsActivity.this.timer_clock == null){
            MapsActivity.this.timer_clock = new Timer();
        }
        MapsActivity.this.timer_clock.schedule(new TimerTask() {
            public void run() {
                if(!pause_timer){
                    duracion += 1;
                    runTimer();
                    runOnUiThread(new Runnable(){
                        public void run() {
                            setDisplayTimer();
                            if(status_Track == 0){
                                statusTrackSave(1);
                            }
                            if(limit_saveline == secuencia_saveLine){
                                secuencia_saveLine = 0;
                                saveLineMaps();
                                saveLineBD();
                                timerAnddistanciaTrackSave(duracion, distancia, promedio_vel_Track);
                            }else{
                                secuencia_saveLine += 1;
                            }
                            text_velocidad.setText(""+MAGPSManager.calcularVelocidad());
                            velocidadTrackSave(MAGPSManager.addspeed);
                            calulePromedioVelocidad(MAGPSManager.addspeed);
                            moveCamaraMaps();
                            if(limit_saveServer == secuencia_saveServer){

                            }else{

                            }
                        }
                    });
                }else{
                    stopTimer();
                }
            }
        }, 1000);
    }
    public void stopTimer(){
        MapsActivity.this.timer_clock.cancel();
        MapsActivity.this.timer_clock = null;
    }

    public void statusTrackSave(int status){
        dataSource.saveElementTrack(track_id, ManagerUser.ColumnRoutesTrack.STATUS_ROUTE_TRACK, status, true);
        if(status == 1){
            dataSource.saveElementTrack(track_id, ManagerUser.ColumnRoutesTrack.START_TRACK, dataSource.stringDateNow(), true);
        }else if(status == 2){
            dataSource.saveElementTrack(track_id, ManagerUser.ColumnRoutesTrack.STOP_TRACK, dataSource.stringDateNow(), true);
        }
        status_Track = status;
    }
    public void velocidadTrackSave(float velmax){
        if(velmax > velocidad_maxima){
            velocidad_maxima = velmax;
            dataSource.saveElementTrack(track_id, ManagerUser.ColumnRoutesTrack.MAX_VELOCITY, velmax, true);
        }
    }

    public void timerAnddistanciaTrackSave(double timerDuracion, int distanciaTotal, double promdeioVel){
        dataSource.saveElementTrack(track_id, ManagerUser.ColumnRoutesTrack.DISTANCIA_TOTAL, distanciaTotal, false);
        dataSource.saveElementTrack(track_id, ManagerUser.ColumnRoutesTrack.TIME_TOTAL, timerDuracion, false);
        dataSource.saveElementTrack(track_id, ManagerUser.ColumnRoutesTrack.PROMEDIO_VELOCITY, promdeioVel, false);
    }

    public void calulePromedioVelocidad(float velocidad){
        plus_promedio_Track += velocidad;
        count_promedio_Track++;
        promedio_vel_Track = plus_promedio_Track/count_promedio_Track;
    }

    public void saveLineMaps(){
        float dis = MAGPSManager.calcularDistancia(locationLast.latitude, locationLast.longitude, MAGPSManager.latitud, MAGPSManager.longitud);
        Log.d("locate", "Distancia:"+dis);
        text_log.setText("Distancia:"+dis+" Timer:"+duracion);
        boolean ifDistancia = checkLimitMove(dis);
        if(ifDistancia){
            mMap.addPolyline(new PolylineOptions().geodesic(true)
                    .add(new LatLng(locationLast.latitude, locationLast.longitude))
                    .add(new LatLng(MAGPSManager.latitud, MAGPSManager.longitud)));
            locationLast = null;
            locationLast = new LatLng(MAGPSManager.latitud, MAGPSManager.longitud);
            distancia += dis;
            setDisplayDistance();
        }
    }

    public void saveLineBD(){
        id_last_location = this.dataSource.addNewPoint(track_id, MAGPSManager.useLocation, duracion);
    }

    public void  moveCamaraMaps(){
        LatLng locatioMove = new LatLng(MAGPSManager.latitud, MAGPSManager.longitud);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(locatioMove));
    }

    public boolean checkLimitMove(float dis){
        if(dis > distancia_limit_track){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("¿Seguro que quieres cancelar el viaje?");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pause_timer = true;
                stopTimer();
                finish();
                Intent startInicio = new Intent(MapsActivity.this, InicioActivity.class);
                startActivity(startInicio);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
