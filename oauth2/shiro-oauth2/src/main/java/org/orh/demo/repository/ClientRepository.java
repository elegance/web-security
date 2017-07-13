package org.orh.demo.repository;

import org.orh.demo.domain.Client;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ClientRepository extends CrudRepository<Client, Long> {

    @Query("select id, clientId, clientName, clientSecret from Client where clientId = :clientId")
    Client findByClientId(@Param("clientId") String clientId);
}
