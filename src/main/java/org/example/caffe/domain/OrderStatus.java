package org.example.caffe.domain;

/**
 * Lifecycle states of an OrderItem in the system.
 *
 * PLACED     → Order has been successfully placed
 * CONFIRMED  → Order has been confirmed by the store
 * PREPARING  → Order is being prepared / in kitchen
 * READY      → Order is ready for pickup / dispatch
 * DELIVERED  → Order has been delivered to the customer
 * CANCELLED  → Order was cancelled (soft-deleted)
 */
public enum OrderStatus {
    PLACED,
    CONFIRMED,
    PREPARING,
    READY,
    DELIVERED,
    CANCELLED
}
