package mx.onlinesellers.developermotoappbeta13;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class PreViajeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ManagerUser dataSource;
    private SimpleCursorAdapter adapter;
    ListView listRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_viaje);
        this.setTitle("Elegir Ruta");
        listRoute = (ListView) findViewById(R.id.listRoute);
        listRoute.setOnItemClickListener(this);
        //Crear nuevo objeto QuotesDataSource
        dataSource = new ManagerUser(this);

        //Iniciando el nuevo Adaptador
        adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.two_line_list_item,
                dataSource.getAllRoutes(),
                new String[]{ManagerUser.ColumnRoutes.ROUTE_NAME, ManagerUser.ColumnRoutes.ROUTE_MODIFY},
                new int[]{android.R.id.text1, android.R.id.text2},
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        );
        listRoute.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_previaje, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.addroute) {
            addRouteAlert();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addRouteAlert(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nombre del viaje *obligatorio");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(input);
        builder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(input.getText().toString() != null && !input.getText().toString().isEmpty()){
                    dataSource.saveRoute(input.getText().toString());
                    adapter.changeCursor(dataSource.getAllRoutes());
                }else{
                    AlertDialog alertDialog = new AlertDialog.Builder(PreViajeActivity.this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("Sin nombre de ruta");
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
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("RowClick", "row:"+ position+ " id:"+ id);
        TextView routename = (TextView) view.findViewById(android.R.id.text1);
        Intent intent = new Intent(PreViajeActivity.this, ViajeConfigActivity.class);
        intent.putExtra("ID_ROUTE", id);
        intent.putExtra("ROUTE_NAME", routename.getText().toString());
        PreViajeActivity.this.startActivity(intent);
    }
}
