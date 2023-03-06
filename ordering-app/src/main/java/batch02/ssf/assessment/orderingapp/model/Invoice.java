package batch02.ssf.assessment.orderingapp.model;

public record Invoice(
    String id,
    String name,
    String address,
    Float total
) {
    
}
