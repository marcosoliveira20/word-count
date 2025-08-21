// src/main/java/.../messaging/WordDetectedListener.java
package br.com.marcosoliveira20.english.messaging;

import br.com.marcosoliveira20.english.dto.WordDetectedEvent;
import br.com.marcosoliveira20.english.service.WordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WordDetectedListener {

    private final WordService wordService;          // o mesmo usado pelo seu controller
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${topics.wordDetected}")
    private String topic;

    @KafkaListener(topics = "${topics.wordDetected}", groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(ConsumerRecord<String, String> record) {
        try {
            var event = mapper.readValue(record.value(), WordDetectedEvent.class);
            String word = event.word();
            String userId = event.userId();

            // Se você também enviar count no evento, pode repetir a operação 'count' vezes
            // ou criar um método 'registerUsageByName(word, userId, count)'.
            wordService.registerUsageByName(word, userId);

            log.info("Uso registrado via Kafka: word='{}', userId='{}'", word, userId);
        } catch (Exception e) {
            log.error("Falha ao processar mensagem do tópico {}: {}", topic, e.getMessage(), e);
            // TODO: redirecionar para DLT/retry conforme necessário
        }
    }
}
