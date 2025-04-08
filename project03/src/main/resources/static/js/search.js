// 전역 변수
var map;
var markers = [];
var infoWindow = null;
var startPlace = null;
var endPlace = null;
var startMarker = null;
var endMarker = null;

// 지도 초기화
function initMap() {
  var mapContainer = document.getElementById('map');
  var mapOption = {
    center: new kakao.maps.LatLng(37.5665, 126.9780),
    level: 5
  };
  map = new kakao.maps.Map(mapContainer, mapOption);
  infoWindow = new kakao.maps.InfoWindow({ zIndex: 1 });

}

// 검색 요청 (기존 마커 초기화 추가)
function searchPlaces(query) {
  fetch(`/search?query=${query}`)
    .then(response => response.json())
    .then(places => {
      clearMarkers(); // 기존 마커 초기화
      processPlaces(places);
    })
    .catch(error => console.error("검색 오류:", error));
}

// 검색된 장소 목록 표시
function displayPlacesList(places) {
  const placesList = document.getElementById("places-list");
  placesList.innerHTML = "";

  places.forEach(function (place) {
    const li = document.createElement("li");
    li.classList.add("place-item");
    li.setAttribute("data-lat", place.latitude);
    li.setAttribute("data-lng", place.longitude);
    li.setAttribute("data-name", place.placeName);
    li.setAttribute("data-address", place.address);
    li.setAttribute("data-road-address", place.roadAddress);

    li.innerHTML = `
      <h4>${place.placeName}</h4>
      <p>${place.address || '주소 정보 없음'}</p>
    `;

    li.addEventListener('click', function () {
      selectPlace(li);
    });

    placesList.appendChild(li);
  });

  if (places.length > 0) {
    moveToPlace(places[0].latitude, places[0].longitude);
    showPlaceInfo(places[0]);
  }
}

// 장소 데이터 처리
function processPlaces(places) {
  createMarkers(places);
  displayPlacesList(places);
}

// 마커 이미지 설정
var startMarkerImage = new kakao.maps.MarkerImage(
  'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png',
  new kakao.maps.Size(24, 35)
);
var endMarkerImage = new kakao.maps.MarkerImage(
  'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_red.png',
  new kakao.maps.Size(24, 35)
);

// 마커 생성
function createMarkers(places) {
  clearMarkers(); // 기존 마커 초기화

  places.forEach(function (place) {
    var markerPosition = new kakao.maps.LatLng(place.latitude, place.longitude);

    var marker = new kakao.maps.Marker({
      position: markerPosition,
      map: map
    });

    kakao.maps.event.addListener(marker, 'click', function () {
      moveToPlace(place.latitude, place.longitude);
      showPlaceInfo(place);
      highlightPlaceItem(place);
    });

    markers.push(marker);
  });
}

// 마커 초기화
function clearMarkers() {
  markers.forEach(marker => marker.setMap(null));
  markers = [];
}

// 출발지/목적지 마커 업데이트
function updateStartMarker(place) {
  if (startMarker) startMarker.setMap(null);
  startMarker = new kakao.maps.Marker({
    position: new kakao.maps.LatLng(place.latitude, place.longitude),
    map: map,
    image: startMarkerImage
  });
}

function updateEndMarker(place) {
  if (endMarker) endMarker.setMap(null);
  endMarker = new kakao.maps.Marker({
    position: new kakao.maps.LatLng(place.latitude, place.longitude),
    map: map,
    image: endMarkerImage
  });
}

// 장소 선택 (출발지/목적지 설정)
window.selectPlace = function (element) {
  var lat = parseFloat(element.getAttribute('data-lat'));
  var lng = parseFloat(element.getAttribute('data-lng'));
  var name = element.getAttribute('data-name');
  var address = element.getAttribute('data-address');

  var selectedPlace = { latitude: lat, longitude: lng, placeName: name, address: address };

  // 기존 라벨 제거 후 새로운 라벨 추가
  document.querySelectorAll(".place-item").forEach(item => {
    item.innerHTML = item.innerHTML.replace(" (출발지)", "").replace(" (목적지)", "");
  });

  if (!startPlace) {
    startPlace = selectedPlace;
    updateStartMarker(startPlace);
    element.innerHTML += " (출발지)";
  } else if (!endPlace) {
    endPlace = selectedPlace;
    updateEndMarker(endPlace);
    element.innerHTML += " (목적지)";
  }

  moveToPlace(lat, lng);
  showPlaceInfo(selectedPlace);
};

// 지도 이동
function moveToPlace(lat, lng) {
  var moveLatLng = new kakao.maps.LatLng(lat, lng);
  map.panTo(moveLatLng);

  setTimeout(function () {
    map.relayout();
  }, 100);
}

// 장소 정보 표시
function showPlaceInfo(place) {
  document.getElementById("info_name").textContent = place.placeName || "장소 이름 없음";
  document.getElementById("info_address").textContent = place.address || "주소 정보 없음";
  document.getElementById("info_road_address").textContent = place.roadAddress || "도로명 주소 없음";

  document.getElementById("place_info").style.display = "block";

  var content = `
    <div style="padding:10px;">
      <h4>${place.placeName || "장소 이름 없음"}</h4>
      <p>${place.address || "주소 정보 없음"}</p>
    </div>
  `;

  infoWindow.setContent(content);
  infoWindow.setPosition(new kakao.maps.LatLng(place.latitude, place.longitude));
  infoWindow.open(map);
}

// 검색 폼 이벤트
document.getElementById("searchForm").addEventListener('submit', function (event) {
  event.preventDefault();
  var query = document.getElementById("query").value;
  searchPlaces(query);
});

// 페이지 로드 시 지도 초기화
document.addEventListener('DOMContentLoaded', function () {
  kakao.maps.load(initMap);
});

// 모달 창 이벤트 (중복 이벤트 해결)
document.addEventListener("DOMContentLoaded", function () {
  const modal = document.getElementById("previousRoutesModal");
  const openBtn = document.getElementById("openPreviousRoutesModal");
  const closeBtn = document.querySelector(".close");

  // 모달 열기
  openBtn.addEventListener("click", function () {
    modal.style.display = "block";
    loadPreviousRoutes(); // 경로 불러오기
  });

  // 모달 닫기 (X 버튼)
  closeBtn.addEventListener("click", function () {
    modal.style.display = "none";
  });

  // 모달 닫기 (바깥 클릭)
  window.addEventListener("click", function (event) {
    if (event.target === modal) {
      modal.style.display = "none";
    }
  });
});

function loadPreviousRoutes() {
  fetch("/api/routes/previous")
    .then(response => response.json())
    .then(routes => {
      const list = document.getElementById("previousRoutesList");
      list.innerHTML = "";

      routes.forEach((route, index) => {
        const li = document.createElement("li");
        li.className = "route-item";

        const text = document.createElement("span");
        text.textContent = `출발지: ${route.startAddress} → 목적지: ${route.endAddress}`;
        text.className = "route-text";
        text.addEventListener("click", () => selectRoute(route));

        const deleteBtn = document.createElement("button");
        deleteBtn.textContent = "삭제";
        deleteBtn.className = "btn-delete";
        deleteBtn.addEventListener("click", () => deleteRoute(route.id));

        li.appendChild(text);
        li.appendChild(deleteBtn);
        list.appendChild(li);
      });
    })
    .catch(error => console.error("이전 경로 로드 오류:", error));
}

function deleteRoute(routeId) {
  fetch(`/api/routes/${routeId}`, {
    method: "DELETE"
  })
    .then(response => {
      if (response.ok) {
        alert("경로 삭제 완료!");
        loadPreviousRoutes(); // 다시 불러오기
      } else {
        alert("삭제 실패");
      }
    });
}

function favoriteRoute(routeId) {
  // 서버에서 즐겨찾기 기능 구현되어 있어야 함
  fetch(`/api/routes/favorite/${routeId}`, {
    method: "POST"
  })
    .then(response => {
      if (response.ok) {
        alert("즐겨찾기에 추가되었습니다!");
      } else {
        alert("즐겨찾기 실패");
      }
    });
}
