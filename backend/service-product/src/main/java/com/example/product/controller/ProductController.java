package com.example.product.controller;

import com.example.product.entity.ImageModel;
import com.example.product.entity.Product;
import com.example.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping(value = {"/products"}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Product addNewProduct(@RequestPart("product") Product product,
                                 @RequestPart("imageFile") MultipartFile[] file) {
        try {
            Set<ImageModel> images = uploadImage(file);
            product.setProductImages(images);
            return productService.addNewProduct(product);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Set<ImageModel> uploadImage(MultipartFile[] multipartFiles) throws IOException {
        Set<ImageModel> imageModels = new HashSet<>();

        for (MultipartFile file: multipartFiles) {
            ImageModel imageModel = new ImageModel(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes()
            );
            imageModels.add(imageModel);
        }

        return imageModels;
    }

    @GetMapping("/products")
    public List<Product> getAllProducts(@RequestParam(defaultValue = "0") int pageNumber,
                                        @RequestParam(defaultValue = "") String searchKey) {
        List<Product> result = productService.getAllProducts(pageNumber, searchKey);
        System.out.println("Result size is "+ result.size());
        return result;
    }

    @GetMapping("/products/{productId}")
    public Product getProductDetailsById(@PathVariable("productId") Integer productId) {
        return productService.getProductDetailsById(productId);
    }

    @DeleteMapping("/products/{productId}")
    public void deleteProductDetails(@PathVariable("productId") Integer productId) {
        productService.deleteProductDetails(productId);
    }




}
