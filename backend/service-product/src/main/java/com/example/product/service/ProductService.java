package com.example.product.service;

import com.example.product.dao.ProductDao;
import com.example.product.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductDao productDao;

    public Product addNewProduct(Product product) {
        return productDao.save(product);
    }

    public List<Product> getAllProducts(int pageNumber, String searchKey) {
        Pageable pageable = PageRequest.of(pageNumber,12);

        if(searchKey.equals("")) {
            return (List<Product>) productDao.findAll(pageable);
        } else {
            return (List<Product>) productDao.findByProductNameContainingIgnoreCaseOrProductDescriptionContainingIgnoreCase(
                    searchKey, searchKey, pageable
            );
        }

    }

    public Product getProductDetailsById(Integer productId) {
        return productDao.findById(productId).get();
    }

    public void deleteProductDetails(Integer productId) {
        productDao.deleteById(productId);
    }

    public Product getProductDetails(boolean isSingleProductCheckout, Integer productId) {
        if(isSingleProductCheckout && productId != 0) {
            // we are going to buy a single product

            List<Product> list = new ArrayList<>();
            Product product = productDao.findById(productId).get();
            list.add(product);
            return product;
        } else {
            // Since we can't access the user's cart in this microservice, we return null or throw an exception
            throw new UnsupportedOperationException("Cannot handle cart operations in Product microservice.");
        }
    }
}
