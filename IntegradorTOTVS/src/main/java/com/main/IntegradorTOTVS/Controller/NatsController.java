package com.main.IntegradorTOTVS.Controller;

import com.main.IntegradorTOTVS.Service.NatsMessagingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/proveedores")
public class NatsController {

    private final NatsMessagingService natsService;

    public NatsController(NatsMessagingService natsService) {
        this.natsService = natsService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> obtenerUsuario(@PathVariable String id) {
        try {
            String datos = natsService.solicitarDatosUsuario(id);
            return ResponseEntity.ok(datos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}

