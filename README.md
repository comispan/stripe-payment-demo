## 🛒 E-Commerce Checkout & Inventory Workflow

This project implements a robust, event-driven system to manage product stock during the checkout process. It ensures that inventory is "held" for a user while they are paying, preventing overselling.

1. Browser Identification
   - Unique Visitor ID: When a user lands on the store page, the frontend uses FingerprintJS to generate a `unique` userId based on the browser's attributes. 
   - Persistence: This ID stays the same even if the user refreshes the page, allowing the system to track their specific "hold" on an item.


2. Inventory Pre-Check & Reservation
   - Stock Verification: Before allowing a checkout, the frontend calls the `/api/inventory/check/{productId}` endpoint. If the item is out of stock, the "Buy" button is automatically disabled. 
   - Creation of Hold: When a user clicks "Buy," the backend creates a Product Reservation record in PostgreSQL with a status of `PENDING`.
   - Calculation: The system calculates "Available Stock" as: `Total Quantity - (Active PENDING Reservations + COMPLETED Sales)`.


3. Secure Payment with Stripe
   - Metadata Attachment: The `productId`, `userId`, and `reservationId` are attached to the Stripe PaymentIntent metadata.
   - Embedded Checkout: The user completes their purchase securely using Stripe Elements without leaving the store.


4. Event-Driven Confirmation (Kafka)
   - Webhook Processing: Upon successful payment, Stripe sends a `payment_intent.succeeded` event to the `StripeWebhookController`.
   - Asynchronous Update: The controller verifies the event signature and publishes a message to Kafka containing the transaction details.
   - Finalization: The Inventory Service consumes this Kafka message and updates the reservation status in the database from `PENDING` to `COMPLETED`.


5. Automatic Stock Release
   - Expiration Logic: To prevent stock from being locked indefinitely by users who abandon their carts, a Scheduled Task runs every minute.
   - Cleanup: Any `PENDING` reservation older than 10 minutes is marked as `EXPIRED`, making that item immediately available for other shoppers.

🛠️ Tech Stack
- Frontend: HTML5, CSS3, JavaScript (ES6 Modules), Stripe Elements.
- Backend: Spring Boot, Java 17+, Spring Data JPA.
- Messaging: Apache Kafka (via Spring Kafka).
- Database: PostgreSQL.
- Infrastructure: Podman/Docker Compose.

# Getting Started

Start stripe CLI:
 - stripe listen --forward-to localhost:8080/api/webhooks/stripe

Credentials to update:
- const stripe = Stripe('pk_test_xxx'); in `checkout.js`
- stripe.api.key=sk_test_xxx in `application.properties`
- stripe.webhook.secret=whsec_xxx in `application.properties`
