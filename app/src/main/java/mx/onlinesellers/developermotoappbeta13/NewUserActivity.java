package mx.onlinesellers.developermotoappbeta13;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import mx.onlinesellers.developermotoappbeta13.Servicios.MAFUNCIONES;

public class NewUserActivity extends AppCompatActivity implements View.OnClickListener {

    Button save_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        save_btn = (Button) findViewById(R.id.user_btn_save);
        save_btn.setOnClickListener(this);
    }

    public void save_data(){

    }

    @Override
    public void onClick(View v) {
        MAFUNCIONES MAFunciones = new MAFUNCIONES(getApplicationContext());
        if (v.getId() == R.id.user_btn_save){
            EditText input_name = (EditText) findViewById(R.id.user_input_name);
            EditText input_alias = (EditText) findViewById(R.id.user_input_alias);
            if(!MAFunciones.stringBoolVerificadorLimit(input_name.getText().toString(), 5)){
                AlertDialog alertDialog = new AlertDialog.Builder(NewUserActivity.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("El nombre debe tener mas de 5 caracteres");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }else if(!MAFunciones.stringBoolVerificadorLimit(input_alias.getText().toString(), 5)){
                AlertDialog alertDialog = new AlertDialog.Builder(NewUserActivity.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("El alias debe tener mas de 5 caracteres");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }else{
                Intent backData = new Intent();
                backData.putExtra("user_name", input_name.getText().toString());
                backData.putExtra("user_alias", input_alias.getText().toString());
                setResult(RESULT_OK, backData);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Tiene que registrar un usuario y alias");
        builder.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                dialog.cancel();
            }
        });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }
}
