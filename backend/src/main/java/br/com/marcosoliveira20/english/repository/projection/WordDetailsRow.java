package br.com.marcosoliveira20.english.repository.projection;

import java.time.LocalDateTime;

public interface WordDetailsRow {
    String getName();
    Integer getUsedTimes();
    LocalDateTime getFirstUse();
    LocalDateTime getLastUse();
}
