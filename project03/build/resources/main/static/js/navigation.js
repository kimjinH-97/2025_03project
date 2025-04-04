// 전역 변수
var map;
var markers = [];
var infoWindow = null;
var startPlace = null;  // 출발지 저장 변수
var endPlace = null;    // 목적지 저장 변수
var startMarker = null; // 출발지 마커
var endMarker = null;   // 목적지 마커
var polyline = null;    // 경로 폴리라인 저장 변수

// 출발지 주소로부터 위도, 경도 계산 후 설정
function setStartPlace() {
  const placeName = document.getElementById("info_name").textContent; // 장소 이름
  const placeAddress = document.getElementById("info_address").textContent; // 실제 주소

  // Geocoder를 사용하여 주소로부터 위도, 경도 찾기
  var geocoder = new kakao.maps.services.Geocoder();
  geocoder.addressSearch(placeAddress, function(result, status) {
    if (status === kakao.maps.services.Status.OK) {
      // 위도, 경도 계산된 값
      var lat = result[0].y;
      var lng = result[0].x;

      // 출발지 설정
      startPlace = {
        placeName: placeName,
        address: placeAddress,
        latitude: lat,
        longitude: lng
      };

      // 출발지 입력란에 설정 - 주소만 설정
      document.getElementById("start").value = placeAddress;

      // 출발지 마커 표시
      if (startMarker) {
        startMarker.setMap(null); // 기존 마커 삭제
      }
      startMarker = new kakao.maps.Marker({
        position: new kakao.maps.LatLng(lat, lng),
        map: map
      });

      // 출발지 설정 후 검색 결과 갱신
      searchPlaces(document.getElementById("query").value);  // 기존 검색어로 다시 검색하여 결과 업데이트
    }
  });
}

// 목적지 주소로부터 위도, 경도 계산 후 설정
function setEndPlace() {
  const placeName = document.getElementById("info_name").textContent; // 장소 이름
  const placeAddress = document.getElementById("info_address").textContent; // 실제 주소

  // Geocoder를 사용하여 주소로부터 위도, 경도 찾기
  var geocoder = new kakao.maps.services.Geocoder();
  geocoder.addressSearch(placeAddress, function(result, status) {
    if (status === kakao.maps.services.Status.OK) {
      // 위도, 경도 계산된 값
      var lat = result[0].y;
      var lng = result[0].x;

      // 목적지 설정
      endPlace = {
        placeName: placeName,
        address: placeAddress,
        latitude: lat,
        longitude: lng
      };

      // 목적지 입력란에 설정 - 주소만 설정
      document.getElementById("end").value = placeAddress;

      // 목적지 마커 표시
      if (endMarker) {
        endMarker.setMap(null); // 기존 마커 삭제
      }
      endMarker = new kakao.maps.Marker({
        position: new kakao.maps.LatLng(lat, lng),
        map: map
      });

      // 목적지 설정 후 검색 결과 갱신
      searchPlaces(document.getElementById("query").value);  // 기존 검색어로 다시 검색하여 결과 업데이트
    }
  });
}

// 경로 점차적으로 지우기
function eraseRoute() {
  const linePath = polyline.getPath();
  let pathLength = linePath.length;

  // 경로를 점차적으로 지우는 시간 비율
  const duration = document.getElementById("routeInfo").value.match(/이동 시간: (\d+)분 (\d+)초/);
  const totalDurationInSeconds = parseInt(duration[1]) * 60 + parseInt(duration[2]);  // 이동 시간 (초 단위)

  if (totalDurationInSeconds <= 0) {
    console.error("유효한 이동 시간이 없습니다.");
    return;
  }

  const intervalTime = totalDurationInSeconds * 1000 / pathLength;  // 경로의 길이에 맞춰 각 점을 지우는 시간 계산

  let currentIndex = 0;
  const interval = setInterval(function() {
    if (currentIndex >= pathLength) {
      clearInterval(interval);
      document.getElementById("status").textContent = "도착!"; // 도착 상태로 변경
    } else {
      linePath.shift();  // 첫 번째 점을 하나씩 제거
      polyline.setPath(linePath); // 경로 업데이트
    }
    currentIndex++;
  }, intervalTime);  // 각 점을 일정 시간 간격으로 삭제
}

// 경로 추천 버튼 클릭 시 기존 마커 제거
function clearOtherMarkers() {
  // 출발지와 목적지 마커는 제외하고 나머지 마커들 삭제
  markers.forEach(marker => {
    if (marker !== startMarker && marker !== endMarker) {
      marker.setMap(null); // 마커 제거
    }
  });

  // 마커 배열 비우기
  markers = [];
}
function closeInfoWindows() {
  if (infoWindow) {
    infoWindow.close(); // 모든 InfoWindow를 닫음
  }
}

let totalDistance = 0;  // 총 거리 (km 단위, 초기값은 0)
let totalDuration = 0;  // 예상 도착 시간 (분 단위, 초기값은 0)
let remainingDistance = 0;  // 남은 거리 (초기값은 총 거리)
let remainingTime = 0;  // 남은 시간 (초 단위로 계산)

// 경로 찾기만 하는 함수
async function findRoute() {

  const startAddress = document.getElementById("start").value;  // 출발지 주소
  const endAddress = document.getElementById("end").value;  // 목적지 주소

  if (!startAddress || !endAddress) {
    alert("출발지와 목적지를 입력하세요!");
    return;
  }

  // 경로 찾기 전에 기존 마커들 제거
  clearOtherMarkers();

  // 기존에 그려진 경로가 있으면 지도에서 제거
    if (polyline) {
      polyline.setMap(null);  // 기존 경로 삭제
    }

  // 출발지 좌표 변환
  const startLatLng = await geocodeAddress(startAddress);
  const endLatLng = await geocodeAddress(endAddress);

  if (!startLatLng || !endLatLng) {
    alert("주소 변환 실패");
    return;
  }

  const REST_API_KEY = 'ccc93a4ef4efcbf5df114841f4c5b32b';  // 발급받은 Kakao API 키
  const url = 'https://apis-navi.kakaomobility.com/v1/directions';

  const origin = `${startLatLng.getLng()},${startLatLng.getLat()}`;  // 출발지 좌표
  const destination = `${endLatLng.getLng()},${endLatLng.getLat()}`;  // 목적지 좌표

  const headers = {
    Authorization: `KakaoAK ${REST_API_KEY}`,
    'Content-Type': 'application/json'
  };

  const queryParams = new URLSearchParams({
    origin: origin,
    destination: destination
  });

  const requestUrl = `${url}?${queryParams}`;

  try {
    const response = await fetch(requestUrl, {
      method: 'GET',
      headers: headers
    });

    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}`);
    }

    const data = await response.json();

    if (data.routes && data.routes.length > 0) {
      const linePath = [];

      // 경로 데이터 처리: vertexes에서 좌표 추출
      data.routes[0].sections[0].roads.forEach(router => {
        router.vertexes.forEach((vertex, index) => {
          if (index % 2 === 0) {
            linePath.push(new kakao.maps.LatLng(router.vertexes[index + 1], router.vertexes[index]));
          }
        });
      });

      // 경로를 지도에 표시
      polyline = new kakao.maps.Polyline({
        path: linePath,
        strokeWeight: 5,  // 선 두께
        strokeColor: '#000000',  // 선 색깔
        strokeOpacity: 0.7,  // 선 투명도
        strokeStyle: 'solid'  // 선 스타일
      });

      polyline.setMap(map);  // map은 지도 객체

      // 거리와 이동시간을 텍스트로 출력
      const distance = data.routes[0].sections[0].distance; // 거리 (m 단위)
      const duration = data.routes[0].sections[0].duration; // 시간 (초 단위)

      const routeInfo = `경로 정보:\n거리: ${distance / 1000} km\n이동 시간: ${Math.floor(duration / 60)}분 ${duration % 60}초`;

      // 경로 정보를 textarea에 출력
      document.getElementById("routeInfo").value = routeInfo;

      // 총 거리 및 예상 시간
      totalDistance = distance / 1000;  // km 단위로 변환
      totalDuration = duration / 60;  // 분 단위로 변환

      // 남은 거리 및 시간 초기화
      remainingDistance = totalDistance;
      remainingTime = totalDuration * 60;  // 분 단위로 변환 후 초 단위로 설정

    } else {
      console.error("경로를 찾을 수 없습니다.");
    }
  } catch (error) {
    console.error('Error:', error);
  }
}

// 주소를 좌표로 변환하는 함수
function geocodeAddress(address) {
  return new Promise((resolve, reject) => {
    const geocoder = new kakao.maps.services.Geocoder();
    geocoder.addressSearch(address, (result, status) => {
      if (status === kakao.maps.services.Status.OK) {
        const latLng = new kakao.maps.LatLng(result[0].y, result[0].x);
        resolve(latLng);  // 좌표 반환
      } else {
        reject("주소 변환 실패");
      }
    });
  });
}

// 출발 시작 버튼을 클릭하면 호출되는 함수
function startJourney() {
  if (!totalDistance || !totalDuration) {
    alert("경로를 먼저 찾으세요!");
    return;
  }

  // 출발 시작 후 남은 거리 및 예상 도착 시간을 출력
  document.getElementById("status").textContent = "출발 중...";

  // 경로 지우기 (출발 시작 버튼 클릭 시 경로 삭제)
  eraseRoute(); // 경로 지우는 함수 호출

  // 1초당 이동 거리 계산 (totalDistance / totalDuration * 60)
  const distancePerSecond = totalDistance / (totalDuration * 60);  // 1초당 이동할 거리 (km)

  // 타이머 시작
  let timeInterval = setInterval(() => {
    if (remainingTime <= 0) {
      clearInterval(timeInterval);  // 타이머 종료
      document.getElementById("routeInfoDetails").value = "목적지에 도착했습니다!";
    } else {
      // 남은 거리와 시간 계산
      remainingDistance -= distancePerSecond;  // 1초가 지날 때마다 이동 거리 감소
      remainingTime -= 1;  // 1초가 지날 때마다 남은 시간 감소 (1초씩)

      // 실시간으로 남은 거리와 예상 도착 시간 업데이트
      const remainingInfo = `남은 거리: ${remainingDistance.toFixed(2)} km\n예상 도착 시간: ${Math.floor(remainingTime / 60)}분 ${remainingTime % 60}초`;
      document.getElementById("routeInfoDetails").value = remainingInfo;
    }
  }, 1000);  // 1초마다 업데이트
}
