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

  loadSavedPlaces(); // 저장된 장소 불러오기
}

// DB에서 저장된 장소 불러오기
function loadSavedPlaces() {
  fetch("/places")
    .then(response => response.json())
    .then(places => processPlaces(places))
    .catch(error => console.error("저장된 장소 로드 오류:", error));
}

// 검색 요청
function searchPlaces(query) {
  fetch(`/search?query=${query}`)
    .then(response => response.json())
    .then(places => processPlaces(places))
    .catch(error => console.error("검색 오류:", error));
}

// 검색된 장소 표시
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

    let label = "";
    if (startPlace && startPlace.address === place.address) label = " (출발지)";
    if (endPlace && endPlace.address === place.address) label = " (목적지)";

    li.innerHTML = `
      <h4>${place.placeName}${label}</h4>
      <p>${place.address || '주소 정보 없음'}</p>
    `;

    li.addEventListener('click', function () {
      selectPlace(li);
    });

    placesList.appendChild(li);
  });
}

// 장소 데이터 처리
function processPlaces(places) {
  createMarkers(places);
  displayPlacesList(places);

  if (places.length > 0) {
    var firstPlace = places[0];
    moveToPlace(firstPlace.latitude, firstPlace.longitude);
    showPlaceInfo(firstPlace);
  }
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
  clearMarkers();

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

// 장소 정보 창 닫기
function closePlaceInfo() {
  document.getElementById("place_info").style.display = "none";
  infoWindow.close();
}

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

// 장소 강조 표시
function highlightPlaceItem(place) {
  document.querySelectorAll('.place-item').forEach(function (item) {
    item.classList.remove('active');
  });

  document.querySelectorAll('.place-item').forEach(function (item) {
    var itemLat = parseFloat(item.getAttribute('data-lat'));
    var itemLng = parseFloat(item.getAttribute('data-lng'));

    if (itemLat === place.latitude && itemLng === place.longitude) {
      item.classList.add('active');
    }
  });
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
