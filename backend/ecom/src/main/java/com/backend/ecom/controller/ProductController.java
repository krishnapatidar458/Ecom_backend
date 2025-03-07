package com.backend.ecom.controller;

import com.backend.ecom.models.Product;
import com.backend.ecom.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService service;

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts(){
        return new ResponseEntity<>(service.getAllProducts(), HttpStatus.OK);
    }

    @GetMapping("/product/{pid}")
    public ResponseEntity<Product> getById(@PathVariable int pid){
        Product p = service.getById(pid);
        return ResponseEntity.of(Optional.ofNullable(p));
    }

    @PostMapping("/product")
    public ResponseEntity<?> addProduct(@RequestPart Product product,@RequestPart MultipartFile imageFile){
        try{
            Product p = service.addProduct(product,imageFile);
            return new ResponseEntity<>(p,HttpStatus.CREATED);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/product/{productId}/image")
    public ResponseEntity<byte[]> getImageByProductId(@PathVariable int productId) {
        Product product = service.getById(productId);
        byte[] imageFile = product.getImageData();
        return ResponseEntity.ok().contentType(MediaType.valueOf(product.getImageType())).body(imageFile);
    }

    @PutMapping("/product/{pid}")
    public ResponseEntity<String> updateProduct(@PathVariable int pid,@RequestPart Product product, @RequestPart MultipartFile imageFile){
        try{
            Product p = service.updateProduct(pid,product,imageFile);
            if(p!=null)
                return ResponseEntity.ok("Product updated successfully");
            else
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }catch(IOException e){
            return new ResponseEntity<>("Failed to update due to file handling", HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (Exception e){
            return new ResponseEntity<>("Failed to update product"+e.getMessage(),HttpStatus.BAD_REQUEST);
        }

    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable int id){
        Product product = service.getById(id);
        if(product!=null){
            service.deleteProduct(id);
            return new ResponseEntity<>("Product deleted",HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Product not found",HttpStatus.NOT_FOUND);
        }
    }

}
