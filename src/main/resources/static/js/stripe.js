//サブスク支払い

const stripe = Stripe('pk_test_51P766BRuhzAsDRtmgYgNH7PCdnH2s60NEpcqhOf0ASkHMXZMGmF3nGFp5hYysebhJuZWknbSzVvjDw4q6yJMl5Gl00gNbyej8x');    
const paymentButton = document.querySelector('#checkout-button');

paymentButton.addEventListener('click', event => {
    // デバッグ用のログを追加
    console.log('Button clicked');
    console.log('Session ID:', sessionId);

    // デフォルトのフォーム送信を抑制
    event.preventDefault();

    stripe.redirectToCheckout({
        sessionId: sessionId
        
    }).then(function (result) {
        if (result.error) {
            // エラーハンドリング(例：ユーザーにエラーメッセージを表示)
            console.error(result.error.message);
        }
    });
});


//document.addEventListener('DOMContentLoaded', (event) => {
//    const stripe = Stripe('pk_test_51P766BRuhzAsDRtmgYgNH7PCdnH2s60NEpcqhOf0ASkHMXZMGmF3nGFp5hYysebhJuZWknbSzVvjDw4q6yJMl5Gl00gNbyej8x'); // Stripeの初期化
//    const paymentButton = document.querySelector('#checkout-button');
//
//    if (paymentButton) {
//        paymentButton.addEventListener('click', (event) => {
//            // デバッグ用のログを追加
//            console.log('Button clicked');
//            console.log('Session ID:', sessionId);
//
//            // デフォルトのフォーム送信を抑制
//            event.preventDefault();
//
//            // Stripeのcheckout処理へ
//            stripe.redirectToCheckout({
//                sessionId: sessionId
//            }).then(function (result) {
//                if (result.error) {
//                    // エラーハンドリング(例：ユーザーにエラーメッセージを表示)
//                    console.error(result.error.message);
//                }
//            });
//        });
//    } else {
//        console.error('Checkout button not found');
//    }
//});


