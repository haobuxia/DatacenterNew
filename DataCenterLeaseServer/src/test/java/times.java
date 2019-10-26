import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @author liulele 2019/10/26
 * @version 0.1
 **/
public class times {
    public static void main(String[] args) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fromDate3 = simpleDateFormat.parse("2018-03-01 00:12:00");
        Date toDate3 = simpleDateFormat.parse("2018-03-01 00:13:03");
        long from3 = fromDate3.getTime();
        long to3 = toDate3.getTime();
        int minutes = (int) ((to3 - from3) / (1000 * 60));
        int sec = (int) ((to3 - from3)/1000 %   60);
        System.out.println((minutes>=10?(""+minutes):("0"+minutes))+":"+(sec>=10?(""+sec):("0"+sec)));

        Random random = new Random();
        System.out.println("Method two:" + random.nextInt(5));
    }
}
