document.addEventListener('DOMContentLoaded', function() {
    // 페이지네이션 처리
    const pagination = document.querySelector('.pagination-large');
    if (pagination) {
        pagination.addEventListener('click', function(e) {
            e.preventDefault();
            const target = e.target.closest('a.page-link-large');
            if (!target) return;

            const pageNum = target.getAttribute('data-num');
            const form = document.querySelector('form');

            // 기존 page 파라미터 제거
            const existingPageInput = form.querySelector('input[name="page"]');
            if (existingPageInput) {
                existingPageInput.remove();
            }

            // 새로운 page 파라미터 추가
            const pageInput = document.createElement('input');
            pageInput.type = 'hidden';
            pageInput.name = 'page';
            pageInput.value = pageNum;
            form.appendChild(pageInput);

            form.submit();
        });
    }

    // 검색 초기화 버튼
    const clearBtn = document.querySelector('.btn-clear-large');
    if (clearBtn) {
        clearBtn.addEventListener('click', function() {
            window.location.href = '/material/MaterialList';
        });
    }

    // 테이블 행 클릭 이벤트 (확장성을 위해 추가)
    const tableRows = document.querySelectorAll('.material-table-large tbody tr[data-material-id]');
    tableRows.forEach(row => {
        row.addEventListener('click', function(e) {
            // 링크가 아닌 부분을 클릭했을 때만 처리
            if (!e.target.closest('a') && !e.target.closest('button')) {
                const materialId = this.getAttribute('data-material-id');
                window.location.href = `/material/MaterialModify?materialId=${materialId}`;
            }
        });
    });
});