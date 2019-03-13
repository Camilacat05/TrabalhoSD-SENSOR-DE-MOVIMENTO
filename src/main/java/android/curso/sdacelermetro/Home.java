package android.curso.sdacelermetro;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Home extends AppCompatActivity implements SensorEventListener {
    private TextView xText, yText, zText, ResultText, texto;
    private Sensor mySensor;
    private SensorManager SM;
    private int MaiorResultado = 5, portaservidor;

    private Button btnParar;
    MediaPlayer MySong;
    private Client myClient;
    private String menssagem = "";
    private ImageView foto;
    TelephonyManager telephonyManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Adicionando a música
        MySong = MediaPlayer.create(getApplicationContext(), R.raw.alarme);
        telephonyManager = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);

        //Botão para parar a música
        btnParar = findViewById(R.id.btnParar);
        btnParar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySong.stop();
                /*if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                String IMEI_Number_Holder = telephonyManager.getDeviceId();
                menssagem = menssagem+IMEI_Number_Holder;*/
                myClient  = new Client("192.168.11.13", portaservidor,menssagem);
                myClient.execute();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        // Criando o Sensor Manager
        SM = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Sensor (Acelerômetro)
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Register sensor Listener
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);

        // Nomeando TextView's
        xText = findViewById(R.id.xText);
        yText = findViewById(R.id.yText);
        zText = findViewById(R.id.zText);
        ResultText = findViewById(R.id.ResultText);
        texto = findViewById(R.id.texto);
        foto = findViewById(R.id.foto);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        int x = Math.round(event.values[0]);
        int y = Math.round(event.values[1]);
        int z = Math.round(event.values[2]);
        int result = Math.round(event.values[0] + event.values[1] + event.values[2]);

        if(result>MaiorResultado){
            MaiorResultado= result;
        }

        xText.setText("X: " + x);
        yText.setText("Y: " + y);
        zText.setText("Z: " + z);
        ResultText.setText("Resultado: "+ result);

        if(y==10 && x==0 && z==0 ){
            foto.setImageResource(R.drawable.android);
            texto.setText("Smarphone está em pé ");
        }
        if(y==0 && (x==10 || x==-10) && z==0){
            foto.setImageResource(R.drawable.deitado);
            texto.setText("Smarphone está deitado ");
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss");

        Date data = new Date();

        Calendar  cal = Calendar.getInstance();
        cal.setTime(data);
        Date data_atual = cal.getTime();

        String data_completa = dateFormat.format(data_atual);
        data_completa = " Data/Hora: "+data_completa;

        menssagem = (menssagem +"Eixo X: "+event.values[0] +" | Eixo Y: "+event.values[1]+" | Eixo Z: "+ event.values[2]+" | Resultado: "+Integer.toString(result)+ " | " + data_completa +"\n");

        if(result > 12 && portaservidor ==9002){
            SoarAlarme();
            //portaservidor =0;
        }
    }

    private void SoarAlarme() {
        MySong.start();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onStart() {
        portaservidor=9002;
        super.onStart();
    }

}