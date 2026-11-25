package com.main.IntegradorTOTVS.Service;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Message;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
public class NatsMessagingService {

    private final Connection connection;
    private static final String SUBJECT_CONSULTAS = "usuarios.consultar";

    public NatsMessagingService(Connection connection) {
        this.connection = connection;
    }

    // 1. RESPONDER: Escucha solicitudes y devuelve datos al buzón temporal (replyTo)
    @PostConstruct
    public void iniciarRespondedor() {
        Dispatcher dispatcher = connection.createDispatcher(msg -> {
            String idSolicitado = new String(msg.getData(), StandardCharsets.UTF_8);
            System.out.println("Solicitud recibida para ID: " + idSolicitado);

            // Lógica de negocio o consulta a base de datos simulada
            String respuestaJson = String.format(
                "{\"id\": \"%s\", \"nombre\": \"Usuario Demo\", \"estado\": \"ACTIVO\"}",
                idSolicitado
            );

            // Si el mensaje espera respuesta, publicamos en msg.getReplyTo()
            if (msg.getReplyTo() != null) {
                connection.publish(
                    msg.getReplyTo(),
                    respuestaJson.getBytes(StandardCharsets.UTF_8)
                );
            }
        });

        dispatcher.subscribe(SUBJECT_CONSULTAS);
    }

    // 2. REQUEST: Envía una solicitud y espera la respuesta (con timeout)
    public String solicitarDatosUsuario(String userId) throws Exception {
        Message respuesta = connection.request(
            SUBJECT_CONSULTAS,
            userId.getBytes(StandardCharsets.UTF_8),
            Duration.ofSeconds(3) // Tiempo máximo de espera
        );

        if (respuesta == null) {
            throw new RuntimeException("Tiempo de espera agotado: nadie respondió a la solicitud");
        }

        return new String(respuesta.getData(), StandardCharsets.UTF_8);
    }
}

