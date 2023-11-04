// 렉 컨트롤 테이블 체크박스 요소 선택
// 최적화 후 코드
function setupTableInputSelecter() {
    const tableInputSelecter = document.querySelectorAll('#hrdWarehouserackeditSystemtableFormBoxControl tbody tr');

    tableInputSelecter.forEach((row, index) => {
        row.dataset.index = index;
        const inputElements = Array.from(row.querySelectorAll('td input'));
        const handleInputChange = (input) => {
            if (input.checked) {
                row.style.backgroundColor = '#eeeeee';
            } else {
                row.style.backgroundColor = '';
            }
        };

        inputElements.forEach(input => handleInputChange(input));

        row.addEventListener("click", () => {
            inputElements.forEach(input => {
                switch (input.type) {
                    case 'radio':
                    case 'checkbox':
                        input.checked = !input.checked;
                        handleInputChange(input);
                        break;
                }
            });

            // radio 버튼 배경색 컨트롤
            const inputRadios = document.querySelectorAll("td input[type='radio']");
            inputRadios.forEach((input) => {
                if (input.checked === true) {
                    input.closest('tr').style.backgroundColor = '#eeeeee';
                } else {
                    input.closest('tr').style.backgroundColor = '';
                }
            });
        });
    });
}

// 함수 실행
setupTableInputSelecter();

