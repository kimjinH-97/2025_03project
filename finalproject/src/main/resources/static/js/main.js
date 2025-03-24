// vue 객체를 사용하여 화면 결과 UI Binding
const searchResult = new Vue({
    // 어떤 dom 들을 건드릴건가.
    el: '#search-result',
    data: {
        search_result: {},
    },
})


const resultList = new Vue({
    el: '#wish-list-result',
    data: {
        result_list: {},  // 결과 리스트 데이터를 저장
    },
    methods: {
        // 검색결과 삭제 메서드
        deleteResult(resultId) {
            console.log('resultlist delete resultid', resultId);
            $.ajax({
                type: 'post',
                url: '/api/resultdelete/' + resultId, // api url
                contentType: 'application/json',
                success: function(response, status, xhr) {
                    console.log('검색결과 삭제 완료', response);
                    // 삭제 후 결과 리스트를 갱신
                    $.get('/api/resultall', function(response) {
                        resultList.result_list = response;
                    });
                },
                error: function(request, status, error) {
                    alert('검색리스트 결과 삭제에 실패하였습니다.');
                },
            });
        },

        // 방문기록 추가 메서드
        updateResult(resultId) {
            console.log('resultlist update resultid', resultId);
            $.ajax({
                type: 'post',
                url: '/api/resultupdate/' + resultId, // api url
                contentType: 'application/json',
                success: function(response, status, xhr) {
                    console.log('방문기록 추가 완료', response);
                    // 방문기록 추가 후 결과 리스트를 갱신
                    $.get('/api/resultall', function(response) {
                        resultList.result_list = response;
                    });
                },
                error: function(request, status, error) {
                    alert('방문기록 추가에 실패하였습니다.');
                },
            });
        }
    }
});



//jquery로 처음 페이지 로딩 될 시에 호출되는 메소드
$(document).ready(function(){
    console.log('jquery ready');
    $('#search-result').hide(); // 처음 로딩 시 검색결과를 숨김
    $('#wish-list-result').hide(); // 처음 로딩 시 검색결과저장 정보를 숨김

    // 검색결과저장 목록 가져오기(이미 검색한 것들은 새로고침 시 위시리스트에 지워지지 않도록)
    $.get('/api/resultall', function(response){
        resultList.result_list = response;
        $('#wish-list-result').show();
    });
});

// 검색란에서 검색어를 입력하고 검색 입력 버튼을 눌렀을때
$('#searchButton').click(function(){
    console.log('search btn click');

    //검색란에서 검색어 값 가져오기
    const query = $('#searchBox').val();

    //실제 backend 에 /api/search url 를 요청
    $.get('/api/search?searchQuery=' + query, function(response){
        console.log('search response 값', response);
        searchResult.search_result = response;

        const title = document.getElementById('wish-title');
         if (title) {
             // HTML 태그를 제거하는 정규 표현식 사용
             title.innerHTML = searchResult.search_result.title.replace(/<[^>]+>/g, '');
         }

        $('#search-result').show();

    });
});

//검색창에서 enter 키를 눌렀을 시 이벤트 처리
$('#searchBox').on('keyup', function(event){
    //13 = enter key
    if(event.key === 'Enter'){     // event.keyCode==13
        console.log("enter key press");
        $('#searchButton').click(); // 클릭 같은 이벤트 실행
    }
});


//검색리스트 추가버튼 클릭시
$('#wish-button').click(function(){
    console.log('wish btn click');
    //jquery ajax 비동기로 검색리스트 내용을 post로 추가요청
    $.ajax({
        type: 'post',
        url: '/api/resultadd', // api url
        contentType: 'application/json',
        data: JSON.stringify(searchResult.search_result), // post로 보낼 파라미터(json으로 보냄..)
        success: function(response, status, xhr){
            console.log('검색리스트 결과 추가 완료', response);

            //response 데이터를 화면에 보이게 하기(바인딩)
            resultList.result_list = response;
        },
        error: function(request, status, error){
            alert('검색리스트 결과 추가에 실패하였습니다.')
        },
    });
});






