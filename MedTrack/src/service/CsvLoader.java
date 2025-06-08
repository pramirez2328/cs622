package service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CsvLoader<T> {
    public List<T> load(String filename, CsvParser<T> parser) throws IOException {
        return Files.readAllLines(Paths.get(filename)).stream()
                .map(String::trim)
                .filter(line -> !line.isEmpty() && !line.startsWith("#")) // optional: ignore blank/comment lines
                .map(parser::parse)
                .toList();
    }
}

