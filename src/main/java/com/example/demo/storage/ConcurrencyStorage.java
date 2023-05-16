package com.example.demo.storage;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConcurrencyStorage<T> {
    private final Map<String, T> storage = new ConcurrentHashMap<>();

    public ConcurrencyStorage<T> put(String key, T data) {
        this.storage.put(key, data);
        return this;
    }

    public T get(String key) {
        return this.storage.get(key);
    }

    public List<T> getAllValue(){
        return new ArrayList<>(this.storage.values());
    }
}
