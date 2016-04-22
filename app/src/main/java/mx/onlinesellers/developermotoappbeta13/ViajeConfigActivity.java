package mx.onlinesellers.developermotoappbeta13;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

public class ViajeConfigActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener, View.OnClickListener {

    public int id_route;

    String[] valuesCloudUnpload = new String[]{"1 min", "5 min", "15 min", "20 min", "al terminar", "Desactivado"};
    String[] valuesPauseAUnpload = new String[]{"5 min", "15 min", "20 min", "30 min", "1 hrs", "Desactivado"};

    int CloudSelect = 5;
    int PauseSelect = 5;

    TextView textCloud;
    TextView textPauseA;

    Button btnContinuar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viaje_config);
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            id_route = 0;
            this.setTitle(extras.getString("Error de ruta"));
        } else {
            id_route = (int) extras.getLong("ID_ROUTE");
            this.setTitle("Ruta " + extras.getString("ROUTE_NAME"));
        }
        Log.d("ID_Route", "id:"+id_route);
        textCloud = (TextView) findViewById(R.id.text_cloud);
        textPauseA = (TextView) findViewById(R.id.text_pausea);
        btnContinuar = (Button) findViewById(R.id.viajeco_btn_start);
        btnContinuar.setOnClickListener(this);
        selectCloud(5);
        selectPauseA(5);
    }

    public void selectAlertCloud(View view){
        final Dialog d = new Dialog(ViajeConfigActivity.this);
        d.setTitle("NumberPicker");
        d.setContentView(R.layout.alert_picker);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(5);
        np.setMinValue(0);
        np.setValue(CloudSelect);
        np.setDisplayedValues(valuesCloudUnpload);
        np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np.setOnValueChangedListener(this);
        b1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Log.d("Select", "S:"+String.valueOf(np.getValue()));
                selectCloud(np.getValue());
                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();
    }
    public void selectAlertPauseA(View view){
        final Dialog d = new Dialog(ViajeConfigActivity.this);
        d.setTitle("NumberPicker");
        d.setContentView(R.layout.alert_picker);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(5);
        np.setMinValue(0);
        np.setValue(PauseSelect);
        np.setDisplayedValues(valuesPauseAUnpload);
        np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np.setOnValueChangedListener(this);
        b1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Log.d("Select", "S:"+String.valueOf(np.getValue()));
                selectPauseA(np.getValue());
                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

    }

    public void selectCloud(int selectCheck){
        textCloud.setText(valuesCloudUnpload[selectCheck].toString());
        if(selectCheck == 5){
            textCloud.setTextColor(0xFFFF4444);
        }else{
            textCloud.setTextColor(0xFF0099CC);
        }
        CloudSelect = selectCheck;
    }

    public void selectPauseA(int selectCheck){
        textPauseA.setText(valuesPauseAUnpload[selectCheck].toString());
        if(selectCheck == 5){
            textPauseA.setTextColor(0xFFFF4444);
        }else{
            textPauseA.setTextColor(0xFF0099CC);
        }
        PauseSelect = selectCheck;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.viajeco_btn_start){
            ManagerUser dataSource = new ManagerUser(this);
            Cursor cursordata = dataSource.countTrack(id_route);
            String[] data;
            if (cursordata != null) {
                while(cursordata.moveToNext()) {
                    data = new String[1];
                    data[0] = Integer.toString(cursordata.getInt(0));
                    String newNametrack = " Vuelta "+ data[0];
                    Log.d("Track", newNametrack);
                    int id_track = dataSource.addNewRouteTrack(id_route, newNametrack);
                    int id_ajustes_track = dataSource.addTrackConfig(id_track, CloudSelect, PauseSelect, 0,0,0,0,0);
                    Log.d("ID_TRACK", ""+id_track);
                    Log.d("ID_TRACk_CONFIG", ""+id_ajustes_track);
                    cursordata.close();
                    Intent intent = new Intent(ViajeConfigActivity.this, MapsActivity.class);
                    intent.putExtra("TRACK_ID", id_track);
                    intent.putExtra("TRACK_CONFIG_ID", id_ajustes_track);
                    intent.putExtra("ROUTE_ID", id_route);
                    intent.putExtra("CLOUD_SELECT", CloudSelect);
                    intent.putExtra("PAUSEA_SELECT", PauseSelect);
                    intent.putExtra("NAME_TRACK", newNametrack);
                    intent.putExtra("PUBLIC_TYPE", 0);
                    ViajeConfigActivity.this.startActivity(intent);
                }
            }
        }
    }
}
