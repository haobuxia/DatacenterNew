import java.util.HashMap;
import java.util.Map;

/**
 * @author liulele 2019/10/15
 * @version 0.1
 **/
public class map {
    public static void main(String[] args) {
        Map map = new HashMap<>();
        map.put(1,2);
        for (Object o : map.keySet()) {
            System.out.println(o);
        }
    }
}
