package com.boynton.store;

import org.eclipse.jetty.server.Server;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.client.Entity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;

import java.util.Map;
import java.util.HashMap;
import java.net.URLEncoder;

public class StoreTest {

    private static WebTarget target = null;

    @Before
    public void setUp() throws Exception {
        synchronized (StoreTest.class) {
            if (target == null) {
                Server server = Main.startServer(new MemoryStorageProvider());
                Client c = ClientBuilder.newClient();
                target = c.target(Main.BASE_URI);
            }
        }
    }

    @After
    public void tearDown() throws Exception {
    }

    Items listItems(Integer limit, String skip) {
        return target.path("store/items/").request().get(Items.class);
    }

    Item getItem(String id) {
        return target.path("store/items/" + id).request().get(Item.class);
    }

    Item updateItem(String id, Item item) {
        return target.path("store/items/" + id).request().put(Entity.entity(item, MediaType.APPLICATION_JSON), Item.class);
    }

    Item createItem(Item item) {
        return target.path("store/items").request().post(Entity.entity(item, MediaType.APPLICATION_JSON), Item.class);
    }

    void deleteItem(String id) {
        target.path("store/items/" + id).request().delete();
    }


    //JSON test
    @Test
    public void testLifecycle() {
        //1. empty to start with
        Items result = listItems(null, null);
        assertNotNull(result.items);
        assert(result.items.size() == 0);
        System.out.println("empty items,as expected!");

        Item i1 = createItem(new Item().name("Joe"));
        assertEquals("Joe", i1.name);
        assertNotNull(i1.id);

        Item i2 = createItem(new Item().name("Bob"));
        assertEquals("Bob", i2.name);
        assertNotNull(i2.id);
        assert(!i2.id.equals(i1.id));

        result = listItems(null, null);
        assertNotNull(result.items);
        assert(result.items.size() == 2);
        int recognized = 0;
        for (Item ii : result.items) {
            assertNotNull(i1.id);
            assertNotNull(i2.id);
            if (ii.id.equals(i1.id) || ii.id.equals(i2.id)) {
                recognized++;
            }
        }
        System.out.println("recognized: " + recognized);
        assertEquals(recognized, 2);
        
        Item i = getItem(i2.id);
        assertNotNull(i);
        assertNotNull(i.id);
        assertEquals(i.id, i2.id);
        assertEquals(i.name, i2.name);
        
        i.name = "Bill";
        i = updateItem(i.id, i);
        assertNotNull(i);
        assertNotNull(i.id);
        assertEquals(i.id, i2.id);
        assertEquals(i.name, "Bill");
        
        deleteItem(i.id);

        result = listItems(null, null);
        assertNotNull(result.items);
        assert(result.items.size() == 1);
    }


    /*
    //GraphQL test
    @Test
    public void testGQL() {
    String json = target.path("myresource/gql").queryParam("q", encoded("{hello}")).request().get(String.class);
    Map<String,Object> result = new HashMap<String,Object>();
    result = fromJSON(json, result.getClass());
    assertNotNull(result);
    try {
    result = (Map<String,Object>) result.get("data");
    String s = (String)result.get("hello");
    assertEquals("world", s);
    } catch (Exception e) {
    fail("Bad result from GraphQL: " + e.getMessage());
    }
    }

    String encoded(String s) {
    try {
    return URLEncoder.encode(s);
    } catch (Exception e) {
    return "X";
    }
    }

    public <T> T fromJSON(String jsonData, Class<T> dataType) {
    try {
    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return mapper.reader(dataType).readValue(jsonData);
    } catch (Exception e) {
    e.printStackTrace();
    return null;
    }
    }
    */
}
