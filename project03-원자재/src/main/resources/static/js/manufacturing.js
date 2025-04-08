function moveNextStep() {
    const productId = document.getElementById("productId").value;

    if (!productId) {
        alert("제품 ID를 입력해주세요.");
        return;
    }

    fetch(`/products/${productId}/next-step`, {
        method: "POST"
    })
    .then(response => {
        if (response.ok) {
            return response.text();
        } else {
            throw new Error("공정 이동 실패");
        }
    })
    .then(message => {
        document.getElementById("result").innerText = message;
    })
    .catch(error => {
        document.getElementById("result").innerText = "오류 발생: " + error.message;
    });
}

function openRegister() {
    window.open("register.html", "제품 등록", "width=500,height=600");
}

let simTimer = null;

function startSimulation() {
    const from = document.getElementById("fromSequence").value;
    if (!from) {
        alert("공정 단계를 입력해주세요.");
        return;
    }

    document.getElementById("startBtn").disabled = true;
    document.getElementById("stopBtn").disabled = false;

    simTimer = setInterval(() => {
        fetch(`/simulation/step/${from}/next`)
            .then(res => res.json())
            .then(data => {
                document.getElementById("simResult").innerHTML = `
                    진행률: ${data.progress}% (${data.movedQuantity} / ${data.totalQuantity})
                `;

                const logList = document.getElementById("logList");

                if (data.log && Array.isArray(data.log)) {
                    data.log.forEach(msg => {
                        const key = msg.split(" → ")[0];
                        const existing = [...logList.children].find(li => li.dataset.key === key);

                        if (existing) {
                            existing.innerText = msg;
                        } else {
                            const li = document.createElement("li");
                            li.dataset.key = key;
                            li.innerText = msg;
                            logList.appendChild(li);
                        }
                    });
                }
            });
    }, 1000);
}

function stopSimulation() {
    clearInterval(simTimer);
    document.getElementById("startBtn").disabled = false;
    document.getElementById("stopBtn").disabled = true;
}

function moveToWarehouse() {
    fetch("/warehouse/move", {
        method: "POST"
    })
    .then(response => response.text())
    .then(message => {
        document.getElementById("warehouseResult").innerText = message;
    })
    .catch(error => {
        document.getElementById("warehouseResult").innerText = "오류 발생: " + error.message;
    });
}
const progressBar = document.getElementById("progressBar");
progressBar.style.width = `${data.progress}%`;
progressBar.innerText = `${data.progress}%`;
