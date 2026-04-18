package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCountDTO {
    private String category;
    private Long count;
    private String code;
}
