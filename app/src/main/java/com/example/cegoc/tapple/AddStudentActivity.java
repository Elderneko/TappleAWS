package com.example.cegoc.tapple;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.sql.Date;
import java.util.Calendar;

import cad.Student;
import cad.Teacher;

public class AddStudentActivity extends AppCompatActivity {

    private Intent change;
    private EditText dni, name, surname1, surname2, email,
            phone, year, month, day;
    private ProgressBar pb;

    private class BackTaskDB extends android.os.AsyncTask<Void, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            cad.TappleCAD t = new cad.TappleCAD();
            int yearI, monthI, dayI;
            yearI = Integer.valueOf(year.getText().toString());
            monthI = Integer.valueOf(month.getText().toString());
            dayI = Integer.valueOf(day.getText().toString());
            // Creo un Calendario
            Calendar cal = Calendar.getInstance();
            // Lo inicializo con los valores del formulario
            cal.set(yearI,monthI,dayI);
            // Creo la fecha basada en ese calendario
            Date birthday = new Date(cal.getTimeInMillis());
            //Creo el estudiante con los datos del formulario
            Student student = new Student(0,
                    getTeacherID(),
                    Integer.valueOf(phone.getText().toString()),
                    dni.getText().toString(),
                    name.getText().toString(),
                    surname1.getText().toString(),
                    surname2.getText().toString(),
                    email.getText().toString(),
                    birthday);
            // Hacer un insert a la BD
            t.addStudent(student);
            return 1;
        }

        @Override
        protected void onPostExecute(Integer aInteger) {
            super.onPostExecute(aInteger);
            pb.setVisibility(View.GONE);
            startActivity(change);
            finishAffinity();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            pb.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        // Init
        change = new Intent(this, StudentsActivity.class);

        pb = findViewById(R.id.pb_register);
        email = findViewById(R.id.edt_email);
        dni = findViewById(R.id.edt_dni);
        name = findViewById(R.id.edt_name);
        surname1 = findViewById(R.id.edt_surname1);
        surname2 = findViewById(R.id.edt_surname2);
        year = findViewById(R.id.edt_year);
        month = findViewById(R.id.edt_month);
        day = findViewById(R.id.edt_day);
        phone = findViewById(R.id.edt_phone);

        Button btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validForm()){
                    new BackTaskDB().execute();
                } else{
                    Toast.makeText(AddStudentActivity.this, "El formulario no es valido",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Devuelve la id del profesor almacenada en el shared preferences
     *
     * @return int id
     */
    private int getTeacherID(){
        return getSharedPreferences("TEACHER_INFO", Context.MODE_PRIVATE).
                getInt("ID_TEACHER",0);
    }

    /**
     * Metodo para tener control sobre el formulario
     *
     * @return true si es valido, false si no
     */
    private boolean validForm(){
        boolean retorno = false;
        // Si todos los campos tienen contenido
        if(!dni.getText().toString().equals("") && !name.getText().toString().equals("") &&
                !surname1.getText().toString().equals("") && !surname2.getText().toString().equals("") &&
                !year.getText().toString().equals("") && !month.getText().toString().equals("") &&
                !day.getText().toString().equals("") && !email.getText().toString().equals("")){
            retorno=true;
        }
        return retorno;
    }
}
