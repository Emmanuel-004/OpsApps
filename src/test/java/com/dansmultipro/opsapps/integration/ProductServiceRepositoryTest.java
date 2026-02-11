package com.dansmultipro.opsapps.integration;

import com.dansmultipro.opsapps.constant.RoleCode;
import com.dansmultipro.opsapps.dto.CreateResponseDto;
import com.dansmultipro.opsapps.dto.PageResponseDto;
import com.dansmultipro.opsapps.dto.UpdateResponseDto;
import com.dansmultipro.opsapps.dto.product.ProductRequestDto;
import com.dansmultipro.opsapps.dto.product.ProductResponseDto;
import com.dansmultipro.opsapps.dto.product.UpdateProductRequestDto;
import com.dansmultipro.opsapps.model.Product;
import com.dansmultipro.opsapps.pojo.AuthorizationPojo;
import com.dansmultipro.opsapps.repository.ProductRepository;
import com.dansmultipro.opsapps.repository.TransactionRepository;
import com.dansmultipro.opsapps.repository.UserRepository;
import com.dansmultipro.opsapps.service.impl.ProductServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ProductServiceRepositoryTest {

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        testProduct = new Product();
        testProduct.setId(UUID.randomUUID());
        testProduct.setName("Test Product");
        testProduct.setCode("TEST001");
        testProduct.setCreatedAt(LocalDateTime.now());
        testProduct.setCreatedBy(UUID.randomUUID());
        testProduct = productRepository.save(testProduct);
    }

    private void setupAuthentication(String role) {

        AuthorizationPojo principal = new AuthorizationPojo(UUID.randomUUID().toString(), RoleCode.SA.name());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority(role))
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void testFindById_Success() {

        ProductResponseDto result = productService.findById(testProduct.getId().toString());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testProduct.getId());
        assertThat(result.getName()).isEqualTo("Test Product");
        assertThat(result.getCode()).isEqualTo("TEST001");
    }

    @Test
    void testFindAll_Success() {

        Product product2 = new Product();
        product2.setId(UUID.randomUUID());
        product2.setName("Product 2");
        product2.setCode("TEST002");
        product2.setCreatedAt(LocalDateTime.now());
        product2.setCreatedBy(UUID.randomUUID());
        productRepository.save(product2);

        Product product3 = new Product();
        product3.setId(UUID.randomUUID());
        product3.setName("Product 3");
        product3.setCode("TEST003");
        product3.setCreatedAt(LocalDateTime.now());
        product3.setCreatedBy(UUID.randomUUID());
        productRepository.save(product3);

        PageResponseDto<ProductResponseDto> result = productService.findAll(1, 5);

        assertThat(result).isNotNull();
        assertThat(result.getData().size()).isEqualTo(3);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(1);
    }

    @Test
    void testCreateProduct_Success() {
        setupAuthentication(RoleCode.SA.name());

        ProductRequestDto requestDto = new ProductRequestDto();
        requestDto.setName("New Product");
        requestDto.setCode("NEW001");

        CreateResponseDto result = productService.createProduct(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getMessage()).isEqualTo("Product created successfully");

        Product savedProduct = productRepository.findById(result.getId()).orElseThrow();
        assertThat(savedProduct.getName()).isEqualTo("New Product");
        assertThat(savedProduct.getCode()).isEqualTo("NEW001");
    }

    @Test
    void testUpdateProduct_Success() {
        setupAuthentication(RoleCode.SA.name());

        UpdateProductRequestDto requestDto = new UpdateProductRequestDto();
        requestDto.setName("Updated Product");
        requestDto.setCode("UPDATED001");
        requestDto.setVersion(testProduct.getVersion());

        UpdateResponseDto result = productService.updateProduct(
                testProduct.getId().toString(),
                requestDto
        );

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("Product updated successfully");

        Product updatedProduct = productRepository.findById(testProduct.getId()).orElseThrow();
        assertThat(updatedProduct.getName()).isEqualTo("Updated Product");
        assertThat(updatedProduct.getCode()).isEqualTo("UPDATED001");
    }

}
