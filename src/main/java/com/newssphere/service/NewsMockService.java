package com.newssphere.service;

import com.newssphere.model.MarketQuote;
import com.newssphere.model.NewsArticle;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service cung cấp dữ liệu tin tức / thị trường GIẢ (mock) để dựng UI dashboard trước.
 * Khi tích hợp NewsAPI + OpenAI thật, tạo NewsService implements cùng các method
 * (getForYou, getTrending, getLatest, getMarketQuotes) rồi swap trong DashboardController.
 */
@Service
public class NewsMockService {

    private final List<NewsArticle> articles = buildMockArticles();

    public List<NewsArticle> getForYou(List<String> interests) {
        if (interests == null || interests.isEmpty()) {
            return getLatest();
        }
        List<NewsArticle> result = articles.stream()
                .filter(a -> interests.contains(a.getCategory()))
                .sorted(Comparator.comparing(NewsArticle::getPublishedAt).reversed())
                .collect(Collectors.toList());
        return result.isEmpty() ? getLatest() : result;
    }

    public List<NewsArticle> getTrending() {
        return articles.stream()
                .filter(NewsArticle::isTrending)
                .sorted(Comparator.comparing(NewsArticle::getPublishedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<NewsArticle> getLatest() {
        return articles.stream()
                .sorted(Comparator.comparing(NewsArticle::getPublishedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<MarketQuote> getMarketQuotes() {
        List<MarketQuote> all = new ArrayList<>();
        all.addAll(getMarketQuotesByClass().get("stocks"));
        all.addAll(getMarketQuotesByClass().get("crypto"));
        all.addAll(getMarketQuotesByClass().get("commodities"));
        return all;
    }

    /** Trả về market quotes nhóm theo loại tài sản, dùng cho tab Stocks/Crypto/Commodities. */
    public Map<String, List<MarketQuote>> getMarketQuotesByClass() {
        Map<String, List<MarketQuote>> map = new LinkedHashMap<>();
        map.put("stocks", List.of(
                new MarketQuote("SPX", "S&P 500", "5,234.10", 0.5, "stocks"),
                new MarketQuote("IXIC", "NASDAQ", "16,421.30", 0.8, "stocks"),
                new MarketQuote("DJI", "Dow Jones", "39,127.00", -0.1, "stocks"),
                new MarketQuote("VNI", "VN-Index", "1,287.40", 1.2, "stocks")
        ));
        map.put("crypto", List.of(
                new MarketQuote("BTC", "Bitcoin", "$67,420", 2.1, "crypto"),
                new MarketQuote("ETH", "Ethereum", "$3,521", -1.2, "crypto"),
                new MarketQuote("SOL", "Solana", "$178.00", 4.3, "crypto"),
                new MarketQuote("BNB", "BNB", "$589.00", 0.7, "crypto")
        ));
        map.put("commodities", List.of(
                new MarketQuote("XAU", "Gold", "$2,041.00", 0.8, "commodities"),
                new MarketQuote("XAG", "Silver", "$24.30", 0.3, "commodities"),
                new MarketQuote("WTI", "Oil (WTI)", "$78.30", -0.3, "commodities"),
                new MarketQuote("GAS", "Gas (VN)", "25,460đ", 1.5, "commodities")
        ));
        return map;
    }

    /** Số lượng bài viết theo từng category, dùng cho badge số ở sidebar. */
    public Map<String, Long> getCategoryCounts() {
        return articles.stream()
                .collect(Collectors.groupingBy(NewsArticle::getCategory, LinkedHashMap::new, Collectors.counting()));
    }

    /** Top 5 chủ đề đang trending, xếp hạng để hiển thị ở widget "Trending Topics". */
    public List<NewsArticle> getTrendingTopics() {
        return getTrending().stream().limit(5).collect(Collectors.toList());
    }
    /** Icon (Tabler) tương ứng cho từng category, dùng ở sidebar danh mục. */
    public Map<String, String> getCategoryIcons() {
        Map<String, String> icons = new LinkedHashMap<>();
        icons.put("Technology", "ti-cpu");
        icons.put("Business", "ti-briefcase");
        icons.put("AI & Science", "ti-bolt");
        icons.put("Cybersecurity", "ti-shield");
        icons.put("Finance", "ti-chart-line");
        icons.put("Sports", "ti-activity");
        icons.put("World News", "ti-world");
        icons.put("Health", "ti-heart");
        return icons;
    }

    private List<NewsArticle> buildMockArticles() {
        LocalDateTime now = LocalDateTime.now();
        List<NewsArticle> list = new ArrayList<>();
        long id = 1;

        list.add(new NewsArticle(id++, "OpenAI announces new reasoning-focused model tier",
                "The new tier reportedly improves multi-step reasoning benchmarks while cutting inference cost for enterprise customers.",
                "TechCrunch", "AI & Science", now.minusMinutes(18), true, 96, 4, "🤖"));

        list.add(new NewsArticle(id++, "Global chip shortage eases as new fabs come online",
                "Analysts say fresh manufacturing capacity in Asia and the US is finally closing the supply gap that began in 2021.",
                "Reuters", "Technology", now.minusMinutes(42), false, 91, 5, "💻"));

        list.add(new NewsArticle(id++, "Central banks signal cautious rate path for Q3",
                "Policymakers across major economies hinted at a slower pace of cuts, citing sticky services inflation.",
                "Bloomberg", "Finance", now.minusHours(1), true, 94, 3, "📉"));

        list.add(new NewsArticle(id++, "Ransomware group targets logistics firms across Europe",
                "Security researchers warn the campaign exploits an unpatched VPN vulnerability disclosed last month.",
                "The Record", "Cybersecurity", now.minusHours(2), true, 89, 6, "🛡️"));

        list.add(new NewsArticle(id++, "Startup raises $40M to build AI-native customer support",
                "The round values the company at $220M, betting that agentic support tools will replace first-line human agents.",
                "TechCrunch", "Business", now.minusHours(3), false, 87, 4, "💰"));

        list.add(new NewsArticle(id++, "Regional trade talks resume after six-month pause",
                "Delegates aim to finalize a tariff framework before the end of the year despite lingering disagreements.",
                "AP News", "World News", now.minusHours(4), false, 82, 4, "🌍"));

        list.add(new NewsArticle(id++, "National team advances to semifinal after penalty shootout",
                "A dramatic finish saw the underdog side edge out the favorites in front of a sold-out crowd.",
                "ESPN", "Sports", now.minusHours(5), false, 78, 3, "⚽"));

        list.add(new NewsArticle(id++, "New study links sleep consistency to long-term heart health",
                "Researchers tracked over 20,000 adults and found irregular sleep schedules carried more risk than short sleep alone.",
                "Reuters Health", "Health", now.minusHours(6), false, 85, 5, "❤️"));

        list.add(new NewsArticle(id++, "Cloud providers cut GPU rental prices amid rising supply",
                "Increased availability of next-gen accelerators is driving prices down for AI training workloads.",
                "The Information", "Technology", now.minusHours(7), true, 90, 4, "☁️"));

        list.add(new NewsArticle(id++, "Gold hits fresh high as investors seek safety",
                "Bullion prices climbed for a third straight session as geopolitical tension weighed on risk appetite.",
                "Bloomberg", "Finance", now.minusHours(9), false, 88, 3, "🪙"));

        list.add(new NewsArticle(id++, "Researchers unveil low-cost water purification material",
                "The material reportedly removes over 99% of common contaminants using a solar-powered filtration process.",
                "Nature News", "AI & Science", now.minusHours(11), false, 93, 6, "🧪"));

        list.add(new NewsArticle(id++, "Major retailer confirms data breach affecting millions",
                "The company said payment data was not exposed, but customer emails and order history were accessed.",
                "The Record", "Cybersecurity", now.minusHours(13), false, 86, 4, "🔓"));

        list.add(new NewsArticle(id++, "City unveils plan for new public transit line",
                "The proposed line would connect three underserved districts and is expected to break ground next year.",
                "Local Wire", "World News", now.minusHours(16), false, 76, 3, "🚇"));

        list.add(new NewsArticle(id++, "Tech giant reports record quarterly revenue",
                "Strong cloud and advertising performance offset a slowdown in hardware sales for the quarter.",
                "CNBC", "Business", now.minusHours(20), true, 92, 4, "🏭"));

        list.add(new NewsArticle(id++, "League announces expanded playoff format for next season",
                "The change adds two extra wildcard slots, a move meant to boost late-season competitiveness.",
                "ESPN", "Sports", now.minusDays(1), false, 74, 3, "🏆"));

        list.add(new NewsArticle(id++, "New guidelines recommend earlier screening for common condition",
                "Health authorities lowered the recommended screening age after reviewing a decade of outcome data.",
                "Reuters Health", "Health", now.minusDays(1).minusHours(4), false, 84, 5, "🩺"));

        return list;
    }
}
