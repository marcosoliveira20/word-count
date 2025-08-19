package br.com.marcosoliveira20.english.repository.projection;

import java.time.LocalDate;

public interface WordUsageRow {
    String getName();
    Long getUsed_times();  // alias deve bater com o SQL
    LocalDate getFirst_use();
    LocalDate getLast_use();
}
