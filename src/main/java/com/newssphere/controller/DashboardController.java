package com.newssphere.controller;

import com.newssphere.model.MarketQuote;
import com.newssphere.model.NewsArticle;
import com.newssphere.model.User;
import com.newssphere.service.NewsMockService;
import com.newssphere.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final UserService userService;
    private final NewsMockService newsMockService;

    public DashboardController(UserService userService, NewsMockService newsMockService) {
        this.userService = userService;
        this.newsMockService = newsMockService;
    }

    // =============================================
    // GET /dashboard — trang chính sau khi đăng nhập
    // =============================================
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        // Spring Security dùng email làm username (xem UserService)
        String email = authentication.getName();
        User user = userService.findByEmail(email);

        List<NewsArticle> forYou = newsMockService.getForYou(user.getInterests());
        List<NewsArticle> trending = newsMockService.getTrending();
        List<NewsArticle> latest = newsMockService.getLatest();
        List<MarketQuote> marketQuotes = newsMockService.getMarketQuotes();

        model.addAttribute("user", user);
        model.addAttribute("forYouArticles", forYou);
        model.addAttribute("trendingArticles", trending);
        model.addAttribute("latestArticles", latest);
        model.addAttribute("marketQuotes", marketQuotes);
        model.addAttribute("marketQuotesByClass", newsMockService.getMarketQuotesByClass());
        model.addAttribute("categoryCounts", newsMockService.getCategoryCounts());
        model.addAttribute("categoryIcons", newsMockService.getCategoryIcons());
        model.addAttribute("totalArticleCount", latest.size());
        model.addAttribute("trendingTopics", newsMockService.getTrendingTopics());

        return "dashboard/dashboard";
    }
}