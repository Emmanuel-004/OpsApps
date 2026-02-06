package com.dansmultipro.opsapps.unit;

import com.dansmultipro.opsapps.dto.CreateResponseDto;
import com.dansmultipro.opsapps.dto.PageResponseDto;
import com.dansmultipro.opsapps.dto.UpdateResponseDto;
import com.dansmultipro.opsapps.dto.product.ProductRequestDto;
import com.dansmultipro.opsapps.dto.product.ProductResponseDto;
import com.dansmultipro.opsapps.dto.product.UpdateProductRequestDto;
import com.dansmultipro.opsapps.model.Product;
import com.dansmultipro.opsapps.model.Role;
import com.dansmultipro.opsapps.model.User;
import com.dansmultipro.opsapps.pojo.AuthorizationPojo;
import com.dansmultipro.opsapps.repository.ProductRepository;
import com.dansmultipro.opsapps.repository.UserRepository;
import com.dansmultipro.opsapps.service.PrincipalService;
import com.dansmultipro.opsapps.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    PrincipalService principalService;

    @InjectMocks
    ProductServiceImpl  productService;

    @Test
    public void shouldReturnData_whenIdValid() {
        UUID productId = UUID.randomUUID();

        Product product = new Product();
        product.setId(productId);
        product.setName("New Product");
        product.setCode("NEW001");

        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ProductResponseDto result = productService.findById(productId.toString());

        Assertions.assertEquals(productId, result.getId());
        Mockito.verify(productRepository, Mockito.times(1)).findById(productId);
    }

    @Test
    public void shouldReturnData_whenRequestInvalid() {
        int page = 1;
        int size = 5;

        Product product1 = new Product();
        product1.setId(UUID.randomUUID());
        product1.setName("New Product");
        product1.setCode("NEW001");

        Product product2 = new Product();
        product2.setId(UUID.randomUUID());
        product2.setName("New Product");
        product2.setCode("NEW001");

        List<Product> productList = List.of(product1, product2);

        Pageable pageable = PageRequest.of((page - 1), size);

        Page<Product> products = new PageImpl<>(productList, pageable, productList.size());

        Mockito.when(productRepository.findAll(pageable)).thenReturn(products);

        PageResponseDto<ProductResponseDto> result = productService.findAll(page, size);

        Assertions.assertEquals(productList.size(), result.getTotalElements());
        Assertions.assertEquals("New Product", result.getData().getFirst().getName());
        Mockito.verify(productRepository, Mockito.times(1)).findAll(pageable);

    }

    @Test
    public void shouldCreated_whenDataValid(){
        productService.setPrincipalService(principalService);

        UUID adminId = UUID.randomUUID();
        AuthorizationPojo principal = new AuthorizationPojo(adminId.toString());

        ProductRequestDto requestDto = new ProductRequestDto();
        requestDto.setName("New Product");
        requestDto.setCode("NEW001");

        Role role = new Role();
        role.setCode("SA");
        role.setName("Super Administrator");
        User admin = new User();
        admin.setId(adminId);
        admin.setRole(role);

        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(requestDto.getName());
        product.setCode(requestDto.getCode());

        Mockito.when(principalService.getPrincipal()).thenReturn(principal);
        Mockito.when(userRepository.findById(adminId)).thenReturn(Optional.of(admin));
        Mockito.when(productRepository.existsByCode(requestDto.getCode())).thenReturn(false);
        Mockito.when(productRepository.existsByName(requestDto.getName())).thenReturn(false);
        Mockito.when(productRepository.save(Mockito.any())).thenReturn(product);

        CreateResponseDto result =  productService.createProduct(requestDto);

        Assertions.assertEquals(result.getId(),product.getId());
        Mockito.verify(principalService, Mockito.atLeast(1)).getPrincipal();
        Mockito.verify(userRepository, Mockito.atLeast(1)).findById(Mockito.any());
        Mockito.verify(productRepository, Mockito.times(1)).existsByCode(Mockito.any());
        Mockito.verify(productRepository, Mockito.atLeast(1)).existsByName(Mockito.any());
        Mockito.verify(productRepository, Mockito.atLeast(1)).save(Mockito.any());
    }

    @Test
    public void shouldUpdate_whenDataValid() {
        productService.setPrincipalService(principalService);

        UUID adminId = UUID.randomUUID();
        UUID  productId = UUID.randomUUID();
        AuthorizationPojo principal = new AuthorizationPojo(adminId.toString());

        Role role = new Role();
        role.setCode("SA");
        role.setName("Super Administrator");
        User admin = new User();
        admin.setId(adminId);
        admin.setRole(role);

        UpdateProductRequestDto requestDto = new UpdateProductRequestDto();
        requestDto.setName("New Product edited");
        requestDto.setCode("NEW002");
        requestDto.setVersion(0);

        Product product = new Product();
        product.setId(productId);
        product.setName("New Product");
        product.setCode("NEW001");
        product.setVersion(0);

        Product savedProduct = new Product();
        savedProduct.setId(productId);
        savedProduct.setName(requestDto.getName());
        savedProduct.setCode(requestDto.getCode());
        savedProduct.setVersion(1);

        Mockito.when(principalService.getPrincipal()).thenReturn(principal);
        Mockito.when(userRepository.findById(adminId)).thenReturn(Optional.of(admin));
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        Mockito.when(productRepository.existsByCode(requestDto.getCode())).thenReturn(false);
        Mockito.when(productRepository.existsByName(requestDto.getName())).thenReturn(false);
        Mockito.when(productRepository.saveAndFlush(Mockito.any())).thenReturn(savedProduct);

        UpdateResponseDto result = productService.updateProduct(productId.toString(), requestDto);

        Assertions.assertEquals(1, result.getVersion());
        Mockito.verify(principalService, Mockito.atLeast(1)).getPrincipal();
        Mockito.verify(productRepository, Mockito.atLeast(1)).findById(Mockito.any());
        Mockito.verify(productRepository, Mockito.atLeast(1)).existsByCode(Mockito.any());
        Mockito.verify(productRepository, Mockito.atLeast(1)).existsByName(Mockito.any());
        Mockito.verify(productRepository, Mockito.atLeast(1)).saveAndFlush(Mockito.any());
    }
}
