
document.getElementById('loginForm').addEventListener('submit', function(event){
    event.preventDefault();  // 폼 제출을 막고, ajax로 데이터를 전송할 수 있습니다.
    const userId = document.getElementById('userId').value;
    const password = document.getElementById('password').value;

    // 서버로 데이터를 보내서 로그인 처리
    fetch('/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ userId, password })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert('로그인 성공');
            window.location.href = '/main';  // 성공 시 main 페이지로 이동
        } else {
            alert('아이디 또는 비밀번호가 일치하지 않습니다.');
        }
    });
});