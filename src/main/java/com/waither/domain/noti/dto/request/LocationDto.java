package com.waither.domain.noti.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LocationDto (
        @NotBlank(message = " 위도(latitude) 값은 필수입니다.")
        @DecimalMax(value = "43.0", inclusive = true, message = "대한민국 내에서만 가능합니다. (33~43)")
        @DecimalMin(value = "33.0", inclusive = true, message = "대한민국 내에서만 가능합니다. (33~43)")
        double latitude,

        @NotBlank(message = " 경도(longitude) 값은 필수입니다.")
        @DecimalMax(value = "132.0", inclusive = true, message = "대한민국 내에서만 가능합니다. (124~132)")
        @DecimalMin(value = "124.0", inclusive = true, message = "대한민국 내에서만 가능합니다. (124~132)")
        double longitude
) {


}
