
const MapApp = (function() {
  let map;
  const markers = [];
  let infoWindow = null;
  let startPlace = null;
  let endPlace = null;
  let startMarker = null;
  let endMarker = null;
  let currentPolyline = null;

  // 지도 초기화
  function initMap() {
    const mapContainer = document.getElementById('map');
    const mapOption = {
      center: new kakao.maps.LatLng(37.5665, 126.9780), // 서울 시청 좌표
      level: 5
    };

    map = new kakao.maps.Map(mapContainer, mapOption);
    infoWindow = new kakao.maps.InfoWindow({ zIndex: 1 });
    console.log("지도 초기화 완료");
  }

  // 장소 검색 함수
  async function searchPlaces(query) {
    if (!query.trim()) {
      alert('검색어를 입력해주세요');
      return;
    }

    try {
      showLoading(true);
      const response = await fetch(`/search?query=${encodeURIComponent(query)}`);

      if (!response.ok) {
        throw new Error(`서버 응답 오류: ${response.status}`);
      }

      const places = await response.json();

      if (!places || !places.length) {
        alert('검색 결과가 없습니다');
        return;
      }

      processPlaces(places);
    } catch (error) {
      console.error("검색 오류:", error);
      alert('검색 중 오류가 발생했습니다');
    } finally {
      showLoading(false);
    }
  }

  // 검색 결과 처리
  function processPlaces(places) {
    console.log("검색된 장소:", places);
    createMarkers(places);
    displayPlacesList(places);

    if (places.length > 0) {
      const firstPlace = places[0];
      moveToPlace(firstPlace.latitude, firstPlace.longitude);
      showPlaceInfo(firstPlace);
    }
  }

  // 검색 결과 목록 표시
  function displayPlacesList(places) {
    const placesList = document.getElementById("places-list");
    placesList.innerHTML = "";

    places.forEach(place => {
      const li = document.createElement("li");
      li.classList.add("place-item");
      li.dataset.lat = place.latitude;
      li.dataset.lng = place.longitude;
      li.dataset.name = place.placeName;
      li.dataset.address = place.address;
      li.dataset.roadAddress = place.roadAddress;

      // 출발지/목적지 표시
      let placeType = '';
      if (startPlace && startPlace.address === place.address) {
        placeType = ' (출발지입니다)';
      } else if (endPlace && endPlace.address === place.address) {
        placeType = ' (목적지입니다)';
      }

      li.innerHTML = `
        <h4>${place.placeName}${placeType}</h4>
        <p>${place.address || '주소 정보 없음'}</p>
      `;

      li.addEventListener('click', () => selectPlace(li));
      placesList.appendChild(li);
    });
  }

  // 마커 생성
  function createMarkers(places) {
    clearMarkers();

    places.forEach(place => {
      const markerPosition = new kakao.maps.LatLng(place.latitude, place.longitude);
      console.log("마커 위치:", markerPosition);

      const marker = new kakao.maps.Marker({
        position: markerPosition,
        map: map
      });

      // 출발지/목적지 마커 스타일 변경
      if (startPlace && startPlace.address === place.address) {
        marker.setImage(getMarkerImage('start'));
      } else if (endPlace && endPlace.address === place.address) {
        marker.setImage(getMarkerImage('end'));
      }

      // 마커 클릭 이벤트
      kakao.maps.event.addListener(marker, 'click', () => {
        moveToPlace(place.latitude, place.longitude);
        showPlaceInfo(place);
        highlightPlaceItem(place);
      });

      markers.push(marker);
    });
  }

  // 마커 이미지 반환
  function getMarkerImage(type) {
    const color = type === 'start' ? 'red' : 'blue';
    const imageSrc = `https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_${color}.png`;
    return new kakao.maps.MarkerImage(imageSrc, new kakao.maps.Size(24, 35));
  }

  // 마커 초기화
  function clearMarkers() {
    markers.forEach(marker => marker.setMap(null));
    markers.length = 0;
  }

  // 장소 선택 처리
  function selectPlace(element) {
    const lat = parseFloat(element.dataset.lat);
    const lng = parseFloat(element.dataset.lng);

    moveToPlace(lat, lng);

    const selectedPlace = {
      latitude: lat,
      longitude: lng,
      placeName: element.dataset.name,
      address: element.dataset.address,
      roadAddress: element.dataset.roadAddress
    };

    showPlaceInfo(selectedPlace);
    highlightPlaceItem(selectedPlace);
  }

  // 지도 이동
  function moveToPlace(lat, lng) {
    const moveLatLng = new kakao.maps.LatLng(lat, lng);
    map.panTo(moveLatLng);

    // 지도 표시 문제 해결을 위해 리사이즈
    setTimeout(() => map.relayout(), 100);
  }

  // 장소 정보 표시
  function showPlaceInfo(place) {
    document.getElementById("info_name").textContent = place.placeName || "장소 이름 없음";
    document.getElementById("info_address").textContent = place.address || "주소 정보 없음";
    document.getElementById("info_road_address").textContent = place.roadAddress || "도로명 주소 없음";
    document.getElementById("place_info").style.display = "block";

    // 인포윈도우에 정보 표시
    const content = `
      <div style="padding:10px;">
        <h4>${place.placeName || "장소 이름 없음"}</h4>
        <p>${place.address || "주소 정보 없음"}</p>
      </div>
    `;

    infoWindow.setContent(content);
    infoWindow.setPosition(new kakao.maps.LatLng(place.latitude, place.longitude));
    infoWindow.open(map);
  }

  // 장소 정보 창 닫기
  function closePlaceInfo() {
    document.getElementById("place_info").style.display = "none";
    infoWindow.close();
  }

  // 장소 항목 강조 표시
  function highlightPlaceItem(place) {
    document.querySelectorAll('.place-item').forEach(item => {
      item.classList.remove('active');

      const itemLat = parseFloat(item.dataset.lat);
      const itemLng = parseFloat(item.dataset.lng);

      if (itemLat === place.latitude && itemLng === place.longitude) {
        item.classList.add('active');
      }
    });
  }

  // 출발지/목적지 설정 (공통 함수)
  async function setPlace(type) {
    const placeName = document.getElementById("info_name").textContent;
    const placeAddress = document.getElementById("info_address").textContent;

    if (!placeAddress) {
      alert('유효한 주소가 없습니다');
      return;
    }

    try {
      showLoading(true);
      const latLng = await geocodeAddress(placeAddress);

      const placeObj = {
        placeName,
        address: placeAddress,
        latitude: latLng.getLat(),
        longitude: latLng.getLng()
      };

      if (type === 'start') {
        startPlace = placeObj;
        document.getElementById("start").value = placeAddress;
        updateMarker('start', latLng);
      } else {
        endPlace = placeObj;
        document.getElementById("end").value = placeAddress;
        updateMarker('end', latLng);
      }

      // 기존 검색 결과 갱신
      const query = document.getElementById("query").value;
      if (query) searchPlaces(query);

    } catch (error) {
      console.error(`${type} 설정 오류:`, error);
      alert('주소 변환에 실패했습니다');
    } finally {
      showLoading(false);
    }
  }

  // 마커 업데이트
  function updateMarker(type, latLng) {
    // 기존 마커 제거
    if (type === 'start' && startMarker) {
      startMarker.setMap(null);
    } else if (type === 'end' && endMarker) {
      endMarker.setMap(null);
    }

    // 새 마커 생성
    const marker = new kakao.maps.Marker({
      position: latLng,
      map: map,
      image: getMarkerImage(type)
    });

    if (type === 'start') {
      startMarker = marker;
    } else {
      endMarker = marker;
    }
  }

  // 주소를 좌표로 변환
  function geocodeAddress(address) {
    return new Promise((resolve, reject) => {
      const geocoder = new kakao.maps.services.Geocoder();
      geocoder.addressSearch(address, (result, status) => {
        if (status === kakao.maps.services.Status.OK) {
          resolve(new kakao.maps.LatLng(result[0].y, result[0].x));
        } else {
          reject(new Error("주소 변환 실패"));
        }
      });
    });
  }

  // 경로 찾기
  async function findRoute() {
    const startAddress = document.getElementById("start").value;
    const endAddress = document.getElementById("end").value;

    if (!startAddress || !endAddress) {
      alert("출발지와 목적지를 입력하세요!");
      return;
    }

    try {
      showLoading(true);

      // 좌표 변환 병렬 처리
      const [startLatLng, endLatLng] = await Promise.all([
        geocodeAddress(startAddress),
        geocodeAddress(endAddress)
      ]);

      // 경로 정보 가져오기
      const routeInfo = await fetchRoute(startLatLng, endLatLng);

      // 경로 표시
      displayRouteInfo(routeInfo);
      drawRouteOnMap(routeInfo);

    } catch (error) {
      console.error('경로 찾기 오류:', error);
      alert('경로를 찾을 수 없습니다');
    } finally {
      showLoading(false);
    }
  }

  // 경로 정보 가져오기
  async function fetchRoute(start, end) {
    const REST_API_KEY = 'ccc93a4ef4efcbf5df114841f4c5b32b';
    const url = `https://apis-navi.kakaomobility.com/v1/directions?origin=${start.getLng()},${start.getLat()}&destination=${end.getLng()},${end.getLat()}`;

    const response = await fetch(url, {
      headers: {
        Authorization: `KakaoAK ${REST_API_KEY}`,
        'Content-Type': 'application/json'
      }
    });

    if (!response.ok) {
      throw new Error(`API 요청 실패: ${response.status}`);
    }

    return await response.json();
  }

  // 지도에 경로 표시
  function drawRouteOnMap(routeData) {
    if (!routeData.routes?.length) return;

    // 기존 폴리라인 제거
    if (currentPolyline) {
      currentPolyline.setMap(null);
    }

    const linePath = [];
    routeData.routes[0].sections[0].roads.forEach(road => {
      road.vertexes.forEach((vertex, index) => {
        if (index % 2 === 0) {
          linePath.push(new kakao.maps.LatLng(road.vertexes[index + 1], road.vertexes[index]));
        }
      });
    });

    // 새 폴리라인 생성
    currentPolyline = new kakao.maps.Polyline({
      path: linePath,
      strokeWeight: 5,
      strokeColor: '#FF0000',
      strokeOpacity: 0.8,
      strokeStyle: 'solid',
      map: map
    });

    // 경로가 포함되도록 지도 범위 조정
    const bounds = new kakao.maps.LatLngBounds();
    linePath.forEach(path => bounds.extend(path));
    map.setBounds(bounds);
  }

  // 경로 정보 표시
  function displayRouteInfo(routeData) {
    if (!routeData.routes?.length) return;

    const route = routeData.routes[0];
    const summary = route.summary;

    const distanceKm = (summary.distance / 1000).toFixed(1);
    const durationMin = Math.ceil(summary.duration / 60);

    const routeInfoText = `
      출발지: ${document.getElementById("start").value}
      목적지: ${document.getElementById("end").value}

      총 거리: ${distanceKm} km
      예상 시간: ${durationMin} 분
    `;

    document.getElementById("routeInfo").value = routeInfoText;
  }

 async function goToOrderPage() {
     const start = document.getElementById("start").value;
     const end = document.getElementById("end").value;

     if (!start || !end) {
         alert('출발지와 목적지를 설정해주세요');
         return;
     }

     try {
         showLoading(true);

         const orderData = {
             startPlace: start,
             endPlace: end,

             companyName: "내 회사", // 기본값 또는 실제 값
             orderQuantity: 1, // 기본값 또는 실제 값
             orderStatus: "대기중"
         };

         const response = await fetch('/api/orders', {
             method: 'POST',
             headers: {
                 'Content-Type': 'application/json',
             },
             body: JSON.stringify(orderData)
         });

         if (!response.ok) {
             throw new Error(`주문 저장 실패: ${response.status}`);
         }

         const order = await response.json();
         window.location.href = `/orders?orderId=${order.order_id}`;

     } catch (error) {
         console.error('주문 처리 오류:', error);
         alert('주문 처리 중 오류가 발생했습니다: ' + error.message);
     } finally {
         showLoading(false);
     }
 }
  // 로딩 상태 표시
  function showLoading(show) {
    const loadingElement = document.getElementById('loading') || createLoadingElement();
    loadingElement.style.display = show ? 'block' : 'none';
  }

  function createLoadingElement() {
    const loadingDiv = document.createElement('div');
    loadingDiv.id = 'loading';
    loadingDiv.style.cssText = `
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: rgba(0,0,0,0.5);
      display: none;
      justify-content: center;
      align-items: center;
      z-index: 1000;
    `;
    loadingDiv.innerHTML = '<div style="color: white; font-size: 24px;">로딩 중...</div>';
    document.body.appendChild(loadingDiv);
    return loadingDiv;
  }

  // Public API
  return {
    initMap,
    searchPlaces,
    setStartPlace: () => setPlace('start'),
    setEndPlace: () => setPlace('end'),
    findRoute,
    closePlaceInfo,
    goToOrderPage,
    selectPlace
  };
})();

// DOM 이벤트 리스너 설정
document.addEventListener('DOMContentLoaded', () => {
  kakao.maps.load(MapApp.initMap);

  document.getElementById("searchForm").addEventListener('submit', function(event) {
    event.preventDefault();
    const query = document.getElementById("query").value;
    MapApp.searchPlaces(query);
  });

  document.getElementById("findRouteBtn").addEventListener('click', MapApp.findRoute);
});

// 전역 함수들 (HTML에서 직접 호출되는 함수들)
window.setStartPlace = MapApp.setStartPlace;
window.setEndPlace = MapApp.setEndPlace;
window.closePlaceInfo = MapApp.closePlaceInfo;
window.goToOrderPage = MapApp.goToOrderPage;
window.selectPlace = MapApp.selectPlace;

