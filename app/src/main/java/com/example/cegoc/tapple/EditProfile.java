package com.example.cegoc.tapple;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.util.Calendar;

import cad.Teacher;

public class EditProfile extends AppCompatActivity {

    private ProgressBar pb;
    private EditText tName, tSurnames, tEmail, tBirthday;

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
                tName.setText(t.getName());
                tSurnames.setText(t.getSurname1() + " " + t.getSurname2());
                tEmail.setText(t.getEmail());
                tBirthday.setText(t.getBirthday().toString());
            } else {

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
        setContentView(R.layout.activity_edit_profile);

        pb = findViewById(R.id.pb_profile);
        tName = findViewById(R.id.edt_profile_name);
        tSurnames = findViewById(R.id.edt_profile_surnames);
        tEmail = findViewById(R.id.edt_profile_email);
        tBirthday = findViewById(R.id.edt_profile_birthday);

        Button btn = findViewById(R.id.btn_sendProfile);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
}
