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
    public String addProduct(@RequestBody Product product) {
        productService.addProduct(product);
        return "Product added successfully";
    }
    @PutMapping("/product/update")
    public Product updateProduct(@RequestBody Product product) {
        return productService.updateProduct(product);
    }

    // Get Product by ID
    @GetMapping("/product/{id}")
    public Product getProduct(@PathVariable("id") Long id) {
        return productService.getProductById(id);
    }

    // Get All Products
    @GetMapping("/product/getall")
    public List<Product> getAllProducts(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Integer days
            ) {
        return productService.getAllProducts(page, size,days);
    }
    @DeleteMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id) {
        return productService.deleteProductByID(id);
    }
}
