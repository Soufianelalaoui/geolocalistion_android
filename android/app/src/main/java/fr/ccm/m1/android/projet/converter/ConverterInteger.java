package fr.ccm.m1.android.projet.converter;

import androidx.databinding.InverseMethod;

public class ConverterInteger {

    public static int convertStringToInt(String text) {
        if(text == null || "".equals(text)){
            return 0;
        }
        return Integer.parseInt(text);
    }

    @InverseMethod(value="convertStringToInt")
    public static String convertIntToString(int value) {
        return Integer.toString(value);
    }
}
