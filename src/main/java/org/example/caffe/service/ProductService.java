package org.example.caffe.service;

import org.example.caffe.domain.Product;
import org.example.caffe.repository.ProductRepository;
import org.springframework.stereotype.Service;

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

    // Add Product
    public Product addProduct(Product product) {
        product.setIsActive(true);
        return productRepository.save(product);
    }
    public Product updateProduct(Product product) {
        product.setIsActive(true);
        return productRepository.save(product);
    }

    public Product getProductById(Long id) {
        return productRepository.findByIdAndIsActiveIsTrue(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public List<Product> getAllProducts(Integer page, Integer size) {
        if (page != null || size != null) {
            int currentPage = (page != null) ? page : 0;
            int pageSize = (size != null) ? size : 10;
            Pageable pageable = PageRequest.of(currentPage, pageSize);
            return productRepository.findAllProductsAndIsActiveIsTrue(pageable).getContent();
        }
        return productRepository.findAllProductsAndIsActiveIsTrue();
    }

    public String deleteProductByID(Long id) {
        Optional<Product> product = productRepository.findById(id);
        product.get().setIsActive(false);
        productRepository.save(product.get());
        return "Product deleted successfully";
    }
}
