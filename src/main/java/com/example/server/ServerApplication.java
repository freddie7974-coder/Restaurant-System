package com.example.server;

import com.example.server.model.MenuItem;
import com.example.server.repository.MenuItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadMenuData(MenuItemRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                // Mains
                repository.save(new MenuItem("Beef Burger",         "100% Angus beef with cheese",           12.99, "Mains"));
                repository.save(new MenuItem("Chicken Wrap",        "Grilled chicken, lettuce, mayo",         10.50, "Mains"));
                repository.save(new MenuItem("Veggie Burger",       "Black bean patty, avocado, tomato",       9.99, "Mains"));
                // Sides
                repository.save(new MenuItem("Fries",               "Crispy golden potato fries",              4.50, "Sides"));
                repository.save(new MenuItem("Onion Rings",         "Beer-battered onion rings",               4.00, "Sides"));
                repository.save(new MenuItem("Samosa (3 pcs)",      "Crispy beef samosa",                      3.50, "Sides"));
                // Drinks
                repository.save(new MenuItem("Coke",                "Chilled cola with ice",                   2.00, "Drinks"));
                repository.save(new MenuItem("Orange Juice",        "Freshly squeezed",                        3.00, "Drinks"));
                repository.save(new MenuItem("Water",               "Still mineral water",                     1.00, "Drinks"));
                // Desserts
                repository.save(new MenuItem("Chocolate Lava Cake", "Warm cake with vanilla ice cream",        6.50, "Desserts"));
                System.out.println("--- Default menu items loaded successfully ---");
            } else {
                System.out.println("--- Menu already seeded, skipping ---");
            }
        };
    }
}
