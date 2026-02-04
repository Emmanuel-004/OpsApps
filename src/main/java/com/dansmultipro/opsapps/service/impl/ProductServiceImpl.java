package com.dansmultipro.opsapps.service.impl;

import com.dansmultipro.opsapps.baseclass.BaseService;
import com.dansmultipro.opsapps.constant.RoleCode;
import com.dansmultipro.opsapps.dto.CreateResponseDto;
import com.dansmultipro.opsapps.dto.DeleteResponseDto;
import com.dansmultipro.opsapps.dto.PageResponseDto;
import com.dansmultipro.opsapps.dto.UpdateResponseDto;
import com.dansmultipro.opsapps.dto.product.ProductRequestDto;
import com.dansmultipro.opsapps.dto.product.ProductResponseDto;
import com.dansmultipro.opsapps.dto.product.UpdateProductRequestDto;
import com.dansmultipro.opsapps.exception.DataIntegrationException;
import com.dansmultipro.opsapps.exception.NotAllowedException;
import com.dansmultipro.opsapps.exception.NotFoundException;
import com.dansmultipro.opsapps.model.Product;
import com.dansmultipro.opsapps.model.User;
import com.dansmultipro.opsapps.pojo.AuthorizationPojo;
import com.dansmultipro.opsapps.repository.ProductRepository;
import com.dansmultipro.opsapps.repository.TransactionRepository;
import com.dansmultipro.opsapps.repository.UserRepository;
import com.dansmultipro.opsapps.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends BaseService implements ProductService {

    private final ProductRepository productRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Override
    public ProductResponseDto findById(String id) {
        UUID productId = validateId(id);

        Product product =  productRepository.findById(productId).orElseThrow(
                () -> new NotFoundException("product not found")
        );

        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getCode()
        );
    }

    @Override
    public PageResponseDto<ProductResponseDto> findAll(Integer page, Integer size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products =  productRepository.findAll(pageable);

        List<ProductResponseDto> responseDto = new ArrayList<>();

        for (Product product : products) {
            responseDto.add(new ProductResponseDto(
                    product.getId(),
                    product.getName(),
                    product.getCode()
            ));
        }

        return new PageResponseDto<>(
                responseDto,
                products.getNumber(),
                products.getSize(),
                products.getTotalPages(),
                products.getTotalElements()
        );
    }

    @Override
    @Transactional(rollbackOn =  Exception.class)
    public CreateResponseDto createProduct(ProductRequestDto requestDto) {
        AuthorizationPojo principal = principalService.getPrincipal();
        UUID adminId = validateId(principal.getId());

        User admin = userRepository.findById(adminId).orElseThrow(
                () -> new NotFoundException("user not found")
        );

        if (productRepository.existsByCode(requestDto.getCode())) {
            throw new NotAllowedException("product code already exists");
        }

        if (productRepository.existsByName(requestDto.getName())) {
            throw new NotAllowedException("product code already exists");
        }

        if (!admin.getRole().getCode().equals(RoleCode.SA.name())){
            throw new NotAllowedException("Only admins can create products");
        }

        Product product = new Product();
        product.setName(requestDto.getName());
        product.setCode(requestDto.getCode());
        setCreate(product);

        Product savedProduct = productRepository.save(product);

        return new CreateResponseDto(savedProduct.getId(), "Product created successfully");
    }

    @Override
    @Transactional(rollbackOn =  Exception.class)
    public UpdateResponseDto updateProduct(String id, UpdateProductRequestDto requestDto) {
        AuthorizationPojo principal = principalService.getPrincipal();
        UUID adminId = validateId(principal.getId());
        UUID productId = validateId(id);

        User admin = userRepository.findById(adminId).orElseThrow(
                () -> new NotFoundException("user not found")
        );

        if (!admin.getRole().getCode().equals(RoleCode.SA.name())){
            throw new NotAllowedException("Only admins can update products");
        }

        Product existingProduct = productRepository.findById(productId).orElseThrow(
                () -> new NotFoundException("product not found")
        );

        if (!existingProduct.getVersion().equals(requestDto.getVersion())){
            throw new DataIntegrationException("version mismatch");
        }

        if (!existingProduct.getName().equals(requestDto.getName())) {
            if (productRepository.existsByName(requestDto.getName())) {
                throw new NotAllowedException("product name already exists");
            }
        }

        if (!existingProduct.getCode().equals(requestDto.getCode())) {
            if (productRepository.existsByCode(requestDto.getCode())) {
                throw new NotAllowedException("product code already exists");
            }
        }

        existingProduct.setName(requestDto.getName());
        existingProduct.setCode(requestDto.getCode());

        setUpdate(existingProduct);

        Product updatedProduct = productRepository.saveAndFlush(existingProduct);

        return new UpdateResponseDto(updatedProduct.getVersion(), "Product updated successfully");
    }

    @Override
    @Transactional(rollbackOn =  Exception.class)
    public DeleteResponseDto deleteProduct(String id) {
        AuthorizationPojo principal = principalService.getPrincipal();
        UUID adminId = validateId(principal.getId());
        UUID productId = validateId(id);

        User admin = userRepository.findById(adminId).orElseThrow(
                () -> new NotFoundException("user not found")
        );

        if (!admin.getRole().getCode().equals(RoleCode.SA.name())){
            throw new NotAllowedException("Only admins can delete products");
        }

        Product existingProduct = productRepository.findById(productId).orElseThrow(
                () -> new NotFoundException("product not found")
        );

        if (transactionRepository.existsByProduct(existingProduct)) {
            throw new NotAllowedException("product already on transactions");
        }

        productRepository.delete(existingProduct);

        return new DeleteResponseDto("product deleted successfully");
    }
}
