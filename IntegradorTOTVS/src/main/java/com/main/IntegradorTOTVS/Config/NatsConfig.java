package com.main.IntegradorTOTVS.Config;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NatsConfig {
    @Value("${nats.server.url}")
    
    private String serverUrl;

    @Bean(destroyMethod = "close")
    public Connection natsProveedoresConnection() throws Exception {
        Options options = new Options.Builder()
                .server(serverUrl)
                .build();
        return Nats.connect(options);
    }

}

