
//	DOMContentLoadedイベントリスナーを使用して、DOMが完全に読み込まれた後にJavaScriptが実行されるようにします。
document.addEventListener('DOMContentLoaded', function () {
	
  let maxDate = new Date();
  maxDate.setMonth(maxDate.getMonth() + 3);

  const config1 = {
    wrap: true,
    locale: 'ja',
    minDate: new Date(),
    maxDate: maxDate,
  };
  
  const config2 = {
    enableTime: true,
  	noCalendar: true,
  	dateFormat: "H:i",
  	minDate: "16:00",
    maxDate: "24:00",
    defaultDate: "16:00",
  	time_24hr: true,
    
  };

  flatpickr('#reservedDate', config1);
  flatpickr('#reservedTime', config2);
  
});
