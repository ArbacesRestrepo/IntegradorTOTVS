package com.main.IntegradorTOTVS.Controller;

import com.main.IntegradorTOTVS.Service.NatsListenerService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.MediaType;


@RestController
@RequestMapping("/api/clientes")
public class ClientesController {
    private final NatsListenerService nats;
    
    public ClientesController(NatsListenerService nats) {
        this.nats = nats;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerClientes() {
        try {
            // Se envía payload vacío porque el SP no recibe parámetros
            String respuesta = nats.request("clientes.consultar", "");
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }   

}
