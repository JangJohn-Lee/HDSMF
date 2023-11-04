
<!-- ===============================================-->
<!--엑셀 양식 다운로드-->
<!-- ===============================================-->
function excelDownload(){
    //workbook 생성
    var wb = XLSX.utils.book_new();

    //시트 생성
    var newWorksheet = excelHandler.getWorksheet();

    //새로 만든 시트에 이름을 주고 붙임
    XLSX.utils.book_append_sheet(wb, newWorksheet, excelHandler.getSheetName());

    //엑셀 파일 만들기
    var wbout = XLSX.write(wb, {bookType:'xlsx',  type: 'binary'});

    //파일 내보내기
    saveAs(new Blob([s2ab(wbout)],{type:"application/octet-stream"}), excelHandler.getExcelFileName());
}

var excelHandler = {
    getExcelFileName : function(){
        return '출고등록(양식).xlsx';
    },
    getSheetName : function(){
        return 'Sheet1';
    },
    getExcelData : function(){
        return [['품번']];
    },
    getWorksheet : function(){
        return XLSX.utils.aoa_to_sheet(this.getExcelData());
    }
}

function s2ab(s) {
    var buf = new ArrayBuffer(s.length); //convert s to arrayBuffer
    var view = new Uint8Array(buf);  //create uint8array as viewer
    for (var i=0; i<s.length; i++) view[i] = s.charCodeAt(i) & 0xFF; //convert to octet
    return buf;
}


<!-- ===============================================-->
<!--출고 리스트 업로드-->
<!--case별로 나누어 배열 형태로 변환 후 searchIOBoundList 함수 실행-->
<!-- ===============================================-->

//case1. 하나의 입력값일 경우(한 개의 출고 등록): 입력값을 배열 형태로 변환
function search() {
    const textSearchList = document.getElementById('textSearchList');
    const keypad = document.getElementById('keypad02');

    var partNo = document.getElementById("outBoundSearch").value;
    var partNoList = new Array();

    textSearchList.style.display='none';
    keypad.style.display='none';
    partNoList.push(partNo);

    searchIOBoundList(partNoList);
}

function selectResult(result) {
    keypad = document.getElementById('keypad02');
    textSearchList = document.getElementById('textSearchList');

    // 클릭한 result 값을 input 요소에 넣음
    $("#outBoundSearch").val(result.textContent);

    $("#textSearchList").empty();
    textSearchList.style.display='none';
    keypad.style.display='none';

}

function searchNow(inputElement) {
    const keyword = $(inputElement).val();
    const textSearchList = document.getElementById('textSearchList');

    if(keyword.length >=1){
        textSearchList.style.display = 'block';
    }

    $.ajax({
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json',
        url: '/hdsmf/searchNumNowOut',
        data: keyword, // 큰따옴표로 감싸지 않고 문자열 그대로 전송
        error: function (err) {
        },
        success: function (data) {
            const textSearchList = $("#textSearchList");
            textSearchList.empty();

            data.forEach((result) => {
                const resultElement = $("<p class='textSearchListUnit'></p>").text(result);
                resultElement.click(function () {
                    selectResult(resultElement[0]);
                });
                textSearchList.append(resultElement);
            });
        }
    });
}

//case2. 엑셀 업로드일 경우(출고 일괄 등록): 입력값을 배열 형태로 변환
function excelUpload(){
    var fileInput = document.getElementById("file");
    var file = fileInput.files[0];

    var formData = new FormData();
    formData.append('excelFile', file);

    if(file == undefined){
        // alert("파일을 선택해주세요");

        const selectFileNotice = document.querySelector(".selectFileNotice");
        selectFileNotice.style.display = "block";

        setTimeout(function(){
            selectFileNotice.style.display = "none";
        }, 2000);

        return;
    }else{
        $.ajax({
            url: 'excelUpload',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function (result){
                searchIOBoundList(result);
            },
            error: function (){
            }
        })
    }
}

//출고 리스트 화면 업로드 함수
function searchIOBoundList(searchList){

    var tdList = document.getElementsByClassName("eachRack");
    for(var i = 0; i < tdList.length; i++){
        tdList[i].classList.remove("rackSearchSelect")
        tdList[i].textContent= null;
    }

    var partNoList = searchList
    var IOBoundList = new Array();

    $.ajax({
        url:'search',
        type:'POST',
        traditional: true,
        data: {
            "partNoList" : partNoList
        },
        success:function (map){

            if(map.resultMap.length != 0){
                str = '<tr>';

                for (let i = 0; i < map.resultMap.length; i++) {
                    var rackNum = String(map.resultArr[i]);
                    str += '<td name="checkList" class="text-center ps-2">'
                    str += `<input type="checkbox" checked name = "outCheck" value = "1" id = "${rackNum}" onclick="checkRackLocation('${rackNum}', ${i})">`;
                    str += '</td><td class = "text-center ioIndex pt-2 ps-2">';
                    str += i + 1;
                    str += '</td><td class = "text-center componentNo ps-2">';
                    str += map.resultMap[i].componentNo;
                    str += '</td><td class = "text-center status ps-2">';
                    switch ((map.resultMap[i].componentNo).slice(-2)){
                        case '-1':
                            str += '소재';
                            break;
                        case '-2':
                            str += 'CNC 1차';
                            break;
                        case '-3':
                            str += 'CNC 2차';
                            break;
                        case '-4':
                            str += 'MCT 1차';
                            break;
                        default:
                            str += '완제품';
                            break;
                    }
                    str += '</td>';
                    str += '<td class="ps-2 componentAmount text-center">';
                    str += map.resultMap[i].componentAmount;
                    str += '</td><td class ="location text-center ps-2" id= ' + map.resultMap[i].rackDetailNo +'>';
                    str += map.resultArr[i];
                    str += '</td></tr>';
                    var td = document.getElementById(map.resultArr[i]);
                    td.classList.add("rackSearchSelect");
                    td.textContent = i+1;

                }
                $('.ioBoundList').empty();
                $('.ioBoundList').append(str);

                // 모달창 종료
                const modalDismissBtn = document.querySelector(".modalDismissBtn");
                modalDismissBtn.click();

            }else if(map.resultMap.length == 0){
                alert("적재된 부품이 존재하지 않습니다.");
            }
        },
        error:function (){
        }
    })
}

function checkRackLocation(result, num){

    var checkBox = $(`input:checkbox[id ='${result}']:checked`);
    var td = document.getElementById(result);

    if(checkBox.length>0){
        td.classList.add("rackSearchSelect");
        td.textContent = num+1;
    }else{
        td.classList.remove("rackSearchSelect");
        td.textContent = null;
    }
}



function selectAll(){

    var checkBox = $('input:checkbox[id = "selectAllCheckBox"]:checked');

    var checkBoxList = $('input:checkbox[name = "outCheck"]');

    if(checkBox.length > 0){
        checkBoxList.prop("checked", true);
        for(var i = 0; i < checkBoxList.length; i++){
            document.getElementById(checkBoxList[i].id).classList.add("rackSearchSelect");
            document.getElementById(checkBoxList[i].id).textContent = i+1;
        }
    }else{
        checkBoxList.prop("checked", false);
        for(var i = 0; i < checkBoxList.length; i++){
            document.getElementById(checkBoxList[i].id).classList.remove("rackSearchSelect");
            document.getElementById(checkBoxList[i].id).textContent = null;
        }
    }
}



<!-- ===============================================-->
<!--출고 처리-->
<!-- ===============================================-->
function outBound(){

    var checkBox = $('input:checkbox[name ="outCheck"]:checked');

//    var result = confirm("출고 처리 하시겠습니까?" +'\n'+ "출고 처리 후 화면 출력은 불가합니다.");
//    if(result == true){
        var outBoundList = new Array();
        checkBox.each(function (i){
            var tr = checkBox.parent().parent().eq(i);
            var td = tr.children();
            var location = td.eq(5).attr('id');
            outBoundList.push(parseInt(location));
        })
        $.ajax({
            url:'outBound',
            type:'POST',
            traditional: true,
            data: {"locationList" : outBoundList
            },
            success: function (){
                location.href = "outbound";
            }
        })
}


(function ($) {
    (function (pluginName) {
        var defaults = {
            inputField: 'input.keypad',
            buttonTemplate: '<button></button>',
            submitButtonText: '입력',
            deleteButtonText: '←',
            submitButtonClass: 'submit',
            deleteButtonClass: 'delete'
        };
        $.fn[pluginName] = function (options) {
            options = $.extend(true, {}, defaults, options);

            return this.each(function () {
                var elem = this,
                    $elem = $(elem),
                    $input = options.inputField instanceof jQuery ? options.inputField : $(options.inputField),
                    $form = $input.parents('form').length ? $($input.parents('form')[0]) : $elem;

                var numbers = Array.apply(null, Array(9)).map(function (_, i) {
                    return $(options.buttonTemplate).html(i + 1).addClass('number');
                });
                numbers.push($(options.buttonTemplate).html(options.submitButtonText).addClass(options.submitButtonClass));
                numbers.push($(options.buttonTemplate).html("0").addClass('number'));
                numbers.push($(options.buttonTemplate).html(options.deleteButtonText).addClass(options.deleteButtonClass));

                $elem.html(numbers).addClass('keypad');

                $elem.find('.number').click(function (e) {
                    $input.val($input.val() + $(e.target).text());
                    $input.trigger('change');
                    //버튼 누르는 동작 css
                });
                $elem.find('.' + options.deleteButtonClass).click(function (e) {
                    $input.val($input.val().slice(0, -1));
                    $input.trigger('change');
                });
                $elem.find('.' + options.submitButtonClass).click(function (e) {
                    $('#keypad02').css('display', 'none');
                });
            });
        };
        $.fn[pluginName].defaults = defaults;
    })('keypad');
})(jQuery);


function downloadExcelFile() {
    // Sample data, replace this with your actual data
    const table = document.getElementById('outBoundTable');
    const thead = table.querySelector('thead');
    const tbody = table.querySelector('tbody');

    const data = [];

    // Read thead (headers)
    const headers = [];
    thead.querySelectorAll('th').forEach((th, index) => {
        // Exclude the "선택" column from headers
        if (index !== 0) {
            headers.push(th.textContent);
        }
    });
    data.push(headers);

    // Read tbody (rows)
    tbody.querySelectorAll('tr').forEach((tr) => {
        const rowData = [];
        tr.querySelectorAll('td').forEach((td, index) => {
            if (index !== 0) {
                rowData.push(td.textContent);
            }
        });
        data.push(rowData);
    });


    const workbook = XLSX.utils.book_new();
    const worksheet = XLSX.utils.aoa_to_sheet(data);

    XLSX.utils.book_append_sheet(workbook, worksheet, 'Sheet1');

    // Export the workbook as a Blob
    const workbookBlob = new Blob([s2ab(XLSX.write(workbook, { type: 'binary', bookType: 'xlsx' }))], {
        type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    });

    // Download the Blob as an Excel file
    const link = document.createElement('a');
    link.href = URL.createObjectURL(workbookBlob);

    link.download = '출고리스트 다운로드.xlsx';
    link.click();
}

// 숫자패드 다른곳 클릭시 사라짐
document.addEventListener('DOMContentLoaded', function() {
  // keypad02 요소를 토글하는 함수
  function toggleKeypad() {
    var keypad = document.getElementById('keypad02');
    if (keypad.style.display === 'none') {
      keypad.style.display = 'block';
    } else {
      keypad.style.display = 'none';
    }
  }

  // input[type="text"]와 input[type="number"] 요소를 클릭해도 keypad02를 숨기지 않도록 이벤트 핸들러 추가
  var textAndNumberInputs = document.querySelectorAll('input[type="text"], input[type="number"]');
  textAndNumberInputs.forEach(function(input) {
    input.addEventListener('click', function(event) {
      event.stopPropagation(); // 이벤트 버블링을 방지하여 body의 클릭 이벤트를 막습니다.
    });
  });

  // body를 클릭했을 때 keypad02를 숨기지 않습니다.
  document.body.addEventListener('click', function(event) {
    var keypad = document.getElementById('keypad02');
    if (!keypad.contains(event.target)) {
      keypad.style.display = 'none';
    }
  });
});
