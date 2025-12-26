package com.shop.dto.admin;

import com.shop.enums.ProductStatus;
import lombok.Data;

@Data
public class UpdateStatusRequest {
    private ProductStatus status;
}
