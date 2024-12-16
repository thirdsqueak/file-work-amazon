package org.example.pars;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import org.example.model.Model;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Processor {


    // Метод для вывода списка популярных товаров
    public List<String> getPopularProducts(List<Model> reviews) {
        // Группировка отзывов по asin (ID товара) и подсчет их количества
        return reviews.stream()
                .map(Model::getAsin) // Извлекаем asin (ID товара)
                .collect(Collectors.groupingBy(as -> as, Collectors.counting())) // Группируем по asin и считаем количество
                .entrySet().stream() // Переходим к отображению
                .sorted((entry1, entry2) -> Long.compare(entry2.getValue(), entry1.getValue())) // Сортируем по количеству
                .map(Map.Entry::getKey) // Извлекаем только asin
                .collect(Collectors.toList()); // Собираем в список
    }

    // Метод для сортировки по рейтингу, учитывая вес отзыва
    public List<Model> getReviewsByRatingWithWeight(List<Model> reviews) {
        return reviews.stream()
                .sorted(Comparator.comparingDouble((Model review) -> {
                    // Ensure overall is a String and parse it to a double
                    String overallStr = review.getOverall().toString();
                    return Double.parseDouble(overallStr); // Преобразуем строку в double
                }).reversed())
                .collect(Collectors.toList());
    }

    // Метод для вывода популярных товаров за период
    public List<String> getPopularProductsByTime(List<Model> reviews, long startTime, long endTime) {
        return reviews.stream()
                .filter(review -> review.getUnixReviewTime() >= startTime && review.getUnixReviewTime() <= endTime)
                .collect(Collectors.groupingBy(Model::getAsin, Collectors.counting()))
                .entrySet().stream()
                .sorted((entry1, entry2) -> Long.compare(entry2.getValue(), entry1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // Метод для поиска товара по совпадению текста с отзывом
    public List<Model> searchProductByReviewText(List<Model> reviews, String searchText) {
        // Проверяем на null, чтобы избежать NullPointerException
        if (reviews == null || searchText == null) {
            return Collections.emptyList(); // Если список или поисковый текст null, возвращаем пустой список
        }

        return reviews.stream()
                .filter(review -> review.getReviewText() != null && review.getReviewText().toLowerCase().contains(searchText.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Model> loadReviewsFromFile(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<Model> reviews = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                Model review = mapper.readValue(line, Model.class);
                reviews.add(review);
            }
        }
        return reviews;
    }
    // Метод для конвертации строки в UNIX timestamp
    public long convertDateToTimestamp(String reviewTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM dd, yyyy");
        try {
            Date date = formatter.parse(reviewTime);
            return date.getTime() / 1000;  // Возвращаем UNIX timestamp
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;  // Возвращаем -1 в случае ошибки
        }
    }
    // Метод для записи данных в CSV
    public void saveToCSV(List<Model> reviews, String fileName) throws IOException {
        FileWriter writer = new FileWriter(fileName);
        CSVWriter csvWriter = new CSVWriter(writer);

        // Записываем заголовки
        String[] header = {"asin", "reviewerID", "reviewerName", "overall", "reviewText", "reviewTime", "unixReviewTime"};
        csvWriter.writeNext(header);

        // Записываем данные
        for (Model review : reviews) {
            String[] data = {
                    review.getAsin(),
                    review.getReviewerID(),
                    review.getReviewerName(),
                    review.getOverall(),
                    review.getReviewText(),
                    review.getReviewTime(),
                    String.valueOf(review.getUnixReviewTime())
            };
            csvWriter.writeNext(data);
        }

        csvWriter.close();
        writer.close();
    }

    // Метод для записи популярных продуктов в CSV
    public void savePopularProductsToCSV(List<String> products, String fileName) throws IOException {
        FileWriter writer = new FileWriter(fileName);
        CSVWriter csvWriter = new CSVWriter(writer);

        // Записываем заголовок
        String[] header = {"asin"};
        csvWriter.writeNext(header);

        // Записываем продукты
        for (String product : products) {
            String[] data = { product };
            csvWriter.writeNext(data);
        }

        csvWriter.close();
        writer.close();
    }
}

