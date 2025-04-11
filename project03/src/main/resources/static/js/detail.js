let map;
let startMarker = null;
let endMarker = null;
let polyline = null;

let totalDistance = 0;
let totalDuration = 0;
let remainingDistance = 0;
let remainingTime = 0;

let eraseCurrentIndex = 0;
let eraseInterval;
let timeInterval;

let movingMarker;  // 전역에서 사용
let moveIndex = 0;
let moveInterval;

async function initMap(startAddress, endAddress) {
  const mapContainer = document.getElementById('map');
  const mapOption = {
    center: new kakao.maps.LatLng(37.5665, 126.9780),
    level: 5
  };

  map = new kakao.maps.Map(mapContainer, mapOption);

  try {
    const startCoords = await geocodeAddress(startAddress);
    const endCoords = await geocodeAddress(endAddress);

    startMarker = new kakao.maps.Marker({
      map,
      position: startCoords,
      title: '출발지',
      image: new kakao.maps.MarkerImage(
        'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png',
        new kakao.maps.Size(24, 35)
      )
    });

    endMarker = new kakao.maps.Marker({
      map,
      position: endCoords,
      title: '도착지',
      image: new kakao.maps.MarkerImage(
        'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_red.png',
        new kakao.maps.Size(24, 35)
      )
    });

    const bounds = new kakao.maps.LatLngBounds();
    bounds.extend(startCoords);
    bounds.extend(endCoords);
    map.setBounds(bounds);

    await drawRouteLine(startCoords, endCoords, startAddress, endAddress);

  } catch (err) {
    alert("주소 변환 또는 경로 정보를 불러오는 중 오류가 발생했습니다.");
    console.error(err);
  }
}

function geocodeAddress(address) {
  return new Promise((resolve, reject) => {
    const geocoder = new kakao.maps.services.Geocoder();
    geocoder.addressSearch(address, (result, status) => {
      if (status === kakao.maps.services.Status.OK) {
        resolve(new kakao.maps.LatLng(result[0].y, result[0].x));
      } else {
        reject("주소 변환 실패: " + address);
      }
    });
  });
}

async function drawRouteLine(startLatLng, endLatLng, startAddress, endAddress) {
  const REST_API_KEY = 'ccc93a4ef4efcbf5df114841f4c5b32b';
  const url = `https://apis-navi.kakaomobility.com/v1/directions?origin=${startLatLng.getLng()},${startLatLng.getLat()}&destination=${endLatLng.getLng()},${endLatLng.getLat()}`;

  try {
    const response = await fetch(url, {
      method: 'GET',
      headers: {
        Authorization: `KakaoAK ${REST_API_KEY}`,
        'Content-Type': 'application/json'
      }
    });

    const data = await response.json();
    const linePath = [];
    const roads = data.routes[0].sections[0].roads;

    roads.forEach(road => {
      const vertexes = road.vertexes;
      for (let i = 0; i < vertexes.length; i += 2) {
        linePath.push(new kakao.maps.LatLng(vertexes[i + 1], vertexes[i]));
      }
    });

    if (polyline) polyline.setMap(null);

    polyline = new kakao.maps.Polyline({
      path: linePath,
      strokeWeight: 5,
      strokeColor: '#00BFFF', // 하늘색
      strokeOpacity: 0.8,
      strokeStyle: 'solid'
    });
    polyline.setMap(map);

    totalDistance = data.routes[0].sections[0].distance / 1000;
    totalDuration = data.routes[0].sections[0].duration / 60;

    document.getElementById("routeInfo").innerHTML =
      `경로 정보:<br>거리: ${totalDistance.toFixed(2)} km<br>이동 시간: ${Math.floor(totalDuration)}분`;

    document.getElementById("placeNames").innerText = `${startAddress} → ${endAddress}`;

    startJourney();

  } catch (error) {
    console.error("경로 API 오류:", error);
  }
}
function startMovingMarker() {
  const path = polyline.getPath();
  moveIndex = 0;

  // 최초 마커 생성
  if (!movingMarker) {
    movingMarker = new kakao.maps.Marker({
      map,
      position: path[0],
      image: new kakao.maps.MarkerImage(
        'https://cdn-icons-png.flaticon.com/512/684/684908.png',
        new kakao.maps.Size(32, 32),
        { offset: new kakao.maps.Point(16, 16) }
      )
    });
  } else {
    movingMarker.setPosition(path[0]);
    movingMarker.setMap(map);
  }

  const intervalTime = (totalDuration * 60 * 1000) / path.length;

  moveInterval = setInterval(() => {
    if (moveIndex >= path.length) {
      clearInterval(moveInterval);
      document.getElementById("status").textContent = "도착!";
      return;
    }

    movingMarker.setPosition(path[moveIndex]);
    moveIndex++;
  }, intervalTime);
}

function startJourney() {
  document.getElementById("status").textContent = "출발 중...";

  remainingDistance = totalDistance;
  remainingTime = totalDuration * 60;
  const distancePerSecond = totalDistance / (totalDuration * 60);

  // 기존 텍스트 갱신 타이머
  timeInterval = setInterval(() => {
    if (remainingTime <= 0) {
      clearInterval(timeInterval);
      clearInterval(moveInterval);
      document.getElementById("routeInfoDetails").innerText = "목적지에 도착했습니다!";
      return;
    }

    remainingDistance -= distancePerSecond;
    remainingTime -= 1;

    document.getElementById("routeInfoDetails").innerText =
      `남은 거리: ${remainingDistance.toFixed(2)} km\n예상 도착 시간: ${Math.floor(remainingTime / 60)}분 ${remainingTime % 60}초`;
  }, 1000);

  // 움직이는 마커 시작
  startMovingMarker();
}

function eraseRoute() {
  eraseCurrentIndex = 0;

  const originalPath = polyline.getPath();
  const linePath = originalPath.map(coord => new kakao.maps.LatLng(coord.getLat(), coord.getLng())); // 깊은 복사
  const totalDurationInSeconds = totalDuration * 60;
  const intervalTime = totalDurationInSeconds * 1000 / linePath.length;

  eraseInterval = setInterval(() => {
    if (eraseCurrentIndex >= linePath.length) {
      clearInterval(eraseInterval);
      document.getElementById("status").textContent = "도착!";
    } else {
      linePath.shift(); // 복사본 수정
      polyline.setPath(linePath); // 새 경로 반영
      eraseCurrentIndex++;
    }
  }, intervalTime);
}


document.addEventListener('DOMContentLoaded', function () {
  const startAddress = document.getElementById("start").value;
  const endAddress = document.getElementById("end").value;
  initMap(startAddress, endAddress);
});
