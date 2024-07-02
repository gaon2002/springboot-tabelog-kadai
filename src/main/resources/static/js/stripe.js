////サブスク支払い
//
//const stripe = Stripe('pk_test_51P766BRuhzAsDRtmgYgNH7PCdnH2s60NEpcqhOf0ASkHMXZMGmF3nGFp5hYysebhJuZWknbSzVvjDw4q6yJMl5Gl00gNbyej8x');    
//const paymentButton = document.querySelector('#checkout-button');
//
//paymentButton.addEventListener('click', event => {
//    // デバッグ用のログを追加
//    console.log('Button clicked');
//    console.log('Session ID:', sessionId);
//
//    // デフォルトのフォーム送信を抑制
//    event.preventDefault();
//
//    stripe.redirectToCheckout({
//        sessionId: sessionId
//        
//    }).then(function (result) {
//        if (result.error) {
//            // エラーハンドリング(例：ユーザーにエラーメッセージを表示)
//            console.error(result.error.message);
//        }
//    });
//});


document.addEventListener('DOMContentLoaded', (event) => {
    const stripe = Stripe('pk_test_51P766BRuhzAsDRtmgYgNH7PCdnH2s60NEpcqhOf0ASkHMXZMGmF3nGFp5hYysebhJuZWknbSzVvjDw4q6yJMl5Gl00gNbyej8x');
    const paymentButton = document.querySelector('#checkout-button');
    const checkoutForm = document.querySelector('#checkout-form');

    console.log('Embedded Session ID:', sessionId);

    paymentButton.addEventListener('click', event => {
        console.log('Button clicked');
        event.preventDefault();

        if (sessionId && sessionId !== "null") {
            // フォームを手動で送信
            checkoutForm.submit();
        } else {
            console.error('Session ID is null or invalid.');
        }
    });
});


