package com.example.interview.service;

import com.example.interview.client.DummyJsonProductClient;
import com.example.interview.client.dto.DummyProductDto;
import com.example.interview.client.dto.DummyProductListDto;
import com.example.interview.dto.ProductResponseDto;
import com.example.interview.dto.SearchAndLogResponse;
import com.example.interview.dto.StatsDto;
import com.example.interview.entity.SearchQueryLog;
import com.example.interview.repository.SearchQueryLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
public class ProductService {

    private final DummyJsonProductClient productClient;
    private final SearchQueryLogRepository queryLogRepository;

    public ProductService(DummyJsonProductClient productClient, SearchQueryLogRepository queryLogRepository) {
        this.productClient = productClient;
        this.queryLogRepository = queryLogRepository;
    }

    public SearchAndLogResponse searchFilterAndLog(String query, Double minPrice, String sortBy) {
        // TODO: Implement the business logic:
        // 1. Fetch raw product data using productClient.
        DummyProductListDto resultDto = productClient.searchProducts(query);
        List<DummyProductDto> products = resultDto.products();
        List<DummyProductDto> resultProducts = null;

        double maxDiscountPercentage = 0;
        double avgPrice = 0;
        double sum = 0;
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i) == null)
                throw new RuntimeException("NoProductsFoundException if no products match or exist.");
            else if (products.get(i).price() >= minPrice) {
                resultProducts.add(products.get(i));
                if (products.get(i).discountPercentage() > maxDiscountPercentage) {
                    maxDiscountPercentage = products.get(i).discountPercentage();
                }
                sum += products.get(i).price();
            }

        }
        avgPrice = sum / resultProducts.size();
        // 2. Filter products: exclude products with price < minPrice (if minPrice is provided).
        //    Throw NoProductsFoundException if no products match or exist.
        // 3. Compute stats: averagePrice, maxDiscount on the filtered products.
        // 4. Sort the filtered products in descending order based on sortBy (price, rating, or discount).
        resultProducts.sort(Comparator.comparing(DummyProductDto::price));
        // 5. Save search log to database via queryLogRepository and get the generated logId.
        SearchQueryLog searchQueryLog = new SearchQueryLog(query,minPrice,resultProducts.size(), LocalDateTime.now());
        Long logId  = queryLogRepository.save(searchQueryLog).getId();
        // 6. Map the sorted list and statistics to SearchAndLogResponse DTO and return.
        StatsDto statsDto = new StatsDto(avgPrice,maxDiscountPercentage);

        List<ProductResponseDto> prList = resultProducts.stream()
                .map(p -> new ProductResponseDto(p.id(), p.title(), p.price(), p.rating(), p.discountPercentage()))
                .toList();
        SearchAndLogResponse response= new SearchAndLogResponse(logId,query,minPrice,statsDto,prList);
        return response; // Replace with your implementation
    }
}
