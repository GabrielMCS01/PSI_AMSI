package com.psi.ciclodias.model;

import com.psi.ciclodias.databinding.ActivityInProgressTrainingBinding;
import com.psi.ciclodias.databinding.ActivityInProgressTrainingMapBinding;
import com.psi.ciclodias.utils.Converter;
import com.psi.ciclodias.view.mapFragment;

public class Chronometer extends Thread {

        public boolean stopVariable = false;
        public boolean stop = false;
        private int timeSeconds = 0;

        public ActivityInProgressTrainingBinding trainingBinding = null;
        public ActivityInProgressTrainingMapBinding mapBinding = null;

        private Chronometer(){

        }
        
        private static Chronometer instancia = null;

        public static synchronized Chronometer getInstancia(boolean newChrometer){
            // Se não existir uma instância do cronometro, esta é criada
            if (instancia == null || newChrometer){
                instancia = new Chronometer();
            }
            return instancia;
        }

        // Executa o cronometro
        public void run(){
            // Cronometro em execução
            while(true){
                // Enquanto a variável não estiver em pausa
                if(!stopVariable) {
                    timeSeconds++;
                    mapFragment.getInstancia().time = timeSeconds;
                    // Se o User tiver a activity de treino aberta esta atualiza o tempo
                    if (trainingBinding != null) {
                        trainingBinding.tvDuracaoTreino.post(new Runnable() {
                            @Override
                            public void run() {
                                trainingBinding.tvDuracaoTreino.setText(Converter.hourFormat(timeSeconds));
                            }
                        });
                    // Se o User tiver a activity do mapa durante o treino aberta esta atualiza o tempo
                    } else if (mapBinding != null) {
                        mapBinding.tvTempo.post(new Runnable() {
                            @Override
                            public void run() {
                                mapBinding.tvTempo.setText(Converter.hourFormat(timeSeconds));
                            }
                        });
                    }
                }
                try {
                    Thread.currentThread().sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Sai do ciclo caso a variável para parar seja TRUE
                if(stop){
                    return;
                }
            }
        }

        // Retorna o tempo em segundos
        public int getTime() {
            return timeSeconds;
    }
}

