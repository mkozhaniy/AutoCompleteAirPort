package com.MaxCompany;

import com.MaxCompany.entity.AirPort;
import com.MaxCompany.repository.AirPortRepository;
import com.MaxCompany.service.Mapper;
import com.MaxCompany.service.ParserFilterStream;
import com.MaxCompany.service.ParserFilterSQL;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Кожуховский Максим
 * mail: maka.kozh65@gmail.com
 * */

public class Main {

    private static final String CSVPATH = "airports.csv";

    public static void main(String[] args) throws IOException, SQLException, InterruptedException {
        String filters;
        String name;

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader csvInput = new BufferedReader(new InputStreamReader(Main
                .class.getClassLoader().getResourceAsStream(CSVPATH)));

        System.out.println("once wait and data is prepared");
        //mapping and sorting entities by name of airport
        Stream<AirPort> sortedEntities = csvInput.lines().map(new Mapper())
                .sorted(Comparator.comparing(ap -> ap.arrStr[0]));

        //if you put db in args of cmd, then you get app with database
        if(args.length > 0 && args[0].equals("db")) {
            try (Connection conn = getConnection()) {
                System.out.println("once wait and db is created");

                //initiate database, that is creating table and sorting data on name of airports
                AirPortRepository db = new AirPortRepository(conn);
                db.createTable(sortedEntities);


                ParserFilterSQL parserFiltersSQL = new ParserFilterSQL();
                String statement;
                while (true) {
                    System.out.println("Enter a filter: ");
                    filters = input.readLine();
                    if (filters.equals("!quit")) {
                        return;
                    }
                    System.out.println("Enter a name of airport: ");
                    name = input.readLine();
                    if (name.equals("!quit")) {
                        return;
                    }
                    try {
                        statement = parserFiltersSQL.createStatement(name, filters);
                        db.getResult(statement);
                    } catch (ParseException e){
                        System.out.println(e.getMessage());
                    }
                }
            }
        } else {
            List<AirPort> airports = sortedEntities.collect(Collectors.toList());
            long start;
            long end;
            while(true){
                System.out.println("Enter a filter: ");
                filters = input.readLine();
                if (filters.equals("!quit")) return;
                System.out.println("Enter a name of airport: ");
                name = input.readLine();
                if (name.equals("!quit")) return;

                ParserFilterStream pfs = new ParserFilterStream(name, filters);
                try {
                    Predicate<AirPort> filterPredicate = pfs.getFilterPredicate();
                    start = System.currentTimeMillis();
                    List<AirPort> result =
                            airports.stream().filter(filterPredicate).collect(Collectors.toList());
                    end = System.currentTimeMillis();
                    for(AirPort ap : result){
                        System.out.println(ap);
                    }
                    System.out.print("Количество найденный строк: " + result.size() +
                            " Время затраченное на поиск: " + (end - start) + "ms\n");
                } catch (Exception e){
                    System.out.println(e.getMessage());
                }
            }
        }

    }

    public static Connection getConnection() throws IOException, SQLException{
        Properties prop = new Properties();
        try(InputStream in = ClassLoader.getSystemResource("database.properties").openStream()){
            prop.load(in);
        }
        return DriverManager.getConnection(prop.getProperty("jdbc.url"),
                prop.getProperty("jdbc.username"), prop.getProperty("jdbc.password"));
    }
}
