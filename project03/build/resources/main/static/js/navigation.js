// 전역 변수
var map;
var markers = [];
var infoWindow = null;
var startPlace = null;
var endPlace = null;
var startMarker = null;
var endMarker = null;
var polyline = null;
let movingMarker = null;
let movingIndex = 0;
let eraseCurrentIndex = 0;
eraseCurrentIndex = 0;
routeCoords = [];
let eraseInterval = null;
let startAddress = "";
let endAddress = "";

// 출발지 설정
function setStartPlace() {
  const placeName = document.getElementById("info_name").textContent;
  const placeAddress = document.getElementById("info_address").textContent;

  var geocoder = new kakao.maps.services.Geocoder();
  geocoder.addressSearch(placeAddress, function (result, status) {
    if (status === kakao.maps.services.Status.OK) {
      var lat = result[0].y;
      var lng = result[0].x;

      startPlace = {
        placeName: placeName,
        address: placeAddress,
        latitude: lat,
        longitude: lng
      };
      document.getElementById("start").value = placeAddress;

      if (startMarker) {
        startMarker.setMap(null);
      }
      startMarker = new kakao.maps.Marker({
        position: new kakao.maps.LatLng(lat, lng),
        map: map
      });

      searchPlaces(document.getElementById("query").value);
    }
  });
}

// 목적지 설정
function setEndPlace() {
  const placeName = document.getElementById("info_name").textContent;
  const placeAddress = document.getElementById("info_address").textContent;

  var geocoder = new kakao.maps.services.Geocoder();
  geocoder.addressSearch(placeAddress, function (result, status) {
    if (status === kakao.maps.services.Status.OK) {
      var lat = result[0].y;
      var lng = result[0].x;

      endPlace = {
        placeName: placeName,
        address: placeAddress,
        latitude: lat,
        longitude: lng
      };
      document.getElementById("end").value = placeAddress;

      if (endMarker) {
        endMarker.setMap(null);
      }
      endMarker = new kakao.maps.Marker({
        position: new kakao.maps.LatLng(lat, lng),
        map: map
      });

      searchPlaces(document.getElementById("query").value);
    }
  });
}

function eraseRoute() {
  if (!polyline) {
    console.error("polyline이 존재하지 않습니다.");
    return;
  }

  const linePath = polyline.getPath();
  if (linePath.length === 0) {
    console.error("경로가 없습니다.");
    return;
  }

  const match = document.getElementById("routeInfo").innerText.match(/이동 시간: (\d+)분 (\d+)초/);
  if (!match) {
    console.error("이동 시간 패턴을 찾을 수 없습니다.");
    return;
  }

  const totalDurationInSeconds = parseInt(match[1]) * 60 + parseInt(match[2]);
  const intervalTime = totalDurationInSeconds * 1000 / linePath.length;

  eraseInterval = setInterval(function () {
    if (eraseCurrentIndex >= linePath.length) {
      clearInterval(eraseInterval);
      document.getElementById("status").textContent = "도착!";
    } else {
      linePath.shift();
      polyline.setPath(linePath);
      eraseCurrentIndex++;
    }
  }, intervalTime);
}



// 마커 정리
function clearOtherMarkers() {
  markers.forEach(marker => {
    if (marker !== startMarker && marker !== endMarker) {
      marker.setMap(null);
    }
  });
  markers = [];
}

let totalDistance = 0;
let totalDuration = 0;
let remainingDistance = 0;
let remainingTime = 0;

// 🚀 [추가] 백엔드로 경로 데이터 전송
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
  startAddress = document.getElementById("start").value;
  endAddress = document.getElementById("end").value;

  if (!startAddress || !endAddress) {
    alert("출발지와 목적지를 입력하세요!");
    return;
  }

  clearOtherMarkers();

  if (polyline) {
    polyline.setMap(null);
  }

  const startLatLng = await geocodeAddress(startAddress);
  const endLatLng = await geocodeAddress(endAddress);

  if (!startLatLng || !endLatLng) {
    alert("주소 변환 실패");
    return;
  }

  const REST_API_KEY = 'ccc93a4ef4efcbf5df114841f4c5b32b';
  const url = 'https://apis-navi.kakaomobility.com/v1/directions';
  const origin = `${startLatLng.getLng()},${startLatLng.getLat()}`;
  const destination = `${endLatLng.getLng()},${endLatLng.getLat()}`;

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
      data.routes[0].sections[0].roads.forEach(router => {
        router.vertexes.forEach((vertex, index) => {
          if (index % 2 === 0) {
            linePath.push(new kakao.maps.LatLng(router.vertexes[index + 1], router.vertexes[index]));
          }
        });
      });

      polyline = new kakao.maps.Polyline({
        path: linePath,
        strokeWeight: 5,
        strokeColor: '#000000',
        strokeOpacity: 0.7,
        strokeStyle: 'solid'
      });

      polyline.setMap(map);

      const distance = data.routes[0].sections[0].distance;
      const duration = data.routes[0].sections[0].duration;

      document.getElementById("routeInfo").innerHTML =
        `경로 정보:<br>거리: ${(distance / 1000).toFixed(2)} km<br>이동 시간: ${Math.floor(duration / 60)}분 ${duration % 60}초`;

      totalDistance = distance / 1000;
      totalDuration = duration / 60;

      remainingDistance = totalDistance;
      remainingTime = totalDuration * 60;

      // 🚀 [추가] 경로 정보 백엔드 저장
            await sendRouteToBackend(startAddress, endAddress, distance, duration);

    } else {
      console.error("경로를 찾을 수 없습니다.");
    }
  } catch (error) {
    console.error('Error:', error);
  }
}

// 주소를 좌표로 변환
function geocodeAddress(address) {
  return new Promise((resolve, reject) => {
    const geocoder = new kakao.maps.services.Geocoder();
    geocoder.addressSearch(address, (result, status) => {
      if (status === kakao.maps.services.Status.OK) {
        const latLng = new kakao.maps.LatLng(result[0].y, result[0].x);
        resolve(latLng);
      } else {
        reject("주소 변환 실패");
      }
    });
  });
}
let timeInterval = null; // 인터벌 전역 선언

// 출발 버튼
function startJourney() {
  if (!totalDistance || !totalDuration) {
    alert("경로를 먼저 찾으세요!");
    return;
  }
// 출발지-도착지 표시
  document.getElementById("placeNames").innerText = `${startAddress} → ${endAddress}`;

  document.getElementById("status").textContent = "출발 중...";
  eraseRoute();

  document.getElementById("start-btn").style.display = "none";
  const stopBtn = document.getElementById("stop-btn");
  stopBtn.style.display = "inline-block";
  stopBtn.textContent = "출발 중지";

  remainingDistance = totalDistance;
  remainingTime = totalDuration * 60;

  const distancePerSecond = totalDistance / (totalDuration * 60);

  timeInterval = setInterval(() => {
    if (remainingTime <= 0) {
      clearInterval(timeInterval);
      document.getElementById("routeInfoDetails").innerText = "목적지에 도착했습니다!";
    } else {
      remainingDistance -= distancePerSecond;
      remainingTime -= 1;

      const remainingInfo =
        `남은 거리: ${remainingDistance.toFixed(2)} km\n예상 도착 시간: ${Math.floor(remainingTime / 60)}분 ${remainingTime % 60}초`;
      document.getElementById("routeInfoDetails").innerText = remainingInfo;
    }
  }, 1000);
}
// 중지 버튼
function pauseJourney() {
  if (timeInterval) {
    clearInterval(timeInterval);
    timeInterval = null;
  }

  if (eraseInterval) {
    clearInterval(eraseInterval);
    eraseInterval = null;
  }

  document.getElementById("status").textContent = "출발 중지됨";

  document.getElementById("start-btn").style.display = "none";
  document.getElementById("stop-btn").style.display = "none";
  document.getElementById("resume-btn").style.display = "inline-block";
  document.getElementById("reset-btn").style.display = "inline-block";

  alert("경로 안내가 중지되었습니다.");
}
// 경로 선 지우기 (출발 시 사용)
function eraseRoute() {
  eraseCurrentIndex = 0;

  const linePath = polyline.getPath();
  const totalDurationInSeconds = totalDuration * 60;
  const intervalTime = totalDurationInSeconds * 1000 / linePath.length;

  eraseInterval = setInterval(() => {
    if (eraseCurrentIndex >= linePath.length) {
      clearInterval(eraseInterval);
      document.getElementById("status").textContent = "도착!";
    } else {
      linePath.shift();
      polyline.setPath(linePath);
      eraseCurrentIndex++;
    }
  }, intervalTime);
}

// 재시작 시 선 다시 지우기
function resumeEraseRoute() {
  if (eraseInterval) {
    clearInterval(eraseInterval);
    eraseInterval = null;
  }

  const linePath = polyline.getPath();
  if (linePath.length === 0) return;

  const remainingPathLength = linePath.length;
  const intervalTime = (remainingTime * 1000) / remainingPathLength;

  eraseInterval = setInterval(() => {
    if (linePath.length === 0) {
      clearInterval(eraseInterval);
      document.getElementById("status").textContent = "도착!";
    } else {
      linePath.shift();
      polyline.setPath(linePath);
    }
  }, intervalTime);
}

// 재시작
function resumeJourney() {
  if (!remainingDistance || !remainingTime) {
    alert("재시작할 수 있는 경로가 없습니다.");
    return;
  }

  document.getElementById("status").textContent = "경로 재시작 중...";

  document.getElementById("resume-btn").style.display = "none";
  document.getElementById("start-btn").style.display = "none";
  document.getElementById("stop-btn").style.display = "inline-block";

  // polyline이 지워졌다면 다시 그리기
  if (!polyline && startPlace && endPlace) {
    const linePath = [
      new kakao.maps.LatLng(startPlace.lat, startPlace.lng),
      new kakao.maps.LatLng(endPlace.lat, endPlace.lng)
    ];
    polyline = new kakao.maps.Polyline({
      path: linePath,
      strokeWeight: 5,
      strokeColor: '#FF0000',
      strokeOpacity: 0.7,
      strokeStyle: 'solid'
    });
    polyline.setMap(map);
  }

  eraseCurrentIndex = 0;
  resumeEraseRoute(); // setInterval 밖에서 한 번만 호출

  const distancePerSecond = totalDistance / (totalDuration * 60);

  timeInterval = setInterval(() => {
    if (remainingTime <= 0) {
      clearInterval(timeInterval);
      document.getElementById("routeInfoDetails").innerText = "목적지에 도착했습니다!";
    } else {
      remainingDistance -= distancePerSecond;
      remainingTime -= 1;

      const remainingInfo =
        `남은 거리: ${remainingDistance.toFixed(2)} km\n예상 도착 시간: ${Math.floor(remainingTime / 60)}분 ${remainingTime % 60}초`;
      document.getElementById("routeInfoDetails").innerText = remainingInfo;
    }
  }, 1000);
}

// 전체 리셋
function resetJourney() {
  if (startMarker) {
    startMarker.setMap(null);
    startMarker = null;
  }

  if (endMarker) {
    endMarker.setMap(null);
    endMarker = null;
  }

  if (polyline) {
    polyline.setMap(null);
    polyline = null;
  }

  if (timeInterval) {
    clearInterval(timeInterval);
    timeInterval = null;
  }

  if (eraseInterval) {
    clearInterval(eraseInterval);
    eraseInterval = null;
  }

  startPlace = null;
  endPlace = null;
  totalDistance = 0;
  totalDuration = 0;
  remainingDistance = null;
  remainingTime = null;
  eraseCurrentIndex = 0;

  document.getElementById("status").textContent = "";
  document.getElementById("routeInfo").innerText = "";
  document.getElementById("routeInfoDetails").innerText = "";

  document.getElementById("start-btn").style.display = "inline-block";
  document.getElementById("stop-btn").style.display = "none";
  document.getElementById("resume-btn").style.display = "none";
  document.getElementById("reset-btn").style.display = "none";

  alert("경로가 초기화되었습니다. 새로운 출발지와 도착지를 선택하세요!");
}

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


function selectRoute(route) {
  const geocoder = new kakao.maps.services.Geocoder();

  // 출발지 주소 → 좌표 변환
  geocoder.addressSearch(route.startAddress, function(startResult, status) {
    if (status === kakao.maps.services.Status.OK) {
      const startLatLng = {
        lat: startResult[0].y,
        lng: startResult[0].x
      };

      // 도착지 주소 → 좌표 변환
      geocoder.addressSearch(route.endAddress, function(endResult, status) {
        if (status === kakao.maps.services.Status.OK) {
          const endLatLng = {
            lat: endResult[0].y,
            lng: endResult[0].x
          };

          // 거리 단위 m → km, 시간 초 → 분
          const distanceKm = route.distance / 1000;
          const durationMin = Math.round(route.duration / 60);

          // 기존 함수 호출
          loadPreviousRoute(startLatLng, endLatLng, distanceKm, durationMin, route.startAddress, route.endAddress);

          map.setCenter(new kakao.maps.LatLng(startLatLng.lat, startLatLng.lng));

        } else {
          alert("도착지 주소를 좌표로 변환하지 못했습니다.");
        }
      });
    } else {
      alert("출발지 주소를 좌표로 변환하지 못했습니다.");
    }
  });
}

function loadPreviousRoutes() {
  fetch("/api/routes/previous")
    .then(response => response.json())
    .then(routes => {
      const list = document.getElementById("previousRoutesList");
      list.innerHTML = "";

      routes.forEach((route) => {
        const li = document.createElement("li");
        li.className = "route-item";

        const start = route.startAddress; // 문자열
        const end = route.endAddress;     // 문자열

        const text = document.createElement("span");
        text.textContent = `출발지: ${start} → 목적지: ${end}`;
        text.className = "route-text";

        text.addEventListener("click", () => {
          // selectRoute에 넘기는 것도 문자열로
          selectRoute({
            startAddress: start,
            endAddress: end,
            distance: route.distance,
            duration: route.duration
          });
        });

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
  const confirmDelete = confirm("정말 삭제하시겠습니까?");
  if (!confirmDelete) {
    return; // 취소 누르면 함수 종료
  }

  fetch(`/api/routes/${routeId}`, {
    method: "DELETE"
  })
    .then(response => {
      if (response.ok) {
        alert("경로 삭제 완료!");
        loadPreviousRoutes(); // 목록 새로고침
      } else {
        alert("삭제 실패");
      }
    });
}

function loadPreviousRoute(startLatLng, endLatLng, distanceKm, durationMin, startAddress, endAddress) {
  // 주소 문자열을 input에 넣어줌
  document.getElementById("start").value = startAddress;
  document.getElementById("end").value = endAddress;
}

