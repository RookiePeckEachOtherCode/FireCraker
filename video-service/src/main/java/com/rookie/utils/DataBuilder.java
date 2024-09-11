package com.rookie.utils;

public class DataBuilder {
    public static String buildCount(Integer count) {
        if (count == null) return "0";
        if(count>=0&&count<1000){return count.toString();}
        if (count >= 1000 && count < 10000) {
            return String.format("%.1f", count / 1000.0) + "k";
        }

        return String.format("%.1f", count / 10000.0) + "w";
    }
}
