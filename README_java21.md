
# 냉동파우치 MES 시스템 (2025_03project)

이 프로젝트는 냉동식품 포장재 생산 과정을 디지털화하기 위한 MES(Manufacturing Execution System)입니다.  
원자재 수령부터 제품 생산, 시퀀스별 공정 처리, 창고 이동 및 배송 시뮬레이션까지  
전체 생산·출고 흐름을 웹 기반으로 통합 관리할 수 있습니다.

## 주요 기능
- 원자재 수령 및 공급사 연동: 납품처와 공급 가능한 원자재를 관리하고 수령된 자재 등록
- 제품 등록 및 공정 흐름 시각화: 수령된 원자재 기반 제품 등록 → 시퀀스별 공정 이동 (1차~4차)
- 창고 처리: 포장 완료된 제품을 자동으로 창고 테이블에 이동, 상태 관리
- 주문 등록 및 경로 설정: 창고 → 고객지점까지의 출고 경로 설정 및 주문 처리
- 배송 시뮬레이션: Kakao Map API 기반 실시간 경로 시뮬레이션, 마커 이동, 남은 거리/도착 시간 출력
- 상태 복원 기능: 페이지 이탈 후에도 localStorage 기반으로 남은 시간, 마커 위치, 진행률 자동 복원

## 기술 스택
- Backend: Java 21, Spring Boot, Spring Security, JPA, MariaDB
- Frontend: Thymeleaf, JavaScript (AJAX), Kakao Map API
- 기타 기술: LocalStorage, 페이징 처리, 검색 필터, DTO 구조

## 포함 파일
- 냉동파우치_MES_기능명세서_비고전체포함.xlsx  
  → 기능별 정리 및 비고 포함된 명세서 엑셀 파일
- project03/  
  → 전체 소스 코드 (Controller, Service, Entity 등 포함)

## 사용 목적
- MES/ERP 포트폴리오용
- 실제 냉동식품 제조공정 기반 비즈니스 로직 구현
- Spring Boot 백엔드 + Kakao API 연동 학습
