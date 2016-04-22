package mx.onlinesellers.developermotoappbeta13;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dis2 on 18/04/16.
 */
public class MangerUserHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Users.db";
    public static final int DATABASE_VERSION = 8;

    public MangerUserHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Crear la tabla Quotes
        db.execSQL(ManagerUser.CREATE_USERS_SCRIPT);
        db.execSQL(ManagerUser.CREATE_ROUTES_SCRIPT);
        db.execSQL(ManagerUser.CREATE_ROUTE_TRACK_SCRIPT);
        db.execSQL(ManagerUser.CREATE_ROUTE_TRACK_POINT_SCRIPT);
        db.execSQL(ManagerUser.CREATE_ROUTE_TRACK_CONFIG_SCRIPT);
        //Insertar registros iniciales
        db.execSQL(ManagerUser.INSERT_QUOTES_SCRIPT);

        /*  Nota: Usamos execSQL() ya que las sentencias son
            para uso interno y no están relacionadas con entradas
            proporcionadas por los usuarios
        */
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            /*  Añade los cambios que se realizarán en el esquema
                en tu proxima versión
             */
        //db.execSQL("ALTER TABLE routes_track_points RENAME TO routes_track_point");
        db.execSQL("DROP TABLE routes_track_points");
        db.execSQL(ManagerUser.CREATE_ROUTE_TRACK_POINT_SCRIPT);


    }
}
