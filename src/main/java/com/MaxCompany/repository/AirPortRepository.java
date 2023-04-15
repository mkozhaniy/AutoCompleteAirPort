package com.MaxCompany.repository;

import com.MaxCompany.entity.AirPort;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class AirPortRepository {
    private final Connection connection;

    public AirPortRepository(Connection connection){
        this.connection = connection;
    }

    /**
     * consumer for entities which transform
     * it in insert SQL statement and execute */
    class Filler implements Consumer<AirPort> {
        private final String insert = "INSERT INTO ports (index, name, city," +
                " country, iata, icao, x, y," +
                " firstNumber, secondNumber, someChar, ocean, type, last) " +
                "VALUES ";

        @Override
        public void accept(AirPort airPort) {
            StringBuilder sb = new StringBuilder();
            sb.append(insert + "(");
            String stat;
            sb.append(airPort.arrNum[0]+ ", ");
            int i ;
            for(i = 0; i < 5; ++i) sb.append("'" + airPort.arrStr[i]
                    .replaceAll("'", "''")).append("', ");
            for(i = 1; i < 5; ++i) sb.append(airPort.arrNum[i] == null ? "NULL" : airPort.arrNum[i]).append(", ");
            for(i = 5; i < 8; ++i) sb.append("'" + airPort.arrStr[i]
                    .replaceAll("'", "''")).append("', ");

            sb.append("'"+ airPort.arrStr[8].replaceAll("'", "''") + "');");
            stat = sb.toString();
            try {
                connection.prepareStatement(stat).execute();
            } catch (SQLException e){
                System.out.println(e.getMessage());
            }
        }
    }

    /**method for creating and filling table
     * @param sortedEntities - stream of sorted entities AirPort*/
    public void createTable(Stream<AirPort> sortedEntities) throws SQLException, IOException{
        ResultSet rs = connection.getMetaData().getTables(null,
                null,
                null,
                new String[] {"TABLE"});
        while(rs.next()){
            if (rs.getString(3).equals("ports")){
                return;
            }
        }
        PreparedStatement statement = connection.prepareStatement("CREATE TABLE ports (" +
                "index int," +
                "name varchar(256), " +
                "city varchar(256), " +
                "country varchar(256), " +
                "iata varchar(256) , " +
                "icao varchar(256), " +
                "x real, " +
                "y real , " +
                "firstNumber int, " +
                "secondNumber real, " +
                "someChar varchar(20), " +
                "ocean varchar(256), " +
                "type varchar(256), " +
                "last varchar(256)," +
                "primary key (index));");
        statement.execute();

        //inserting data
        sortedEntities.forEach(new Filler());
    }

    /**method for executing statement and presenting data
     * @param statement String SQL statement*/
    public void getResult(String statement) throws SQLException{
        PreparedStatement stat = connection.prepareStatement(statement);
        Long start = System.currentTimeMillis();
        ResultSet resultSet = stat.executeQuery();
        Long end = System.currentTimeMillis();
        int count = 0;
        while(resultSet.next()){
            System.out.print(resultSet.getString(2) + "[");
            for(int i = 1; i<13; ++i){
                System.out.print(resultSet.getString(i) + ",");
            }
            System.out.print(resultSet.getString(14) + "]\n");
            ++count;
        }
        System.out.print("Количество найденный строк: " + count +
                " Время затраченное на поиск: " + (end - start) + "ms\n");
    }
}
