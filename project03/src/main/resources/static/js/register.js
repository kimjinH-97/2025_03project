 function registerProduct() {
        const selected = document.querySelector('input[name="materialId"]:checked');
        const result = document.getElementById("result");

        if (!selected) {
            alert("원자재를 선택해주세요.");
            return;
        }

        const materialId = selected.value;

        fetch(`/products/register-from-material/${materialId}`, {
            method: "POST"
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("요청 실패");
            }
            return response.text();
        })
        .then(message => {
            result.innerText = message;
        })
        .catch(error => {
            result.innerText = "오류 발생: " + error.message;
        });
    }