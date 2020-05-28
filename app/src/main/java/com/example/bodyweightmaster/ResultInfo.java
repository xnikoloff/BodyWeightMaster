package com.example.bodyweightmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ResultInfo extends AppCompatActivity {

    private String id;
    private EditText editUsername;
    private TextView weightInfo, heightInfo, resultInfo, dateInfo;
    private Button btnUpdate, btnRemove;

    protected void closeActivity(){
        finishActivity(200);

        Intent intent = new Intent(ResultInfo.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_info);

        editUsername = findViewById(R.id.editUsername);
        weightInfo = findViewById(R.id.weightInfoText);
        heightInfo = findViewById(R.id.heightInfoText);
        resultInfo = findViewById(R.id.resultInfoText);
        dateInfo = findViewById(R.id.dateInfoText);
        btnUpdate = findViewById(R.id.updateBtn);
        btnRemove = findViewById(R.id.removeBtn);

        Bundle b = getIntent().getExtras();

        if(b != null){
            try{
                id = b.getString("id");
                editUsername.setText(b.getString("username"));
                weightInfo.setText(b.getString("weight"));
                heightInfo.setText(b.getString("height"));
                resultInfo.setText(b.getString("result"));
                dateInfo.setText(b.getString("resultDate"));
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(),
                        Toast.LENGTH_LONG).show();
            }

        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = null;

                try{
                    db = SQLiteDatabase.openOrCreateDatabase(
                            getFilesDir().getPath() + "/" + "bwmDb.db",
                            null
                    );

                    String username = editUsername.getText().toString();

                    String q = "UPDATE RESULTS SET USERNAME = ? ";
                    q += " WHERE ID = ?; ";

                    db.execSQL(q, new Object[]{username, id});

                    Toast.makeText(getApplicationContext(), R.string.resultUpdated,
                            Toast.LENGTH_LONG).show();



                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();
                }finally {
                    if(db != null){
                        db.close();
                        db = null;
                    }
                }

                closeActivity();

            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = null;

                try{
                    db = SQLiteDatabase.openOrCreateDatabase(
                            getFilesDir().getPath() + "/" + "bwmDb.db",
                            null
                    );

                    String q = "DELETE FROM RESULTS WHERE ID = ?; ";

                    db.execSQL(q, new Object[]{id});

                    Toast.makeText(getApplicationContext(), R.string.resultRemoved,
                            Toast.LENGTH_LONG).show();

                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();
                }finally {
                    if(db != null){
                        db.close();
                        db = null;
                    }
                }

                closeActivity();

            }
        });
    }
}
