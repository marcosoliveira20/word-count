package br.com.marcosoliveira20.english.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class WordDetailsDTO {
    private String name;
    private int usedTimes;
    @Nullable
    private LocalDateTime firstUse;
    @Nullable private LocalDateTime lastUse;
}
