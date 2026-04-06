package com.example.server.controller;

import com.example.server.model.MenuItem;
import com.example.server.repository.MenuItemRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Menu management endpoints.
 *
 * Tablet (read-only):
 *   GET  /api/menu           — all available items
 *   GET  /api/menu/all       — all items including unavailable
 *
 * Kitchen (full management):
 *   POST   /api/menu              — add a new item
 *   PUT    /api/menu/{id}         — update name/price/description
 *   DELETE /api/menu/{id}         — remove an item permanently
 *   PATCH  /api/menu/{id}/toggle  — mark available or unavailable
 *                                    (e.g. "we ran out of beef today")
 */
@RestController
@RequestMapping("/api/menu")
@CrossOrigin
public class MenuController {

    private final MenuItemRepository menuRepository;

    public MenuController(MenuItemRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    /** Returns only available items — what the tablet shows customers */
    @GetMapping
    public List<MenuItem> getAvailableMenu() {
        return menuRepository.findByAvailableTrue();
    }

    /** Returns all items including unavailable — kitchen management view */
    @GetMapping("/all")
    public List<MenuItem> getAllMenuItems() {
        return menuRepository.findAll();
    }

    /** Single item by id */
    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getById(@PathVariable Long id) {
        return menuRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Kitchen adds a new menu item.
     * POST /api/menu
     * Body: {"name":"Samosa","description":"Crispy beef samosa","price":1.50,"category":"Snacks"}
     */
    @PostMapping
    public ResponseEntity<MenuItem> addMenuItem(@Valid @RequestBody MenuItem item) {
        item.setAvailable(true);
        MenuItem saved = menuRepository.save(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Kitchen updates an existing item (price change, rename etc).
     * PUT /api/menu/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(
            @PathVariable Long id,
            @Valid @RequestBody MenuItem updated) {

        return menuRepository.findById(id)
                .map(existing -> {
                    existing.setName(updated.getName());
                    existing.setDescription(updated.getDescription());
                    existing.setPrice(updated.getPrice());
                    existing.setCategory(updated.getCategory());
                    return ResponseEntity.ok(menuRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Kitchen removes an item permanently.
     * DELETE /api/menu/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        if (!menuRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        menuRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Kitchen toggles item availability without deleting it.
     * Useful for "we ran out of beef today" situations.
     * PATCH /api/menu/{id}/toggle
     *
     * Returns the updated item with its new available status.
     */
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<MenuItem> toggleAvailability(@PathVariable Long id) {
        return menuRepository.findById(id)
                .map(existing -> {
                    existing.setAvailable(!existing.isAvailable());
                    return ResponseEntity.ok(menuRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
