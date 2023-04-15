package com.MaxCompany.service;

import com.MaxCompany.entity.AirPort;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mdkt.compiler.InMemoryJavaCompiler;
import java.lang.reflect.Method;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ParserFilterStream extends Filter {
    private String name;
    private String filter;
    private static final String[] columns = new String[] {"sdgs", "arrNum[0]", "arrStr[0]",
            "arrStr[1]", "arrStr[2]", "arrStr[3]", "arrStr[4]", "arrNum[1]", "arrNum[2]",
            "arrNum[3]", "arrNum[4]", "arrStr[5]", "arrStr[6]", "arrStr[7]", "arrStr[8]"};

    /**
     * method for getting predicate from name of airport and filters
     * by compiling string code in
     * <a href="https://github.com/trung/InMemoryJavaCompiler">InMemoryJavaCompiler </a>
     * @throws Exception - for parse or compile exceptions
     * @return Predicate which is equals to string filters*/
    public Predicate<AirPort> getFilterPredicate() throws Exception {
        Predicate<AirPort> namePredicate =
                airPort -> airPort.arrStr[0].toUpperCase().startsWith(name.toUpperCase());
        Predicate<AirPort> filterPredicate = airPort -> true;
        if(!filter.equals("")){
            String variable = "airPort";
            //checking the correctness of the filter
            isCorrect(filter);

            String code = "package com.MaxCompany;\nimport com.MaxCompany.entity.AirPort;\n";
            code += "import java.util.function.Predicate;\nimport java.lang.NullPointerException;\n";
            code += "public class PredClass {\n";
            code +="public Predicate<AirPort> getPred(){\n";
            code += "return " + variable + " -> {try{ return " + replaceString(filter, variable) + ";}" +
                    "catch (NullPointerException e) {return false;}};\n}}";
            Class<?> predCLass = InMemoryJavaCompiler.newInstance().compile("com.MaxCompany.PredClass", code);
            Method method = predCLass.getDeclaredMethod("getPred");
            method.setAccessible(true);
            filterPredicate = (Predicate<AirPort>) method.invoke(predCLass.newInstance());

        }
        return namePredicate.and(filterPredicate);
    }

    /**
     * method for replacing filters and operators with
     * entity fields and equivalent operators in java
     * @param filter
     * @param variable - string name of variable in predicate
     * @return String replaced filter*/
    private String replaceString(String filter, String variable){
        StringBuilder newFilter = new StringBuilder();
        for(int i = 0; i < filter.length(); ++i) {
            if (filter.substring(i).startsWith("column")) {
                String subStat = filter.substring(i);
                int end = endIndOfCond(subStat);
                subStat = subStat.substring(0, end);

                if(subStat.matches(regexString)){
                    //replacing single quotes at the end and start single filter
                    Matcher matcher = Pattern.compile(regexString).matcher(subStat);
                    matcher.find();
                    String value = matcher.group(9);
                    if(value != null)
                        subStat = subStat.replace(value, "\"" +
                                value.substring(1,value.length() - 1) + "\"");

                    if(subStat.matches(".*=.*")) {
                        subStat = subStat.replaceAll("\\s*=\\s*", ".equals(") + ")";
                    } else if(subStat.matches(".*<>.*")){
                        subStat = "!" +subStat.replaceAll("\\s*<>\\s*", ".equals(") + ")";
                    } else if(subStat.matches(".*<.*")){
                        subStat = subStat.replaceAll("\\s*<\\s*", ".compareTo(") +")";
                        subStat += " < 0";
                    } else {
                        subStat = subStat.replaceAll("\\s*>\\s*", ".compareTo(") +")";
                        subStat += " > 0";
                    }
                } else {
                    subStat = subStat.replaceAll("=", "==");
                    subStat = subStat.replaceAll("<>", "!=");
                }

                Matcher m = patternCol.matcher(subStat);
                m.find();
                int col = AirPort.parseInt(m.group(1));
                subStat =  subStat.replaceAll("column\\[" + col + "]",
                        variable+ "." + columns[col]);

                newFilter.append(subStat);
                i += end - 1;
            } else {
                newFilter.append(filter.charAt(i));
            }
        }
        return newFilter.toString().replaceAll("&", "&&");
    }

}
