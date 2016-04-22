package mx.onlinesellers.developermotoappbeta13;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;

public class InicioActivity extends AppCompatActivity {

    public String route_string;
    //Código de envío
    public final static int ADD_REQUEST_CODE = 1;
    //Atributos para datos
    private ManagerUser dataSource;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        //Crear nuevo objeto QuotesDataSource
        dataSource = new ManagerUser(this);
        Cursor users = dataSource.getAllUsers();
        int countuser = users.getCount();
        Log.d("Count", "c:"+countuser);
        if(users.getCount() == 1){
            initNewUser();
        }
    }

    public void newRoute(View view){

        Log.d("Click", "New Route");
        Intent intent = new Intent(InicioActivity.this, PreViajeActivity.class);
        InicioActivity.this.startActivity(intent);

        /*
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nombre del viaje *obligatorio");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(input.getText().toString() != null && !input.getText().toString().isEmpty()){
                    route_string = input.getText().toString();
                    Intent intent = new Intent(InicioActivity.this, MapsActivity.class);
                    intent.putExtra("NAME_ROUTE", route_string);
                    InicioActivity.this.startActivity(intent);
                }else{
                    //errorRoute();
                    AlertDialog alertDialog = new AlertDialog.Builder(InicioActivity.this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("Sin nombre de viaje");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }
        });
        builder.show();
        */

    }

    public void viewRoute(View view){
        Intent intent = new Intent(InicioActivity.this, RecorridosActivity.class);
        InicioActivity.this.startActivity(intent);
        Log.d("Click", "View Route");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //Insertando el registro con los datos del formulario
                String username = data.getStringExtra("user_name");
                String useralias = data.getStringExtra("user_alias");
                dataSource.saveUserRow(username,useralias);
                //Refrescando la lista manualmente
            }
        }
    }

    private void initNewUser(){
        //Iniciando la actividad Form
        Intent intent = new Intent(this, NewUserActivity.class);
        //Inicio de la actividad esperando un resultado
        startActivityForResult(intent, ADD_REQUEST_CODE);
    }

}
