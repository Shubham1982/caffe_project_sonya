package org.example.caffe.event;

public class AdjustStatsEvent {
    private final Long productId;
    private final Long orderItemId;
    private final Long quantityDelta;
    private final String notes;

    public AdjustStatsEvent(Long productId, Long orderItemId, Long quantityDelta, String notes) {
        this.productId = productId;
        this.orderItemId = orderItemId;
        this.quantityDelta = quantityDelta;
        this.notes = notes;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public Long getQuantityDelta() {
        return quantityDelta;
    }

    public String getNotes() {
        return notes;
    }
}
