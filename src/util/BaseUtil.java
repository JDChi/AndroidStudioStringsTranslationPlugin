package util;

import java.util.Random;

public class BaseUtil {

    /**
     * 生成随机数
     * @return
     */
    public static String genRandomNum(int length){
        Random random = new Random();

        double process = (1 + random.nextDouble()) * Math.pow(10 , length);
        String processString = String.valueOf(process);
        return  processString.substring(1 , length + 1);
    }
}
