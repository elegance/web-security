package org.orh.demo.web.controller;

import org.orh.demo.domain.Client;
import org.orh.demo.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    @PostMapping(value = "/client")
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        client = clientRepository.save(client);
        return new ResponseEntity<Client>(client, HttpStatus.OK);
    }

    @GetMapping(value = "/client")
    public Iterable<Client> findClient() {
        return clientRepository.findAll();
    }

    @GetMapping(value = "/client/{id}")
    public Client getClient(@PathVariable("id") Long id) {
        return clientRepository.findOne(id);
    }

}
