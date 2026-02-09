package com.dansmultipro.opsapps.contoller;

import com.dansmultipro.opsapps.dto.CreateResponseDto;
import com.dansmultipro.opsapps.dto.DeleteResponseDto;
import com.dansmultipro.opsapps.dto.PageResponseDto;
import com.dansmultipro.opsapps.dto.UpdateResponseDto;
import com.dansmultipro.opsapps.dto.product.ProductRequestDto;
import com.dansmultipro.opsapps.dto.product.ProductResponseDto;
import com.dansmultipro.opsapps.dto.product.UpdateProductRequestDto;
import com.dansmultipro.opsapps.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SA', 'PG', 'CUS')")
    public ResponseEntity<ProductResponseDto> getById(@PathVariable String id){
        ProductResponseDto responseDto = productService.findById(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SA', 'PG', 'CUS')")
    public ResponseEntity<PageResponseDto<ProductResponseDto>> getAllProducts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "5") Integer size) {
        PageResponseDto<ProductResponseDto> pageResponseDto = productService.findAll(page, size);
        return new ResponseEntity<>(pageResponseDto, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SA')")
    public ResponseEntity<CreateResponseDto> createProduct(@RequestBody ProductRequestDto productRequestDto){
        CreateResponseDto responseDto =  productService.createProduct(productRequestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SA')")
    public ResponseEntity<UpdateResponseDto> updateProduct(@PathVariable String id, @RequestBody @Valid UpdateProductRequestDto updateRequestDto){
        UpdateResponseDto responseDto = productService.updateProduct(id, updateRequestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SA')")
    public ResponseEntity<DeleteResponseDto> deleteProduct(@PathVariable String id){
        DeleteResponseDto responseDto = productService.deleteProduct(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }


}
