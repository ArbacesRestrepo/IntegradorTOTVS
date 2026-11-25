package com.main.IntegradorTOTVS.Service;

import com.main.IntegradorTOTVS.Repositorys.ProveedorRepository;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Message;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;


import tools.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;


@Service
public class NatsListenerService {
    private final Connection connection;
    private final ProveedorRepository proveedorRepository;
    private final ObjectMapper objectMapper;


    @Value("${IntegradorTOTVS.GrupoEmpresa}")
    private String grupoEmpresa;

    @Value("${IntegradorTOTVS.Empresa}")
    private String empresa;


    public NatsListenerService(Connection connection, ProveedorRepository proveedorRepository, ObjectMapper objectMapper) {
        this.connection = connection;
        this.proveedorRepository = proveedorRepository;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void registrarListeners() {
        Dispatcher dispatcher = connection.createDispatcher(msg -> {
            String respuesta;

            try {
                switch (msg.getSubject()) {
                    case "proveedores.consultar" -> {
                        List<Map<String, Object>> proveedores = proveedorRepository.buscarProveedores(grupoEmpresa, empresa);
                        respuesta = proveedores.toString();
                        respuesta = objectMapper.writeValueAsString(proveedores);
                    }
                    // Cuando agregues otros repositorios, reutilizas param1 y param2 aquí mismo:
                    // case "clientes.listar" -> respuesta = clienteRepository.listarClientes(param1, param2).toString();
                    default -> respuesta = "Subject no soportado";
                }
            } catch (Exception e) {
                respuesta = "Error consultando BD: " + e.getMessage();
            }

            if (msg.getReplyTo() != null) {
                connection.publish(msg.getReplyTo(), respuesta.getBytes(StandardCharsets.UTF_8));
            }
        });

        dispatcher.subscribe("proveedores.consultar");
    }

    // Método que faltaba para enviar solicitudes y esperar respuesta
    public String request(String subject, String payload) throws Exception {
        Message reply = connection.request(
            subject,
            payload.getBytes(StandardCharsets.UTF_8),
            Duration.ofSeconds(5)
        );
        if (reply == null) {
            throw new RuntimeException("Sin respuesta en el subject: " + subject);
        }
        return new String(reply.getData(), StandardCharsets.UTF_8);
    }
}
