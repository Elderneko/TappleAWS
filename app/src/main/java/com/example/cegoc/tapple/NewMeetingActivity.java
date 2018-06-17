package com.example.cegoc.tapple;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Spinner;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import cad.Meeting;
import cad.Student;

public class NewMeetingActivity extends AppCompatActivity {

    private Intent change;
    private ProgressBar pb_spin, pb;
    private int id_alumno_aux;
    private Spinner spin, isPaid;
    private ArrayList<ListaSpinner> lista;
    private EditText money;
    private static final String TAG = "NewMeetingActivity";
    private TextView mDisplayDate, mDisplayDate2;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;

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

            // Coge la fecha y tiempo en string y la pasa a TimeStamp
            DateFormat format =new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.UK);
            Timestamp fechaCreaccion = null;
            try {
                fechaCreaccion = new Timestamp(format.parse(
                        mDisplayDate.getText().toString()+ " " +
                                mDisplayDate2.getText().toString()).getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }


            // ToDo Crear meeting con los datos del formulario para hacer el insert
            Meeting m = new Meeting(0,
                    id_alumno_aux,
                    Integer.valueOf(money.getText().toString()),
                    new Timestamp(System.currentTimeMillis()),
                    fechaCreaccion,
                    false,
                    Boolean.valueOf(isPaid.getSelectedItem().toString()));
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

        change = new Intent(NewMeetingActivity.this, MeetingList.class);

        pb_spin = findViewById(R.id.pb_spinner);
        pb = findViewById(R.id.pb_add_meeting);
        money = findViewById(R.id.meet_money);
        isPaid = findViewById(R.id.meet_paid);

        Button btn_add = findViewById(R.id.btn_add_meeting);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToDo Si validForm(), ejecuta tarea
                new BackTaskDB1().execute();
            }
        });

        // Spinner
        // Este codigo hace funcionar el ComboBox o Spinner
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
		// End Spinner

        // Se lanza la tarea asincrona
        new BackTaskDB0().execute();

        // Datepicker
        mDisplayDate = findViewById(R.id.mostrar_fecha);
        mDisplayDate.setKeyListener(null);
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        NewMeetingActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: yyyy-mm-dd: " + year + "-" + month + "-" + day);

                String date = year + "-" + month + "-" + day;
                mDisplayDate.setText(date);
            }
        };
        // End Datepicker

        // Timepicker
        mDisplayDate2 = findViewById(R.id.mostrar_hora);
        mDisplayDate2.setKeyListener(null);
        mDisplayDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int hours = cal.get(Calendar.HOUR_OF_DAY);
                int minutes = cal.get(Calendar.MINUTE);

                TimePickerDialog dialog = new TimePickerDialog(
                        NewMeetingActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mTimeSetListener,
                        hours,minutes, true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hours, int minutes) {

                String time = hours + ":" + minutes;
                mDisplayDate2.setText(time);
            }
        };
        // End Timepicker

    }

    // ToDo Crear metodo para controlar formulario,
    // ToDo en el caso del spinner si el valor es -1 es que no hay nada
    // ToDo el metodo debe retornar true o false

    //ToDo Un metodo para validar los datos de cada EditText, que se meteran dentro del if de validForm()

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
