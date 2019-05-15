package com.openclassrooms.realestatemanager.Util;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.openclassrooms.realestatemanager.Model.Photo;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

    public static int formatCurrentDateToInt(){
        Calendar calendar = Calendar.getInstance();
        String day = "" + calendar.get(Calendar.DAY_OF_MONTH);
        if(calendar.get(Calendar.DAY_OF_MONTH) < 10) day = "0" + day;
        int valueMonth = calendar.get(Calendar.MONTH) + 1;
        String month = "" + valueMonth;
        if(calendar.get(Calendar.MONTH) < 10) month = "0" + month;
        int year = calendar.get(Calendar.YEAR);
        return Integer.parseInt("" + year + month + day);
    }

    public static String formatIntDateToString(int date){
        String newDate = "" + date;
        String day = "", month = "", year = "";
        if(newDate.length() == 8){
            day = newDate.substring(6, 8) + "/";
            month = newDate.substring(4,6) + "/";
            year = newDate.substring(0,4);
            return day + month + year;
        } else return newDate;
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
        if(day < 10) strDay = "0" + strDay;
        if(month < 10) strMonth = "0" + strMonth;
        return strDay + "/" + strMonth + "/" + year;
    }

    public static int formatIntDate(int year, int month, int day){
        month = month + 1;
        String strMonth = "" + month;
        String strDay = "" + day;
        if(day < 10) strDay = "0" + strDay;
        if(month < 10) strMonth = "0" + strMonth;
        return Integer.parseInt("" + year + strMonth + strDay);
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
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
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

    public static String uppercaseFirstLetter(String str){
        return str.substring(0,1).toUpperCase() + str.substring(1);
    }

    public static double calculateMonthly(double amount, double rate, int duration){
        double realRate = rate/100;
        double monthly = ((amount*realRate)/12)/(1-Math.pow(1+(realRate/12), duration*12*-1));

        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(2);

        monthly = Double.parseDouble(decimalFormat.format(monthly));

        return monthly;
    }
}
