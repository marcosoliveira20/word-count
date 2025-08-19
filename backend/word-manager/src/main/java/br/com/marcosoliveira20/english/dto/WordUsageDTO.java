package br.com.marcosoliveira20.english.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class WordUsageDTO {
    private String name;
    private long used_times;
    private LocalDate first_use;
    private LocalDate last_use;
}
