package br.com.marcosoliveira20.filemanager.messaging;

import br.com.marcosoliveira20.filemanager.events.FileStoredEvent;
import br.com.marcosoliveira20.filemanager.events.WordDetectedEvent;
import br.com.marcosoliveira20.filemanager.service.ParserService;
import br.com.marcosoliveira20.filemanager.service.PublisherService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileStoredListener {

    private final MinioClient minioClient;
    private final ParserService parserService;
    private final PublisherService publisher;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${topics.wordDetected}")
    private String wordDetectedTopic;

    @KafkaListener(topics = "${topics.fileStored}", groupId = "${spring.kafka.consumer.group-id}")
    public void onFileStored(ConsumerRecord<String, String> record) {
        try {
            FileStoredEvent event = mapper.readValue(record.value(), FileStoredEvent.class);
            log.info("Processando arquivo do bucket={}, key={}, userId={}",
                    event.bucket(), event.objectKey(), event.userId());

            byte[] data = readAll(minioClient.getObject(GetObjectArgs.builder()
                    .bucket(event.bucket())
                    .object(event.objectKey())
                    .build()));

            // parse simples
            List<String> words = parserService.parse(data);

            // (opção A) publica 1 evento por ocorrência (maior volume)
            for (String w : words) {
                publisher.publish(wordDetectedTopic,
                        new WordDetectedEvent(w, event.userId()),
                        event.userId());
            }

            // (opção B) agrega por palavra e publica uma só vez por palavra
            /*
            Map<String, Long> counts = words.stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            for (var e : counts.entrySet()) {
                publisher.publish(wordDetectedTopic,
                        Map.of("word", e.getKey(), "userId", event.userId(), "count", e.getValue()),
                        event.userId());
            }
            */

            log.info("Arquivo processado: {} palavras detectadas (userId={})", words.size(), event.userId());
        } catch (Exception e) {
            log.error("Falha ao processar evento de arquivo: {}", e.getMessage(), e);
            // TODO: enviar para DLT ou notificar
        }
    }

    private byte[] readAll(InputStream in) throws Exception {
        try (in; var out = new ByteArrayOutputStream()) {
            in.transferTo(out);
            return out.toByteArray();
        }
    }
}
