package br.com.marcosoliveira20.filemanager.service;

import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;


import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class ParserService {

    // aceita letras e apóstrofos internos (we'll, don't) — ajuste conforme necessidade
    private static final Pattern SPLIT = Pattern.compile("[^\\p{L}']+");

    public List<String> parse(byte[] data) {
        String text = new String(data, StandardCharsets.UTF_8).toLowerCase(Locale.ROOT);

        String[] parts = SPLIT.split(text);
        List<String> tokens = new ArrayList<>(parts.length);
        for (String p : parts) {
            String w = p.strip();
            if (w.isEmpty()) continue;
            // remove apóstrofo nas pontas (ex.: "'test'" -> "test")
            w = trimApostrophes(w);
            if (!w.isEmpty()) tokens.add(w);
        }
        return tokens;
    }

    private String trimApostrophes(String s) {
        int start = 0, end = s.length();
        while (start < end && s.charAt(start) == '\'') start++;
        while (end > start && s.charAt(end - 1) == '\'') end--;
        return (start == 0 && end == s.length()) ? s : s.substring(start, end);
    }
}