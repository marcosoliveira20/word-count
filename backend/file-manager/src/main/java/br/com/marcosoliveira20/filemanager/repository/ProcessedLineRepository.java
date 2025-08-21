package br.com.marcosoliveira20.filemanager.repository;


import br.com.marcosoliveira20.filemanager.model.ProcessedLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedLineRepository extends JpaRepository<ProcessedLine, Long> {
}
