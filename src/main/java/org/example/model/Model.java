package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Model {

    private String overall;       // Рейтинг (тип double для правильного преобразования)
    private boolean verified;     // Проверен ли отзыв
    private String reviewTime;    // Время отзыва
    private String reviewerID;    // ID рецензента
    private String asin;          // ID товара
    private String reviewerName;  // Имя рецензента
    private String reviewText;    // Текст отзыва
    private String summary;       // Краткое содержание
    private long unixReviewTime;  // Время отзыва в Unix формате

    public String getAsin() {
        return asin;
    }

    public String getReviewerID() {
        return reviewerID;
    }

    public String getReviewerName() {
        return reviewerName;  // Исправлено: getReviewerName должно возвращать reviewerName, а не reviewerID
    }

    public String getOverall() {
        return overall;
    }

    public String getReviewText() {
        return reviewText;
    }

    public long getUnixReviewTime() {
        return unixReviewTime;
    }

    public String getReviewTime() {
        return reviewTime;
    }
}
