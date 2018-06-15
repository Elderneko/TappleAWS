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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.util.Calendar;

import cad.Teacher;

public class RegisterActivity extends AppCompatActivity {

    private Intent change;
    private EditText dni, name, surname1, surname2, email, user, pass, answer,
            pass2, phone, year, month, day, date;
    private ProgressBar pb;

    private static final String TAG = "RegisterActivity";
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private class BackTaskDB extends android.os.AsyncTask<Void, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            cad.TappleCAD t = new cad.TappleCAD();
            boolean existe = t.checkRegister(user.getText().toString(), email.getText().toString(),
                    dni.getText().toString());
            if(!existe){
                // Creo un objeto teacher con los datos del formulario
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
                //Creo el profesor con los datos del formulario
                Teacher teacher = new Teacher(0,
                        Integer.valueOf(phone.getText().toString()),
                        dni.getText().toString(),
                        name.getText().toString(),
                        surname1.getText().toString(),
                        surname2.getText().toString(),
                        email.getText().toString(),
                        user.getText().toString(),
                        pass.getText().toString(),
                        answer.getText().toString(),
                        birthday);
                // Hacer un insert a la BD
                t.addTeacher(teacher);
            }
            // Retornar el id del profesor con usuario el que esta en el formulario
            return t.checkLogin(user.getText().toString(), pass.getText().toString());
        }

        @Override
        protected void onPostExecute(Integer aInteger) {
            super.onPostExecute(aInteger);
            pb.setVisibility(View.GONE);
            // Si no existe el usuario no se hace otra llamada a la BD
            if(aInteger != 0){
                // Iniciando sesion...
                saveInfo(aInteger, user.getText().toString());
                startActivity(change);
                finishAffinity();
            } else {
                // Registro incorrecto
                Toast.makeText(RegisterActivity.this,
                        "Registro no valido",Toast.LENGTH_LONG).show();
            }
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
        setContentView(R.layout.activity_register);

        // Init
        change = new Intent(this, MainMenu.class);

        pb = findViewById(R.id.pb_register);
        user = findViewById(R.id.edt_user);
        email = findViewById(R.id.edt_email);
        dni = findViewById(R.id.edt_dni);
        name = findViewById(R.id.edt_name);
        surname1 = findViewById(R.id.edt_surname1);
        surname2 = findViewById(R.id.edt_surname2);
        date = findViewById(R.id.mostrar_fecha);
        pass = findViewById(R.id.edt_pass);
        pass2 = findViewById(R.id.edt_pass2);
        phone = findViewById(R.id.edt_phone);
        answer = findViewById(R.id.edt_answer);

        Button btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validForm()){
                    new BackTaskDB().execute();
                } else{
                    Toast.makeText(RegisterActivity.this, "El formulario no es valido",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        mDisplayDate = findViewById(R.id.mostrar_fecha);
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        RegisterActivity.this,
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
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = month + "/" + day + "/" + year;
                mDisplayDate.setText(date);
            }
        };
    }

    /**
     * Guarda en el shared preferences una id y un nombre
     *
     * @param id_param id a guardar
     * @param user nombre de usuario a guardar
     */
    private void saveInfo(int id_param, String user){
        SharedPreferences preferencias=getSharedPreferences("TEACHER_INFO",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putInt("ID_TEACHER", id_param);
        editor.putString("USER_TEACHER", user);
        editor.apply();
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
                !day.getText().toString().equals("") && !email.getText().toString().equals("") &&
                !phone.getText().toString().equals("") && !pass.getText().toString().equals("") &&
                !pass2.getText().toString().equals("") && !answer.getText().toString().equals("")){
            // Si pass y pass2 son iguales
            if(pass.getText().toString().equals(pass2.getText().toString())){
                retorno = true;
            }
        }
        return retorno;
    }

    /**
     * Lleva al formulario de login
     *
     * @param v
     */
    public void goLogin(View v){
        startActivity(new Intent(this, LoginActivity.class));
        finishAffinity();
    }
}
