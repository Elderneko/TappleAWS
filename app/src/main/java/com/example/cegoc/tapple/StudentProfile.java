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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import cad.Student;

public class StudentProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String address, city;
    private ProgressBar pb;
    private int id_student;
    private Button mapa;

    private class BackTaskDB extends android.os.AsyncTask<Void, Student, Student> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
            mapa.setEnabled(false);
        }

        @Override
        protected Student doInBackground(Void... voids) {
            cad.TappleCAD t = new cad.TappleCAD();
            // Si la id es 0, no existe el usuario
            if(id_student != 0){
                return t.showStudent(id_student);
            } else{
                return null;
            }
        }

        @Override
        protected void onPostExecute(Student s) {
            super.onPostExecute(s);
            pb.setVisibility(View.GONE);
            if(s != null){
                // Se va a usar en el maps
                city = s.getCity();
                address = s.getAddress();
                mapa.setEnabled(true);
                //ToDo Muestra datos (a la espera del diseño)
                Toast.makeText(StudentProfile.this, s.getAddress()+ "," + s.getCity(), Toast.LENGTH_LONG).show();
                // Seria hacer solo los setText a los elementos del xml, no crearlo por codigo
            } else {
                //ToDo No existe
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            pb.setVisibility(View.GONE);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        id_student = getIntent().getIntExtra("ID_STUDENT", 0);
        pb = findViewById(R.id.pb_profile_student);

        mapa = findViewById(R.id.btn_map);
        mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StudentProfile.this, StudentLocation.class);
                i.putExtra("ADDRESS", address);
                i.putExtra("CITY", city);
                // Se inicia la actividad
                startActivity(i);
            }
        });

        FloatingActionButton fab = findViewById(R.id.btn_edit_student);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(StudentProfile.this, "Ir a EditStudent", Toast.LENGTH_SHORT).show();
                //ToDo Quitar el comentario cuando se cree StudentEdit
                //Intent i = new Intent(StudentProfile.this, StudentEdit.class);
                //i.putExtra("ID_STUDENT", 0);
                //startActivity(i);
            }
        });

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
}
