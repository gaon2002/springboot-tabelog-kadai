
// stripe.js
document.addEventListener('DOMContentLoaded', function() {
    if (window.Stripe) {
        const stripe = Stripe('pk_test_51P766BRuhzAsDRtmgYgNH7PCdnH2s60NEpcqhOf0ASkHMXZMGmF3nGFp5hYysebhJuZWknbSzVvjDw4q6yJMl5Gl00gNbyej8x');
        const elements = stripe.elements();
        const cardElement = elements.create('card');
        cardElement.mount('#card-element');

        const form = document.getElementById('payment-form');
        form.addEventListener('submit', async (event) => {
            event.preventDefault();

            try {
                const {paymentMethod, error} = await stripe.createPaymentMethod('card', cardElement);

                if (error) {
                    console.error(error);
                    alert('支払い情報の更新に失敗しました。再試行してください。');
                } else {
                    document.getElementById('paymentMethodId').value = paymentMethod.id;
                    form.submit();
                }
            } catch (err) {
                console.error('エラーが発生しました', err);
                alert('システムエラーが発生しました。');
            }
        });
    } else {
        console.error('Stripeライブラリが正しく読み込まれていません。');
        alert('Stripeライブラリの読み込みに失敗しました。');
    }
});
