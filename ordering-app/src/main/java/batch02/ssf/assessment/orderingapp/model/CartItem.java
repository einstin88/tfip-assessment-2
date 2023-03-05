package batch02.ssf.assessment.orderingapp.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CartItem(
        @NotBlank(message = "No <b>item</b> selected")
        String item,

        @NotNull(message = "<b>Quantity</b> cannot be empty")
        @Min(value = 1, message = "<b>Quantity</b> must be at least 1")
        Integer quantity) {
}
