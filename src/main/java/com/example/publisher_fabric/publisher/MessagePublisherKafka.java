package com.example.publisher_fabric.publisher;

import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import com.example.publisher_fabric.service.PollOutBoxService;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@AllArgsConstructor
@Component
public class MessagePublisherKafka implements IMessgaePublisher {

	private final KafkaTemplate<String, String> kafkaTemplate;

//	public MessagePublisherKafka(KafkaTemplate<String, String> kafkaTemplate) {
//		this.kafkaTemplate = kafkaTemplate;
//	}

	@Override
	public void publish(String topic, String payload) {

		CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, payload);

		future.whenComplete((result, ex) -> {
			if (ex != null) {
				log.error("Error sending message to Kafka: payload" + payload + ", message: " + ex.getMessage());
			} else {
				log.info("Message sent successfully: " + result.getProducerRecord().value());
			}
		});

	}

}
