package org.example.caffe.service;

import org.example.caffe.domain.Product;
import org.example.caffe.repository.ProductRepository;
import org.example.caffe.dto.ProductSummaryDto;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Cacheable(value = "productSummaries")
    public List<ProductSummaryDto> getAllActiveProductSummaries() {
        return productRepository.findAllActiveProductSummaries();
    }

    public List<Product> searchProductsByName(String name) {
        return productRepository.findByProductNameContainingIgnoreCaseAndIsActiveTrue(name);
    }

    // Add Product
    @CacheEvict(value = {"productList", "productSummaries"}, allEntries = true)
    public Product addProduct(Product product) {
        if (productRepository.findByProductName(product.getProductName()).isPresent()) {
            throw new IllegalArgumentException("Product with name " + product.getProductName() + " already exists");
        }
        product.setIsActive(true);
        return productRepository.save(product);
    }

    @CacheEvict(value = {"products", "productList", "productSummaries"}, allEntries = true)
    public Product updateProduct(Product product) {
        validateProduct(product);
        
        productRepository.findByProductName(product.getProductName())
                .ifPresent(existingProduct -> {
                    if (!existingProduct.getId().equals(product.getId())) {
                        throw new IllegalArgumentException("Product with name " + product.getProductName() + " already exists");
                    }
                });
                
        return productRepository.save(product);
    }

    private void validateProduct(Product product) {
        if (product.getId() == null) {
            throw new IllegalArgumentException("Product ID must not be null");
        }

        if (product.getProductActualMadePrice() > product.getProductPrice()) {
            throw new IllegalArgumentException("Actual made price cannot be greater than product price");
        }
    }

    @Cacheable(value = "products", key = "#id")
    public Product getProductById(Long id) {
        return productRepository.findByIdAndIsActiveIsTrue(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Cacheable(value = "productList", key = "{#page, #size, #days}")
    public List<Product> getAllProducts(Integer page, Integer size, Integer days) {

        int filterDays = (days != null) ? days : 30;

        Timestamp fromDate = Timestamp.valueOf(
                LocalDateTime.now().minusDays(filterDays)
        );

        if (page != null || size != null) {
            int currentPage = (page != null) ? page : 0;
            int pageSize = (size != null) ? size : 10;

            Pageable pageable = PageRequest.of(currentPage, pageSize);

            return productRepository
                    .findAllProductsAndIsActiveIsTrue(fromDate, pageable)
                    .getContent();
        }

        return productRepository.findAllProductsAndIsActiveIsTrue(fromDate);
    }

    @CacheEvict(value = {"products", "productList", "productSummaries"}, allEntries = true)
    public String deleteProductByID(Long id) {
        Optional<Product> product = productRepository.findById(id);
        product.get().setIsActive(false);
        productRepository.save(product.get());
        return "Product deleted successfully";
    }
}
