import crudl.model.*;
import crudl.client.*;

public class Test implements ClientConfig {

    public void run() {
        CrudlClient client = new CrudlClient(this);
        ListItemsResponse listingResponse = client.listItems(ListItemsRequest.builder().build());
        System.out.println("-> " + Util.pretty(listingResponse));

        for (int i=0; i<10; i++) {
            CreateItemResponse createdResponse = client.createItem(CreateItemRequest.builder().
                                                                   item(Item.builder()
                                                                        .id("item" + i)
                                                                        .data("Item " + i + "!")
                                                                        .build())
                                                                   .build());
        }
        System.out.println("10 items CREATED:");
        listingResponse = client.listItems(ListItemsRequest.builder().build());
        System.out.println("-> " + Util.pretty(listingResponse));

        listingResponse = client.listItems(ListItemsRequest.builder().limit(5).build());
        System.out.println("-> " + Util.pretty(listingResponse));
        String next = listingResponse.getItems().getNext();
        if (next != null) {
            System.out.println("Paginated! Here is the rest:");
            System.out.println(Util.pretty(client.listItems(ListItemsRequest.builder().limit(5).skip(next).build())));
        }

    }

    public static void main(String[] args) {
        new Test().run();
    }
    @Override
    public String getTarget() {
        return "http://localhost:8080/";
    }
    
}
