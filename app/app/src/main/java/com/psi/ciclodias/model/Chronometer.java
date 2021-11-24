package com.psi.ciclodias.model;

import com.psi.ciclodias.databinding.ActivityInProgressTrainingBinding;
import com.psi.ciclodias.databinding.ActivityInProgressTrainingMapBinding;
import com.psi.ciclodias.databinding.ActivityPausedTrainingBinding;

public class Chronometer extends Thread {

        public boolean stopVariable = false;
        public boolean stop = false;
        private long timeSeconds = 0;

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
                                trainingBinding.tvDuracaoTreino.setText("" + timeSeconds);
                            }
                        });
                    } else if (mapBinding != null) {
                        mapBinding.tvTempo.setText("" + timeSeconds);
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

    public long getTime() {
            return timeSeconds;
    }
}
