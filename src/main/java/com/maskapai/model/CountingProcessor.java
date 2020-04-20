package com.maskapai.model;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class CountingProcessor {
    private final static Logger LOGGER = LoggerFactory.getLogger(CountingProcessor.class);

    Map<String, Refrigerator> refrigerators = new Hashtable<>();

    public CountingProcessor() {
        // In real world this will be coming from persistent storage ie: DB
        Map<String, Integer> itemMap1 = new Hashtable<>();
        itemMap1.put("soda", 3);
        itemMap1.put("pizza", 2);
        refrigerators.put("1", new Refrigerator("1", itemMap1));
        Map<String, Integer> itemMap2 = new Hashtable<>();
        itemMap2.put("soda", 5);
        itemMap2.put("milk", 2);
        itemMap2.put("meat", 3);
        refrigerators.put("2", new Refrigerator("2", itemMap2));
        Map<String, Integer> itemMap3 = new Hashtable<>();
        itemMap3.put("soda", 5);
        itemMap3.put("milk", 2);
        itemMap3.put("meat", 3);
        refrigerators.put("3", new Refrigerator("3", itemMap3));
    }

    public Boolean isId(String id) {
        if (getId(id) == null) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean isItem(String id, String itemName) {
        if (isId(id) && getId(id).getItem(itemName) > 0) {
            return true;
        } else {
            return false;
        }
    }

    public String getItem(String id, String itemName) {
        Integer itemCount = getId(id).getItem(itemName);
        JSONObject refrigeratorJson = new JSONObject();
        JSONObject itemJson = new JSONObject();
        itemJson.put(itemName, itemCount);
        refrigeratorJson.put("refrigerator", id).put("item", itemJson);
        return refrigeratorJson.toString();
    }

    public String postItem(String id, String itemName, String itemCount) {
        getId(id).addItem(itemName, Integer.parseInt(itemCount));
        return getItem(id, itemName);
    }

    public String putItem(String id, String itemName, String itemCount) {
        getId(id).replaceItem(itemName, Integer.parseInt(itemCount));
        return getItem(id, itemName);
    }

    public String deleteItem(String id, String itemName, String itemCount) {
        getId(id).removeItem(itemName, Integer.parseInt(itemCount));
        return getItem(id, itemName);
    }

    public Refrigerator getId(String id) {
        return refrigerators.get(id);
    }

    public Refrigerator deleteId(String id) {
        refrigerators.remove(id);
        return getId(id);
    }

    public void postItemJson(String id, String itemsJson) {
        JSONObject jsonObject = new JSONObject(itemsJson);
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            postItem(id, key, jsonObject.getString(key));
        }
    }

    public void putItemJson(String id, String itemsJson) {
        JSONObject jsonObject = new JSONObject(itemsJson);
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            putItem(id, key, jsonObject.getString(key));
        }
    }

    public void deleteItemJson(String id, String itemsJson) {
        JSONObject jsonObject = new JSONObject(itemsJson);
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            deleteItem(id, key, jsonObject.getString(key));
        }
    }
}
