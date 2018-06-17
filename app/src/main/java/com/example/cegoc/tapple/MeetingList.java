package com.example.cegoc.tapple;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

import cad.Meeting;
import cad.Student;

public class MeetingList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ProgressBar pb;
    private ArrayList<Student> listado;

    private class ListaEstudiantes extends android.os.AsyncTask<Void, ArrayList<Student>, ArrayList<Student>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
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

    private class BackTaskDB extends android.os.AsyncTask<Void, ArrayList<Meeting>, ArrayList<Meeting>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Meeting> doInBackground(Void... voids) {
            cad.TappleCAD t = new cad.TappleCAD();
            int id_teacher=getTeacherID();
            // Si la id es 0, no existe el usuario
            if(id_teacher != 0){
                return t.showAllMeetings(id_teacher);
            } else{
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Meeting> list) {
            super.onPostExecute(list);
            pb.setVisibility(View.GONE);
            // Si hay citas se crea la intefaz
            if(list.size() != 0){
                for(Meeting m : list){
                    createViewMeeting(m, buscaEnArray(listado, m.getId_student()));
                }
            } else {
                //ToDo No tiene citas
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
        setContentView(R.layout.activity_meeting_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.btn_new_meeting);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MeetingList.this, NewMeetingActivity.class));
            }
        });
        pb = findViewById(R.id.pb_meeting);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        loadUsername();

        // Tarea asincrona que cuando acaba llama a la de crear la interfaz
        new ListaEstudiantes().execute();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            startActivity(new Intent(MeetingList.this, MainMenu.class));
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
     * Crea una linea con el nombre y apellidos del alumno y su onclick
     */
    private void createViewMeeting(Meeting m, String nombre){
        LinearLayout sv = findViewById(R.id.linear_meetings);
        LinearLayout auxLinear = new LinearLayout(this);
        auxLinear.setOrientation(LinearLayout.HORIZONTAL);
        auxLinear.setLayoutParams(new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        auxLinear.setPadding(toDp(10, auxLinear),toDp(10, auxLinear),
                toDp(10, auxLinear),toDp(10, auxLinear));
        TextView auxText = new TextView(this);
        auxText.setGravity(Gravity.CENTER);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        auxText.setText(sdf.format(m.getMeeting_date()) + " " + nombre);
        auxLinear.setTag(m.getId_meet());
        auxLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = (int) v.getTag();
                //ToDo Quitar comentario cuando se cree la actividad de MeetingDetails
//                Intent i = new Intent(MeetingList.this, MeetingDetails.class);
//                i.putExtra("ID_MEET", id);
//                startActivity(i);
            }
        });
        auxLinear.addView(auxText);
        sv.addView(auxLinear);
    }

    /**
     * Carga el nombre del profesor que hemos guardado en el shared preferences
     * en el menu lateral
     */
    private void loadUsername(){
        NavigationView nv = findViewById(R.id.nav_view);
        View headerView = nv.getHeaderView(0);
        TextView tv = headerView.findViewById(R.id.txt_drawer_username);
        String aux = getSharedPreferences("TEACHER_INFO", Context.MODE_PRIVATE).
                getString("USER_TEACHER","");
        tv.setText(aux);
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
     * Este metodo pasa de pixeles a dp
     *
     * @param num numero de pixeles
     * @return num transformado a dp
     */
    private int toDp(int num, LinearLayout lea){
        float factor = lea.getResources().getDisplayMetrics().density;
        return (int) factor*num;
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
