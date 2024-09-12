package com.example.publisher_fabric.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.publisher_fabric.entities.Outbox;
import com.example.publisher_fabric.publisher.IMessgaePublisher;
import com.example.publisher_fabric.publisher.MessagePublisherKafka;
import com.example.publisher_fabric.repositories.OutBoxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
@EnableScheduling
@AllArgsConstructor
@NoArgsConstructor
public class PollOutBoxService {
	@Value("${spring.kafka.topic}")
	private String topic;
	
	
	@Autowired
	OutBoxRepository outBoxRepository;

	@Autowired
	IMessgaePublisher iMessgaePublisher;

	@Scheduled(fixedRateString = "${scheduler.pollingInterval}")
	public void performTask() {
		List<Outbox> outboxs = outBoxRepository.findByIsProccessed(false);
		outboxs.parallelStream().forEach(e -> sendMessageKafka(e));
		log.info("Simple job is running - " + System.currentTimeMillis() / 1000 + ", Length: " + outboxs.size());
	}

	public void sendMessageKafka(Outbox dataOutbox) {
		try {
			FilterProvider filters = new SimpleFilterProvider().addFilter("myFilter",
					SimpleBeanPropertyFilter.filterOutAllExcept("payload", "eventType"));
			ObjectMapper mapper = new ObjectMapper();
			String dataOutboxString = mapper.writer(filters).writeValueAsString(dataOutbox);
			iMessgaePublisher.publish(topic, dataOutboxString);
			dataOutbox.setIsProccessed(true);
			outBoxRepository.save(dataOutbox);
		} catch (Exception e) {
			// TODO: handle exception
			sendMessageKafka(dataOutbox);
			log.error("error sendMessage", e);
		}
	}
}
