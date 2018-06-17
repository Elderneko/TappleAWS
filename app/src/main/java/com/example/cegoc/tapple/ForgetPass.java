package com.example.cegoc.tapple;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import cad.Teacher;

public class ForgetPass extends AppCompatActivity {

    private Button btn_send;
    private ProgressBar pb;
    private EditText user, answer;

    private class Tarea extends android.os.AsyncTask<Void, Teacher, Teacher> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
            btn_send.setEnabled(false);
        }

        @Override
        protected Teacher doInBackground(Void... voids) {
            cad.TappleCAD t = new cad.TappleCAD();
            return t.findTeacherByUser(user.getText().toString(), answer.getText().toString());
        }

        @Override
        protected void onPostExecute(Teacher t) {
            super.onPostExecute(t);
            pb.setVisibility(View.GONE);
            // Si no existe el usuario no se hace otra llamada a la BD
            if(t != null){
                Intent i = new Intent(ForgetPass.this, ChangePass.class);
                i.putExtra("USER", t.getUser());
                startActivity(i);
            } else {
                btn_send.setEnabled(true);
                Toast.makeText(ForgetPass.this, "Usuario no existente",
                        Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_forget_pass);

        pb = findViewById(R.id.pb_forget);
        user = findViewById(R.id.edt_user);
        answer = findViewById(R.id.edt_answer);
        btn_send = findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToDo If ControlFormulario
                new Tarea().execute();
            }
        });
    }

    // ToDo ControlFormulario
}
