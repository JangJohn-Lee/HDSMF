window.onload = function (){

    sysDateTime();
}

function check(){

    var inputText = document.getElementById("componentNum");
    var inputTextValue = inputText.value;
    var searchText = document.getElementById("searchText");
    var rackNum = document.getElementById("rackNum");

    const formData = {
        "componentNo" : inputTextValue
    }

    if(inputText.value === "") {
    }
    else{
        $.ajax({
            url: 'componentCheck',
            type: 'POST',
            data: /*JSON.stringify(formData)*/
            inputTextValue,
            processData: false,
            contentType: false,
            success: function (result) {

                //list 있으면 submit
                if(result.componentNo >= 1){
                    document.getElementById('search_form').submit();
                }else{
                    alert("현재 적재된 부품이 없습니다.");
                    location.href = "racksearch";

                }
            }
        })
    }
}

function downloadExcel() {
    const data = [
        ['품번']
    ];

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
    link.download = '일괄조회 양식.xlsx';
    link.click();
}

function s2ab(s) {
    const buf = new ArrayBuffer(s.length);
    const view = new Uint8Array(buf);
    for (let i = 0; i < s.length; i++) view[i] = s.charCodeAt(i) & 0xff;
    return buf;
}

function downloadExcelFile() {
    // Sample data, replace this with your actual data
    const table = document.getElementById('table_info');
    const thead = table.querySelector('thead');
    const tbody = table.querySelector('tbody');

    const data = [];

    // Read thead (headers)
    const headers = [];
    thead.querySelectorAll('th').forEach((th) => {
        headers.push(th.textContent);
    });
    data.push(headers);

    // Read tbody (rows)
    tbody.querySelectorAll('tr').forEach((tr) => {
        const rowData = [];
        tr.querySelectorAll('td').forEach((td) => {
            rowData.push(td.textContent);
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

//파일 업로드
function uploadFile() {
    const fileInput = document.getElementById('upfile');
    const file = fileInput.files[0];

    const formData = new FormData();
    formData.append('file', file);

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
            url: 'upload',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function (result) {

                //현재 시간 출력
                sysDateTime();

                const tableBody = document.querySelector('#table_info tbody'); // tbody 요소 가져오기
                $('#table_info tbody').empty();

                // 정의된 필드 순서
                const fieldOrder = ['rackId', 'componentNo', 'product', 'componentAmount', 'componentState', 'inboundDate'];
                if(result.list == "") {
                    alert("현재 적재된 부품이 없습니다.");

                    return;
                }

                let countNum = 1; // 번호 초기값 설정

                for (const item of result.list) {
                    const newRow = document.createElement('tr'); // 새로운 <tr> 요소 생성

                    // 첫 번째 열에 체크박스 추가
                    const checkboxCell = document.createElement('td');
                    checkboxCell.classList.add('align-middle', 'text-center', 'px-3');
                    const checkbox = document.createElement('input');
                    checkbox.type = 'checkbox';
                    checkbox.name = 'checkbox'; // 체크박스의 name 속성 설정
                    checkbox.value = item.componentNo; // 체크박스의 값 설정
                    checkboxCell.appendChild(checkbox);
                    newRow.appendChild(checkboxCell);

                    // 두 번째 열에 번호 추가
                    const numberCell = document.createElement('td');
                    numberCell.classList.add('align-middle', 'text-center', 'px-2');
                    numberCell.textContent = countNum++; // 현재 번호 설정 후 증가
                    console.log(numberCell);
                    newRow.appendChild(numberCell);

                    for (const field of fieldOrder) {
                        const td = document.createElement('td'); // 새로운 <td> 요소 생성

                        td.classList.add('align-middle', 'text-center', 'px-3');


                        if (field === 'componentState') { // componentState 값에 따라 출력할 내용 설정
                            let stateText = '';
                            switch (item[field]) {
                                case '0':
                                    stateText = '완제품';
                                    break;
                                case '1':
                                    stateText = '소재';
                                    break;
                                case '2':
                                    stateText = 'cnc1';
                                    break;
                                case '3':
                                    stateText = 'cnc2';
                                    break;
                                case '4':
                                    stateText = 'mct1';
                                    break;
                            }
                            td.textContent = stateText;
                        }else if(field === 'inboundDate'){
                            const inboundDate = new Date(item[field]);

                            const dateFormat = new Intl.DateTimeFormat('ko-KR', {
                                year: 'numeric',
                                month: '2-digit',
                                day: '2-digit',
                                hour: '2-digit',
                                minute: '2-digit',
                                hour12: true
                            });

                            const formmatDate = dateFormat.format(inboundDate);
                            td.textContent = formmatDate;
                        }else {
                            td.textContent = item[field]; // 데이터 값 설정
                        }

                        newRow.appendChild(td); // <td> 요소를 <tr>에 추가
                    }

                    tableBody.appendChild(newRow); // <tr> 요소를 테이블 본문에 추가
                }

                // 모달창 종료
                const modalDismissBtn = document.querySelector(".modalDismissBtn");
                modalDismissBtn.click();
            },
            error: function () {
            }
        })
    }
}

//테이블 출력
function updateTable(data) {

    const tableBody = document.getElementById('table_info').querySelector('tbody');
    tableBody.innerHTML = '';

    for (const rowData of data) {
        const row = document.createElement('tr');

        const dateCell = document.createElement('td');
        dateCell.textContent = rowData.inboundDate;
        row.appendChild(dateCell);

        const componentNoCell = document.createElement('td');
        componentNoCell.textContent = rowData.componentNo;
        row.appendChild(componentNoCell);

        const productCell = document.createElement('td');
        productCell.textContent = rowData.product;
        row.appendChild(productCell);

        const componentAmountCell = document.createElement('td');
        componentAmountCell.textContent = rowData.componentAmount;
        row.appendChild(componentAmountCell);

        const stateCell = document.createElement('td');
        switch (rowData.componentState) {
            case 0:
                stateCell.textContent = "완제품";
                break;
            case 1:
                stateCell.textContent = "소재";
                break;
            case 2:
                stateCell.textContent = "cnc1";
                break;
            case 3:
                stateCell.textContent = "cnc2";
                break;
            case 4:
                stateCell.textContent = "mct1";
                break;
            default:
                stateCell.textContent = "null";
        }
        row.appendChild(stateCell);

        const rackIdCell = document.createElement('td');
        rackIdCell.textContent = rowData.rackId;
        row.appendChild(rackIdCell);

        tableBody.appendChild(row);
    }
}

function sysDateTime() {
    const now = new Date();
    const formattedTime = now.toLocaleString();

    const upToDateElem = document.querySelector('#sysdate');
    upToDateElem.textContent =  '최종 시간 : ' + formattedTime;
}

