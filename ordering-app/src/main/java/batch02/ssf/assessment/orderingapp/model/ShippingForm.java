package batch02.ssf.assessment.orderingapp.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ShippingForm(

    @NotBlank(message = "<b>Name</b> is mandatory")
    @Size(min = 2, message = "<b>Name</b> must be at least 2 characters long")
    String name,

    @NotBlank(message = "<b>Address</b> is mandatory")
    String address
) {
    
}
