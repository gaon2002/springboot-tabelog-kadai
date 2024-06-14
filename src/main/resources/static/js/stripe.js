//サブスク支払い

 const stripe = Stripe('pk_test_51P766BRuhzAsDRtmgYgNH7PCdnH2s60NEpcqhOf0ASkHMXZMGmF3nGFp5hYysebhJuZWknbSzVvjDw4q6yJMl5Gl00gNbyej8x');
 const paymentButton = document.querySelector('#checkout-button');
 
 paymentButton.addEventListener('click', () => {
// Stripeのcheckout処理へ
   stripe.redirectToCheckout({
     sessionId: sessionId
   })
 });