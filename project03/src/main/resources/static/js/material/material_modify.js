
    let errorMsg = ''

    // 필드와 오류 코드에 대한 사용자 친화적인 메시지 매핑
    const fieldMessages = {
        materialName: '원자재명이',
        materialQuantity: '수량이',

    }

    const codeMessages = {
        NotEmpty: '비어있습니다.',
    }

    if (errors) {
        for (let i = 0; i < errors.length; i++) {
            // 필드명에 해당하는 사용자 친화적인 메시지를 찾음
            const field = fieldMessages[errors[i].field] || errors[i].field;
            // 오류 코드에 해당하는 메시지를 찾음
            const code = codeMessages[errors[i].code] || errors[i].code;

            errorMsg += `${field} ${code} \n`
        }
        alert(errorMsg)
    }

    //수정 버튼

    const formObj = document.querySelector("#f1")

    //List버튼 처리
    document.querySelector(".listBtn").addEventListener("click", function(e){
        e.preventDefault()
        e.stopPropagation()

        formObj.reset()
        self.location = `/material/MaterialList?${link}`
    },false)

    //삭제 처리버튼
    document.querySelector(".removeBtn").addEventListener("click", function(e){
        e.preventDefault()
        e.stopPropagation()

        formObj.action = `/material/MaterialRemove`
        formObj.method = 'post'
        formObj.submit()
    },false)

    //최종적으로 삭제될 파일들을 목록(임시 저장 변수)
    const removeFileList = []
    function removeFile(uuid,fileName, obj){
        if(!confirm("파일을 삭제하시겠습니까?")){
            return
        }
        console.log(uuid)
        console.log(fileName)
        console.log(obj)
        removeFileList.push({uuid,fileName})

        const targetDiv = obj.closest(".card")
        targetDiv.remove()
    }

    //업로드 모달
    const uploadModal = new bootstrap.Modal(document.querySelector(".uploadModal"))
    document.querySelector(".uploadFileBtn").addEventListener("click", function(e){
        e.stopPropagation()
        e.preventDefault()
        uploadModal.show()
    }, false);

    //Close버튼 동작
    const closeUploadBtn = document.querySelector(".closeUploadBtn");
    closeUploadBtn.addEventListener("click", function(e){
        uploadModal.hide();
    },false);

    //upload버튼 동작
    document.querySelector(".uploadBtn").addEventListener("click", function(e){
        const formObj = new FormData();
        const fileInput = document.querySelector("input[name='files']")

        const files = fileInput.files;

        for(let i = 0; i < files.length; i++){
            formObj.append("files", files[i]);
        }

        uploadToServer(formObj).then(result => {
            console.log(result);
            for(const uploadResult of result){
                showUploadFile(uploadResult);
            }
            uploadModal.hide();
        }).catch(e => {
            uploadModal.hide();
        })
    },false);

    const uploadResult = document.querySelector(".uploadResult");
    function showUploadFile({materialUuid, materialFileName, link}) {
        const str = `
            <div class="card col-4">
                <div class="card-header d-flex justify-content-center">
                    ${materialFileName}
                    <button class="btn-sm btn-danger" onclick="javascript:removeFile('${materialUuid}', '${materialFileName}', this)">X</button>
                </div>
                <div class="card-body">
                    <img src="/view/${link}" data-src="${materialUuid+"_"+materialFileName}">
                </div>
            </div>
        `
        uploadResult.innerHTML += str;
    }

    document.querySelector(".modBtn").addEventListener("click", function(e){
        e.stopPropagation()
        e.preventDefault()

        //1.첨부파일을 수정하기 위해서 form전송에 맞는 input 값을 hidden으로 추가
        appendFileData();

        //2.removeFileList에 있는 삭제대상 파일들을 삭제
        callRemoveFiles();

        formObj.action = `/material/MaterialModify?${link}`;
        formObj.method = 'post';
        formObj.submit();
    },false);

    function appendFileData(){
        const target = document.querySelector(".uploadHidden");     //uploadHidden클래스를 가진 dom을 조회
        const uploadFiles = uploadResult.querySelectorAll("img");   // 업로드된 모든 이미지 태그의 dom을 조회

        //첨부된 파일의 이름들만 서버로 전송하기 위해 input타입의 hidden태그 코드 삽입
        let str = '';   //input타입의 hidden html태그 문자열
        for (let i = 0; i < uploadFiles.length; i++) {
            const uploadFile = uploadFiles[i];                      //실제 이미지 정보의 img 태그
            const imgLink = uploadFile.getAttribute("data-src");         //이미지 주소

            str += `<input type='hidden' name='materialFileNames' value="${imgLink}">`;

        }
        target.innerHTML = str;
    }
    function callRemoveFiles(){
        //첨부파일 삭제 api 호출
        removeFileList.forEach(({uuid, fileName}) => {
            removeFileToServer(uuid, fileName);
        });
    }