package com.dansmultipro.opsapps.service;

import com.dansmultipro.opsapps.dto.CreateResponseDto;
import com.dansmultipro.opsapps.dto.DeleteResponseDto;
import com.dansmultipro.opsapps.dto.PageResponseDto;
import com.dansmultipro.opsapps.dto.UpdateResponseDto;
import com.dansmultipro.opsapps.dto.product.ProductRequestDto;
import com.dansmultipro.opsapps.dto.product.ProductResponseDto;
import com.dansmultipro.opsapps.dto.product.UpdateProductRequestDto;

public interface ProductService {
    ProductResponseDto findById(String id);
    PageResponseDto<ProductResponseDto> findAll(Integer page, Integer size);
    CreateResponseDto createProduct(ProductRequestDto requestDto);
    UpdateResponseDto updateProduct(String id, UpdateProductRequestDto requestDto);
    DeleteResponseDto deleteProduct(String id);
}
