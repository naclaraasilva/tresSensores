package com.example.tressensores;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensorUmidade;
    private Sensor sensorTemperatura;
    private Sensor sensorPressao;

    private TextView txtTemperatura, txtUmidade, txtPressao;

    private LineChart grafico;

    private float ultimaTemperatura = 0;
    private float ultimaUmidade = 0;
    private float ultimaPressao = 0;

    private boolean existeTemperatura = false;
    private boolean existeUmidade = false;
    private boolean existePressao = false;

    private int segundos = 0;

    private Handler handler = new Handler(Looper.getMainLooper());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        txtTemperatura = findViewById(R.id.txtTemperatura);
        txtUmidade = findViewById(R.id.txtUmidade);
        txtPressao = findViewById(R.id.txtPressao);

        grafico = findViewById(R.id.grafico);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        sensorTemperatura = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        sensorUmidade = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        sensorPressao = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        //vamos ver se realmente criou,se existe o sensor
        if (sensorTemperatura != null) {
            existeTemperatura = true;
            txtTemperatura.setText("temperatura: Aguardando");
        }else{
            //se não existe o sensor, vamos informar ao usuário
            txtTemperatura.setText("temperatura: inexistente");
        }
        if (sensorUmidade != null) {
            existeUmidade = true;
            txtUmidade.setText("Umidade: Aguardando");
        }else{
            //se não existe o sensor, vamos informar ao usuário
            txtUmidade.setText("Umidade: inexistente");
        }
        if (sensorPressao != null) {
            existePressao = true;
            txtPressao.setText("Pressao: Aguardando");
        }else{
            //se não existe o sensor, vamos informar ao usuário
            txtPressao.setText("Pressao: inexistente");
        }
        //Preparar o gráfico
        //
        configurarGrafico();
        //

    }// fim do oncreate

    @Override
    public void onSensorChanged(android.hardware.SensorEvent event) {
        //
        //vai receber as leituras dos sensores
        if(event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE){
            //aqui eu sei que o sensor que gerou o evento foi o de temperatura
            ultimaTemperatura = event.values[0];
            txtTemperatura.setText("Temperatura:" + String.format("%.1f", ultimaTemperatura));
        }
        if(event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY){
            //aqui eu sei que o sensor que gerou o evento foi o de umidade
            ultimaUmidade = event.values[0];
            txtUmidade.setText("Umidade:" + String.format("%.1f", ultimaUmidade));
        }
        if(event.sensor.getType() == Sensor.TYPE_PRESSURE){
            //aqui eu sei que o sensor que gerou o evento foi o de pressão
            ultimaPressao = event.values[0];
            txtPressao.setText("Pressão:" + String.format("%.1f", ultimaPressao));
        }

    }// fim da onSensorChanged

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume(){
        super.onResume();

        if(sensorTemperatura != null){
            sensorManager.registerListener(this, sensorTemperatura, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if(sensorUmidade != null) {
            sensorManager.registerListener(this, sensorUmidade, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if(sensorPressao != null) {
            sensorManager.registerListener(this, sensorPressao, SensorManager.SENSOR_DELAY_NORMAL);
        }

        handler.removeCallbacks(timerGrafico);
        handler.postDelayed(timerGrafico, 1000);

    }


    private void configurarGrafico() {
        // Configurações do gráfico
        //temperatura
        LineDataSet linhaTemperatura =
            new LineDataSet(
                    new ArrayList<>(), "Temperatura"
            );
        linhaTemperatura.setColor(Color.RED);
        linhaTemperatura.setLineWidth(2f);
        linhaTemperatura.setDrawCircles(false);
        linhaTemperatura.setDrawValues(false);

        //
        //umidade

        LineDataSet linhaUmidade =
                new LineDataSet(
                        new ArrayList<>(), "Umidade"
                );
        linhaUmidade.setColor(Color.BLUE);
        linhaUmidade.setLineWidth(2f);
        linhaUmidade.setDrawCircles(false);
        linhaUmidade.setDrawValues(false);


        //
        //pressão

        LineDataSet linhaPressao =
                new LineDataSet(
                        new ArrayList<>(), "Pressão"
                );
        linhaPressao.setColor(Color.GREEN);
        linhaPressao.setLineWidth(2f);
        linhaPressao.setDrawCircles(false);
        linhaPressao.setDrawValues(false);

        //
         // Configurações do gráfico
        LineData dados = new LineData(
                linhaTemperatura, linhaUmidade, linhaPressao
        );

        grafico.setData(dados);
        grafico.getAxisLeft().setAxisMinimum(0);
        grafico.getAxisRight().setEnabled(false);
        grafico.getDescription().setText("Sensores Ambientais");

        grafico.invalidate();

    }

    private void adicionarLeituraGrafico(){
        LineData dados = grafico.getData();
        float temperaturaGrafico = 0;
        float umidadeGrafico = 0;
        float pressaoGrafico = 0;
        if(existeTemperatura){
            temperaturaGrafico = ultimaTemperatura;
        }
        if(existeUmidade){
            umidadeGrafico = ultimaUmidade;
        }
        if(existePressao) {
            pressaoGrafico = ultimaPressao / 10;
        }
        dados.addEntry(
                new Entry(segundos, temperaturaGrafico),0
        );
        dados.addEntry(
                new Entry(segundos, umidadeGrafico),1
        );
        dados.addEntry(
                new Entry(segundos, pressaoGrafico),2
        );

        segundos++;// contador de segundos

        dados.notifyDataChanged();
        grafico.notifyDataSetChanged();

        if (segundos <= 30) {
            grafico.invalidate();
        } else {
            grafico.setVisibleXRangeMaximum(30);
            grafico.moveViewToX(segundos);
        }
    }

    private Runnable timerGrafico = new Runnable() {
        @Override
        public void run() {
            adicionarLeituraGrafico();
            handler.postDelayed(this, 1000);
        }
    };

}