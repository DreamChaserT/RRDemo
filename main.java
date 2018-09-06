import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class main {
    public void init() {
        RRLogic.RRBean<String> s1 = new RRLogic.RRBean<>("192.168.0.103", 3);//3
        RRLogic.RRBean<String> s2 = new RRLogic.RRBean<>("192.168.0.102", 2);//2
        RRLogic.RRBean<String> s3 = new RRLogic.RRBean<>("192.168.0.104", 4);//6
        RRLogic.RRBean<String> s4 = new RRLogic.RRBean<>("192.168.0.106", 6);//4
        RRLogic.RRBean<String> s5 = new RRLogic.RRBean<>("192.168.0.101", 1);//1
        ArrayList serverList = new ArrayList<RRLogic.RRBean<String>>();
        serverList.add(s1);
        serverList.add(s2);
        serverList.add(s3);
        serverList.add(s4);
        serverList.add(s5);


    }


    public static void main(String[] args) {
        RRLogic<String> obj = new RRLogic<>();


        RRLogic.RRBean<String> s1 = new RRLogic.RRBean<>("192.168.0.103", 3);//3
        RRLogic.RRBean<String> s2 = new RRLogic.RRBean<>("192.168.0.102", 2);//2
        RRLogic.RRBean<String> s3 = new RRLogic.RRBean<>("192.168.0.104", 4);//6
        RRLogic.RRBean<String> s4 = new RRLogic.RRBean<>("192.168.0.106", 6);//4
        RRLogic.RRBean<String> s5 = new RRLogic.RRBean<>("192.168.0.101", 1);//1


        obj.add(s1);
        obj.add(s2);
        obj.add(s3);
        obj.add(s4);
        obj.add(s5);

        Map<String, Integer> countResult = new HashMap<String, Integer>();

        new Thread(() -> {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 100000; i++) {
                RRLogic.RRBean<String> s = obj.next();
                String log = "ip:" + s.getOne() + ";weight:" + s.getWeight();
                if (countResult.containsKey(log)) {
                    countResult.put(log, countResult.get(log) + 1);
                } else {
                    countResult.put(log, 1);
                }
                System.out.println(log);
            }

            for (Map.Entry<String, Integer> map : countResult.entrySet()) {
                System.out.println("服务器1 " + map.getKey() + " 请求次数： " + map.getValue());
            }
            long end = System.currentTimeMillis();
            System.out.println("server 1 "+(end-start));

        }).start();

        new Thread(() -> {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 100000; i++) {
                RRLogic.RRBean<String> s = obj.next();
                String log = "ip:" + s.getOne() + ";weight:" + s.getWeight();
                if (countResult.containsKey(log)) {
                    countResult.put(log, countResult.get(log) + 1);
                } else {
                    countResult.put(log, 1);
                }
                System.out.println(log);
            }

            for (Map.Entry<String, Integer> map : countResult.entrySet()) {
                System.out.println("服务器2 " + map.getKey() + " 请求次数： " + map.getValue());
            }
            long end = System.currentTimeMillis();
            System.out.println("server 2 "+(end-start));
        }).start();


    }
}
