package com.example.cegoc.tapple;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

import cad.Meeting;
import cad.Student;

public class MeetDetails extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private int id_meet;
    private LinearLayout r;
    private ArrayList<Student> listado;
    private ProgressBar pb;
    private TextView tStudent, tMoney, tIsPaid, tIsDone, tCreation, tMeet;

    private class ListaEstudiantes extends android.os.AsyncTask<Void, ArrayList<Student>, ArrayList<Student>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
            r.setVisibility(View.INVISIBLE);
        }

        @Override
        protected ArrayList<Student> doInBackground(Void... voids) {
            cad.TappleCAD t = new cad.TappleCAD();
            int id_teacher=getTeacherID();
            // Si la id es 0, no existe el usuario
            if(id_teacher != 0){
                listado = t.showStudents(id_teacher);
                return null;
            } else{
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Student> list) {
            super.onPostExecute(list);
            // Cuando acaba este hilo se ejecuta el que carga la interfaz
            new BackTaskDB().execute();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            pb.setVisibility(View.GONE);
        }
    }

    private class BackTaskDB extends android.os.AsyncTask<Void, Meeting, Meeting> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Meeting doInBackground(Void... voids) {
            cad.TappleCAD t = new cad.TappleCAD();
            // Si la id es 0, no existe el usuario
            if(id_meet != 0){
                return t.showMeeting(id_meet);
            } else{
                return null;
            }
        }

        @Override
        protected void onPostExecute(Meeting m) {
            super.onPostExecute(m);
            pb.setVisibility(View.GONE);
            // Si no existe el usuario no se hace otra llamada a la BD
            if(m != null){
                // Se muestran todos los datos de la citaa
                Log.i("AAA", "- "+ m.getId_student());
                tStudent.setText(buscaEnArray(listado, m.getId_student()));
                tMoney.setText(String.valueOf(m.getPayment()));
                tIsPaid.setText(String.valueOf(m.isPaid()));
                tIsDone.setText(String.valueOf(m.isDone()));
                tCreation.setText(m.getCreation_date().toString());
                tMeet.setText(m.getMeeting_date().toString());

                r.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(MeetDetails.this, "No se han podido mostrar datos",
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
        setContentView(R.layout.activity_meet_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        id_meet = getIntent().getIntExtra("ID_MEET", 0);

        r = findViewById(R.id.linear_content);
        pb = findViewById(R.id.pb_details_meet);
        tStudent = findViewById(R.id.txt_student);
        tMoney = findViewById(R.id.meet_money);
        tIsPaid = findViewById(R.id.meet_paid);
        tIsDone = findViewById(R.id.meet_done);
        tCreation = findViewById(R.id.mostrar_datetime_creation);
        tMeet = findViewById(R.id.mostrar_datetime_meet);

        Button btn_noti = findViewById(R.id.notificar_alumno);
        btn_noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MeetDetails.this,
                        "WIP: Notificar a alumno por SMS o EMAIL", Toast.LENGTH_LONG).show();
            }
        });


        FloatingActionButton fab = findViewById(R.id.btn_edit_meeting);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Ir a EditarCita
//                Intent i = new Intent(MeetDetails.this, MeetEdit.class);
//                i.putExtra("ID_MEET", id_meet);
//                startActivity(i);
                Toast.makeText(MeetDetails.this,
                        "WIP: Ir a EditarCita", Toast.LENGTH_LONG).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Tarea asincrona que cuando acaba llama a la de crear la interfaz
        new ListaEstudiantes().execute();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            startActivity(new Intent(MeetDetails.this, MeetingList.class));
            finishAffinity();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(this, MainMenu.class));
            finishAffinity();
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            finishAffinity();
        } else if (id == R.id.nav_students) {
            startActivity(new Intent(this, StudentsActivity.class));
            finishAffinity();
        } else if (id == R.id.nav_meeting) {
            startActivity(new Intent(this, MeetingList.class));
            finishAffinity();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
     * Metodo que busca en un array una id, y devuelve el nombre y apellidos
     *
     * @param lista ArrayList de alumnos en el que vamos a buscar
     * @param id id buscada
     * @return nombre y apellido1 concatenados
     */
    private String buscaEnArray(ArrayList<Student> lista, int id){
        for (int i=0; i<lista.size(); i++){
            if(lista.get(i).getId_student() == id){
                return lista.get(i).getName() + " " + lista.get(i).getSurname1();
            }
        }
        return null;
    }
}
