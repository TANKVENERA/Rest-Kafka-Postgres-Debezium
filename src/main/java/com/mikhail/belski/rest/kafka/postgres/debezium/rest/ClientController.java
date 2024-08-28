package com.mikhail.belski.rest.kafka.postgres.debezium.rest;

import javax.validation.Valid;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.ClientDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.service.ClientService;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class ClientController {

    private ClientService clientService;

    @PostMapping(path = "/client")
    public void publishClient(@Valid @RequestBody final ClientDto clientDto) {
        clientService.publishClient(clientDto);
    }
}
