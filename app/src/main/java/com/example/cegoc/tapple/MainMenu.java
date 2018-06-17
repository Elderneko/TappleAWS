package com.example.cegoc.tapple;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

public class MainMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TextView fWonderfull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        loadUsername();


        //Seccion Mr.Wonderfull
        fWonderfull = findViewById(R.id.txt_fraseWonderfull);
        Resources res = getResources();
        String[] frase = res.getStringArray(R.array.frases);
        int fraseNum = 0;
        fraseNum = frase.length;
        Random rnd = new Random();
        int random = (int) (Math.random()* fraseNum);
        fWonderfull.setText(frase[random]);
        //Fin de la seccion moñas
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // ToDo Traducciones
            creaAlerta("Atencion!",
                    "Esta seguro de que desea cerrar la app?").show();
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
        // Activity Main nombre Profesor en textview
        TextView name = findViewById(R.id.txt_drawer_username);
        name.setText(aux);
    }


    public void GoToStudents(View view)
    {
        startActivity(new Intent(this, StudentsActivity.class));
        finishAffinity();
    }

    public void GoToMeetings(View view)
    {
        startActivity(new Intent(this, MeetingList.class));
        finishAffinity();
    }

    /**
     * Metodo auxiliar para crear DialogAlert en pantalla
     *
     * @param titulo  titulo de la ventana
     * @param mensaje mensaje que queremos mostrar
     * @return un dialogo, hay que usar .show() para que se muestre en pantalla
     */
    private Dialog creaAlerta(String titulo, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
        builder.setMessage(mensaje).setTitle(titulo);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }
}
