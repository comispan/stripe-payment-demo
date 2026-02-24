# Getting Started

Start stripe CLI:
 - stripe listen --forward-to localhost:8080/api/webhooks/stripe
 - update stripe.webhook.secret in application.properties

Credentials to update:
- const stripe = Stripe('pk_test_xxx'); in checkout.js
- stripe.api.key=sk_test_xxx in application.properties 
- stripe.webhook.secret=whsec_xxx in application.properties
