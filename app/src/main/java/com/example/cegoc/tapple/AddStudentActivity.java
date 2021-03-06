package com.example.cegoc.tapple;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import cad.Student;
import cad.Teacher;

public class AddStudentActivity extends AppCompatActivity {

    private Intent change;
    private EditText dni, name, surname1, surname2, email, address, city,
            phone, mDisplayDate;
    private ProgressBar pb;

    private static final String TAG = "AddStudentActivity";
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    /**
     * Clase asincrona que inserta un estudiante en la BD
     */
    private class BackTaskDB extends android.os.AsyncTask<Void, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            cad.TappleCAD t = new cad.TappleCAD();
            // Coge la fecha en string y la pasa a fecha
            DateFormat format =new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
            Date birthday = null;
            try {
                birthday = new Date(format.parse(mDisplayDate.getText().toString()).getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //Creo el estudiante con los datos del formulario
            Student student = new Student(0,
                    getTeacherID(),
                    Integer.valueOf(phone.getText().toString()),
                    dni.getText().toString().toUpperCase(),
                    name.getText().toString().toUpperCase(),
                    surname1.getText().toString().toUpperCase(),
                    surname2.getText().toString().toUpperCase(),
                    email.getText().toString(),
                    address.getText().toString().toUpperCase(),
                    city.getText().toString().toUpperCase(),
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
        address = findViewById(R.id.edt_address);
        city = findViewById(R.id.edt_city);
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

        mDisplayDate = findViewById(R.id.mostrar_fecha);
        mDisplayDate.setKeyListener(null);
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        AddStudentActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: yyyy-mm-dd: " + year + "-" + month + "-" + day);

                String date = year + "-" + month + "-" + day;
                mDisplayDate.setText(date);
            }
        };
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
        if(!dni.getText().toString().equals("") || !name.getText().toString().equals("") ||
                !surname1.getText().toString().equals("") || !surname2.getText().toString().equals("") ||
                !email.getText().toString().equals("") || !address.getText().toString().equals("")
                || !city.getText().toString().equals("")){
            retorno=true;
        }
        return retorno;
    }
}
