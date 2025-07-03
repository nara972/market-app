package com.group.marketapp.controller;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.ui.Model;
import com.group.marketapp.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@EnableMethodSecurity
@RequiredArgsConstructor
public class PageController {

    @GetMapping("/")
    public String indexPage(Model model){
        return "index";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup"; 
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/product/create")
    public String productCreate() {
        return "product-create";
    }

    @GetMapping("/product/list")
    public String productListPage() {
        return "product-list";
    }

    @GetMapping("/product/detail")
    public String productDetailPage() {
        return "product-detail";
    }

    @GetMapping("/product/update")
    public String productUpdatePage() {
        return "product-update";
    }

    @GetMapping("/order/create")
    public String orderCreatePage() {
        return "order-create";
    }

    @GetMapping("/coupon/create")
    public String couponCreatePage() {
        return "coupon-create";
    }

    @GetMapping("/coupon/list")
    public String couponListPage() {
        return "coupon-list";
    }

    @GetMapping("/coupon/update")
    public String couponUpdatePage() {
        return "coupon-update";
    }

}
