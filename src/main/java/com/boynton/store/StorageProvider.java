package com.boynton.store;

public interface StorageProvider {

    public void createItem(Item item);

    public Item readItem(String id);
   
    public void updateItem(Item newItem);

    public void deleteItem(String id);

    public Items listItems(Integer limit, String skip);

}
