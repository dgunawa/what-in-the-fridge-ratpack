package com.maskapai.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class Refrigerator {
    private final static Logger LOGGER = LoggerFactory.getLogger(Refrigerator.class);

    private String id;
    private Map<String, Integer> items;

    private final int MAX_SODA = 12;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Integer> getItems() {
        return items;
    }

    public void setItems(Map<String, Integer> items) {
        this.items = items;
    }

    public Integer getItem(String itemName) {
        Integer currCount = items.get(itemName) == null ? 0 : items.get(itemName);
        return currCount;
    }

    public Integer addItem(String itemName, Integer count) {
        Integer currCount = items.get(itemName) == null ? 0 : items.get(itemName);
        int newCount = currCount + count;
        if (newCount > MAX_SODA) {
            newCount = MAX_SODA;
        }
        items.put(itemName, newCount);
        return newCount;
    }

    public Integer replaceItem(String itemName, Integer count) {
        if (count > MAX_SODA) {
            count = MAX_SODA;
        }
        items.put(itemName, count);
        return count;
    }

    public Integer removeItem(String itemName, Integer count) {
        Integer currCount = items.get(itemName);
        if (currCount != null) {
            if (currCount > count) {
                int newCount = currCount - count;
                items.put(itemName, newCount);
                return newCount;
            } else {
                items.remove(itemName);
                return 0;
            }
        } else {
            return 0;
        }
    }

    public Refrigerator(String id, Map<String, Integer> itemMap) {
        this.id = id;
        this.items = itemMap;
    }
}
