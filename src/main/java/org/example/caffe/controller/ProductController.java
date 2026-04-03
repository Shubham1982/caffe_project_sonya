package org.example.caffe.controller;

import org.example.caffe.domain.Product;
import org.example.caffe.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Add Product
    @PostMapping("/product/add")
    public Product addProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }
    @PutMapping("/product/update")
    public Product updateProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    // Get Product by ID
    @GetMapping("/product/{id}")
    public Product getProduct(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    // Get All Products
    @GetMapping("/product/getall")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }
    @DeleteMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        return productService.deleteProductByID(id);
    }
}
