package com.example.cegoc.tapple;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class StudentEdit extends AppCompatActivity {

    private ProgressBar pb;
    private int id_student;
    private EditText tDNI, tName, tSurname1, tSurname2, tEmail, tBirthday, tPhone, tAddress, tCity;
    private LinearLayout r;
    private Button btn;

    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private class BackTaskDB extends android.os.AsyncTask<Void, Student, Student> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
            r.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Student doInBackground(Void... voids) {
            cad.TappleCAD t = new cad.TappleCAD();
            // Si la id es 0, no existe el usuario
            if(id_student != 0){
                return t.showStudent(id_student);
            } else{
                return null;
            }
        }

        @Override
        protected void onPostExecute(Student t) {
            super.onPostExecute(t);
            pb.setVisibility(View.GONE);
            // Si no existe el usuario no se hace otra llamada a la BD
            if(t != null){
                // Se muestran todos los datos del estudiante
                tDNI.setText(t.getDni(), TextView.BufferType.EDITABLE);
                tName.setText(t.getName(), TextView.BufferType.EDITABLE);
                tSurname1.setText(t.getSurname1(), TextView.BufferType.EDITABLE);
                tSurname2.setText(t.getSurname2(), TextView.BufferType.EDITABLE);
                tEmail.setText(t.getEmail(), TextView.BufferType.EDITABLE);
                tBirthday.setText(t.getBirthday().toString(), TextView.BufferType.EDITABLE);
                tPhone.setText(String.valueOf(t.getPhone()), TextView.BufferType.EDITABLE);
                tAddress.setText(t.getAddress(), TextView.BufferType.EDITABLE);
                tCity.setText(t.getCity(), TextView.BufferType.EDITABLE);

                r.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(StudentEdit.this, "No se han podido mostrar datos",
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            pb.setVisibility(View.GONE);
        }
    }

    private class BackTaskDB2 extends android.os.AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
            btn.setEnabled(false);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(controlFormulario()){
                cad.TappleCAD t = new cad.TappleCAD();
                // Coge la fecha en string y la pasa a fecha
                DateFormat format =new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
                Date birthday = null;
                try {
                    birthday = new Date(format.parse(tBirthday.getText().toString()).getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //Creo el estudiante con los datos del formulario
                Student s = new Student(id_student, 0,
                        Integer.valueOf(tPhone.getText().toString()),
                        tDNI.getText().toString().toUpperCase(),
                        tName.getText().toString().toUpperCase(),
                        tSurname1.getText().toString().toUpperCase(),
                        tSurname2.getText().toString().toUpperCase(),
                        tEmail.getText().toString(),
                        tAddress.getText().toString(),
                        tCity.getText().toString(),
                        birthday
                );
                // Hacer un update a la BD
                t.editStudent(s);
            } else{
                Toast.makeText(StudentEdit.this, "Error", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            pb.setVisibility(View.GONE);
            btn.setEnabled(true);

            Intent i = new Intent(StudentEdit.this, StudentProfile.class);
            i.putExtra("ID_STUDENT", id_student);
            startActivity(i);
            finishAffinity();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            pb.setVisibility(View.GONE);
            btn.setEnabled(true);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_edit);

        id_student = getIntent().getIntExtra("ID_STUDENT", 0);
        pb = findViewById(R.id.pb_profile_student);

        r = findViewById(R.id.contenedor_principal);
        tDNI = findViewById(R.id.txt_profile_dni);
        tName = findViewById(R.id.txt_profile_name);
        tSurname1 = findViewById(R.id.txt_profile_surname1);
        tSurname2 = findViewById(R.id.txt_profile_surname2);
        tEmail = findViewById(R.id.txt_profile_email);
        tBirthday = findViewById(R.id.txt_profile_birthday);
        tPhone = findViewById(R.id.txt_profile_phone);
        tAddress = findViewById(R.id.txt_profile_address);
        tCity = findViewById(R.id.txt_profile_city);

        btn = findViewById(R.id.btn_sendProfile);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToDo Control de formulario
                new BackTaskDB2().execute();
            }
        });

        // Datepicker
        tBirthday.setKeyListener(null);
        tBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        StudentEdit.this,
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

                String date = year + "-" + month + "-" + day;
                tBirthday.setText(date);
            }
        };
        // End Datepicker

        // Se ejecuta la tarea asincrona
        new BackTaskDB().execute();
    }

    /**
     * Control de formulario
     *
     * @return
     */
    private boolean controlFormulario(){
        if(tDNI.getText().toString().equals("") || tName.getText().toString().equals("")
                || tSurname1.getText().toString().equals("") ||
                tSurname2.getText().toString().equals("")
                || tEmail.getText().toString().equals("") ||
                tPhone.getText().toString().equals("") ||
                tBirthday.getText().toString().equals("") ||
                tAddress.getText().toString().equals("") ||
                tCity.getText().toString().equals("")){
            return false;
        } else{
            return true;
        }
    }

}
