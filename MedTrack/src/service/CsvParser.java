package service;

@FunctionalInterface
public interface CsvParser<T> {
    T parse(String line);
}
