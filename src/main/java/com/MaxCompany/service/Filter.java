package com.MaxCompany.service;


import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Filter {
    /** regex for numbers columns*/
    public static final String regexNumeric =
            "\\(*(column\\[([1789]|(10))]\\s*([<>=]|(<>))\\s*[0-9]++\\.?[0-9]*)\\)*";
    /** regex for string columns*/
    public static final String regexString =
            "\\(*(column\\[([23456]|(11)|(12)|(13)|(14))\\]\\s*([<>=]|(<>))\\s*('.*'))\\)*";
    /** group for sign and after symbols */
    public static final Pattern patternSign =
            Pattern.compile("\\s*((<>)|[<>=])\\s*(.*)\\s*");
    /** group for all columns */
    public static final Pattern patternCol =
            Pattern.compile("column\\[([\\d]*)]");

    /**
     * method for checking correcting of filter
     * @param filter
     * @throws ParseException - if filter not corrent throws exception*/
    public static void isCorrect(String filter) throws ParseException {
        if(!checkParenthesis(filter))
            throw new ParseException("incorrect filter, replace parenthesis: " + filter, 1);
        String[] predicatesAnd;
        String[] predciatesOr;
        Pattern patternString = Pattern.compile(regexString);
        Pattern patternNumeric = Pattern.compile(regexNumeric);
        predicatesAnd = filter.split("&");
        for(String and: predicatesAnd){
            predciatesOr = and.split("\\|\\|");
            for (String or: predciatesOr){
                Matcher matcherNumeric = patternNumeric.matcher(or.trim());
                Matcher matcherString = patternString.matcher(or.trim());
                if(matcherNumeric.find()){
                    if(matcherNumeric.group(1) == null)
                        throw new ParseException("incorrect filter",1);
                } else if(matcherString.find()){
                    if(matcherString.group(1) == null)
                    throw new ParseException("incorrect filter",1);
                } else throw new ParseException("incorrect filter",1);
            }
        }
    }

    /**
     * method for checking parenthesis in filter
     * @param filter
     * @return true if correct*/
    private static boolean checkParenthesis(String filter){
        int openParen = 0;
        int closeParen = 0;
        for(int i = 0; i < filter.length(); ++i){
            if(filter.charAt(i) == '(') ++openParen;
            else if(filter.charAt(i) == ')') ++closeParen;
        }
        return closeParen == openParen;
    }

    /**
     * method for finding of end of single filter
     * @param stat
     * @return int index of end*/
    public static int endIndOfCond(String stat){
        for(int i = 0; i < stat.length(); ++i){
            if(stat.charAt(i) == '&' || stat.charAt(i) == '|' || stat.charAt(i) == ')') return i - 1;
        }
        return stat.length();
    }
}
