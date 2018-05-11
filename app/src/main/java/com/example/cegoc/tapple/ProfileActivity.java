package com.example.cegoc.tapple;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import cad.Teacher;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ProgressBar pb;

    private class Tarea extends android.os.AsyncTask<Void, Teacher, Teacher> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected Teacher doInBackground(Void... voids) {
            cad.TappleCAD t = new cad.TappleCAD();
            int id_teacher=getTeacherID();
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
            // Si no existe el usuario no se hace otra llamada a la BD
            if(t != null){
                TextView tName = findViewById(R.id.txt_profile_name);
                TextView tSurnames = findViewById(R.id.txt_profile_surnames);
                TextView tEmail = findViewById(R.id.txt_profile_email);
                TextView tBirthday = findViewById(R.id.txt_profile_birthday);
                tName.setText(t.getName());
                tSurnames.setText(t.getSurname1() + " " + t.getSurname2());
                tEmail.setText(t.getEmail());
                tBirthday.setText(t.getBirthday().toString());
            } else {

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
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pb = findViewById(R.id.pb_profile);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        loadUsername();
        new Tarea().execute();
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
}
