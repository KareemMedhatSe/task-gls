package com.example.interview.controller;

import com.example.interview.dto.SearchAndLogResponse;
import com.example.interview.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/search-and-log")
    public ResponseEntity<SearchAndLogResponse> searchAndLog(
            @RequestParam("q") String q,
            @RequestParam(value = "minPrice", required = false) Double minPrice,
            @RequestParam(value = "sortBy", required = false, defaultValue = "price") String sortBy) {
        return ResponseEntity.ok(productService.searchFilterAndLog(q, minPrice, sortBy));
    }
}
