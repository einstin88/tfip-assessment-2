package batch02.ssf.assessment.orderingapp.model;

import java.util.List;

public record QuotationResponse(
    String quoteId,
    List<QuotationItem> quotations
) {
    
}
