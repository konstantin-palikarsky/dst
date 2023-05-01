package dst.ass2.ioc.lock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class LockManager {
    private static final Map<String, ReentrantLock> lockMap = new ConcurrentHashMap<>();


    private LockManager() {

    }


    public static synchronized void lock(String name) {
        if (lockMap.containsKey(name)) {
            lockMap.get(name).lock();
        } else {
            var newLock = new ReentrantLock();
            lockMap.put(name, newLock);
            newLock.lock();
        }

    }

    public static void unlock(String name) {
        if (lockMap.containsKey(name)) {
            lockMap.get(name).unlock();
            lockMap.remove(name);
        }
    }


}
