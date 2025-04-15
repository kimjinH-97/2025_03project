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

let movingMarker;
let moveIndex = 0;
let moveInterval;
let isJourneyStarted = false;
let isPaused = false;
function getEstimatedDuration() {
  return totalDuration;  // 전역 변수에서 경로의 이동 시간
}

function getEstimatedDistance() {
  return totalDistance;  // 전역 변수에서 경로의 거리
}

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
    if (!startCoords || !endCoords) {
      alert("좌표를 불러오지 못했습니다.");
      return;
    }

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

    totalDistance = data.routes[0].sections[0].distance / 1000; // km 단위
    totalDuration = data.routes[0].sections[0].duration / 60; // 분 단위

    // 출력 업데이트
    document.getElementById("routeInfo").innerHTML =
      `경로 정보:<br>거리: ${totalDistance.toFixed(2)} km<br>이동 시간: ${Math.floor(totalDuration)}분`;

    document.getElementById("placeNames").innerText = `${startAddress} → ${endAddress}`;
  } catch (error) {
    console.error("경로 API 오류:", error);
  }
}
function startMovingMarker() {
   const path = polyline.getPath();

   // 최초 마커 생성
   if (!movingMarker) {
     movingMarker = new kakao.maps.Marker({
       map: map,
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

     const position = path[moveIndex];
     movingMarker.setPosition(position);
     moveIndex++;
     if (someMarker) {
         someMarker.setPosition(new kakao.maps.LatLng(lat, lng));
     }
     // 현재 마커 위치를 객체({lat, lng})로 저장
     const pos = movingMarker.getPosition();
     localStorage.setItem("markerPosition", JSON.stringify({ lat: pos.getLat(), lng: pos.getLng() }));
   }, intervalTime);
 }


// 배송 시작 함수
async function startJourney() {
     const startAddress = document.getElementById("start").value;
        const endAddress = document.getElementById("end").value;

        // 지도 초기화 및 경로 그리기
        await initMap(startAddress, endAddress);  // 경로 새로 그리기

        const estimatedDuration = getEstimatedDuration();
        const estimatedDistance = getEstimatedDistance();

        if (!estimatedDuration || !estimatedDistance) {
            alert("경로를 찾을 수 없습니다.");
            return;
        }

        // 초기화
        isJourneyStarted = true;
        isPaused = false;
        remainingTime = estimatedDuration * 60; // 초 단위
        remainingDistance = estimatedDistance; // km 단위

        // 출발 상태
        document.getElementById("status").textContent = "배송 중...";

        // 버튼 상태
        document.getElementById("pauseButton").disabled = false;
        document.getElementById("pauseButton").innerText = "중지";

        // 마커 이동 시작
        startMovingMarker();

        // 타이머 시작
        startTimer();

        // 로컬 저장
        localStorage.setItem("deliveryStarted", "true");
        localStorage.setItem("startTime", Date.now().toString());
        localStorage.setItem("totalDuration", estimatedDuration);
        localStorage.setItem("totalDistance", estimatedDistance);

        // 타이머 (남은 거리 및 시간 업데이트)
        timeInterval = setInterval(() => {
            if (remainingTime <= 0) {
                clearInterval(timeInterval);
                document.getElementById("routeInfoDetails").innerText = "목적지에 도착했습니다!";
                document.getElementById("status").textContent = "도착 완료";

                // 마커 제거
                if (movingMarker) {
                    movingMarker.setMap(null);
                    movingMarker = null;
                }

                localStorage.clear();
            } else {
                const distancePerSecond = totalDistance / (totalDuration * 60);
                remainingDistance -= distancePerSecond;
                remainingTime -= 1;

                // 출력 정보 업데이트
                const remainingInfo =
                    `남은 거리: ${remainingDistance.toFixed(2)} km\n예상 도착 시간: ${Math.floor(remainingTime / 60)}분 ${remainingTime % 60}초`;
                document.getElementById("routeInfoDetails").innerText = remainingInfo;
            }
        }, 1000);
    }


// 마커 복원 함수
function restoreMarkerPosition() {
     const savedPositionData = localStorage.getItem("markerPosition");
      if (savedPositionData) {
        const savedPosition = JSON.parse(savedPositionData);
        const latLng = new kakao.maps.LatLng(savedPosition.lat, savedPosition.lng);

        if (!movingMarker) {
          movingMarker = new kakao.maps.Marker({
            map: map,
            position: latLng,
            image: new kakao.maps.MarkerImage(
              'https://cdn-icons-png.flaticon.com/512/684/684908.png',
              new kakao.maps.Size(32, 32),
              { offset: new kakao.maps.Point(16, 16) }
            )
          });
        } else {
          movingMarker.setPosition(latLng);
          movingMarker.setMap(map);
        }
      }
    }
// 초기화 함수
window.onload = function () {
    const start = document.getElementById('start').value;
    const end = document.getElementById('end').value;
    initMap(start, end);
};
// 배송 일시 중지 함수
function pauseJourney() {
     if (!isJourneyStarted) {
            alert("배송이 시작되지 않았습니다.");
            return;
        }

        if (isPaused) {
            alert("배송이 이미 일시 중지되었습니다.");
            return;
        }

        // 타이머와 마커 이동을 중지합니다.
        clearInterval(timeInterval);
        clearInterval(moveInterval);

        // 일시 중지 상태로 설정
        isPaused = true;

        // 상태 텍스트 업데이트
        document.getElementById("status").textContent = "일시 중지됨";
    }
function startTimer() {
     const distancePerSecond = totalDistance / (totalDuration * 60);

     timeInterval = setInterval(() => {
         if (remainingTime <= 0) {
             completeDelivery();
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
 }
// 배송 재개 함수
function resumeJourney() {
    if (!isJourneyStarted || !isPaused) {
        alert("일시 중지된 상태에서만 재개할 수 있습니다.");
        return;
    }

    isPaused = false;
    document.getElementById("status").textContent = "배송 중...";

    // 마커 이동 재개
    startMovingMarker();

    // 타이머 재개
    startTimer();
}
//취소
function cancelJourney() {
    if (!isJourneyStarted) {
        alert("배송이 시작되지 않았습니다.");
        return;
    }

    // 타이머 및 마커 이동 정지
    clearInterval(moveInterval);  // 이동 마커 관련 타이머
    clearInterval(timeInterval);  // 남은 시간 타이머

    // 마커 제거
    if (movingMarker) {
        movingMarker.setMap(null);
        movingMarker = null;
    }

    // 변수 초기화
    isJourneyStarted = false;
    isPaused = false;
    remainingTime = 0;
    remainingDistance = 0;

    // 로컬 스토리지 초기화
    localStorage.clear();

    // UI 상태 초기화
    document.getElementById("status").textContent = "배송 준비 중";
    document.getElementById("routeInfoDetails").innerText = "";  // 경로 정보 초기화
    document.getElementById("pauseButton").innerText = "중지";
    document.getElementById("pauseButton").disabled = true;

    // 배송출발 버튼 활성화
    const startButton = document.getElementById("startButton");
    startButton.disabled = false;  // 배송 출발 버튼 활성화
    startButton.innerText = "배송 시작"; // 버튼 텍스트 복원
}


function completeDelivery() {
    clearInterval(moveInterval);
    clearInterval(timeInterval);

    if (movingMarker) {
        movingMarker.setMap(null);
        movingMarker = null;
    }

    isJourneyStarted = false;
    isPaused = false;

    document.getElementById("status").textContent = "배송 완료";
    document.getElementById("routeInfoDetails").innerText = "목적지에 도착했습니다!";
    localStorage.clear();

    document.getElementById("pauseButton").disabled = true;
}

// 경로 지우기
function eraseRoute() {
    eraseCurrentIndex = 0;

    const originalPath = polyline.getPath();
    const linePath = originalPath.map(coord => new kakao.maps.LatLng(coord.getLat(), coord.getLng()));
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
    const startButton = document.getElementById("startButton");
    const pauseButton = document.getElementById("pauseButton");
    const cancelButton = document.getElementById("cancelButton");

    let timeInterval;  // 타이머 변수
    let isJourneyStarted = false;  // 여정 시작 여부를 체크하는 변수

     // 취소버튼
        cancelButton.addEventListener("click", function () {
            // DB 상태 변경 (AJAX를 이용해 DB 상태를 '준비중'으로 변경)
            fetch('/updateDeliveryStatus', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ status: '준비중' }),
            }).then(response => response.json())
              .then(data => {
                  console.log("배송 상태가 '준비중'으로 변경되었습니다.");
              })
              .catch(error => console.error("오류 발생:", error));


            // 다시 시작 버튼 활성화
            startButton.disabled = false;
            cancelButton.disabled = true;
            pauseButton.disabled = true;

            // 경로 정보 및 시간 초기화
            document.getElementById("routeInfoDetails").innerText = "";
            document.getElementById("status").textContent = "준비중";

            // 타이머 중지
            if (timeInterval) {
                clearInterval(timeInterval);
            }
        });
        //중지버튼클릭시
         pauseButton.addEventListener("click", function () {
             fetch('/updateDeliveryStatus', {
                 method: 'POST',
                 headers: {
                     'Content-Type': 'application/json',
                 },
                 body: JSON.stringify({ status: '일시중지' }),
             }).then(response => response.json())
               .then(data => {
                   console.log("배송 상태가 '일시중지'로 변경되었습니다.");
               })
               .catch(error => console.error("오류 발생:", error));

             // 타이머, 마커 이동 중지
             if (timeInterval) {
                 clearInterval(timeInterval);
                 timeInterval = null;
             }

             if (moveInterval) {
                 clearInterval(moveInterval);
                 moveInterval = null;
             }

             document.getElementById("status").textContent = "일시중지됨";
             startButton.disabled = false;
             cancelButton.disabled = false;

             // 버튼 텍스트 및 기능 변경: "중지" → "재시작"
             pauseButton.textContent = "재시작";
             pauseButton.id = "resumeButton";

             // 새로 바뀐 resumeButton에 이벤트 리스너 다시 등록
             const resumeButton = document.getElementById("resumeButton");

             resumeButton.addEventListener("click", function () {
                 fetch('/updateDeliveryStatus', {
                     method: 'POST',
                     headers: {
                         'Content-Type': 'application/json',
                     },
                     body: JSON.stringify({ status: '배송중' }),
                 }).then(response => response.json())
                   .then(data => {
                       console.log("배송 상태가 '배송중'으로 변경되었습니다.");
                   })
                   .catch(error => console.error("오류 발생:", error));

                 document.getElementById("status").textContent = "출발 중...";

                 // 다시 마커 이동 시작
                 startMovingMarker();

                 // 타이머 다시 시작
                 timeInterval = setInterval(() => {
                     if (remainingTime <= 0) {
                         clearInterval(timeInterval);
                         clearInterval(moveInterval);

                         document.getElementById("routeInfoDetails").innerText = "목적지에 도착했습니다!";
                         document.getElementById("status").textContent = "도착 완료";

                         if (movingMarker) {
                             movingMarker.setMap(null);
                             movingMarker = null;
                         }

                         startButton.disabled = false;
                         pauseButton.disabled = true;
                         cancelButton.disabled = true;
                         localStorage.clear();
                         return;
                     }

                     remainingDistance -= totalDistance / (totalDuration * 60);
                     remainingTime -= 1;

                     document.getElementById("routeInfoDetails").innerText =
                         `남은 거리: ${remainingDistance.toFixed(2)} km\n예상 도착 시간: ${Math.floor(remainingTime / 60)}분 ${remainingTime % 60}초`;
                 }, 1000);

                 // 버튼 다시 "중지"로 되돌림
                 resumeButton.textContent = "중지";
                 resumeButton.id = "pauseButton";
             });
         });
    // 배송 시작 버튼 클릭 시 startJourney 호출
    if (startButton) {
        startButton.addEventListener("click", function () {
            if (isJourneyStarted) return;  // 이미 배송이 시작된 경우 다시 시작하지 않음

             const startAddress = document.getElementById("start").value;
                const endAddress = document.getElementById("end").value;

            initMap(startAddress, endAddress).then(() => {
                const deliveryStarted = localStorage.getItem("deliveryStarted");
                const startTime = localStorage.getItem("startTime");

                if (deliveryStarted === "true" && startTime) {
                    const elapsedSeconds = Math.floor((Date.now() - Number(startTime)) / 1000);

                    totalDuration = Number(localStorage.getItem("totalDuration"));
                    totalDistance = Number(localStorage.getItem("totalDistance"));

                    remainingTime = totalDuration * 60 - elapsedSeconds;
                    remainingDistance = totalDistance - (totalDistance * elapsedSeconds / (totalDuration * 60));

                    if (remainingTime > 0) {
                        isJourneyStarted = true;
                        document.getElementById("status").textContent = "출발 중...";

                        // 남은 거리와 시간 출력
                        document.getElementById("routeInfoDetails").innerText =
                            `남은 거리: ${remainingDistance.toFixed(2)} km\n예상 도착 시간: ${Math.floor(remainingTime / 60)}분 ${remainingTime % 60}초`;

                        // 마커 복구
                        const savedPosition = JSON.parse(localStorage.getItem("markerPosition"));
                        if (savedPosition) {
                            const latLng = new kakao.maps.LatLng(savedPosition.La, savedPosition.Ma);
                            if (!movingMarker) {
                                movingMarker = new kakao.maps.Marker({
                                    map,
                                    position: latLng,
                                    image: new kakao.maps.MarkerImage(
                                        'https://cdn-icons-png.flaticon.com/512/684/684908.png',
                                        new kakao.maps.Size(32, 32),
                                        { offset: new kakao.maps.Point(16, 16) }
                                    )
                                });
                            } else {
                                movingMarker.setPosition(latLng);
                                movingMarker.setMap(map);
                            }
                        }

                        // 마커 이동 시작
                        startMovingMarker();

                        // 텍스트 업데이트 타이머
                        timeInterval = setInterval(() => {
                            if (remainingTime <= 0) {
                                clearInterval(timeInterval); // 타이머 멈추기
                                clearInterval(moveInterval); // 마커 이동 멈추기

                                document.getElementById("routeInfoDetails").innerText = "목적지에 도착했습니다!";
                                document.getElementById("status").textContent = "도착 완료";

                                // 마커 제거
                                if (movingMarker) {
                                    movingMarker.setMap(null);
                                    movingMarker = null;
                                }

                                startButton.disabled = false;
                                pauseButton.disabled = true;
                                cancelButton.disabled = true;

                                localStorage.clear();
                                return;
                            }

                            remainingDistance -= totalDistance / (totalDuration * 60);
                            remainingTime -= 1;

                            document.getElementById("routeInfoDetails").innerText =
                                `남은 거리: ${remainingDistance.toFixed(2)} km\n예상 도착 시간: ${Math.floor(remainingTime / 60)}분 ${remainingTime % 60}초`;
                        }, 1000);
                    } else {
                        document.getElementById("routeInfoDetails").innerText = "목적지에 도착했습니다!";
                        document.getElementById("status").textContent = "도착 완료";

                        // 마커 제거
                        if (movingMarker) {
                            movingMarker.setMap(null);
                            movingMarker = null;
                        }

                        startButton.disabled = false;
                        pauseButton.disabled = true;
                        cancelButton.disabled = true;

                        localStorage.clear();
                    }
                }
            });
        });
    } else {
        console.error('startButton 요소를 찾을 수 없습니다.');
    }
});
// 업데이트
function updateOrderStatus(orderId, status) {
    const url = `http://localhost:8084/orders/update-status/${orderId}?status=${encodeURIComponent(status)}`;

    fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        }
    })
    .then(response => {
        if (!response.ok) throw new Error("서버 오류!");
        return response.json();
    })
    .then(data => {
        if (data.success) {
            alert("상태가 성공적으로 업데이트되었습니다.");
            document.getElementById('State').textContent = status;
        } else {
            alert("상태 변경에 실패했습니다.");
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert("서버와 통신 중 문제가 발생했습니다.");
    });
}