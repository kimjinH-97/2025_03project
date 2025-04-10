<script th:inline="javascript">
    document.addEventListener("DOMContentLoaded", function () {
        const errors = /*[[${errors}]]*/ [];
        const link = /*[[${materialPageRequestDTO.getLink()}]]*/ '';

        const formObj = document.querySelector("#f1");
        const uploadModal = new bootstrap.Modal(document.querySelector(".uploadModal"));
        const uploadResult = document.querySelector(".uploadResult");
        const removeFileList = [];

        // 오류 메시지 표시
        if (errors && errors.length > 0) {
            const fieldMessages = {
                materialName: '원자재명이',
                materialQuantity: '수량이'
            };
            const codeMessages = {
                NotEmpty: '비어있습니다.'
            };
            let errorMsg = '';
            errors.forEach(err => {
                const field = fieldMessages[err.field] || err.field;
                const code = codeMessages[err.code] || err.code;
                errorMsg += `${field} ${code} \n`;
            });
            alert(errorMsg);
        }

        // 수정 버튼
        document.querySelector(".modBtn").addEventListener("click", function (e) {
            e.preventDefault();

            appendFileData();
            callRemoveFiles();

            formObj.action = `/material/MaterialModify?${link}`;
            formObj.method = 'post';
            formObj.submit();
        });

        // 목록 버튼
        document.querySelector(".listBtn").addEventListener("click", function (e) {
            e.preventDefault();
            formObj.reset();
            location.href = `/material/MaterialList?${link}`;
        });

        // 삭제 버튼
        document.querySelector(".removeBtn").addEventListener("click", function (e) {
            e.preventDefault();
            formObj.action = `/material/MaterialRemove`;
            formObj.method = 'post';
            formObj.submit();
        });

        // 업로드 모달
        document.querySelector(".uploadFileBtn").addEventListener("click", function (e) {
            e.preventDefault();
            uploadModal.show();
        });

        document.querySelector(".closeUploadBtn").addEventListener("click", function () {
            uploadModal.hide();
        });

        // 업로드 버튼
        document.querySelector(".uploadBtn").addEventListener("click", function () {
            const fileInput = document.querySelector("input[name='files']");
            const files = fileInput.files;
            const formData = new FormData();

            for (let file of files) {
                formData.append("files", file);
            }

            uploadToServer(formData).then(result => {
                result.forEach(showUploadFile);
                uploadModal.hide();
            }).catch(() => {
                uploadModal.hide();
            });
        });

        // 업로드 파일 표시
        function showUploadFile({materialUuid, materialFileName, link}) {
            const str = `
                <div class="card col-4">
                    <div class="card-header d-flex justify-content-center">
                        ${materialFileName}
                        <button class="btn-sm btn-danger" onclick="removeFile('${materialUuid}', '${materialFileName}', this)">X</button>
                    </div>
                    <div class="card-body">
                        <img src="/view/${link}" data-src="${materialUuid + "_" + materialFileName}" class="img-fluid">
                    </div>
                </div>`;
            uploadResult.innerHTML += str;
        }

        // 삭제 처리용
        window.removeFile = function (uuid, fileName, obj) {
            if (!confirm("파일을 삭제하시겠습니까?")) return;
            removeFileList.push({uuid, fileName});
            obj.closest(".card").remove();
        }

        // 히든 인풋 추가
        function appendFileData() {
            const target = document.querySelector(".uploadHidden");
            const imgs = uploadResult.querySelectorAll("img");
            let str = "";
            imgs.forEach(img => {
                const src = img.getAttribute("data-src");
                str += `<input type='hidden' name='materialFileNames' value="${src}">`;
            });
            target.innerHTML = str;
        }

        function callRemoveFiles() {
            removeFileList.forEach(({uuid, fileName}) => {
                removeFileToServer({uuid, fileName});
            });
        }
    });
</script>
