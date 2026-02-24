import { initializeCheckout } from './checkout.js';

// Helper to generate/retrieve unique Browser ID
async function getBrowserId() {
    const fpPromise = import('https://openfpcdn.io/fingerprintjs/v4')
        .then(FingerprintJS => FingerprintJS.load());

    const fp = await fpPromise;
    const result = await fp.get();
    return result.visitorId;
}

/**
 * Pre-check: Verifies stock levels with the Inventory Service.
 */
async function verifyStock(productId) {
    try {
        const response = await fetch(`/api/inventory/check/${productId}`);
        const data = await response.json();
        const buyButton = document.querySelector(`[data-product-id="${productId}"]`);

        if (buyButton && !data.available) {
            buyButton.disabled = true;
            buyButton.innerText = "Out of Stock";
            buyButton.classList.add("disabled-button");
        }
    } catch (error) {
        console.error("Inventory check failed for " + productId, error);
    }
}

// 1. Run inventory check for all products on page load
document.querySelectorAll('.buy-button').forEach(button => {
    const productId = button.getAttribute('data-product-id');
    verifyStock(productId);
});

// 2. Setup "Buy Now" click listeners
document.querySelectorAll('.buy-button').forEach(button => {
    button.addEventListener('click', async () => {
        const productId = button.getAttribute('data-product-id');

        // Retrieve the browser fingerprint
        const userId = await getBrowserId();

        // Switch UI views
        document.getElementById('product-list').style.display = 'none';
        document.getElementById('checkout-section').style.display = 'block';

        // Pass the productId and the Fingerprint ID to the checkout logic
        initializeCheckout(productId, userId);
    });
});

// 3. Setup Back Button
const backButton = document.getElementById('back-to-store');
if (backButton) {
    backButton.addEventListener('click', () => {
        location.reload();
    });
}