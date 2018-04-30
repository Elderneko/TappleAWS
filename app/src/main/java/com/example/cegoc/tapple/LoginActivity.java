package com.example.cegoc.tapple;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cad.Login;

public class LoginActivity extends AppCompatActivity {

    private TextView feedback;
    private EditText edt_user, edt_pass;
    private Intent change;

    private class Tarea extends android.os.AsyncTask<Void, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //ToDo Estaria bien avisar de que se esta haciendo algo y que hay que esperar
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            cad.TappleCAD t = new cad.TappleCAD();
            int id = t.checkLogin(edt_user.getText().toString(), edt_pass.getText().toString());
            return id;
        }

        @Override
        protected void onPostExecute(Integer aInteger) {
            super.onPostExecute(aInteger);
            if(aInteger != 0){
                // Iniciando sesion...
                feedback.setText("Usuario encontrado (id="+aInteger+")");
                startActivity(change);
                finishAffinity();
            } else {
                feedback.setText("Usuario no encontrado (id="+aInteger+")");
                // Credenciales incorrectas
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        change = new Intent(this, MainMenu.class);
        edt_user = findViewById(R.id.edt_user);
        edt_pass = findViewById(R.id.edt_pass);
        feedback = findViewById(R.id.txt_feedback);
        Button bLogin = findViewById(R.id.btn_login);

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    new Tarea().execute();
            }
        });
    }
}