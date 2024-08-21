package com.waither.domain.noti.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record LocationDto (
        @NonNull
        @DecimalMax(value = "43.0", inclusive = true, message = "대한민국 내에서만 가능합니다. (33~43)")
        @DecimalMin(value = "33.0", inclusive = true, message = "대한민국 내에서만 가능합니다. (33~43)")
        Double latitude,

        @NonNull
        @DecimalMax(value = "132.0", inclusive = true, message = "대한민국 내에서만 가능합니다. (124~132)")
        @DecimalMin(value = "124.0", inclusive = true, message = "대한민국 내에서만 가능합니다. (124~132)")
        Double longitude
) {


}
