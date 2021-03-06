import crudl.model.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.time.Instant;

//
// A memory-based implementation of the service
//
public class CrudlController implements Crudl {
   
    static Map<String,Item> storage = new TreeMap<String,Item>();
    
    public CreateItemResponse createItem(CreateItemRequest req) {
        Item item = req.getEntity();
        String key = item.getId();
        synchronized (storage) {
            if (storage.containsKey(key)) {
                throw new BadRequest("Already exists: " + key);
            }
            item = Item.builder().id(key).modified(Instant.now()).data(item.getData()).build();
            storage.put(key, item);
        }
        return CreateItemResponse.builder().entity(item).build();
    }
    
    public GetItemResponse getItem(GetItemRequest req) {
        String key = req.getId();
        synchronized (storage) {
            if (!storage.containsKey(key)) {
                throw new NotFound("Item not found: " + key);
            }
            Item item = storage.get(key);
            Instant modified = item.getModified();
            Instant since = req.getIfNewer();
            if (since != null) {
                if (modified.isBefore(since)) {
                    throw new NotModified("Item has not been modified");
                }
            }
            return GetItemResponse.builder().entity(item).modified(item.getModified()).build();
        }
    }
    
    public PutItemResponse putItem(PutItemRequest req) {
        Item item = req.getEntity();
        String key = item.getId();
        synchronized (storage) {
            if (!storage.containsKey(key)) {
                throw new NotFound("Item not found: " + key);
            }
            item = Item.builder().id(key).modified(Instant.now()).data(item.getData()).build();
            storage.put(key, item);
            return PutItemResponse.builder().entity(item).build();
        }
    }
    
    public DeleteItemResponse deleteItem(DeleteItemRequest req) {
        String key = req.getId();
        synchronized (storage) {
            if (!storage.containsKey(key)) {
                throw new NotFound("Item not found: " + key);
            }
            storage.remove(key);
            return new DeleteItemResponse();
        }
    }
    
    public ListItemsResponse listItems(ListItemsRequest req) {
        List<Item> lst = new ArrayList<Item>();
        String next = null;
        int count = 0;
        int limit = req.getLimit();
        String skip = req.getSkip();
        for (Map.Entry<String,Item> e : storage.entrySet()) {
            String key = e.getKey();
            if (skip != null) {
                if (!skip.equals(key)) {
                    continue;
                }
                skip = null;
            }
            count++;
            if (count > limit) {
                next = e.getKey();
                break;
            }
            lst.add(e.getValue());
        }
        return ListItemsResponse.builder().entity(ItemListing.builder().items(lst).next(next).build()).build();
    }
    
}
