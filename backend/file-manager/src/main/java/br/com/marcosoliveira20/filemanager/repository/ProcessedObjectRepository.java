package br.com.marcosoliveira20.filemanager.repository;


import br.com.marcosoliveira20.filemanager.model.ProcessedObject;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProcessedObjectRepository extends JpaRepository<ProcessedObject, Long> {
    Optional<ProcessedObject> findByObjectKeyAndEtag(String objectKey, String etag);
}
