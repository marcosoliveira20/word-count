package br.com.marcosoliveira20.filemanager.controller;


import br.com.marcosoliveira20.filemanager.events.FileStoredEvent;
import br.com.marcosoliveira20.filemanager.service.PublisherService;
import br.com.marcosoliveira20.filemanager.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;
    private final PublisherService publisherService;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> upload(@RequestPart("file") MultipartFile file,
                                    @AuthenticationPrincipal Jwt jwt) throws Exception {
        String userId = jwt.getSubject();
        String contentType = (file.getContentType() != null ? file.getContentType() : "text/plain");
        String objectKey = uploadService.store(file, userId);

        // publica evento para o próprio serviço consumir
        FileStoredEvent event = new FileStoredEvent(
                uploadService.getBucket(),
                objectKey,
                userId,
                contentType
        );

        publisherService.publish(uploadService.getFileStoredTopic(), event, userId);

        return ResponseEntity.accepted().body(
                String.format("Upload recebido: %s. Evento publicado para processamento.", StringUtils.getFilename(objectKey))
        );
    }
}