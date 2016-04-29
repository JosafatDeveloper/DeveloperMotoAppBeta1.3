package mx.onlinesellers.developermotoappbeta13;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.provider.BaseColumns;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Created by dis2 on 18/04/16.
 */
public class ManagerUser {
    //Metainformación de la base de datos
    public static final String USERS_TABLE_NAME = "users";
    public static final String ROUTES_TABLE_NAME = "routes";
    public static final String ROUTE_TRACK_TABLE_NAME = "routes_track";
    public static final String ROUTE_TRACK_CONFIG_TABLE_NAME = "routes_track_config";
    public static final String ROUTE_TRACK_POINT_TABLE_NAME = "routes_track_points";
    public static final String ROUTE_TRACK_POINT_MOVE_TABLE_NAME = "point_move";
    public static final String STRING_TYPE = "text";
    public static final String INT_TYPE = "integer";
    public static final String DATETIME_TYPE = "datetime";
    public static final String DOUBLE_TYPE = "double";
    public static final String DOUBLEC_TYPE = "double(20,10)";
    public static final String FLOAT_TYPE = "float";

    //Campos de la tabla Quotes
    public static class ColumnUsers{
        public static final String ID_USER = BaseColumns._ID;
        public static final String USER_NAME = "user_name";
        public static final String USER_ALIAS = "user_alias";
    }

    public static class  ColumnRoutes{
        public static final String ID_ROUTE = BaseColumns._ID;
        public static final String ROUTE_NAME = "route_name";
        public static final String ROUTE_MODIFY = "route_modify";
    }
    public static class ColumnRoutesTrack{
        public static final String ID_ROUTE_TRACK = BaseColumns._ID;
        public static final String ID_ROUTE = "id_route";
        public static final String ROUTE_TRACK_NAME = "route_track_name";
        public static final String STATUS_ROUTE_TRACK = "status_route_track";
        public static final String MAX_VELOCITY = "velocidad_max";
        public static final String PROMEDIO_VELOCITY = "velocidad_promedio";
        public static final String TIME_TOTAL = "total_timer";
        public static final String DISTANCIA_TOTAL = "distancia_total";
        public static final String START_TRACK = "start_track";
        public static final String STOP_TRACK = "stop_track";
        public static final String SAVE_TRACK = "save_track";
        public static final String MODIFY_TRACK = "modify_track";
    }
    public static class ColumnRoutesTrackConfig{
        public static final String ID_CONFIG = BaseColumns._ID;
        public static final String ID_TRACK = "id_track";
        public static final String UPLOAD_CLOUD = "upload_cloud";
        public static final String PAUSEA_AUTO = "pausea_auto";
        public static final String START_LAT = "start_lat";
        public static final String START_LON = "start_lon";
        public static final String FINISH_LAT = "finish_lat";
        public static final String FINISH_LON = "finish_lon";
        public static final String TYPE_PUBLIC =  "type_public";
    }

    public static class ColumnOther{
        public static final String NAME_TRACK_AND_NAME_ROUTE = "other_nt_and_nr";
    }

    public static class ColumnRoutesTrackPoint{
        public static final String ID_POINT = BaseColumns._ID;
        public static final String ID_TRACK = "id_track";
        public static final String GPS_LATITUD = "gps_latitud";
        public static final String GPS_LONGITUD = "gps_longitud";
        public static final String GPS_VELOCIDAD = "gps_velocidad";
        public static final String GPS_ALTITUDE = "gps_altitude";
        public static final String TIMER_CHECK = "timer_check";
        public static final String CHECK_DATETIME = "check_datetime";
    }

    //Script de Creación de la tabla Quotes
    public static final String CREATE_USERS_SCRIPT =
            "create table "+USERS_TABLE_NAME+"(" +
                    ColumnUsers.ID_USER+" "+INT_TYPE+" primary key autoincrement," +
                    ColumnUsers.USER_NAME+" "+STRING_TYPE+" not null," +
                    ColumnUsers.USER_ALIAS+" "+STRING_TYPE+" not null)";

    public static final String CREATE_ROUTES_SCRIPT =
            "create table "+ROUTES_TABLE_NAME+"("+
                    ColumnRoutes.ID_ROUTE+" "+INT_TYPE+" primary key autoincrement,"+
                    ColumnRoutes.ROUTE_NAME+" "+STRING_TYPE+" not null,"+
                    ColumnRoutes.ROUTE_MODIFY+" "+DATETIME_TYPE+" DEFAULT CURRENT_TIMESTAMP)";


    public static final String CREATE_ROUTE_TRACK_SCRIPT =
            "create table "+ROUTE_TRACK_TABLE_NAME+"("+
                    ColumnRoutesTrack.ID_ROUTE_TRACK+" "+INT_TYPE+" primary key autoincrement,"+
                    ColumnRoutesTrack.ID_ROUTE+" "+INT_TYPE+" not null,"+
                    ColumnRoutesTrack.ROUTE_TRACK_NAME+" "+STRING_TYPE+" not null,"+
                    ColumnRoutesTrack.STATUS_ROUTE_TRACK+" "+INT_TYPE+" not null,"+
                    ColumnRoutesTrack.MAX_VELOCITY+" "+FLOAT_TYPE+" not null,"+
                    ColumnRoutesTrack.PROMEDIO_VELOCITY+" "+FLOAT_TYPE+" not null,"+
                    ColumnRoutesTrack.TIME_TOTAL+" "+INT_TYPE+" not null,"+
                    ColumnRoutesTrack.DISTANCIA_TOTAL+" "+INT_TYPE+" not null,"+
                    ColumnRoutesTrack.START_TRACK+" "+DATETIME_TYPE+" not null,"+
                    ColumnRoutesTrack.STOP_TRACK+" "+DATETIME_TYPE+" not null,"+
                    ColumnRoutesTrack.SAVE_TRACK+" "+DATETIME_TYPE+" not null,"+
                    ColumnRoutesTrack.MODIFY_TRACK+" "+DATETIME_TYPE+" not null)";

    public static final String CREATE_ROUTE_TRACK_POINT_SCRIPT =
            "create table "+ROUTE_TRACK_POINT_TABLE_NAME+"("+
                    ColumnRoutesTrackPoint.ID_POINT+" "+INT_TYPE+" primary key autoincrement,"+
                    ColumnRoutesTrackPoint.ID_TRACK+" "+INT_TYPE+" not null,"+
                    ColumnRoutesTrackPoint.GPS_LATITUD+" "+DOUBLEC_TYPE+" not null,"+
                    ColumnRoutesTrackPoint.GPS_LONGITUD+" "+DOUBLEC_TYPE+" not null,"+
                    ColumnRoutesTrackPoint.GPS_VELOCIDAD+" "+FLOAT_TYPE+" not null,"+
                    ColumnRoutesTrackPoint.GPS_ALTITUDE+" "+DOUBLEC_TYPE+" not null,"+
                    ColumnRoutesTrackPoint.TIMER_CHECK+" "+INT_TYPE+" not null,"+
                    ColumnRoutesTrackPoint.CHECK_DATETIME+" "+DATETIME_TYPE+" not null)";

    public static final String CREATE_ROUTE_TRACK_CONFIG_SCRIPT =
            "create table "+ROUTE_TRACK_CONFIG_TABLE_NAME+"("+
                    ColumnRoutesTrackConfig.ID_CONFIG+" "+INT_TYPE+" primary key autoincrement,"+
                    ColumnRoutesTrackConfig.ID_TRACK+" "+INT_TYPE+" not null,"+
                    ColumnRoutesTrackConfig.UPLOAD_CLOUD+" "+INT_TYPE+" not null,"+
                    ColumnRoutesTrackConfig.PAUSEA_AUTO+" "+INT_TYPE+" not null,"+
                    ColumnRoutesTrackConfig.START_LAT+" "+DOUBLE_TYPE+" not null,"+
                    ColumnRoutesTrackConfig.START_LON+" "+DOUBLE_TYPE+" not null,"+
                    ColumnRoutesTrackConfig.FINISH_LAT+" "+DOUBLE_TYPE+" not null,"+
                    ColumnRoutesTrackConfig.FINISH_LON+" "+DOUBLE_TYPE+" not null,"+
                    ColumnRoutesTrackConfig.TYPE_PUBLIC+" "+INT_TYPE+" not null)";

    //Scripts de inserción por defecto
    public static final String INSERT_QUOTES_SCRIPT =
            "insert into "+USERS_TABLE_NAME+" values(" +
                    "null," +
                    "\"Developer\"," +
                    "\"developer\")";


    //Variables para manipulación de datos
    private MangerUserHelper openHelper;
    private SQLiteDatabase database;

    public ManagerUser(Context context) {
        //Creando una instancia hacia la base de datos
        openHelper = new MangerUserHelper(context);
        database = openHelper.getWritableDatabase();
    }

    public void saveUserRow(String user_name,String user_alias){
        //Nuestro contenedor de valores
        ContentValues values = new ContentValues();
        //Seteando body y author
        values.put(ColumnUsers.USER_NAME,user_name);
        values.put(ColumnUsers.USER_ALIAS,user_alias);
        //Insertando en la base de datos
        database.insert(USERS_TABLE_NAME,null,values);
    }
    public void saveRoute(String nameRoute){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(new Date());
        //Nuestro contenedor de valores
        ContentValues values = new ContentValues();
        //Seteando body y author
        values.put(ColumnRoutes.ROUTE_NAME,nameRoute);
        values.put(ColumnRoutes.ROUTE_MODIFY,date);
        //Insertando en la base de datos
        database.insert(ROUTES_TABLE_NAME,null,values);
    }

    public Cursor getAllUsers(){
        //Seleccionamos todas las filas de la tabla Quotes
        return database.rawQuery(
                "select * from " + USERS_TABLE_NAME, null);
    }

    public Cursor getAllRoutes(){
        //Seleccionamos todas las filas de la tabla Quotes
        return database.rawQuery(
                "select * from " + ROUTES_TABLE_NAME + " order by datetime(\""+ColumnRoutes.ROUTE_MODIFY+"\") DESC", null);
    }

    public Cursor countTrack(int route_id){
        return database.rawQuery(
                "select COUNT(*) from "+ ROUTE_TRACK_TABLE_NAME +" where "+ColumnRoutesTrack.ID_ROUTE+" = "+ route_id, null);
    }

    public Cursor getAllTrackInfo(){
        return database.rawQuery(
                "select "+
                        ROUTE_TRACK_TABLE_NAME+"."+ColumnRoutesTrack.ID_ROUTE_TRACK+","+
                        "("+ROUTE_TRACK_TABLE_NAME+"."+ColumnRoutesTrack.ROUTE_TRACK_NAME+"|| ', ' ||"+ROUTES_TABLE_NAME+"."+ColumnRoutes.ROUTE_NAME+") as "+ColumnOther.NAME_TRACK_AND_NAME_ROUTE+","+
                        ROUTE_TRACK_TABLE_NAME+"."+ColumnRoutesTrack.MODIFY_TRACK+
                        " from "+ROUTE_TRACK_TABLE_NAME+","+ROUTES_TABLE_NAME+
                        " where "+ROUTE_TRACK_TABLE_NAME+"."+ColumnRoutesTrack.ID_ROUTE+" = "+ROUTES_TABLE_NAME+"."+ColumnRoutes.ID_ROUTE+
                        " order by datetime(\""+ColumnRoutesTrack.MODIFY_TRACK+"\") DESC", null);
    }

    public Cursor getTrackInfo(int id_track){
        return database.rawQuery(
                "select "+
                        " * from "+
                        ROUTE_TRACK_TABLE_NAME+
                        " where "+ColumnRoutesTrack.ID_ROUTE_TRACK+" = "+id_track, null);
    }

    public Cursor getAllPointTrack(int id_track){
        return database.rawQuery(
                "select "+ColumnRoutesTrackPoint.GPS_LATITUD+", "+ColumnRoutesTrackPoint.GPS_LONGITUD+", "+ColumnRoutesTrackPoint.GPS_VELOCIDAD+" from "+ ROUTE_TRACK_POINT_TABLE_NAME + " where " +ColumnRoutesTrackPoint.ID_TRACK+ " = " + id_track, null);
    }

    public int addNewRouteTrack(int route_id, String name_route){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(new Date());
        ContentValues values = new ContentValues();
        values.put(ColumnRoutesTrack.ID_ROUTE,route_id);
        values.put(ColumnRoutesTrack.ROUTE_TRACK_NAME,name_route);
        values.put(ColumnRoutesTrack.STATUS_ROUTE_TRACK,0);
        values.put(ColumnRoutesTrack.MAX_VELOCITY,0);
        values.put(ColumnRoutesTrack.PROMEDIO_VELOCITY,0);
        values.put(ColumnRoutesTrack.TIME_TOTAL,0);
        values.put(ColumnRoutesTrack.DISTANCIA_TOTAL, 0);
        values.put(ColumnRoutesTrack.START_TRACK,"0000-00-00 00:00:00");
        values.put(ColumnRoutesTrack.STOP_TRACK,"0000-00-00 00:00:00");
        values.put(ColumnRoutesTrack.SAVE_TRACK,date);
        values.put(ColumnRoutesTrack.MODIFY_TRACK,date);
        int id_track = (int) database.insert(ROUTE_TRACK_TABLE_NAME,null,values);
        return id_track;
    }
    public int addTrackConfig(int track_id, int upload_cloud, int pausea_auto, double s_lat, double s_lon, double f_lat, double f_lon, int public_type){
        ContentValues values = new ContentValues();
        values.put(ColumnRoutesTrackConfig.ID_TRACK,track_id);
        values.put(ColumnRoutesTrackConfig.UPLOAD_CLOUD,upload_cloud);
        values.put(ColumnRoutesTrackConfig.PAUSEA_AUTO,pausea_auto);
        values.put(ColumnRoutesTrackConfig.START_LAT,s_lat);
        values.put(ColumnRoutesTrackConfig.START_LON,s_lon);
        values.put(ColumnRoutesTrackConfig.FINISH_LAT,f_lat);
        values.put(ColumnRoutesTrackConfig.FINISH_LON,f_lon);
        values.put(ColumnRoutesTrackConfig.TYPE_PUBLIC,public_type);
        int id_config = (int) database.insert(ROUTE_TRACK_CONFIG_TABLE_NAME,null,values);
        return id_config;

    }

    public int addNewPoint(int id_track, Location location, double timer_check){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(new Date());
        ContentValues values = new ContentValues();
        values.put(ColumnRoutesTrackPoint.ID_TRACK,id_track);
        values.put(ColumnRoutesTrackPoint.GPS_LATITUD,location.getLatitude());
        values.put(ColumnRoutesTrackPoint.GPS_LONGITUD,location.getLongitude());
        values.put(ColumnRoutesTrackPoint.GPS_VELOCIDAD,location.getSpeed());
        values.put(ColumnRoutesTrackPoint.GPS_ALTITUDE,location.getAltitude());
        values.put(ColumnRoutesTrackPoint.TIMER_CHECK,(int) timer_check);
        values.put(ColumnRoutesTrackPoint.CHECK_DATETIME,date);
        int id_point = (int) database.insert(ROUTE_TRACK_POINT_TABLE_NAME,null,values);
        return id_point;
    }

    // Save Track ALL
    public void saveElementTrack(int idTrack, String row, Object value, boolean dateprint) {
        ContentValues values = new ContentValues();
        if (value.getClass() == Float.class) {
            values.put(row, (float) value);
        } else if (value.getClass() == Integer.class) {
            values.put(row, (int) value);
        } else if (value.getClass() == Double.class) {
            values.put(row, (double) value);
        } else if (value.getClass() == String.class) {
            values.put(row, (String) value);
        } else {
            values.put(row, (String) value);
        }
        if (dateprint) {
            values.put(ColumnRoutesTrack.MODIFY_TRACK, stringDateNow());
        }
        String selection = ColumnRoutesTrack.ID_ROUTE_TRACK + " = ?";
        String[] selectionArgs = {idTrack + ""};
        database.update(ROUTE_TRACK_TABLE_NAME, values, selection, selectionArgs);
    }

    public String stringDateNow(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(new Date());
        return date;
    }
}
