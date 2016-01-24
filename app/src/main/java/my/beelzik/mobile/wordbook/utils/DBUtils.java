package my.beelzik.mobile.wordbook.utils;

import java.util.Collection;

/**
 * Created by Andrey on 06.01.2016.
 */
public class DBUtils {


    public static String getSqlNumberSet(Collection<? extends Number> numbers){

        StringBuilder sb = new StringBuilder();

        sb.append("(");

        int i = 0;
        for (Number number: numbers) {
            sb.append(number);
            if(i < numbers.size() - 1){
                sb.append(",");
            }
            i++;

        }
        /*for (int i = 0; i < numbers.length; i++){
            sb.append(numbers[i]);
            if(i < numbers.length - 1){
                sb.append(",");
            }

        }*/
        sb.append(")");
        return sb.toString();
    }

    public static String getSqlNumberSet(Number[] numbers){

        StringBuilder sb = new StringBuilder();

        sb.append("(");
        for (int i = 0; i < numbers.length; i++){
            sb.append(numbers[i]);
            if(i < numbers.length - 1){
                sb.append(",");
            }

        }
        sb.append(")");
        return sb.toString();
    }
}
