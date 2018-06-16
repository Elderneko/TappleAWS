package com.example.cegoc.tapple;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import cad.Teacher;

public class EditProfile extends AppCompatActivity {

    private Button btn;
    private ProgressBar pb;
    private EditText tName, tSurname1, tSurname2, tPhone, tEmail, tBirthday, tDNI;

    private class Tarea extends android.os.AsyncTask<Void, Teacher, Teacher> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected Teacher doInBackground(Void... voids) {
            cad.TappleCAD t = new cad.TappleCAD();
            int id_teacher=getTeacherID();
            // Si la id es 0, no existe el usuario
            if(id_teacher != 0){
                return t.showProfile(id_teacher);
            } else{
                return null;
            }
        }

        @Override
        protected void onPostExecute(Teacher t) {
            super.onPostExecute(t);
            pb.setVisibility(View.GONE);
            // Si no existe el usuario no se hace otra llamada a la BD
            if(t != null){
                tDNI.setText(t.getDni(), TextView.BufferType.EDITABLE);
                tName.setText(t.getName(), TextView.BufferType.EDITABLE);
                tSurname1.setText(t.getSurname1(), TextView.BufferType.EDITABLE);
                tSurname2.setText(t.getSurname2(), TextView.BufferType.EDITABLE);
                tEmail.setText(t.getEmail(), TextView.BufferType.EDITABLE);
                tBirthday.setText(t.getBirthday().toString(), TextView.BufferType.EDITABLE);
                tPhone.setText(String.valueOf(t.getPhone()), TextView.BufferType.EDITABLE);
            } else {

            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            pb.setVisibility(View.GONE);
        }
    }

    private class BackTaskDB extends android.os.AsyncTask<Void, Void, Void> {

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
                //Creo el profesor con los datos del formulario
                Teacher teacher = new Teacher(getTeacherID(),
                        Integer.valueOf(tPhone.getText().toString()),
                        tDNI.getText().toString().toUpperCase(),
                        tName.getText().toString().toUpperCase(),
                        tSurname1.getText().toString().toUpperCase(),
                        tSurname2.getText().toString().toUpperCase(),
                        tEmail.getText().toString(),
                        "",
                        "",
                        "",
                        birthday
                        );
                // Hacer un insert a la BD
                t.editTeacher(teacher);
            } else{
                Toast.makeText(EditProfile.this, "Error", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            pb.setVisibility(View.GONE);
            btn.setEnabled(true);

            startActivity(new Intent(EditProfile.this, ProfileActivity.class));
            finishAffinity();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            pb.setVisibility(View.GONE);
            btn.setEnabled(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        pb = findViewById(R.id.pb_profile);
        tDNI = findViewById(R.id.edt_profile_dni);
        tName = findViewById(R.id.edt_profile_name);
        tSurname1 = findViewById(R.id.edt_profile_surname1);
        tSurname2 = findViewById(R.id.edt_profile_surname2);
        tEmail = findViewById(R.id.edt_profile_email);
        tBirthday = findViewById(R.id.edt_profile_birthday);
        tPhone = findViewById(R.id.edt_profile_phone);

        new Tarea().execute();

        btn = findViewById(R.id.btn_sendProfile);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BackTaskDB().execute();
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

    private boolean controlFormulario(){
        if(tDNI.getText().toString().equals("") || tName.getText().toString().equals("")
                || tSurname1.getText().toString().equals("") ||
                tSurname2.getText().toString().equals("")
                || tEmail.getText().toString().equals("") ||
                tPhone.getText().toString().equals("") ||
                tBirthday.getText().toString().equals("")){
            return false;
        } else{
            return true;
        }
    }
}
