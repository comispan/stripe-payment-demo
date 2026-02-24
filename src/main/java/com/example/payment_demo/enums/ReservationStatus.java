package com.example.payment_demo.enums;

/**
 * Represents the current lifecycle stage of a product reservation.
 */
public enum ReservationStatus {
    /** The item is currently held for a user awaiting payment confirmation. */
    PENDING,

    /** The payment was successful and the stock has been permanently deducted. */
    COMPLETED,

    /** The 10-minute window expired without a successful payment. */
    EXPIRED,

    /** The transaction was cancelled by the user or failed at checkout. */
    CANCELLED
}