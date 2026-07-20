package com.newssphere.model;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * POJO tạm thời cho dữ liệu tin tức hiển thị trên dashboard.
 * TODO: Chuyển thành @Entity (JPA) khi pipeline lấy tin thật
 *       (NewsAPI fetch + tóm tắt bằng OpenAI) được triển khai.
 *       Lúc đó chỉ cần đổi NewsMockService -> NewsService thật,
 *       giữ nguyên field để không phải sửa template.
 */
public class NewsArticle {

    private Long id;
    private String title;
    private String aiSummary;
    private String sourceName;
    private String category;
    private LocalDateTime publishedAt;
    private boolean trending;
    private int aiConfidence;
    private int readMinutes;
    private String icon;

    public NewsArticle() {}

    public NewsArticle(Long id, String title, String aiSummary, String sourceName,
                       String category, LocalDateTime publishedAt, boolean trending) {
        this(id, title, aiSummary, sourceName, category, publishedAt, trending, 90, 4, "📰");
    }

    public NewsArticle(Long id, String title, String aiSummary, String sourceName,
                       String category, LocalDateTime publishedAt, boolean trending,
                       int aiConfidence, int readMinutes, String icon) {
        this.id = id;
        this.title = title;
        this.aiSummary = aiSummary;
        this.sourceName = sourceName;
        this.category = category;
        this.publishedAt = publishedAt;
        this.trending = trending;
        this.aiConfidence = aiConfidence;
        this.readMinutes = readMinutes;
        this.icon = icon;
    }

    // ---- Getters & Setters ----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAiSummary() { return aiSummary; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }

    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }

    public boolean isTrending() { return trending; }
    public void setTrending(boolean trending) { this.trending = trending; }

    public int getAiConfidence() { return aiConfidence; }
    public void setAiConfidence(int aiConfidence) { this.aiConfidence = aiConfidence; }

    public int getReadMinutes() { return readMinutes; }
    public void setReadMinutes(int readMinutes) { this.readMinutes = readMinutes; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    /** Nhãn thời gian kiểu "2h ago" dùng trực tiếp trong Thymeleaf. */
    public String getTimeAgoLabel() {
        if (publishedAt == null) return "";
        Duration d = Duration.between(publishedAt, LocalDateTime.now());
        long minutes = d.toMinutes();
        if (minutes < 1) return "just now";
        if (minutes < 60) return minutes + "m ago";
        long hours = d.toHours();
        if (hours < 24) return hours + "h ago";
        long days = d.toDays();
        return days + "d ago";
    }
}
