package com.rido.utils;

public class ConvertToHoursAndMinutes {
    public static String convertToHoursAndMinutes(int totalMinutes) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;

        String hoursString = hours < 10 ? "0" + hours : String.valueOf(hours);
        String minutesString = minutes < 10 ? "0" + minutes : String.valueOf(minutes);

        return hoursString+":"+minutesString;
    }
}
