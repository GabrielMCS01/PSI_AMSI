package com.psi.ciclodias.model;

import com.psi.ciclodias.databinding.ActivityInProgressTrainingBinding;
import com.psi.ciclodias.databinding.ActivityInProgressTrainingMapBinding;
import com.psi.ciclodias.databinding.ActivityPausedTrainingBinding;
import com.psi.ciclodias.utils.Converter;
import com.psi.ciclodias.view.mapFragment;

import java.util.Formatter;
import java.util.Locale;

public class Chronometer extends Thread {

        public boolean stopVariable = false;
        public boolean stop = false;
        private int timeSeconds = 0;

        private String strHours, strMinutes, strSeconds ,strUnits;


        public ActivityInProgressTrainingBinding trainingBinding = null;
        public ActivityInProgressTrainingMapBinding mapBinding = null;

        private Chronometer(){

        }
        
        private static Chronometer instancia = null;

        public static synchronized Chronometer getInstancia(){
            if (instancia == null){ instancia = new Chronometer();}
            return instancia;
        }

        public void halt() {
            stopVariable = true;
        }


        public void run(){
            while(true){
                if(!stopVariable) {
                    timeSeconds++;
                    mapFragment.getInstancia().time = timeSeconds;  
                    if (trainingBinding != null) {
                        trainingBinding.tvDuracaoTreino.post(new Runnable() {
                            @Override
                            public void run() {
                                trainingBinding.tvDuracaoTreino.setText(Converter.hourFormat(timeSeconds));

                            }
                        });
                    } else if (mapBinding != null) {
                        mapBinding.tvTempo.post(new Runnable() {
                            @Override
                            public void run() {
                                mapBinding.tvTempo.setText("Tempo: \n"  + Converter.hourFormat(timeSeconds));
                            }
                        });
                    }
                }
                try {
                    Thread.currentThread().sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(stop){
                    return;
                }
            }
        }

        public int getTime() {
            return timeSeconds;
    }
}

