/**
 * CSVファイル出力用
 * ADD:2024-07-13
 */
/* 会員一覧 */
$(document).on('submit','form[action="/admin/users/csv"]', function() {
    var inputValue = $('input[name="keyword"]').val();
    console.log('入力された値: ' + inputValue);
    $('input[name="keyword2"]').val(inputValue);
});
