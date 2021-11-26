package com.psi.ciclodias.model;

import com.psi.ciclodias.databinding.ActivityInProgressTrainingBinding;
import com.psi.ciclodias.databinding.ActivityInProgressTrainingMapBinding;
import com.psi.ciclodias.databinding.ActivityPausedTrainingBinding;

import java.util.Formatter;
import java.util.Locale;

public class Chronometer extends Thread {

        public boolean stopVariable = false;
        public boolean stop = false;
        private long timeSeconds = 0;

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
                    if (trainingBinding != null) {
                        trainingBinding.tvDuracaoTreino.post(new Runnable() {
                            @Override
                            public void run() {
                                hourFormat();
                                trainingBinding.tvDuracaoTreino.setText(strHours + ":" + strMinutes + ":" + strSeconds + strUnits);

                            }
                        });
                    } else if (mapBinding != null) {
                        mapBinding.tvTempo.post(new Runnable() {
                            @Override
                            public void run() {
                                hourFormat();
                                mapBinding.tvTempo.setText("Tempo: \n"  + strHours + ":" + strMinutes + ":" + strSeconds + strUnits);
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


        private void hourFormat(){

            long hours = timeSeconds/3600;
            long minutes = 0;
            long seconds = 0;

            if(timeSeconds % 3600 != 0){
                minutes = timeSeconds % 3600 / 60;
            }
            if(timeSeconds % 60 != 0){
                seconds = timeSeconds % 60;
            }

            Formatter fmt = new Formatter(new StringBuilder());
            fmt.format(Locale.US, "%02d", hours);
            strHours= fmt.toString();


            fmt = new Formatter(new StringBuilder());
            fmt.format(Locale.US, "%02d", minutes);
            strMinutes = fmt.toString();

            fmt = new Formatter(new StringBuilder());
            fmt.format(Locale.US, "%02d", seconds);
            strSeconds = fmt.toString();


            // Unidade de medida
            strUnits = " h";
        }

        public long getTime() {
            return timeSeconds;
    }
}

