package com.example.foryaphoto.data;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Aleksandr Karpachev
 *         Created on 26.05.18
 */

public class RequestManager {

    public static RequestManager getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    private static class SingletonHolder {
        private static final RequestManager HOLDER_INSTANCE = new RequestManager();
    }

    private Set<Integer> mRequestQueue = new HashSet<>();

    synchronized public boolean add(int numberPhoto) {
        return mRequestQueue.add(numberPhoto);
    }

    synchronized public void remove(int numberPhoto) {
        mRequestQueue.contains(numberPhoto);
    }
}
