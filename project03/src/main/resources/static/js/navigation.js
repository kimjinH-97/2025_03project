// 전역 변수
var map;
var markers = [];
var infoWindow = null;
var startPlace = null;  // 출발지 저장 변수
var endPlace = null;    // 목적지 저장 변수
var startMarker = null; // 출발지 마커
var endMarker = null;   // 목적지 마커
var polyline = null;    // 경로 폴리라인 저장 변수

// 출발지 설정
function setStartPlace() {
  const placeName = document.getElementById("info_name").textContent;
  const placeAddress = document.getElementById("info_address").textContent;

  var geocoder = new kakao.maps.services.Geocoder();
  geocoder.addressSearch(placeAddress, function(result, status) {
    if (status === kakao.maps.services.Status.OK) {
      var lat = result[0].y;
      var lng = result[0].x;

      startPlace = { placeName, address: placeAddress, latitude: lat, longitude: lng };
      document.getElementById("start").value = placeAddress;

      if (startMarker) startMarker.setMap(null);
      startMarker = new kakao.maps.Marker({ position: new kakao.maps.LatLng(lat, lng), map: map });

      searchPlaces(document.getElementById("query").value);
    }
  });
}

// 목적지 설정
function setEndPlace() {
  const placeName = document.getElementById("info_name").textContent;
  const placeAddress = document.getElementById("info_address").textContent;

  var geocoder = new kakao.maps.services.Geocoder();
  geocoder.addressSearch(placeAddress, function(result, status) {
    if (status === kakao.maps.services.Status.OK) {
      var lat = result[0].y;
      var lng = result[0].x;

      endPlace = { placeName, address: placeAddress, latitude: lat, longitude: lng };
      document.getElementById("end").value = placeAddress;

      if (endMarker) endMarker.setMap(null);
      endMarker = new kakao.maps.Marker({ position: new kakao.maps.LatLng(lat, lng), map: map });

      searchPlaces(document.getElementById("query").value);
    }
  });
}

// 기존 마커 제거
function clearOtherMarkers() {
  markers.forEach(marker => {
    if (marker !== startMarker && marker !== endMarker) {
      marker.setMap(null);
    }
  });
  markers = [];
}

// 주소 → 좌표 변환 함수
function geocodeAddress(address) {
  return new Promise((resolve, reject) => {
    const geocoder = new kakao.maps.services.Geocoder();
    geocoder.addressSearch(address, (result, status) => {
      if (status === kakao.maps.services.Status.OK) {
        resolve(new kakao.maps.LatLng(result[0].y, result[0].x));
      } else {
        reject("주소 변환 실패");
      }
    });
  });
}

// 백엔드로 경로 데이터 전송
async function sendRouteToBackend(start, end, distance, duration) {
  try {
    const response = await fetch('/api/routes/save', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        start: { placeName: start, address: start }, // start 필드 맞추기
        end: { placeName: end, address: end },       // end 필드 맞추기
        distance,
        duration
      })
    });

    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}`);
    }
    console.log("경로 정보가 성공적으로 저장되었습니다.");
  } catch (error) {
    console.error("백엔드 저장 오류:", error);
  }
}


// 경로 찾기
async function findRoute() {
  const startAddress = document.getElementById("start").value;
  const endAddress = document.getElementById("end").value;

  if (!startAddress || !endAddress) {
    alert("출발지와 목적지를 입력하세요!");
    return;
  }

  clearOtherMarkers();
  if (polyline) polyline.setMap(null);

  const startLatLng = await geocodeAddress(startAddress);
  const endLatLng = await geocodeAddress(endAddress);

  if (!startLatLng || !endLatLng) {
    alert("주소 변환 실패");
    return;
  }

  const REST_API_KEY = 'ccc93a4ef4efcbf5df114841f4c5b32b';
  const url = `https://apis-navi.kakaomobility.com/v1/directions?origin=${startLatLng.getLng()},${startLatLng.getLat()}&destination=${endLatLng.getLng()},${endLatLng.getLat()}`;

  try {
    const response = await fetch(url, { method: 'GET', headers: { Authorization: `KakaoAK ${REST_API_KEY}`, 'Content-Type': 'application/json' } });

    if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);

    const data = await response.json();

    if (data.routes && data.routes.length > 0) {
      const linePath = [];
      data.routes[0].sections[0].roads.forEach(router => {
        router.vertexes.forEach((vertex, index) => {
          if (index % 2 === 0) {
            linePath.push(new kakao.maps.LatLng(router.vertexes[index + 1], router.vertexes[index]));
          }
        });
      });

      polyline = new kakao.maps.Polyline({
        path: linePath,
        strokeWeight: 5, strokeColor: '#000000', strokeOpacity: 0.7, strokeStyle: 'solid'
      });

      polyline.setMap(map);

      const distance = data.routes[0].sections[0].distance / 1000;
      const duration = data.routes[0].sections[0].duration / 60;

      document.getElementById("routeInfo").innerHTML = `경로 정보:<br>거리: ${distance} km<br>이동 시간: ${Math.floor(duration)}분`;

      // 경로 정보 백엔드 저장
      await sendRouteToBackend(startAddress, endAddress, distance, duration);

    } else {
      console.error("경로를 찾을 수 없습니다.");
    }
  } catch (error) {
    console.error('Error:', error);
  }
}
