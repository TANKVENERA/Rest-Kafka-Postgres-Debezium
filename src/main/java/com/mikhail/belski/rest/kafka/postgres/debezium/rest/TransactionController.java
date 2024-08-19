package com.mikhail.belski.rest.kafka.postgres.debezium.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.TransactionDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.service.TransactionService;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class TransactionController {

    private TransactionService transactionProducerService;

    @PostMapping(path = "/transaction")
    public void publishClient(@RequestBody final TransactionDto transaction) {
        transactionProducerService.publishTransaction(transaction);
    }

}
