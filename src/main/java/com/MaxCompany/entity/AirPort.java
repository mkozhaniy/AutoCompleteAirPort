package com.MaxCompany.entity;

public class AirPort {

    /**field numeric columns */
    public Double[] arrNum;
    /**field String columns */
    public String[] arrStr;

    public static Integer parseInt(String str){
        try{
            return Integer.parseInt(str);
        } catch (NumberFormatException e){
            return null;
        }
    }
    public static Double parseDouble(String str){
        try{
            return Double.parseDouble(str);
        } catch (NumberFormatException e){
            return null;
        }
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(arrStr[0]).append("[");
        sb.append(arrNum[0].intValue()).append(", ");
        int i;
        for(i = 0; i < 5; ++i) sb.append("\"").append(arrStr[i]).append("\", ");
        for(i = 1; i < 5; ++i) sb.append(arrNum[i]).append(", ");
        for(i = 5; i < 8; ++i) sb.append("\"").append(arrStr[i]).append("\", ");
        sb.append("\"").append(arrStr[8]).append("\"]");
        return sb.toString();
    }
}
