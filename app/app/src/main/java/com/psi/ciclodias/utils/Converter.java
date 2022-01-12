package com.psi.ciclodias.utils;

import org.eclipse.paho.client.mqttv3.MqttClient;

import java.util.Formatter;
import java.util.Locale;

public class Converter {

    public static String hourFormat(int seconds){

        long hours = seconds/3600;
        long minutes = 0;
        long secondsString = 0;

        if(seconds % 3600 != 0){
            minutes = seconds % 3600 / 60;
        }
        if(seconds % 60 != 0){
            secondsString = seconds % 60;
        }

        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%02d", hours);
        String strHours = fmt.toString();


        fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%02d", minutes);
        String strMinutes = fmt.toString();

        fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%02d", secondsString);
        String strSeconds = fmt.toString();


        // Unidade de medida
        String strUnits = " h";

        return strHours + ":" + strMinutes + ":" + strSeconds + " " + strUnits;
    }

    public static String distanceFormat(double distance){

        distance = distance/1000;
        Formatter fmt = new Formatter(new StringBuilder());
        // 7 casas e 2 decimais
        fmt.format(Locale.US, "%3.2f", distance);
        String strDistance = fmt.toString();
        strDistance = strDistance.replace(' ', '0');

        // Unidade de medida
        String strUnits = " km";

        return strDistance + strUnits;
    }

    public static String velocityFormat(double velocity){

        // Formata os dados
        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%5.2f", velocity);
        String strVelocity = fmt.toString();
        strVelocity = strVelocity.replace(' ', '0');

        // Unidade de medida
        String strUnits = " Km/h";

        return strVelocity + strUnits;
    }

}
