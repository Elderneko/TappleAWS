package com.example.cegoc.tapple;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import cad.Teacher;

public class  LoginActivity extends AppCompatActivity {

    private EditText edt_user, edt_pass;
    private Intent change;
    private ProgressBar pb;
    private Button btn;

    private class BackTaskDB extends android.os.AsyncTask<Void, Teacher, Teacher> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
            btn.setEnabled(false);
        }

        @Override
        protected Teacher doInBackground(Void... voids) {
            cad.TappleCAD t = new cad.TappleCAD();
            int id_teacher = t.checkLogin(edt_user.getText().toString(), edt_pass.getText().toString());
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
            btn.setEnabled(true);
            // Si no existe el usuario no se hace otra llamada a la BD
            if(t != null){
                // Iniciando sesion...
                saveInfo(t.getId_teacher(), t.getUser());
                startActivity(change);
                finishAffinity();
            } else {
                // Credenciales incorrectas
                Toast.makeText(LoginActivity.this,
                        "Credenciales incorrectos",Toast.LENGTH_LONG).show();
            }
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
        setContentView(R.layout.activity_login);

        pb = findViewById(R.id.pb_login);
        btn = findViewById(R.id.btn_login);
        change = new Intent(this, MainMenu.class);
        edt_user = findViewById(R.id.edt_user);
        edt_pass = findViewById(R.id.edt_pass);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BackTaskDB().execute();
            }
        });
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
     * Lleva al formulario de registro
     *
     * @param v
     */
    public void goRegister(View v){
        startActivity(new Intent(this, RegisterActivity.class));
    }

//    public void goForgetPass(View v){
//        startActivity(new Intent(this, ForgetActivity.class));
//    }

}