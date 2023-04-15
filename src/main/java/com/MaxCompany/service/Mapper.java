package com.MaxCompany.service;

import com.MaxCompany.entity.AirPort;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * class for stream.map which extends Function*/
public class Mapper implements Function<String, AirPort> {
    private static final String regex =
            "(.*?),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*)";

    @Override
    public AirPort apply(String s) {
        AirPort ap = new AirPort();
        ap.arrNum = new Double[5];
        ap.arrStr = new String[9];
        s = s.trim();
        s = s.replaceAll("\"", "");
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        int gap;
        if(matcher.find()){
            ap.arrNum[0] = AirPort.parseDouble(matcher.group(1));
            for(int i = 2; i < 15; ++i){
                if(i < 7 || i > 10){
                    if(i >10) gap = 6;
                    else  gap = 2;
                    ap.arrStr[i-gap] = matcher.group(i).replaceAll("\"","");
                } else {
                    ap.arrNum[i-6] = AirPort.parseDouble(matcher.group(i));
                }
            }
        }
        return ap;
    }
}
