import java.util.HashMap;
import java.util.Map;

/**
 * @author liulele 2019/9/29
 * @version 0.1
 **/
public class demo2 {
    public static void main(String[] args) {
        Map map = new HashMap();
        map.put("aa",11);
        map.put("aa",22);
        for (Object o : map.keySet()) {
            System.out.println(map.get(o));
        }
    }
}
