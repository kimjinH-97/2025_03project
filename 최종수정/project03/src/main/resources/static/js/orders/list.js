document.addEventListener("DOMContentLoaded", function () {
    // 제품명 클릭
    document.querySelectorAll(".product-link").forEach(function (td) {
        td.addEventListener("click", function (event) {
            const orderId = this.getAttribute("data-id");
            window.location.href = `/orders/${orderId}/detail`;
            event.stopPropagation();
        });
    });

    // URL 파라미터 체크
    const urlParams = new URLSearchParams(window.location.search);
    const status = urlParams.get("status");

    // 숨김 ID 목록 불러오기
    const hiddenOrders = JSON.parse(sessionStorage.getItem("hiddenOrders") || "[]");

    // 완료 버튼 처리
    document.querySelectorAll(".btn-complete-hide").forEach(function (btn) {
        const orderId = btn.getAttribute("data-id");

        // 이미 숨긴 항목이면 버튼 안 보이게
        if (hiddenOrders.includes(orderId) && status !== "완료") {
            const row = document.getElementById("row-" + orderId);
            if (row) row.style.display = "none";
            btn.style.display = "none";
        }

        // 버튼 클릭 시 → 숨기고 세션에 저장
        btn.addEventListener("click", function () {
            const row = document.getElementById("row-" + orderId);
            if (row) row.style.display = "none";
            btn.style.display = "none";

            // 저장
            if (!hiddenOrders.includes(orderId)) {
                hiddenOrders.push(orderId);
                sessionStorage.setItem("hiddenOrders", JSON.stringify(hiddenOrders));
            }
        });
    });

    // 드롭다운이 "완료"일 때는 다시 보이게
    if (status === "완료") {
        document.querySelectorAll("tr[id^='row-']").forEach(row => {
            row.style.display = "";
        });
        document.querySelectorAll(".btn-complete-hide").forEach(btn => {
            btn.style.display = "none"; // 버튼은 다시 안 보임!
        });
    }
});
