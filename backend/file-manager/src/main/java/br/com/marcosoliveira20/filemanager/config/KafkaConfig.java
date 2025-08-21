package br.com.marcosoliveira20.filemanager.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;

import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrap;

    @Value("${topics.fileStored}")
    private String fileStoredTopic;

    @Value("${topics.wordDetected}")
    private String wordDetectedTopic;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(
                Map.of(
                        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap,
                        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class
                )
        );
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // Cria os tópicos se não existirem (útil para dev)
    @Bean
    public NewTopic topicFileStored() {
        return TopicBuilder.name(fileStoredTopic).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic topicWordDetected() {
        return TopicBuilder.name(wordDetectedTopic).partitions(1).replicas(1).build();
    }
}
