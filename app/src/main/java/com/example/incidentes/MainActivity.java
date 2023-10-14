package com.example.incidentes;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private TextView textHora, textFecha;
    private Handler handler;
    private Runnable actualizarHora;
    private EditText nombre, rut, incidente;
    private Button grabar, finalizar;
    private Boolean loop = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ubicacion en XML
        textHora = findViewById(R.id.textHora);
        textFecha = findViewById(R.id.textFecha);
        nombre = findViewById(R.id.nombrePersona);
        rut = findViewById(R.id.rut);
        incidente = findViewById(R.id.incidente);
        grabar = findViewById(R.id.btnGrabarIncidente);
        finalizar = findViewById(R.id.btnCerrarAplicacion);

        // Inicializar el Handler y el Runnable
        handler = new Handler(Looper.getMainLooper());

        actualizarHora = new Runnable() {
            @Override
            public void run() {
                updateCurrentTime();
                handler.postDelayed(this, 1000); //
            }
        };
        grabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validar_guardar(nombre, rut, incidente);
            }
        });
        finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        super.onResume();
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0) //verificamos que exista acelerometro
        {
            sm.registerListener(this, sensors.get(0), SensorManager.SENSOR_DELAY_GAME);
        }

    }
    private  void Validar_guardar(EditText nombre,EditText rut,EditText incidente){

        String nombre_str = nombre.getText().toString().trim();
        String rut_str = rut.getText().toString().trim();
        String incidente_str = incidente.getText().toString().trim();

        if (TextUtils.isEmpty(nombre_str)){
            nombre.setError("Campo Obligatorio");
            return;
        }

        if (TextUtils.isEmpty(rut_str)){
            rut.setError("Campo Obligatorio");
            return;
        }

        if (!validarRut(rut_str)){
            rut.setError("RUT Inválido");
            return;
        }

        if (TextUtils.isEmpty(incidente_str)){
            incidente.setError("Campo Obligatorio");
            return;
        }
        Toast.makeText(getApplicationContext(), "Datos grabados", Toast.LENGTH_SHORT).show();
    }
    public boolean validarRut(String rut){
        boolean validacion = false;
        try{
            rut = rut.toUpperCase();
            rut = rut.replace(".","");
            rut = rut.replace("-","");

            int rutAux = Integer.parseInt(rut.substring(0, rut.length() - 1));

            char dv = rut.charAt(rut.length() - 1);

            int m = 0,s = 1;
            for (; rutAux != 0; rutAux /= 10){
                s = (s + rutAux % 10 * (9 - m++ % 6)) % 11;
            }
            if (dv ==  (char) (s != 0 ? s + 47 : 75)){
                validacion = true;
            }
        } catch (java.lang.NumberFormatException e) {
        } catch (Exception e) {
        }
        return validacion;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Actualizar hora
        handler.post(actualizarHora);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(actualizarHora);
    }

    private void updateCurrentTime() {
        Date currentTime = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String formattedTime = sdf.format(currentTime);

        SimpleDateFormat sdfFecha = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedFecha = sdfFecha.format(currentTime);

        textHora.setText("Hora actual: " + formattedTime);
        textFecha.setText("Fecha: "+ formattedFecha);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            float y = event.values[1];
            if (y > 8.5 || y < -8.5){
                if (loop) {
                    Log.d("test", "giro");
                    Validar_guardar(nombre, rut, incidente);
                    loop = false;
                }
            }else {
                loop = true;
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}