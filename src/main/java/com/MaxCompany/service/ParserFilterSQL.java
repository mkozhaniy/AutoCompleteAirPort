package com.MaxCompany.service;

import com.MaxCompany.entity.AirPort;

import java.text.ParseException;
import java.util.regex.Matcher;

public class ParserFilterSQL extends Filter{
    private static final String[] columns = new String[] {"sdgs", "index", "name", "city",
            "country", "iata", "icao", "x", "y", "firstNumber",
            "secondNumber", "someChar", "ocean", "type", "lastCol"};

    /**
     * method for creating statement for database
     * @param name - name airport
     * @param filter
     * @return String - statement for db*/
    public String createStatement(String name, String filter) throws ParseException {
        name = name.trim();
        filter = filter.trim();
        String result;
        if(!name.equals(""))
            result = "SELECT * FROM ports WHERE UPPER(name) LIKE UPPER('" + name + "%') AND ";
        else
            result = "SELECT * FROM ports WHERE ";
        if(!filter.equals("")){
            isCorrect(filter);
            Matcher m = patternCol.matcher(filter);
            while(m.find()){
                int col = AirPort.parseInt(m.group(1));
                filter = filter.replaceAll("column\\[" + col + "]", columns[col]);
            }
            filter = filter.replaceAll("&", " AND ");
            filter = filter.replaceAll("\\|\\|", " OR ");

            result += filter + ";";
            return  result;
        } else{
            return "SELECT * FROM ports WHERE UPPER(name) LIKE UPPER('" + name + "%');";
        }
    }

}
