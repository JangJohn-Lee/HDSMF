window.onload = function() {
    // select 요소의 변경 이벤트를 감지
    var searchComponent = document.getElementById("searchComponent");
    searchComponent.onchange = function () {
        // 선택된 옵션의 값 추출
        var selectedValue = this.value;
    };
    const sButton = document.getElementById("sButton");
    const fWeight = document.getElementById("fWeight");

    sButton.addEventListener("click", function () {
        if (fWeight.value == "" || fWeight.value == null) {
            fWeight.value = 0.0;
        }
    })
}

// 체크 박스 선택 후 조회
function selectedComponentss() {
    const selectedCheckboxes = document.querySelectorAll('input[type="checkbox"]:checked');

    if (selectedCheckboxes.length === 0) {
        alert("수정할 사항을 선택해주십시오");
        return;
    }

    document.getElementById('stockCorBtn').click();


    // 마지막 체크된 체크박스의 value만 사용
    const lastSelectedCheckbox = selectedCheckboxes[selectedCheckboxes.length - 1];
    const componentValue = lastSelectedCheckbox.value;

    const requestData = {
        "componentNo": componentValue
    };

    // 수정
    $.ajax({
        url: '/hdsmf/components/edit',
        type: 'POST',
        data: JSON.stringify(requestData),
        contentType: 'application/json',
        success: function (response) {
            document.querySelector('input[name="plantNo1"]').value = response.plantNo;
            document.querySelector('input[name="componentNo1"]').value = response.componentNo;
            document.querySelector('input[name="categoryName1"]').value = response.categoryName;
            document.querySelector('input[name="model1"]').value = response.model || '';
            document.querySelector('input[name="componentState1"]').value = response.componentState || '';
            document.querySelector('input[name="product1"]').value = response.product || '';
            document.querySelector('input[name="sNo1"]').value = response.sno || '';
            document.querySelector('input[name="sWeight1"]').value = response.sweight;
            document.querySelector('input[name="fWeight1"]').value = response.fweight || '';
        },
        error: function () {
        }
    });
}

//업데이트
function updatedComponents() {
    //value값 다 받아오기
    const plantNo = document.querySelector('input[name="plantNo1"]').value;
    const componentNo = document.querySelector('input[name="componentNo1"]').value; //파라미터 값으로 전달
    const categoryName = document.querySelector('input[name="categoryName1"]').value;
    const model = document.querySelector('input[name="model1"]').value;
    const componentState = document.querySelector('input[name="componentState1"]').value;
    const product = document.querySelector('input[name="product1"]').value;
    const sno = document.querySelector('input[name="sNo1"]').value;
    const sWeight = document.querySelector('input[name="sWeight1"]').value;
    const fWeight = document.querySelector('input[name="fWeight1"]').value;

    // 수정
    const requestData = {
        "plantNo": plantNo,
        "componentNo": componentNo,
        "categoryName": categoryName,
        "model": model,
        "componentState" : componentState,
        "product": product,
        "sno": sno,
        "sWeight": sWeight,
        "fWeight": fWeight
    };

    // 수정
    $.ajax({
        url: '/hdsmf/components/update',
        type: 'POST',
        data: JSON.stringify(requestData),
        contentType: 'application/json',
        success: function() {
            location.href="/hdsmf/components";
        },
        error: function() {
        }
    });
}

//체크 박스 선택 후 삭제(중복 체크 및 삭제 1)
function deleteSelectedComponents() {
    const selectedCheckboxes = document.querySelectorAll('input[type="checkbox"]:checked');
    const selectedComponentNo = Array.from(selectedCheckboxes).map(checkbox => checkbox.value);

    if (selectedCheckboxes.length === 0) {
        alert("삭제할 사항을 선택해주십시오");
        return;
    }


    if(confirm('삭제하시겠습니까?')){

        const requestData = {
            "componentNo": selectedComponentNo
        };

        $.ajax({
            url: '/hdsmf/components/deleteComponents',
            type: 'POST',
            data: JSON.stringify(requestData),
            contentType: 'application/json',
            success: function (response) {

                location.href="/hdsmf/components";
            },
            error: function (xhr) {
                if (xhr.status === 400) {
                    alert("이미 사용중인 컴포넌트 번호입니다.");
                    if(confirm('그래도 삭제하시겠습니까?')) {
                        deleteSelectedComponentsCheck();
                    }else{
                        alert("취소되었습니다.");
                    }
                }
            }
        });
    }else{
        alert("취소되었습니다.");
    }
}

//삭제 2
function deleteSelectedComponentsCheck(){
    const selectedCheckboxes = document.querySelectorAll('input[type="checkbox"]:checked');
    const selectedComponentNo = Array.from(selectedCheckboxes).map(checkbox => checkbox.value);
    const requestData = {
        "componentNo": selectedComponentNo
    };



    $.ajax({
        url: '/hdsmf/components/deleteComponentsCheck',
        type: 'POST',
        data: JSON.stringify(requestData),
        contentType: 'application/json',
        success: function (response) {
            location.href="/hdsmf/components";
        },
        error: function () {
        }

    });
}

function downloadExcel() {
    // Sample data, replace this with your actual data
    const data = [
        ['품번','카테고리명','완제품무게','모델명','공정상태','플랜트 번호','완제품명','소재품번','소재무게']
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
    link.download = '일괄등록 양식.xlsx';
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
    const table = document.getElementById('component_table_info');
    const thead = table.querySelector('thead');
    const tbody = table.querySelector('tbody');

    const data = [];

    // Read thead (headers)
    const headers = [];
    //체크박스 열 제외
    thead.querySelectorAll('th:not(:first-child)').forEach((th) => {
        headers.push(th.textContent);
    });
    data.push(headers);

    // Read tbody (rows)
    tbody.querySelectorAll('tr').forEach((tr) => {
        const rowData = [];
        //체크박스 열 제외
        tr.querySelectorAll('td:not(:first-child)').forEach((td) => {
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

    link.download = '부품리스트 다운로드.xlsx';
    link.click();
}

//파일 업로드
function uploadFileSave() {
    const fileInput = document.getElementById('uploadFile');

    const file = fileInput.files[0];
    if(file == undefined){
        // alert("파일을 선택해주세요");

        const selectFileNotice = document.querySelector(".selectFileNotice");
        selectFileNotice.style.display = "block";

        setTimeout(function(){
            selectFileNotice.style.display = "none";
        }, 2000);

        return;
    }else{
    const formData = new FormData();
    formData.append('file', file);
    $.ajax({
        url: '/hdsmf/components/uploadSave',
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        success: function (result){

            // 모달창 종료
            const modalDismissBtn = document.querySelector(".modalDismissBtn");
            modalDismissBtn.click();
        },
        error: function (xhr) {
            if (xhr.status === 400) {

                alert("중복된 컴포넌트번호가 존재합니다.");
                if(confirm('덮어씌우시겠습니까?')) {
                    uploadFileSaveCheck();
                }else{
                    alert("취소되었습니다.");
                }
            } else {
            }
        }
    })
    }
}

function uploadFileSaveCheck(){
    const fileInput = document.getElementById('uploadFile');
    const file = fileInput.files[0];
    const formData = new FormData();
    formData.append('file', file);

    $.ajax({
        url: '/hdsmf/components/uploadSaveCheck',
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        success: function (result){
            console.log("성공");

            // 모달창 종료
            const modalDismissBtn = document.querySelector(".modalDismissBtn");
            modalDismissBtn.click();
        },
        error: function () {
            console.log("실패");
        }
    })
}

function selectAll(){

    var checkBox = $('input:checkbox[id = "selectAllCheckBox"]:checked');

    var checkBoxList = $('input:checkbox[name = "checkbox"]');

    if(checkBox.length > 0){
        checkBoxList.prop("checked", true);
        for(var i = 0; i < checkBoxList.length; i++){
            document.getElementById(checkBoxList[i].id).style.border = "3px solid red";
            document.getElementById(checkBoxList[i].id).textContent = i+1;
        }
    }else{
        checkBoxList.prop("checked", false);
        for(var i = 0; i < checkBoxList.length; i++){
            document.getElementById(checkBoxList[i].id).style.border = "2px solid lightgrey";
            document.getElementById(checkBoxList[i].id).textContent = null;
        }
    }
}

function componentKey(componentNo){
    const keyword = $(componentNo).val();

    $.ajax({
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json',
        url: '/hdsmf/componentNumNow',
        data: keyword,
        error: function (err) {
        },
        success: function (data) {
            //data는 0또는 1
            const componentText = document.getElementById('componentNo-valid');

            if(data >= 1){
                componentText.style.display = 'block'; //이미 사용중
            }else{
                componentText.style.display = 'none';
            }
        }
    });

}