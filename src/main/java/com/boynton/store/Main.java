package com.boynton.store;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

public class Main {

    public static String BASE_URI = "http://localhost:8080/";

    public static Server startServer(StorageProvider provider) throws Exception {
        URI baseUri = UriBuilder.fromUri(BASE_URI).build();
        ResourceConfig config = new ResourceConfig(Resources.class).register(new Binder(provider));
        Server server = JettyHttpContainerFactory.createServer(baseUri, config);
        
        server.start();
        System.out.println(String.format("Service started at %s", BASE_URI));
        
        return server;
    }

    static class Binder extends AbstractBinder {
        StorageProvider provider;

        Binder(StorageProvider provider) {
            this.provider = provider;
        }

        @Override
        protected void configure() {
            bind(provider).to(StorageProvider.class);
        }
    }
    public static void main(String[] args) {
        try {
            Server server = startServer(new MemoryStorageProvider());
            server.join();
        } catch (Exception e) {
            System.err.println("*** " + e);
        }
    }

}

