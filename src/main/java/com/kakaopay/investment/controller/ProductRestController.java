package com.kakaopay.investment.controller;

import com.kakaopay.investment.controller.dto.ProductInfo;
import com.kakaopay.investment.exception.InvestmentException;
import com.kakaopay.investment.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@Slf4j
public class ProductRestController {
    private final ProductService productService;

    public ProductRestController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 현재 투자가능 상품 조회
     *
     * @return
     */
    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<List<ProductInfo>> getAllAvailableProduct() throws InvestmentException {
        List<ProductInfo> products = productService.getAllAvailableProduct();
        return new ResponseEntity<List<ProductInfo>>(products, HttpStatus.OK);
    }

    /**
     * 투자가능 상품 Redis 적재
     */
    @PostMapping
    public ResponseEntity<String> setAllAvailableProductToRedis() {
        String result = productService.addProductListToRedis();
        return new ResponseEntity<String>(result, HttpStatus.OK);
    }


}
