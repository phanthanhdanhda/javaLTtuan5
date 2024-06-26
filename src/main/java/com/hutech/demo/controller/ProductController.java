package com.hutech.demo.controller;

import com.hutech.demo.model.Product;
import com.hutech.demo.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    /*Index*/
    @GetMapping()
    public String index(Model model) {
        model.addAttribute("listProduct", productService.GetAll());
        return "products/index";
    }

    /*Create*/
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("product", new Product());
        return "products/create";
    }

    @PostMapping("/create")
    public String create(@Valid Product newProduct, BindingResult result, @RequestParam MultipartFile imageProduct, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("product", newProduct);
            return "products/create";
        }
        //Save image to static/images folder
        if (imageProduct != null && imageProduct.getSize()>0) {
            try {
                File saveFile = new ClassPathResource("/static/images").getFile();
                String newImageFile = UUID.randomUUID() + ".png";
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + newImageFile);
                Files.copy(imageProduct.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                newProduct.setImage(newImageFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        productService.addProduct(newProduct);
        return "redirect:/products";
    }

    /*Edit*/
    @GetMapping("/edit/{id}")
    public String getEditForm(@PathVariable("id") int id, Model model) {
        Product product = productService.get(id);
        if (product != null) {
            model.addAttribute("product", product);
            return "products/edit";
        } else {
            return "redirect:/products";
        }
    }

    @PostMapping("/edit/{id}")
    public String edit(@Valid Product editProduct, BindingResult result, @RequestParam MultipartFile imageProduct, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("product", editProduct);
            return "products/edit";
        }
        if (imageProduct != null && imageProduct.getSize() > 0) {
            try {
                File saveFile = new ClassPathResource("/static/images").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + editProduct.getImage());
                Files.copy(imageProduct.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        productService.edit(editProduct);
        return "redirect:/products";
    }

    /*Delete*/
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") int id, Model model) {
        Product product = productService.get(id);
        productService.deleteProduct(product);
        return "redirect:/products";
    }

    /*Search*/
    @GetMapping("/search")
    public String searchProduct(String query, Model model) {
        List<Product> searchResults = productService.searchProducts(query);
        model.addAttribute("listProduct", searchResults);
        return "products/search";
    }
}
