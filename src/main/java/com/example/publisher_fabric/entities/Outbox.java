package com.example.publisher_fabric.entities;

import com.fasterxml.jackson.annotation.JsonFilter;

import jakarta.persistence.Entity;

@Entity
@JsonFilter("myFilter")
public class Outbox extends EventType<String> {


}
