const stripe = Stripe('pk_test_xxx');

/**
 * Logic for the Checkout Page
 */
export async function initializeCheckout(productId, userId) {

    const response = await fetch("/api/payments/create-intent", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                productId: productId,
                userId: userId
            })
        });

    const { clientSecret } = await response.json();

    // Pass the locale to ensure the PayNow interface is localized correctly
    const elements = stripe.elements({
        clientSecret,
        locale: 'en-SG'
    });

    const paymentElement = elements.create("payment");
    paymentElement.mount("#payment-element");

    const form = document.getElementById('payment-form');
    const submitButton = document.getElementById('submit');
    const buttonText = document.getElementById('button-text');
    const spinner = document.getElementById('spinner');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        // 1. Disable the button to prevent multiple clicks
        setLoading(true);

        const { error } = await stripe.confirmPayment({
            elements,
            confirmParams: {
                return_url: window.location.origin + "/status.html",
            },
        });

        // If we get here, the payment started.
        // We can now "poll" Stripe to see when the status changes to succeeded.
        const checkPayment = setInterval(async () => {
            const { paymentIntent } = await stripe.retrievePaymentIntent(clientSecret);

            if (paymentIntent.status === "succeeded") {
                clearInterval(checkPayment);
                // Update the UI on the ORIGINAL page
                document.getElementById('checkout-section').innerHTML = "<h1>Payment Successful!</h1>";
            }
        }, 3000); // Check every 3 seconds

        // 2. If there's an error, re-enable the button so the user can try again
        if (error) {
            setLoading(false);
            document.getElementById('error-message').textContent = error.message;
        }
    });

    // Helper function to toggle UI state
    function setLoading(isLoading) {
        if (isLoading) {
            submitButton.disabled = true;
            buttonText.style.display = 'none';
            spinner.style.display = 'inline';
        } else {
            submitButton.disabled = false;
            buttonText.style.display = 'inline';
            spinner.style.display = 'none';
        }
    }

}

/**
 * Logic for the Success/Status Page
 */
export async function checkStatus() {
    const clientSecret = new URLSearchParams(window.location.search).get("payment_intent_client_secret");
    if (!clientSecret) return;

    const { paymentIntent } = await stripe.retrievePaymentIntent(clientSecret);
    const heading = document.getElementById("status-heading");
    const message = document.getElementById("status-message");
    const details = document.getElementById("transaction-details");

    // Display the Transaction ID (starts with pi_...)
    if (details) {
        details.innerHTML = `<strong>Transaction ID:</strong> ${paymentIntent.id}`;
    }

    switch (paymentIntent.status) {
        case "succeeded":
            heading.innerText = "Payment Succeeded! 🎉";
            message.innerText = `Success! Your payment of ${(paymentIntent.amount / 100).toFixed(2)} ${paymentIntent.currency.toUpperCase()} was processed.`;
            break;
        case "processing":
            heading.innerText = "Payment Processing...";
            message.innerText = "Your payment is currently processing. We'll update you as soon as it's confirmed.";
            break;
        case "requires_payment_method":
            heading.innerText = "Payment Failed.";
            if (paymentIntent.last_payment_error) {
                message.innerText = paymentIntent.last_payment_error.message;
            } else {
                message.innerText = "Please try another payment method.";
            }
            break;
        case "canceled":
            heading.innerText = "Payment Canceled.";
            message.innerText = "This payment was manually canceled.";
            break;
        default:
            heading.innerText = "Something went wrong.";
            message.innerText = "An unexpected error occurred. Please contact support.";
            break;
    }
}