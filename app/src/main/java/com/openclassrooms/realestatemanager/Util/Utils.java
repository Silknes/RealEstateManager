package com.openclassrooms.realestatemanager.Util;

import android.content.Context;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.util.DisplayMetrics;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Philippe on 21/02/2018.
 */

public class Utils {

    /**
     * Conversion d'un prix d'un bien immobilier (Dollars vers Euros)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param dollars
     * @return
     */
    public static int convertDollarToEuro(int dollars){
        return (int) Math.round(dollars * 0.812);
    }

    public static int convertEuroToDollar(int euro){
        return (int) Math.round(euro * 1.13);
    }

    /**
     * Conversion de la date d'aujourd'hui en un format plus approprié
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @return
     */
    public static String getTodayDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(new Date());
    }

    public static Date convertStringToDate(String stringToConvert){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(stringToConvert);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return convertedDate;
    }

    public static String formatStringDate(int year, int month, int day){
        month = month + 1;
        String strMonth = "" + month;
        String strDay = "" + day;
        if(month < 10) {
            strMonth = "0" + strMonth;
            if(day < 10) {
                strDay = "0" + strDay;
            }
        }
        return strDay + "/" + strMonth + "/" + year;
    }

    public static boolean compareDate(Date dateSelected, Date todayDate){
        return dateSelected.before(todayDate);
    }

    /**
     * Vérification de la connexion réseau
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param context
     * @return
     */
    public static Boolean isInternetAvailable(Context context){
        WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }

    public static int convertDpToPx(int pxValue){
        return pxValue*(Resources.getSystem().getDisplayMetrics().densityDpi/160);
    }

    public static boolean isEmailCorrect(String mail){
        return mail.contains("@")
                && mail.contains(".")
                && mail.lastIndexOf("@") < mail.lastIndexOf(".")
                && mail.lastIndexOf("@") + 1 != mail.lastIndexOf(".")
                && mail.lastIndexOf(".") + 1 < mail.length();
    }

    public static boolean isPasswordCorrect(String password){
        return password.length() > 7
                && password.matches(".*\\d.*")
                && password.matches(".*[a-z].*");
    }

    public static boolean isUsernameCorrect(String username){
        return username.length() > 3;
    }
}
