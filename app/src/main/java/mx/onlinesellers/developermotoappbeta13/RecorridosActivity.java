package mx.onlinesellers.developermotoappbeta13;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class RecorridosActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView listView;
    private ManagerUser dataSource;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorridos);
        this.setTitle("Recorridos");
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        dataSource = new ManagerUser(this);
        //Iniciando el nuevo Adaptador
        adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.two_line_list_item,
                dataSource.getAllTrackInfo(),
                new String[]{ManagerUser.ColumnOther.NAME_TRACK_AND_NAME_ROUTE, ManagerUser.ColumnRoutesTrack.MODIFY_TRACK},
                new int[]{android.R.id.text1, android.R.id.text2},
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        );
        listView.setAdapter(adapter);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(RecorridosActivity.this, PrevioMapActivity.class);
        intent.putExtra("ID_TRACK", id);
        RecorridosActivity.this.startActivity(intent);

    }
}
