const errors = [[${errors}]];
console.log(errors);

let errorMsg = '';

// 필드와 오류 코드에 대한 사용자 친화적인 메시지 매핑
const fieldMessages = {
    materialName: '원자재명이',
    materialQuantity: '수량이',
    materialSize: '크기가'
};

const codeMessages = {
    NotEmpty: '비어있습니다.',
};

if (errors) {
    for (let i = 0; i < errors.length; i++) {
        // 필드명에 해당하는 사용자 친화적인 메시지를 찾음
        const field = fieldMessages[errors[i].field] || errors[i].field;
        // 오류 코드에 해당하는 메시지를 찾음
        const code = codeMessages[errors[i].code] || errors[i].code;

        errorMsg += `${field} ${code} \n`;
    }
    alert(errorMsg);
}

// 취소 버튼 처리
document.querySelector("#cancelBtn").addEventListener("click", function(e) {
    e.preventDefault();
    e.stopPropagation();
    window.location.href = "/material/MaterialList";
}, false);

// 파일 업로드 관련 기능
const uploadModal = new bootstrap.Modal(document.querySelector("#uploadModal"));
const uploadResult = document.querySelector("#uploadResult");

// 업로드 버튼 클릭 시 모달 열기
document.querySelector(".uploadFileBtn").addEventListener("click", function(e) {
    e.stopPropagation();
    e.preventDefault();
    uploadModal.show();
}, false);

// 모달 내 업로드 버튼 클릭 시
document.querySelector("#uploadBtn").addEventListener("click", function(e) {
    const formObj = new FormData();
    const fileInput = document.querySelector("#uploadModal input[name='files']");

    const files = fileInput.files;

    if (files.length === 0) {
        alert("파일을 선택해주세요.");
        return;
    }

    for (let i = 0; i < files.length; i++) {
        formObj.append("files", files[i]);
    }

    uploadToServer(formObj).then(result => {
        console.log(result);
        for (const uploadResultObj of result) {
            showUploadFile(uploadResultObj);
        }
        fileInput.value = ""; // 파일 입력 초기화
        uploadModal.hide();
    }).catch(e => {
        console.error(e);
        alert("파일 업로드 중 오류가 발생했습니다.");
        uploadModal.hide();
    });
}, false);

// 업로드된 파일 표시
function showUploadFile({materialUuid, materialFileName, link}) {
    const str = `
        <div class="card col-4">
            <div class="card-header d-flex justify-content-between align-items-center">
                ${materialFileName}
                <button class="btn btn-sm btn-danger" onclick="removeFile('${materialUuid}', '${materialFileName}', this)">X</button>
            </div>
            <div class="card-body">
                <img src="/view/${link}" data-src="${materialUuid + "_" + materialFileName}">
            </div>
        </div>
    `;
    uploadResult.insertAdjacentHTML('beforeend', str);
}

// 파일 삭제
function removeFile(uuid, fileName, obj) {
    if (!confirm("파일을 삭제하시겠습니까?")) {
        return;
    }
    removeFileList.push({uuid, fileName});
    const targetDiv = obj.closest(".card");
    targetDiv.remove();
}

// 최종적으로 삭제될 파일들을 목록(임시 저장 변수)
const removeFileList = [];

// 서버에 파일 업로드 함수 (예시)
async function uploadToServer(formData) {
    try {
        const response = await fetch('/material/upload', {
            method: 'POST',
            body: formData
        });
        return await response.json();
    } catch (e) {
        console.error(e);
        throw e;
    }
}

// 서버에서 파일 삭제 함수 (예시)
async function removeFileToServer({uuid, fileName}) {
    try {
        await fetch(`/material/removeFile?uuid=${uuid}&fileName=${fileName}`, {
            method: 'DELETE'
        });
    } catch (e) {
        console.error(e);
    }
}

// 폼 제출 전 파일 데이터 추가
document.querySelector("#materialForm").addEventListener("submit", function(e) {
    appendFileData();
    callRemoveFiles();
});

function appendFileData() {
    const form = document.querySelector("#materialForm");
    const uploadFiles = uploadResult.querySelectorAll("img");

    // 기존 hidden input 제거
    const existingInputs = form.querySelectorAll("input[name='materialFileNames']");
    existingInputs.forEach(input => input.remove());

    // 새로운 hidden input 추가
    uploadFiles.forEach(uploadFile => {
        const imgLink = uploadFile.getAttribute("data-src");
        const input = document.createElement("input");
        input.type = 'hidden';
        input.name = 'materialFileNames';
        input.value = imgLink;
        form.appendChild(input);
    });
}
