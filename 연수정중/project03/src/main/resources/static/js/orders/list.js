document.addEventListener("DOMContentLoaded", function () {
    // 제품명 클릭 시 detail 페이지로 이동
    document.querySelectorAll(".product-link").forEach(function (td) {
        td.addEventListener("click", function (event) {
            const orderId = this.getAttribute("data-id");
            window.location.href = `/orders/${orderId}/detail`;
            event.stopPropagation(); // 행 전체 이벤트 방지
        });
    });
});