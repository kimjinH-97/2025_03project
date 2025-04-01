// 전역 변수
var map;
var markers = [];
var infoWindow = null;
var startPlace = null;  // 출발지 저장 변수
var endPlace = null;    // 목적지 저장 변수
var startMarker = null; // 출발지 마커
var endMarker = null;   // 목적지 마커

// 지도 초기화 함수
function initMap() {
  var mapContainer = document.getElementById('map');
  var mapOption = {
    center: new kakao.maps.LatLng(37.5665, 126.9780), // 초기 위치 서울
    level: 5 // 초기 확대 레벨
  };

  map = new kakao.maps.Map(mapContainer, mapOption);
  infoWindow = new kakao.maps.InfoWindow({zIndex:1});
  console.log("지도 초기화 완료"); // 지도 초기화 확인
}

// 비동기식 장소 검색 요청 함수
function searchPlaces(query) {
  fetch(`/search?query=${query}`)
    .then(response => response.json())
    .then(places => processPlaces(places))
    .catch(error => console.error("검색 오류:", error));
}

// 검색된 장소 목록을 화면에 출력
function displayPlacesList(places) {
  const placesList = document.getElementById("places-list");
  placesList.innerHTML = "";  // 기존 목록 초기화

  places.forEach(function(place) {
    const li = document.createElement("li");
    li.classList.add("place-item");
    li.setAttribute("data-lat", place.latitude);
    li.setAttribute("data-lng", place.longitude);
    li.setAttribute("data-name", place.placeName);
    li.setAttribute("data-address", place.address);
    li.setAttribute("data-road-address", place.roadAddress);

    // 출발지가 설정된 경우 "출발지입니다" 표시
    if (startPlace && startPlace.address === place.address) {
      li.innerHTML = `
        <h4>${place.placeName} (출발지입니다)</h4>
        <p>${place.address || '주소 정보 없음'}</p>
      `;
    }
    // 목적지가 설정된 경우 "목적지입니다" 표시
    else if (endPlace && endPlace.address === place.address) {
      li.innerHTML = `
        <h4>${place.placeName} (목적지입니다)</h4>
        <p>${place.address || '주소 정보 없음'}</p>
      `;
    } else {
      li.innerHTML = `
        <h4>${place.placeName}</h4>
        <p>${place.address || '주소 정보 없음'}</p>
      `;
    }

    li.addEventListener('click', function() {
      selectPlace(li);
    });

    placesList.appendChild(li);
  });
}

// 장소 데이터 처리
function processPlaces(places) {
  console.log("검색된 장소:", places); // 장소 데이터 확인
  createMarkers(places);
  displayPlacesList(places);

  // 첫 번째 장소로 이동 (장소가 있는 경우)
  if (places.length > 0) {
    var firstPlace = places[0];
    moveToPlace(firstPlace.latitude, firstPlace.longitude);
    showPlaceInfo(firstPlace);
  }
}

// 마커 생성
function createMarkers(places) {
  clearMarkers();  // 기존 마커 모두 지우기

  places.forEach(function(place) {
    var markerPosition = new kakao.maps.LatLng(place.latitude, place.longitude);
    console.log("마커 위치:", markerPosition); // 마커 위치 확인

    var marker = new kakao.maps.Marker({
      position: markerPosition,
      map: map
    });

    // 출발지 마커 색상 변경
    if (startPlace && startPlace.address === place.address) {
      marker.setImage(new kakao.maps.MarkerImage(
        'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png',
        new kakao.maps.Size(24, 35)
      ));
    }

    // 목적지 마커 색상 변경
    if (endPlace && endPlace.address === place.address) {
      marker.setImage(new kakao.maps.MarkerImage(
        'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png',
        new kakao.maps.Size(24, 35)
      ));
    }

    // 마커 클릭 이벤트
    kakao.maps.event.addListener(marker, 'click', function() {
      moveToPlace(place.latitude, place.longitude);
      showPlaceInfo(place);
      highlightPlaceItem(place);
    });

    markers.push(marker);
  });
}

// 마커 초기화
function clearMarkers() {
  markers.forEach(function(marker) {
    marker.setMap(null);
  });
  markers = [];
}

// 장소 선택 함수 (목록에서 클릭 시 호출)
window.selectPlace = function(element) {
  var lat = parseFloat(element.getAttribute('data-lat'));
  var lng = parseFloat(element.getAttribute('data-lng'));

  moveToPlace(lat, lng);

  // 해당 장소 정보 찾기
  var selectedPlace = {
    latitude: lat,
    longitude: lng,
    placeName: element.getAttribute('data-name'),
    address: element.getAttribute('data-address'),
    roadAddress: element.getAttribute('data-road-address')
  };

   // 텍스트 박스를 한 번 클릭하면 보이고, 두 번 클릭하면 숨기기
    var infoWindowVisible = element.classList.contains('active');  // 기존에 active 클래스가 있으면 이미 클릭된 것

    if (infoWindowVisible) {
      // 클릭된 장소 정보 비활성화 (숨김 처리)
      closePlaceInfo();
      element.classList.remove('active'); // active 클래스 제거
    } else {
      // 처음 클릭했을 때 장소 정보 표시
      showPlaceInfo(selectedPlace);
      element.classList.add('active'); // active 클래스 추가
    }

    // 해당 아이템을 강조 표시
    highlightPlaceItem(selectedPlace);
  };

  // 장소 정보 창 닫기 함수
  function closePlaceInfo() {
    const placeInfo = document.getElementById("place_info");
    if (placeInfo) {
      placeInfo.style.display = "none";  // 장소 정보 창 숨김
    }
    infoWindow.close();  // 지도에서 열린 정보 창 닫기
  }

// 지도 이동 함수
function moveToPlace(lat, lng) {
  var moveLatLng = new kakao.maps.LatLng(lat, lng);
  map.panTo(moveLatLng);

  // 지도 표시 문제 해결을 위해 리사이즈
  setTimeout(function() {
    map.relayout();
  }, 100);
}

// 장소 정보 표시
function showPlaceInfo(place) {
  document.getElementById("info_name").textContent = place.placeName || "장소 이름 없음";
  document.getElementById("info_address").textContent = place.address || "주소 정보 없음";
  document.getElementById("info_road_address").textContent = place.roadAddress || "도로명 주소 없음";

  // 정보 창 표시
  document.getElementById("place_info").style.display = "block";

  // 인포윈도우에 정보 표시 (옵션)
  var content = '<div style="padding:10px;">' +
                '<h4>' + (place.placeName || "장소 이름 없음") + '</h4>' +
                '<p>' + (place.address || "주소 정보 없음") + '</p>' +
                '</div>';

  infoWindow.setContent(content);
  infoWindow.setPosition(new kakao.maps.LatLng(place.latitude, place.longitude));
  infoWindow.open(map);
}

// 장소 정보 창 닫기
window.closePlaceInfo = function() {
  document.getElementById("place_info").style.display = "none";
  infoWindow.close();
};

// 장소 항목 강조 표시
function highlightPlaceItem(place) {
  document.querySelectorAll('.place-item').forEach(function(item) {
    item.classList.remove('active');
    var details = item.querySelector('.place-details');
    if (details) {
      details.style.display = 'none';
    }
  });

  document.querySelectorAll('.place-item').forEach(function(item) {
    var itemLat = parseFloat(item.getAttribute('data-lat'));
    var itemLng = parseFloat(item.getAttribute('data-lng'));

    if (itemLat === place.latitude && itemLng === place.longitude) {
      item.classList.add('active');
      var details = item.querySelector('.place-details');
      if (details) {
        details.style.display = 'block';
      }
    }
  });
}



// 검색 폼 제출 시 비동기식 검색
document.getElementById("searchForm").addEventListener('submit', function(event) {
  event.preventDefault();
  var query = document.getElementById("query").value;
  searchPlaces(query);
});

// 페이지 로드 시 지도 초기화
document.addEventListener('DOMContentLoaded', function() {
  kakao.maps.load(initMap);
});

