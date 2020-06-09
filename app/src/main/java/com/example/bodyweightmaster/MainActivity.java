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


import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    private EditText username, weight, height;
    private Button listBtn, btnCalcBmi, locationBtn;
    private TextView bmiResult, bmiResultSummary;

    //gets the body mass index according to user's weight and height
    private double GetBmi(double weight, double height){
        double bmi = 0;

        //convert height to m2
        height /= 100;
        double heightM2 = height * height;

        //check if user has entered invalid values
        if(weight <= 0 || heightM2 <= 0){
            bmiResult.setText(R.string.validationError);
        }

        else{
            bmi = weight / heightM2;
            bmiResult.setText(Double.toString((bmi)));

            //get the summary
            GetSummary(bmi);
        }

        return (double) Math.round(bmi);
    }

    //gets bmi result summary
    private void GetSummary(double bmi){
        if(bmi < 18.5){
            bmiResultSummary.setText(R.string.underweight);
        }

        else if(bmi >= 18.5 && bmi <= 24.9){
            bmiResultSummary.setText(R.string.normal);
        }

        else if(bmi >= 25 && bmi <= 29.9){
            bmiResultSummary.setText(R.string.overweight);
        }

        else if(bmi >= 30){
            bmiResultSummary.setText(R.string.obese);
        }
    }

    public void initDb() throws SQLException {
        SQLiteDatabase db = null;

        db = SQLiteDatabase.openOrCreateDatabase(getFilesDir().getPath() +
                "/" + "bwmDb.db", null);

        String q = "CREATE TABLE IF NOT EXISTS RESULTS(";
        q += " ID INTEGER PRIMARY KEY AUTOINCREMENT, ";
        q += " USERNAME VARCHAR NOT NULL, ";
        q += " WEIGHT DOUBLE NOT NULL, ";
        q += " HEIGHT DOUBLE NOT NULL, ";
        q += " RESULT DOUBLE NOT NULL, ";
        q += " RESULT_DATE default current_timestamp); ";


        db.execSQL(q);
        db.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.userName);
        weight = findViewById(R.id.bodyWeight);
        height = findViewById(R.id.bodyHeight);
        listBtn = findViewById(R.id.listRecordsBtn);
        locationBtn = findViewById(R.id.locationBtn);
        btnCalcBmi = findViewById(R.id.calcBtn);
        bmiResult = findViewById(R.id.bmiResultText);
        bmiResultSummary = findViewById(R.id.bmiResultSummary);

        try{
            initDb();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),
                    e.getLocalizedMessage(),
                    Toast.LENGTH_LONG).show();
        }

        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent intent = new Intent(MainActivity.this, ListResults.class);
                    startActivityForResult(intent, 200);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();
                }

            }
        });

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });


        btnCalcBmi.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                SQLiteDatabase db = null;

                try{
                    double weightValue = Double.parseDouble(weight.getText().toString());
                    double heightValue = Double.parseDouble(height.getText().toString());
                    double bmi = GetBmi(weightValue, heightValue);
                    String  usernameValue = username.getText().toString();



                    //insert into datebase
                    db = SQLiteDatabase.openOrCreateDatabase(
                            getFilesDir().getPath() + "/" + "bwmDb.db", null
                    );

                    String q = "INSERT INTO RESULTS (USERNAME, WEIGHT, HEIGHT, RESULT, RESULT_DATE) ";
                    q += "VALUES (?, ?, ?, ?, ?); ";

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String strDate = sdf.format(new Date(System.currentTimeMillis()));

                    int validationError = R.string.validationError;
                    String validation = bmiResult.toString();

                    if(validation.equals("Моля, проверете данните, коите сте въвели")){
                        Toast.makeText(getApplicationContext(),
                                R.string.notSavedToDb,
                                Toast.LENGTH_LONG).show();
                    }

                    else{
                        db.execSQL(q, new Object[]{usernameValue, weightValue, heightValue, bmi, strDate});

                        Toast.makeText(getApplicationContext(),
                                R.string.savedToDb,
                                Toast.LENGTH_LONG).show();
                    }

                }catch(Exception e){
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
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
