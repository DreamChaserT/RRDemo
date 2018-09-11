import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RRLogic<T> {

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private int currentIndex = -1;// 上一次选择的服务器
    private int currentWeight = 0;// 当前调度的权值
    private int maxWeight = 0; // 最大权重
    private int gcdWeight = 0; //所有服务器权重的最大公约数
    private int serverCount = 0; //服务器数量
    private List<RRBean<T>> serverList = new ArrayList<>(); //服务器集合
    private static Logger log = Logger.getLogger("RRLogic");

    private static void err(String msg) {
        log.log(Level.WARNING, msg);
    }

    public static class RRBean<T> {
        private T one;
        private int weight;

        public RRBean(T one, int weight) {
            if (weight < 0) {
                weight = 0;
                err("weight<0???");
            }
            this.one = one;
            this.weight = weight;
        }

        public T getOne() {
            return one;
        }

        public void setOne(T one) {
            this.one = one;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }
    }

    public List<RRBean<T>> getList() {
        return serverList;
    }

    public void refresh() {
        currentIndex = -1;
        currentWeight = 0;
        serverCount = serverList.size();
        maxWeight = getMaxWeightForServers(serverList);
        gcdWeight = getGCDForServers(serverList);
    }

    public void add(RRBean<T> one) {
        lock.writeLock().lock();
        try {
            serverList.add(one);
            refresh();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean del(RRBean<T> one) {
        lock.writeLock().lock();
        try {
            boolean res = serverList.remove(one);
            refresh();
            return res;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 返回最大公约数
     *
     * @param a
     * @param b
     * @return
     */
    private static int gcd(int a, int b) {
        BigInteger b1 = new BigInteger(String.valueOf(a));
        BigInteger b2 = new BigInteger(String.valueOf(b));
        BigInteger gcd = b1.gcd(b2);
        return gcd.intValue();
    }


    /**
     * 返回所有服务器权重的最大公约数
     *
     * @param serverList
     * @return
     */
    private int getGCDForServers(List<RRBean<T>> serverList) {
        int w = 0;
        for (int i = 0, len = serverList.size(); i < len - 1; i++) {
            if (w == 0) {
                w = gcd(serverList.get(i).weight, serverList.get(i + 1).weight);
            } else {
                w = gcd(w, serverList.get(i + 1).weight);
            }
        }
        return w;
    }


    /**
     * 返回所有服务器中的最大权重
     *
     * @param serverList
     * @return
     */
    public int getMaxWeightForServers(List<RRBean<T>> serverList) {
        if (1 == serverList.size()) {
            return serverList.get(0).weight;
        }
        int w = 0;
        for (int i = 0, len = serverList.size(); i < len - 1; i++) {
            if (w == 0) {
                w = Math.max(serverList.get(i).weight, serverList.get(i + 1).weight);
            } else {
                w = Math.max(w, serverList.get(i + 1).weight);
            }
        }
        return w;
    }

    /**
     * 算法流程：
     * 假设有一组服务器 S = {S0, S1, …, Sn-1}
     * 有相应的权重，变量currentIndex表示上次选择的服务器
     * 权值currentWeight初始化为0，currentIndex初始化为-1 ，当第一次的时候返回 权值取最大的那个服务器，
     * 通过权重的不断递减 寻找 适合的服务器返回，直到轮询结束，权值返回为0
     */
    public synchronized RRBean<T> next() {
        lock.readLock().lock();
        try {
            while (true) {
                currentIndex = (currentIndex + 1) % serverCount;
                if (currentIndex == 0) {
                    currentWeight = currentWeight - gcdWeight;
                    if (currentWeight <= 0) {
                        currentWeight = maxWeight;
                        if (currentWeight == 0)
                            return null;
                    }
                }
                if (serverList.get(currentIndex).weight >= currentWeight) {
                    return serverList.get(currentIndex);
                }
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    public T nextValue() {
        return next().getOne();
    }
}
