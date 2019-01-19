package com.boynton.store;

import java.util.List;
import java.util.ArrayList;
import java.util.TreeMap;

public class MemoryStorageProvider implements StorageProvider {
    TreeMap<String,Item> items;
    int seq;

    public MemoryStorageProvider() {
        items = new TreeMap<String,Item>();
        seq = 1;
    }
    private String nextId() {
        int id;
        synchronized (this) {
            id = seq++;
        }
        return String.valueOf(id);
    }

    public void createItem(Item item) {
        item.id = nextId();
        synchronized (items) {
            items.put(item.id, item);
        }
    }

    public Item readItem(String id) {
        synchronized (items) {
            return items.get(id);
        }
    }
   
    public void updateItem(Item newItem) {
        synchronized (items) {
            items.put(newItem.id, newItem);
        }
    }

    public void deleteItem(String id) {
        synchronized (items) {
            items.remove(id);
        }
    }

    public Items listItems(Integer limit, String skip) {
        //TODO implement limit/skip
        Items result = new Items();
        List<Item> lst = new ArrayList<Item>();
        int i = 0;
        synchronized (items) {
            for (Item item : items.values()) {
                lst.add(item);
            }
        }
        return new Items().items(lst);
    }


    void error(String msg) {
        throw new StorageException(msg);
    }
}
