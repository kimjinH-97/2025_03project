// 전역 변수
let simTimer = null;

// DOM 요소 가져오기 헬퍼 함수
function getElement(id) {
    const el = document.getElementById(id);
    if (!el) console.error(`Element with ID '${id}' not found`);
    return el;
}

// 공통 로그 추가 함수
function addLog(message, isError = false) {
    const logList = getElement('logList');
    if (!logList) return;

    const timestamp = new Date().toLocaleTimeString();
    const logItem = document.createElement('li');
    logItem.innerHTML = `
        <span class="timestamp">[${timestamp}]</span>
        <span class="message ${isError ? 'error' : ''}">${message}</span>
    `;
    logList.prepend(logItem);
}

// 진행률 업데이트 함수
function updateProgress(percent) {
    const progressFill = getElement('progressFill');
    const progressPercent = getElement('progressPercent');

    if (progressFill) progressFill.style.width = `${percent}%`;
    if (progressPercent) progressPercent.textContent = `${percent}%`;

    // 색상 변화
    const progressBar = getElement('progressBar') || progressFill;
    if (progressBar) {
        let gradient;
        if (percent < 30) {
            gradient = 'linear-gradient(90deg, #e74c3c, #f39c12)';
        } else if (percent < 70) {
            gradient = 'linear-gradient(90deg, #f39c12, #f1c40f)';
        } else {
            gradient = 'linear-gradient(90deg, #2ecc71, #27ae60)';
        }
        progressBar.style.background = gradient;
    }
}

// 로그 지우기 함수
function clearLogs() {
    const logList = getElement('logList');
    if (logList) logList.innerHTML = '';
}

// 제품 공정 이동 함수
function moveNextStep() {
    const productId = getElement('productId').value;
    if (!productId) {
        alert("제품 ID를 입력해주세요.");
        return;
    }

    addLog(`제품 ID ${productId} 공정 이동 시도`);

    fetch(`/products/${productId}/next-step`, {
        method: "POST"
    })
    .then(response => {
        if (!response.ok) throw new Error("공정 이동 실패");
        return response.text();
    })
    .then(message => {
        getElement('result').innerText = message;
        addLog(`제품 ID ${productId} 이동 성공: ${message}`);

        // 공정 이동 후 시퀀스 0 리스트 비동기로 갱신
                refreshProductList();
    })
    .catch(error => {
        const errorMsg = `오류 발생: ${error.message}`;
        getElement('result').innerText = errorMsg;
        addLog(errorMsg, true);
    });
}
let isProgressCompleteLogged = false; // 진행 완료 로그 출력 여부 플래그

// 공정 시뮬레이션 시작
function startSimulation() {
    const from = getElement('fromSequence').value;
    if (!from) {
        alert("공정 단계를 입력해주세요.");
        return;
    }

    // 초기화
    if (simTimer) clearInterval(simTimer);
    updateProgress(0);
    clearLogs();
    addLog(`공정 시작 (단계 ${from})`);
    isProgressCompleteLogged = false; // 시뮬 시작할 때 플래그 초기화

    getElement('startBtn').disabled = true;
    getElement('stopBtn').disabled = false;

    simTimer = setInterval(() => {
        fetch(`/simulation/step/${from}/next`)
            .then(res => {
                if (!res.ok) throw new Error(`HTTP ${res.status}`);
                return res.json();
            })
            .then(data => {
                // 진행률 업데이트
                if (typeof data.progress === 'number') {
                    updateProgress(data.progress);
                    const simResult = getElement('simResult');
                    if (simResult) {
                        simResult.innerHTML = `진행률: ${data.progress}% (${data.movedQuantity || 0}/${data.totalQuantity || 100})`;
                    }
                     // 100% 도달 시 로그 1회 출력 + 타이머 정지
                    if (data.progress === 100 && !isProgressCompleteLogged) {
                        addLog(`진행 상태: ${data.progress}% 완료`);
                        isProgressCompleteLogged = true;
                        stopSimulation(); // 자동 정지
                        return; // 이후 처리 방지
                    }
                }

                // 로그 업데이트
                if (data.log?.length) {
                    data.log.forEach(msg => addLog(msg));
                } else if (data.progress !== undefined) {
                    addLog(`진행 상태: ${data.progress}% 완료`);
                }
            })
            .catch(error => {
                console.error("Fetch error:", error);
                addLog(`시뮬레이션 오류: ${error.message}`, true);
                const simResult = getElement('simResult');
                if (simResult) simResult.innerText = `오류 발생: ${error.message}`;
            });
    }, 1000);
}

// 시뮬레이션 중지
function stopSimulation() {
    if (simTimer) {
        clearInterval(simTimer);
        simTimer = null;
        addLog("공정 중지");
    }
    getElement('startBtn').disabled = false;
    getElement('stopBtn').disabled = true;
}

// 창고 이동 함수
function moveToWarehouse() {
    addLog("창고 이동 요청 시작");

    fetch("/warehouse/move", {
        method: "POST"
    })
    .then(response => response.text())
    .then(message => {
        getElement('warehouseResult').innerText = message;
        addLog(`창고 이동 결과: ${message}`);
    })
    .catch(error => {
        const errorMsg = `창고 이동 오류: ${error.message}`;
        getElement('warehouseResult').innerText = errorMsg;
        addLog(errorMsg, true);
    });
}

// 제품 등록 창 열기
function openRegister() {
  const width = 500;
  const height = 600;
  const screenLeft = window.screenLeft !== undefined ? window.screenLeft : screen.left;
  const screenTop = window.screenTop !== undefined ? window.screenTop : screen.top;

  const left = screenLeft + (window.innerWidth / 2) + 100 - (width / 2); // 살짝 오른쪽
  const top = screenTop + (window.innerHeight / 2) - (height / 2); // 수직 정중앙

  window.open("/products/register", "제품 등록", `width=${width},height=${height},top=${top},left=${left}`);
}


//제품(product)에서 원자재(material)로 공정취소(이동)
function cancelProcess() {
    const productId = document.getElementById("productId").value;
    if (!productId) {
        alert("제품 ID를 입력하세요.");
        return;
    }

    fetch(`/process/cancel/${productId}`, {
        method: 'POST'
    })
    .then(response => {
        if (!response.ok) throw new Error("공정 취소 실패");
        return response.text();
    })
    .then(message => {
        document.getElementById("result").innerText = message;

        // 비동기 리스트 갱신 추가
        refreshProductList();
    })
    .catch(error => {
        document.getElementById("result").innerText = error.message;
    });
}



//시퀀스 0인 제품리스트 보여주기.
function fetchAvailableProducts() {
    fetch("/process/available-products")
        .then(response => {
            if (!response.ok) throw new Error("서버 오류 발생");
            return response.json();
        })
        .then(data => {
            const productListDiv = document.getElementById("productList");
            productListDiv.innerHTML = "";

            if (data.length === 0) {
                productListDiv.innerHTML = "<p>시퀀스 0인 제품이 없습니다.</p>";
                return;
            }
            data.forEach(product => {
                const div = document.createElement("div");
                div.className = "product-item";
                div.innerHTML = `
                    <span class="badge">#${product.productId}</span>
                    <span class="product-name">${product.productName}</span>
            `;
            productListDiv.appendChild(div);
            });

        })
        .catch(error => {
            console.error("Error fetching products:", error);
            document.getElementById("productList").innerHTML = "<p>불러오기에 실패했습니다.</p>";
        });
}



function refreshProductList() {
    fetchAvailableProducts();
}
//시퀀스 0보여주는 js (필수) 삭제 x
document.addEventListener("DOMContentLoaded", function () {
    fetchAvailableProducts(); // 여기가 핵심!
});



