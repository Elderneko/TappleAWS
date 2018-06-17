package com.example.cegoc.tapple;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ChangePass extends AppCompatActivity {

    private Button btn;
    private ProgressBar pb;
    private String user;
    private EditText pass, pass2;

    private class BackTaskDB extends android.os.AsyncTask<Void, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
            btn.setEnabled(false);
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            cad.TappleCAD t = new cad.TappleCAD();
            if (user != null && controlFormulario()){
                t.changePass(user, pass.getText().toString());
                return 1;
            } else {
                btn.setEnabled(true);
                Toast.makeText(ChangePass.this, "Las contraseñas no coinciden",
                        Toast.LENGTH_SHORT).show();
                return 0;
            }
        }

        @Override
        protected void onPostExecute(Integer aInteger) {
            super.onPostExecute(aInteger);
            pb.setVisibility(View.GONE);
            if(aInteger == 1){
                startActivity(new Intent(ChangePass.this, LoginActivity.class));
                finishAffinity();
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
        setContentView(R.layout.activity_change_pass);

        pb = findViewById(R.id.pb_change);
        user = getIntent().getStringExtra("USER");
        pass = findViewById(R.id.edt_pw1);
        pass2 = findViewById(R.id.edt_pw2);
        btn = findViewById(R.id.btn_change_pw);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToDo If controlFormulario
                new BackTaskDB().execute();
            }
        });

    }

    public boolean controlFormulario(){
        return pass.getText().toString().equals(pass2.getText().toString());
    }
}