import com.alibaba.druid.sql.dialect.h2.visitor.H2ASTVisitor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.SimpleFormatter;

/**
 * @author liulele 2019/10/23
 * @version 0.1
 **/
public class demo5 {
    public static void main(String[] args) {
        Double num = 16.2;

        BigDecimal bigDecimal = new BigDecimal(num);
        num = bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        System.out.println(num);
        double s = 12346;
        long time = 3066;
        double s1 = time/1000.0;
        Map map = new HashMap();
        map.put("key",new Date().getTime());
        map.put("key1",new Date().getTime());
        long s2 = (long)map.get("key");
        long s3 = (long)map.get("key1");
        System.out.println(s3);
        System.out.println(s2);

    }


}
