package com.example.cegoc.tapple;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Date;

import cad.Teacher;

public class RegisterActivity extends AppCompatActivity {

    private Intent change;
    private EditText dni, name, surname1, surname2, birthday, email, user, pass, pass2;

    private class Tarea extends android.os.AsyncTask<Void, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //ToDo Estaria bien avisar de que se esta haciendo algo y que hay que esperar
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            cad.TappleCAD t = new cad.TappleCAD();
            boolean existe = t.checkRegister(user.getText().toString(), email.getText().toString(),
                    dni.getText().toString());
            if(!existe){
                // Creo un objeto teacher con los datos del formulario
                Teacher teacher = new Teacher(0,
                        dni.getText().toString(),
                        name.getText().toString(),
                        surname1.getText().toString(),
                        surname2.getText().toString(),
                        email.getText().toString(),
                        user.getText().toString(),
                        pass.getText().toString(),
                        (Date) birthday.getText());
                // Hacer un insert a la BD
                t.addTeacher(teacher);
            }
            // Retornar el id del profesor con usuario el que esta en el formulario
            return t.checkLogin(user.getText().toString(), pass.getText().toString());
        }

        @Override
        protected void onPostExecute(Integer aInteger) {
            super.onPostExecute(aInteger);
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
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        change = new Intent(this, MainMenu.class);

        user = findViewById(R.id.edt_user);
        email = findViewById(R.id.edt_email);
        dni = findViewById(R.id.edt_dni);
        name = findViewById(R.id.edt_name);
        surname1 = findViewById(R.id.edt_surname1);
        surname2 = findViewById(R.id.edt_surname2);
        birthday = findViewById(R.id.edt_birthday);
        pass = findViewById(R.id.edt_pass);
        pass2 = findViewById(R.id.edt_pass2);

        Button btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validForm()){
                    new Tarea().execute();
                } else{
                    Toast.makeText(RegisterActivity.this, "El formulario no es valido",
                            Toast.LENGTH_SHORT).show();
                }
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
     * Metodo para tener control sobre el formulario
     *
     * @return true si es valido, false si no
     */
    private boolean validForm(){
        boolean retorno = false;
        // Si todos los campos tienen contenido
        if(!dni.getText().toString().equals("") && !name.getText().toString().equals("") &&
                !surname1.getText().toString().equals("") && !surname2.getText().toString().equals("") &&
                !birthday.getText().toString().equals("") && !email.getText().toString().equals("") &&
                !pass.getText().toString().equals("") && !pass2.getText().toString().equals("")){
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
