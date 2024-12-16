package org.example;

import org.example.model.Model;
import org.example.pars.Processor;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String filePath = "C:\\Users\\Lvasi\\Desktop\\Software_5.json"; // Путь к файлу с отзывами
        Processor processor = new Processor();
        Scanner scanner = new Scanner(System.in);

        try {
            // Загружаем отзывы из файла
            List<Model> reviews = processor.loadReviewsFromFile(filePath);

            // Начало цикла для меню
            while (true) {
                // Печатаем меню с выбором действия
                System.out.println("\nВыберите действие:");
                System.out.println("1. Список продуктов от самого популярного");
                System.out.println("2. Список продуктов по рейтингу (учитывать вес самого отзыва)");
                System.out.println("3. Вывести самые популярные товары за период");
                System.out.println("4. Поиск товара по совпадению текста с отзывом");
                System.out.println("5. Выход");

                // Читаем выбор пользователя
                int choice = scanner.nextInt();
                scanner.nextLine(); // Поглощаем лишнюю новую строку после ввода числа

                // Действия по выбору
                switch (choice) {
                    case 1:
                        // Список продуктов от самого популярного
                        List<String> popularProducts = processor.getPopularProducts(reviews);
                        System.out.println("Продукты от самого популярного:");
                        popularProducts.forEach(System.out::println);

                        // Сохраняем популярные продукты в CSV
                        processor.savePopularProductsToCSV(popularProducts, "popular_products.csv");
                        break;

                    case 2:
                        // Список продуктов по рейтингу с учетом веса отзыва
                        List<Model> sortedReviews = processor.getReviewsByRatingWithWeight(reviews);
                        System.out.println("Продукты по рейтингу (с учетом веса отзыва):");
                        sortedReviews.forEach(review -> System.out.println(review.getReviewerName() + ": " + review.getOverall()));
                        // Сохраняем отзывы в CSV
                        processor.saveToCSV(sortedReviews, "sorted_reviews.csv");
                        break;

                    case 3:
                        // Вывести самые популярные товары за период
                        System.out.println("Введите начальную дату (в формате MM dd, yyyy):");
                        String startDate = scanner.nextLine();
                        System.out.println("Введите конечную дату (в формате MM dd, yyyy):");
                        String endDate = scanner.nextLine();
                        long startTime = processor.convertDateToTimestamp(startDate);
                        long endTime = processor.convertDateToTimestamp(endDate);

                        if (startTime == -1 || endTime == -1) {
                            System.out.println("Неверный формат даты.");
                            break;
                        }

                        List<String> popularProductsByTime = processor.getPopularProductsByTime(reviews, startTime, endTime);
                        System.out.println("Самые популярные товары за период:");
                        popularProductsByTime.forEach(System.out::println);
                        // Сохраняем популярные товары за период в CSV
                        processor.savePopularProductsToCSV(popularProductsByTime, "popular_products_by_time.csv");
                        break;

                    case 4:
                        // Поиск товара по совпадению текста с отзывом
                        System.out.println("Введите текст для поиска:");
                        String searchText = scanner.nextLine();
                        List<Model> foundProducts = processor.searchProductByReviewText(reviews, searchText);
                        System.out.println("Продукты, соответствующие вашему запросу:");
                        foundProducts.forEach(review -> System.out.println(review.getAsin() + ": " + review.getReviewText()));
                        processor.saveToCSV(foundProducts, "search_results.csv");
                        break;

                    case 5:
                        // Выход
                        System.out.println("Выход из программы.");
                        scanner.close();
                        System.exit(0); // Завершаем программу

                    default:
                        System.out.println("Неверный выбор. Попробуйте еще раз.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
