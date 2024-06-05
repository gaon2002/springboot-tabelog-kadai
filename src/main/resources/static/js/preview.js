const imageInput = document.getElementById('imageFile');
const imagePreview = document.getElementById('imagePreview');

// ファイルが選択されたときやファイルが変更されたとき('change')
imageInput.addEventListener('change', () => {
  //選択されたファイルがあればtrue
  if (imageInput.files[0]) {
	// fileReaderオブジェクトを作成
    let fileReader = new FileReader();
    
    // ファイルの読み込みが完了したときに実行される関数
    fileReader.onload = () => {
	  // classがimagePreviewのdivに''内の要素を追加する
      imagePreview.innerHTML = `<img src="${fileReader.result}" class="mb-3">`;
    }
    //FileReaderオブジェクトを使用して、選択されたファイルを読み込む。
    fileReader.readAsDataURL(imageInput.files[0]);
    
  } else {
	// 選択されたファイルがない場合、image要素の中身を空にする
    imagePreview.innerHTML = '';
  }
})
