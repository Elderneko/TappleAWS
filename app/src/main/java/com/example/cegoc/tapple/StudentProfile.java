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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import cad.Student;

public class StudentProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String address, city;
    private ProgressBar pb;
    private int id_student;
    private ImageView mapa;
    private TextView tDNI, tName, tSurname1, tSurname2, tEmail, tBirthday, tPhone, tAddress, tCity;
    private LinearLayout r;

    private class BackTaskDB extends android.os.AsyncTask<Void, Student, Student> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
            mapa.setEnabled(false);
            r.setVisibility(View.INVISIBLE);
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
        protected void onPostExecute(Student t) {
            super.onPostExecute(t);
            pb.setVisibility(View.GONE);
            mapa.setEnabled(true);
            // Si no existe el usuario no se hace otra llamada a la BD
            if(t != null){
                // Variables para el maps
                address = t.getAddress();
                city = t.getCity();
                // Se muestran todos los datos del estudiante
                tDNI.setText(t.getDni());
                tName.setText(t.getName());
                tSurname1.setText(t.getSurname1());
                tSurname2.setText(t.getSurname2());
                tEmail.setText(t.getEmail());
                tBirthday.setText(t.getBirthday().toString());
                tPhone.setText(String.valueOf(t.getPhone()));
                tAddress.setText(t.getAddress());
                tCity.setText(t.getCity());

                r.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(StudentProfile.this, "No se han podido mostrar datos",
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            pb.setVisibility(View.GONE);
            mapa.setEnabled(true);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        id_student = getIntent().getIntExtra("ID_STUDENT", 0);
        pb = findViewById(R.id.pb_profile_student);

        r = findViewById(R.id.contenedor_principal);
        tDNI = findViewById(R.id.txt_profile_dni);
        tName = findViewById(R.id.txt_profile_name);
        tSurname1 = findViewById(R.id.txt_profile_surname1);
        tSurname2 = findViewById(R.id.txt_profile_surname2);
        tEmail = findViewById(R.id.txt_profile_email);
        tBirthday = findViewById(R.id.txt_profile_birthday);
        tPhone = findViewById(R.id.txt_profile_phone);
        tAddress = findViewById(R.id.txt_profile_address);
        tCity = findViewById(R.id.txt_profile_city);

        mapa = findViewById(R.id.img_map);
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
                Intent i = new Intent(StudentProfile.this, StudentEdit.class);
                i.putExtra("ID_STUDENT", id_student);
                startActivity(i);
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

        // Se ejecuta la tarea asincrona
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
            startActivity(new Intent(this, MeetingList.class));
            finishAffinity();
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
