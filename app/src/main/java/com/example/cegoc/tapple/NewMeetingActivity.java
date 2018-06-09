package com.example.cegoc.tapple;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Spinner;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import cad.Meeting;
import cad.Student;

public class NewMeetingActivity extends AppCompatActivity {

    private Intent change;
    private ProgressBar pb_spin, pb;
    private int id_alumno_aux;
    private Spinner spin;
    private ArrayList<ListaSpinner> lista;

    /**
     * Clase asincrona que ejecuta la consulta a la BD para rellenar el Spinner
     */
    private class BackTaskDB0 extends android.os.AsyncTask<Void, ArrayList<Student>, ArrayList<Student>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb_spin.setVisibility(View.VISIBLE);
            spin.setVisibility(View.GONE);
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
            pb_spin.setVisibility(View.GONE);
            // Si no existe el usuario no se hace otra llamada a la BD
            if(list.size() != 0){
                String nombrecompleto;
                // Se rellena el arraylist para poder usar el spinner
                for(Student s : list){
                    nombrecompleto = s.getName() + " " + s.getSurname1() + " " + s.getSurname2();
                    lista.add(new ListaSpinner(nombrecompleto, s.getId_student()));
                }
            } else {
                //ToDo No tiene alumnos
            }
            spin.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            pb_spin.setVisibility(View.GONE);
            spin.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Clase asincrona para insertar la cita en la BD
     */
    private class BackTaskDB1 extends android.os.AsyncTask<Void, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            cad.TappleCAD t = new cad.TappleCAD();
            Meeting m = null;
            // ToDo Crear meeting con los datos del formulario para hacer el insert
            t.addMeeting(m, id_alumno_aux);
            return 1;
        }

        @Override
        protected void onPostExecute(Integer aInteger) {
            super.onPostExecute(aInteger);
            pb.setVisibility(View.GONE);
            startActivity(change);
            finishAffinity();
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
        setContentView(R.layout.activity_add_meeting);

        change = new Intent(this, MeetingList.class);

        pb_spin = findViewById(R.id.pb_spinner);
        pb = findViewById(R.id.pb_add_meeting);

        Button btn_add = findViewById(R.id.btn_add_meeting);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToDo Si ControlDeFormulario(), ejecuta tarea
                new BackTaskDB1().execute();
            }
        });

        // Spinner list
        lista = new ArrayList<>();
        lista.add(new ListaSpinner("-- None --", -1));
        spin = findViewById(R.id.spn_student);
        MySpinnerAdapter adapter2 =
			new MySpinnerAdapter(NewMeetingActivity.this,
					android.R.layout.simple_spinner_item, lista);
		//adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setOnItemSelectedListener(new OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                ListaSpinner obj = (ListaSpinner)(parent.getItemAtPosition(position));
                // Pilla la id para usarla en la segunda tarea ASync (Falta de crear)
                id_alumno_aux = obj.getValue();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                id_alumno_aux = -1;
            }

        });
		spin.setAdapter(adapter2);

        Toast.makeText(NewMeetingActivity.this, "OnCreate",
                Toast.LENGTH_SHORT).show();
        new BackTaskDB0().execute();

    }

    // ToDo Crear metodo para controlar formulario,
    // ToDo en el caso del spinner si el valor es -1 es que no hay nada
    // ToDo el metodo debe retornar true o false
    /**
     * Devuelve la id del profesor almacenada en el shared preferences
     *
     * @return int id
     */
    private int getTeacherID(){
        return getSharedPreferences("TEACHER_INFO", Context.MODE_PRIVATE).
                getInt("ID_TEACHER",0);
    }

    // Clase auxiliar
    public class ListaSpinner{

		private String text;
	    private int id;


	    public ListaSpinner(String text, int value){
	    	this.text = text;
	    	this.id = value;
	    }

	    public void setText(String text){
	        this.text = text;
	    }

	    public String getText(){
	        return this.text;
	    }

	    public void setValue(int value){
	        this.id = value;
	    }

	    public int getValue(){
	        return this.id;
	    }
	}

	// Adaptador auxiliar
	public class MySpinnerAdapter extends ArrayAdapter<ListaSpinner>{

	    private Context context;
	    private ArrayList<ListaSpinner> myObjs;

	    public MySpinnerAdapter(Context context, int textViewResourceId,
                                ArrayList<ListaSpinner> myObjs) {
	        super(context, textViewResourceId, myObjs);
	        this.context = context;
	        this.myObjs = myObjs;
	    }

	    public int getCount(){
	       return myObjs.size();
	    }

	    public ListaSpinner getItem(int position){
	       return myObjs.get(position);
	    }

	    public long getItemId(int position){
	       return position;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        TextView label = new TextView(context);
	        label.setText(myObjs.get(position).getText());
	        return label;
	    }

	    @Override
	    public View getDropDownView(int position, View convertView,
	            ViewGroup parent) {
	        TextView label = new TextView(context);
	        label.setText(myObjs.get(position).getText());
	        return label;
	    }
	}
}
