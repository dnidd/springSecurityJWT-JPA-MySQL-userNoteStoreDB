package com.snidd.note_store.controller;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    // Product record/object
    private record Product(Integer id, String productName, double price) {}

    // Mockup/mimic DB with products
    // List representing mockup DB
    List<Product> products = new ArrayList<>(
            List.of(
                    new Product(1, "Trekking shoes", 109.0),
                    new Product(2, "Hiking pants", 209.0)
            )
    );

    @GetMapping
    public List<Product> getProducts() {
        return products;
    }

    // POST cannot be executed while CSRF not handled
    @PostMapping
    public Product saveProduct(@RequestBody Product product) {
        products.add(product);
        return product;
    }


}
