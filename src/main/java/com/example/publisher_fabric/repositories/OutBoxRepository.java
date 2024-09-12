package com.example.publisher_fabric.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.publisher_fabric.entities.Outbox;

@Repository
public interface OutBoxRepository extends JpaRepository<Outbox, Integer> {

	List<Outbox> findByIsProccessed(Boolean isProccessed);
}
