
<!-- ===============================================-->
<!--엑셀 양식 다운로드-->
<!-- ===============================================-->
function excelDownload(){
    //workbook 생성
    var wb = XLSX.utils.book_new();

    //첫 번째 시트 생성
    var firstWorkSheet = excelHandler.getWorksheet();
    XLSX.utils.book_append_sheet(wb, firstWorkSheet, excelHandler.getSheetName());

    //두 번째 시트 생성
    var secondWorkSheetData = [
        ['공정상태', '번호'],
        ['소재', 1],
        ['CNC 1차', 2],
        ['CNC 2차', 3],
        ['MCT 1차', 4],
        ['완제품', 0]
    ]
    var secondWorkSheet = XLSX.utils.aoa_to_sheet(secondWorkSheetData);
    XLSX.utils.book_append_sheet(wb, secondWorkSheet, '공정상태 정보'); // 시트 이름 설정

    //엑셀 파일 만들기
    var wbout = XLSX.write(wb, {bookType:'xlsx',  type: 'binary'});

    //파일 내보내기
    saveAs(new Blob([s2ab(wbout)],{type:"application/octet-stream"}), excelHandler.getExcelFileName());
}

var excelHandler = {
    getExcelFileName : function(){
        return '입고 등록(양식).xlsx';
    },
    getSheetName : function(){
        return 'Sheet1';
    },
    getExcelData : function(){
        return [['품번', '공정상태', '개수']];
    },
    getWorksheet : function(){
        return XLSX.utils.aoa_to_sheet(this.getExcelData());
    }
}

function s2ab(s) {
    var buf = new ArrayBuffer(s.length);
    var view = new Uint8Array(buf);
    for (var i=0; i<s.length; i++) view[i] = s.charCodeAt(i) & 0xFF; //convert to octet
    return buf;
}


function downloadExcelFile() {
    // Sample data, replace this with your actual data
    const table = document.getElementById('inBoundTable');
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

    link.download = '리스트 다운로드.xlsx';
    link.click();
}


<!-- ===============================================-->
<!--입고 리스트 추가-->
<!-- ===============================================-->

//case1. 개별 추가 후 업로드
function add() {
    var numOfTable = $('#inBoundTable >tbody tr').length;
    //var selectBox = document.getElementById("inBoundOption");
    var processSelectBox = document.getElementById("processOption");

    //var optionVal = selectBox.options[selectBox.selectedIndex].value;
    var processOptionVal = processSelectBox.options[processSelectBox.selectedIndex].value;
    var inBoundText = document.getElementById("inBoundText").value;
    var inBoundNum = document.getElementById("inBoundNum").value;
    const keypad02 = document.getElementById("keypad02");
    const textSearchList = document.getElementById("textSearchList");

    textSearchList.style.display = "none";
    keypad02.style.display = "none";
    if(inBoundText.length == 0){
//        alert("품번을 입력해주세요!");
    }else if(inBoundNum.length == 0 || inBoundNum == 0){
//        alert("수량을 입력해주세요!");
    }else if(processOptionVal.length == 0){
//        alert("공정을 선택해주세요!");
    }

    var componentList = new Array();           //품명, 모델명 등
    componentList.push(inBoundText);

    var process = new Array();                 //공정
    process.push(processOptionVal);

    var inBoundAmount = new Array();             //개수
    inBoundAmount.push(inBoundNum);


    var data
        = {"componentList" : componentList,
        "process" : process,
        "inBoundAmount" : inBoundAmount}

    if(inBoundText.length != 0 && inBoundNum.length != 0 && inBoundNum != 0 && processOptionVal.length !=0){
        /*document.getElementById("load").style.display = "";*/
        addInBoundList(data);
    }else{
    }
}


//case2. 엑셀 파일 업로드를 통한 일괄 등록
function addFile(){
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
            url: 'excelInboundUpload',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function (result){
                $('.inBoundTable').empty();
                document.getElementById("load").style.display = "";
                addInBoundList(result);

                // 모달창 종료
                const modalDismissBtn = document.querySelector(".modalDismissBtn");
                modalDismissBtn.click();
            },
            error: function (){
            }
        })
    }
}


//case1 또는 case2 실행 후 필요한 데이터 가져오는 함수
function addInBoundList(data){
    $.ajax({
        url: 'addList',
        type: 'post',
        dataType : 'json',
        data: JSON.stringify(data),
        contentType : 'application/json',
        traditional: true,
        success: function (result){
            addList(result);
        },
        error: function (){
        }
    })
}
function addList(result){

    const number = result.componentNo.length;

    if(result.componentNo[0] != null){

        str = '<tr>';
        for (let i = 0; i < number; i++) {
            var palletNo = result.palletNo[i];
            str += '<td class = "text-center checkList pt-2 ps-5">';
            if(result.failReason[i] == null && result.componentNo[i] != null){
                str += `<input type="checkbox" checked name = "inBoundCheck" value = "1" id = "${palletNo}">`
            }else {
                str += `<input type="checkbox"  name = "inBoundCheck" value = "1" id = "${palletNo}">`
            }
            str += '</td><td class="text-center ps-2" name = palletNo>';
            str += palletNo;
            str += '</td><td class="text-center ps-2" name = "componentNo">';
            str += result.componentNo[i];
            str += '</td><td class = "text-center modelName ps-2">';
            str += result.modelName[i];
            str += '</td><td class = "text-center status ps-2">';
            str += result.process[i];
            str += '</td><td class = "text-center num ps-2">';
            str += result.inBoundAmount[i];
            str += '</td><td class="text-center ps-2">';
            str += result.rackLocation[i];
            str += '</td><td class="text-center ps-2">';
            if(result.failReason[i] != null){
                switch (result.failReason[i]){
                    case 'A':
                        str += '적재 공간 부족';
                        break;
                    case 'B':
                        str += 'Rack 하중 초과';
                        break;
                }
            }else if(result.componentNo[i] == null){
                str += '부적절한 부품 번호';
            }else{
                str += '';
            }
            str += '</td></tr>'
        }
        $('.inBoundTable').append(str);
    }else if (result.componentNo[0] == null){
        alert("등록된 제품이 없습니다. \n 제품 등록 후 입고 처리 바랍니다.");
    }
    document.getElementById("load").style.display = "none";
}

// JavaScript로 체크박스 제어 함수
function selectAll(qualifiedName, value) {
    const selectAllCheckBox = document.getElementById('selectAllCheckBox');
    const checkBoxes = document.querySelectorAll('tbody input[type="checkbox"]');

    // 선택 전체 체크박스 상태에 따라 다른 동작 수행
    if (selectAllCheckBox.checked) {
        // 선택 전체 체크박스가 체크된 경우, 모든 체크박스를 체크합니다.
        checkBoxes.forEach((checkBox) => {
            checkBox.checked = true;
            //checkBox.setAttribute("checked","checked");
        });
    } else {
        // 선택 전체 체크박스가 해제된 경우, 모든 체크박스의 체크를 해제합니다.
        checkBoxes.forEach((checkBox) => {
            checkBox.checked = false;
            // checkBox.removeAttribute("checked");
        });
    }
}

//입고 temp 삭제
function deleteList() {

    //체크 박스 선택 후 삭제 버튼
    const selectedCheckbox = document.querySelectorAll('input[type="checkbox"]:checked');
    /*    const selectedTempPalletNo = Array.from(selectedCheckbox).map(checkbox => checkbox.id);*/

    const selectAllCheckBox = document.getElementById("selectAllCheckBox");
    const selectedTempPalletNo = [];

    if (selectedCheckbox.length === 0) {
        alert("삭제할 사항을 선택해주십시오");
        return;
    }

    selectAllCheckBox.checked = false;

    selectedCheckbox.forEach(function(checkbox) {
        if (checkbox.id !== 'selectAllCheckBox') { // "selectAllCheckBox" 체크박스 제외
            selectedTempPalletNo.push(checkbox.id);
        }
    });

    const requestData = {
        "tempPalletNo" : selectedTempPalletNo
    };

    $.ajax({
        url: 'deleteInBound',
        type: 'POST',
        data: JSON.stringify(requestData),
        contentType: 'application/json',
        success: function () {
            updateList();
        },
        error: function() {
        }
    })
}
function updateList(){

    $.ajax({
        url: 'reSelect',
        type: 'POST',
        success: function(result){
            $('.inBoundTable').empty();


            const table = document.querySelector('#inBoundTable'); // 테이블 전체
            const tableHead =  document.querySelector('#inBoundTable thead'); //thead 요소 가져오기
            const tableBody = document.querySelector('#inBoundTable tbody'); // tbody 요소 가져오기

            tableBody.innerHTML = ''; // 기존 데이터 삭제

            const fieldOrder = ['tempPalletNo', 'componentNo', 'modelName', 'process', 'componentAmount', 'rackLocation', 'reason'];

            /*            'categoryNo', 'totalWeight'*/

            //thead
            const theadRow = document.createElement('tr');
            for (const th of tableHead.querySelectorAll('th')) {
                const newTh = document.createElement('th');
                newTh.textContent = th.textContent;
                theadRow.appendChild(newTh);
            }
            for (const item of result) {
                const newRow = document.createElement('tr'); // 새로운 <tr> 요소 생성

                // 첫번째 <td>에 체크박스 추가
                const checkboxTd = document.createElement('td');
                checkboxTd.classList.add('text-center', 'checkList', 'pt-2', 'ps-5');

                const checkbox = document.createElement('input');
                checkbox.type = 'checkbox';
                checkbox.name = 'inBoundCheck';
                checkbox.value = '1';
                checkbox.id = item.tempPalletNo;
                if (item.reason === null && item.componentNo !== null) {
                    checkbox.checked = false;
                } else {
                    checkbox.checked = true;
                    /*checkbox.disabled = true;*/
                }
                checkboxTd.appendChild(checkbox);
                newRow.appendChild(checkboxTd);

                for (const field of fieldOrder) {
                    const td = document.createElement('td'); // 새로운 <td> 요소 생성
                    td.classList.add('text-center', 'ps-2');
                    if (field === 'reason') {
                        if (item.reason != null) {
                            switch (item.reason) {
                                case 'A':
                                    td.textContent = '적재 공간 부족';
                                    break;
                                case 'B':
                                    td.textContent = 'Rack 하중 초과';
                                    break;
                                default:
                                    td.textContent = '';
                                    break;
                            }
                        } else if (item.componentNo == null) {
                            td.textContent = '부적절한 부품 번호';
                        } else {
                            td.textContent = '';
                        }
                    } else {
                        td.textContent = item[field]; // 데이터 값 설정
                    }
                    newRow.appendChild(td); // <td> 요소를 <tr>에 추가
                }

                tableBody.appendChild(newRow); // <tr> 요소를 테이블 본문에 추가

                //selectAllCheckBox.removeAttribute("checked");
                //기존 체크박스 미선택
                //selectAllCheckBox.checked = false;
            }
        },
        error: function () {
        }
    });
}

//입고 등록
function inBound(){

    var checkBox = $('input:checkbox[name ="inBoundCheck"]:checked');

    if(checkBox.length === 0){
        alert("입고처리 할 사항을 선택해주십시오");
    }else{
//        var result = confirm("입고 처리 하시겠습니까?");
//        if(result == true){
        var inBoundList = new Array();
        checkBox.each(function (i){
            var tr = checkBox.parent().parent().eq(i);
            var td = tr.children();
            var palletNo = td.eq(1).text();
            var location = td.eq(6).text();

            if(location != '적재 불가'){
                inBoundList.push(parseInt(palletNo));
            }

        })

        if(inBoundList.length == 0){
            alert("적재 가능한 제품이 없습니다.");
        }else{
            $.ajax({
                url:'insertInBound',
                type:'POST',
                traditional: true,
                data: {"inBoundList" : inBoundList
                },
                success: function (){
//                        alert("입고 처리 되었습니다.");
                    location.href = "inbound";
                }
            })
        }
    }
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
                    $('#textSearchList').css('display', 'none');
                });
            });
        };
        $.fn[pluginName].defaults = defaults;
    })('keypad');
})(jQuery);

function selectResult(result) {
    keypad = document.getElementById('keypad02');

    // 클릭한 result 값을 input 요소에 넣음
    $("#inBoundText").val(result.textContent);

    $("#textSearchList").empty();
    keypad.style.display='none';

}

function search(inputElement) {
    const keyword = $(inputElement).val();
    const textSearchList = document.getElementById('textSearchList');

    if(keyword.length >=1){
        textSearchList.style.display = "block";


        $.ajax({
            type: 'POST',
            dataType: 'json',
            contentType: 'application/json',
            url: '/hdsmf/searchNumNow',
            data: keyword, // 큰따옴표로 감싸지 않고 문자열 그대로 전송
            error: function (err) {
            },
            success: function (data) {
                const textSearchList = $("#textSearchList");
                textSearchList.empty();

                data.forEach((result) => {
                    if(result.length>0){

                        const resultElement = $("<p class='textSearchListUnit'></p>").text(result);
                        resultElement.click(function () {
                            selectResult(resultElement[0]);
                        });
                        textSearchList.append(resultElement);}
                });
            }
        });
    }
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
