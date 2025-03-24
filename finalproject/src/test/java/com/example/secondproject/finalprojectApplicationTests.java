package com.example.secondproject;

import com.example.secondproject.naver.NaverAPIClient;
import com.example.secondproject.naver.dto.SearchImageRequestDto;
import com.example.secondproject.naver.dto.SearchImageResponseDto;
import com.example.secondproject.naver.dto.SearchRegionRequestDto;
import com.example.secondproject.naver.dto.SearchRegionResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class finalprojectApplicationTests {

	@Autowired
	private NaverAPIClient naverAPIClient;

	@Test
	void contextLoads() {
	}

	@Test
	@DisplayName("네이버 지역검색 OpenAPI 테스트")
	void naverSearchRegionAPITest() {
		SearchRegionRequestDto searchRegionRequestDto = new SearchRegionRequestDto();
		searchRegionRequestDto.setQuery("커피");

		SearchRegionResponseDto result = naverAPIClient.searchRegion(searchRegionRequestDto);

		System.out.println("지역검색 응답값: " + result);
	}

	@Test
	@DisplayName("네이버 이미지검색 openapi 테스트")
	void naverSearchImageAPITest(){
		SearchImageRequestDto searchImageRequestDto = new SearchImageRequestDto();
		searchImageRequestDto.setQuery("커피");

		SearchImageResponseDto result = naverAPIClient.searchImage(searchImageRequestDto);
		System.out.println("이미지 응답값: " + result);
	}

}
