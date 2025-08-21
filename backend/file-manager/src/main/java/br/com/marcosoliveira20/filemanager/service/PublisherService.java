package br.com.marcosoliveira20.filemanager.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PublisherService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public void publish(String topic, Object payload, String key) {
        String json = objectMapper.writeValueAsString(payload);
        kafkaTemplate.send(topic, key, json);
    }
}