package com.example.bodyweightmaster;

import androidx.annotation.CallSuper;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;

public class ListResults extends AppCompatActivity {

    private ListView listView;
    private TextView textView;
    private Button btnClear;
    ArrayAdapter<String> adapter;

    public void selectDb() throws SQLException {
        SQLiteDatabase db = null;

        db = SQLiteDatabase.openOrCreateDatabase(
                getFilesDir().getPath() + "/" + "bwmDb.db", null
        );

        listView.clearChoices();

        ArrayList<String> listResults = new ArrayList<String>();
        String q = "SELECT * FROM RESULTS ORDER BY RESULT_DATE";

        Cursor c = db.rawQuery(q, null);

        while(c.moveToNext()){
            String id = c.getString(c.getColumnIndex("ID"));
            String username =c.getString(c.getColumnIndex("USERNAME"));
            String weight =c.getString(c.getColumnIndex("WEIGHT"));
            String height =c.getString(c.getColumnIndex("HEIGHT"));
            String result =c.getString(c.getColumnIndex("RESULT"));
            String resultDate =c.getString(c.getColumnIndex("RESULT_DATE"));

            listResults.add(username + "\t" + weight + "\t" + height + "\t" + result + "\t" + resultDate + "\t" + id);
        }

        adapter = new ArrayAdapter<String>(
                getApplicationContext(),
                R.layout.activity_list_view,
                R.id.textView,
                listResults
        );

        adapter.notifyDataSetChanged();

        listView.setAdapter(adapter);

        db.execSQL(q);
        db.close();

    }

    @Override
    @CallSuper
    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, requestCode, data);

        try{
            selectDb();
        }catch (Exception e){

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_results);

        textView = findViewById(R.id.textView);
        listView = findViewById(R.id.resultsList);
        btnClear = findViewById(R.id.clearBtn);

        try{
            selectDb();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(),
                    Toast.LENGTH_LONG).show();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = "";
                TextView clickedText = view.findViewById(R.id.textView);
                selectedItem = clickedText.getText().toString();

                String[] itemContent = selectedItem.split("\t");

                String username = itemContent[0];
                String weight = itemContent[1];
                String height = itemContent[2];
                String result = itemContent[3];
                String date = itemContent[4];
                String itemId = itemContent[5];

                Intent intent = new Intent(ListResults.this, ResultInfo.class);

                Bundle bundle = new Bundle();

                bundle.putString("username", username);
                bundle.putString("weight", weight);
                bundle.putString("height", height);
                bundle.putString("result", result);
                bundle.putString("resultDate", date);
                bundle.putString("id", itemId);

                intent.putExtras(bundle);

                startActivityForResult(intent, 200, bundle);

            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = null;
                try{
                    db = SQLiteDatabase.openOrCreateDatabase(
                            getFilesDir().getPath() + "/" + "bwmDb.db", null
                    );

                    String q = "DELETE FROM RESULTS";
                    db.execSQL(q);

                    selectDb();


                    Toast.makeText(getApplicationContext(), R.string.listCleared,
                            Toast.LENGTH_LONG).show();
                }catch(Exception e){
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();
                }finally {
                    if(db != null){
                        db.close();
                        db = null;
                    }
                }
            }
        });
    }

}
