import cn.hutool.core.date.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author liulele 2019/10/24
 * @version 0.1
 **/
public class demo6 {
    public static void main(String[] args) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse = simpleDateFormat.parse("2019-09-22 11:08:59");
        System.out.println(parse.getTime());
        //1569121739000  1569121739000
    }
}
