package com.main.IntegradorTOTVS.Controller;

import com.main.IntegradorTOTVS.Service.NatsListenerService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.MediaType;


@RestController
@RequestMapping("/api/proveedores")
public class ProveedoresController {
    private final NatsListenerService nats;

    public ProveedoresController(NatsListenerService nats) {
        this.nats = nats;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerProveedores() {
        try {
            // Se envía payload vacío porque el SP no recibe parámetros
            String respuesta = nats.request("proveedores.consultar", "");
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }   
}
