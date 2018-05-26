package com.example.cegoc.tapple;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import cad.Student;

public class StudentsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ProgressBar pb;

    private class BackTaskDB extends android.os.AsyncTask<Void, ArrayList<Student>, ArrayList<Student>> {

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
                return t.showStudents(id_teacher);
            } else{
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Student> list) {
            super.onPostExecute(list);
            pb.setVisibility(View.GONE);
            // Si no existe el usuario no se hace otra llamada a la BD
            if(list.size() != 0){
                for(Student s : list){
                    createViewStudent(s.getName(), s.getSurname1(), s.getSurname2());
                }
            } else {
                //ToDo No tiene alumnos
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
        setContentView(R.layout.activity_students);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.btn_add_student);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StudentsActivity.this, AddStudentActivity.class));
            }
        });
        pb = findViewById(R.id.pb_students);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        loadUsername();
        new BackTaskDB().execute();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
            // ToDo Ir a la actividad de citas
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
     * Crea una linea con el nombre y apellidos del alumno
     *
     * @param nombre
     * @param ap1
     * @param ap2
     */
    private void createViewStudent(String nombre, String ap1, String ap2){
        LinearLayout sv = findViewById(R.id.linear_students);
        LinearLayout auxLinear = new LinearLayout(this);
        auxLinear.setOrientation(LinearLayout.HORIZONTAL);
        auxLinear.setLayoutParams(new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        auxLinear.setPadding(toDp(10, auxLinear),toDp(10, auxLinear),
                toDp(10, auxLinear),toDp(10, auxLinear));
        TextView auxText = new TextView(this);
        auxText.setGravity(Gravity.CENTER);
        auxText.setText(nombre + " " +ap1 + " " + ap2);
        auxLinear.addView(auxText);
        sv.addView(auxLinear);
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
}
