// カード番号変更

// Stripeオブジェクトの初期化
var stripe = Stripe('pk_test_51P766BRuhzAsDRtmgYgNH7PCdnH2s60NEpcqhOf0ASkHMXZMGmF3nGFp5hYysebhJuZWknbSzVvjDw4q6yJMl5Gl00gNbyej8x');

// Stripe Elementsの初期化
var elements = stripe.elements();

// カード入力フィールドのスタイル
var style = {
  base: {
    fontSize: '16px',
    color: '#32325d',
  }
};

// カード番号入力フィールドの作成とマウント
var cardNumber = elements.create('cardNumber', {style: style});
cardNumber.mount('#card-element');

// 有効期限入力フィールドの作成とマウント
var cardExpiry = elements.create('cardExpiry', {style: style});
cardExpiry.mount('#card-expiry');

// CVCフィールドの作成とマウント
var cardCvc = elements.create('cardCvc', {style: style});
cardCvc.mount('#card-cvc');

// フォームの送信イベントリスナー
var form = document.getElementById('payment-form');
form.addEventListener('submit', function(event) {
  event.preventDefault();

  stripe.createPaymentMethod({
    type: 'card',
    card: cardNumber,
  }).then(function(result) {
    if (result.error) {
      // エラー処理
      var errorElement = document.getElementById('card-errors');
      errorElement.textContent = result.error.message;
    } else {
      // PaymentMethod IDをサーバーに送信
      var hiddenInput = document.createElement('input');
      hiddenInput.setAttribute('type', 'hidden');
      hiddenInput.setAttribute('name', 'paymentMethodId');
      hiddenInput.setAttribute('value', result.paymentMethod.id);
      form.appendChild(hiddenInput);

      // フォームを送信
      form.submit();
    }
  });
});

// 各フィールドの入力エラーハンドリング
cardNumber.addEventListener('change', function(event) {
  var errorElement = document.getElementById('card-errors');
  if (event.error) {
    errorElement.textContent = event.error.message;
  } else {
    errorElement.textContent = '';
  }
});

cardExpiry.addEventListener('change', function(event) {
  var errorElement = document.getElementById('card-errors');
  if (event.error) {
    errorElement.textContent = event.error.message;
  } else {
    errorElement.textContent = '';
  }
});

cardCvc.addEventListener('change', function(event) {
  var errorElement = document.getElementById('card-errors');
  if (event.error) {
    errorElement.textContent = event.error.message;
  } else {
    errorElement.textContent = '';
  }
});




//document.addEventListener('DOMContentLoaded', function() {
//    if (window.Stripe) {
//        const stripe = Stripe('pk_test_51P766BRuhzAsDRtmgYgNH7PCdnH2s60NEpcqhOf0ASkHMXZMGmF3nGFp5hYysebhJuZWknbSzVvjDw4q6yJMl5Gl00gNbyej8x');
//        const elements = stripe.elements();
//        const cardElement = elements.create('card');
//        cardElement.mount('#card-element');
//
//        const form = document.getElementById('payment-form');
//        form.addEventListener('submit', async (event) => {
//            event.preventDefault();
//
//            try {
//                const {paymentMethod, error} = await stripe.createPaymentMethod('card', cardElement);
//
//                if (error) {
//                    console.error(error);
//                    alert('支払い情報の更新に失敗しました。再試行してください。');
//                } else {
//                    document.getElementById('paymentMethodId').value = paymentMethod.id;
//                    form.submit();
//                }
//            } catch (err) {
//                console.error('エラーが発生しました', err);
//                alert('システムエラーが発生しました。');
//            }
//        });
//    } else {
//        console.error('Stripeライブラリが正しく読み込まれていません。');
//        alert('Stripeライブラリの読み込みに失敗しました。');
//    }
//});
