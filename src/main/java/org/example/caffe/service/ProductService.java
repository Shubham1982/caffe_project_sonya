package org.example.caffe.service;

import org.example.caffe.domain.Product;
import org.example.caffe.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Product getProductById(Long id) {
        return productRepository.findByIdAndIsActiveIsTrue(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public List<Product> getAllProducts() {
        return productRepository.findAllProductsAndIsActiveIsTrue();
    }

    public String deleteProductByID(Long id) {
        Optional<Product> product = productRepository.findById(id);
        product.get().setIsActive(false);
        productRepository.save(product.get());
        return "Product deleted successfully";
    }
}
