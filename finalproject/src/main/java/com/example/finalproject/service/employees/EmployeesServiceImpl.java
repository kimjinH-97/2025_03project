package com.example.finalproject.service.employees;

import com.example.finalproject.domain.login.Employees;
import com.example.finalproject.dto.employees.EmployeesDTO;
import com.example.finalproject.dto.employees.EmployeesPageRequestDTO;
import com.example.finalproject.dto.employees.EmployeesPageResponseDTO;
import com.example.finalproject.repository.login.EmployeesRepository;
import org.modelmapper.ModelMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class EmployeesServiceImpl implements EmployeesService{
    /* ModelMapper 역할 : DB 에서 가져온 Entity 객체를 클라이언트에 전달할 DTO 객체로 변환,
                        클라이언트가 보낸 DTO를 처리할 Entity로 변환하는 작업. */
    private final ModelMapper modelMapper;
    private final EmployeesRepository employeesRepository;

//    @Autowired
//    public EmployeesServiceImpl(ModelMapper modelMapper) {
//        this.modelMapper = modelMapper;
//    }

    @Override
    public EmployeesPageResponseDTO<EmployeesDTO> list(EmployeesPageRequestDTO employeesPageRequestDTO){
        String[] types = employeesPageRequestDTO.getTypes();
        String keyword = employeesPageRequestDTO.getKeyword();
        Pageable pageable = employeesPageRequestDTO.getPageable("userId");

        Page<Employees> result = employeesRepository.searchAll(types, keyword, pageable);

        List<EmployeesDTO> dtoList = result.getContent().stream().map(employees -> modelMapper.map(
                employees, EmployeesDTO.class)).collect(Collectors.toList());

        return EmployeesPageResponseDTO.<EmployeesDTO>withAll()
                .employeesPageRequestDTO(employeesPageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();
    }
}
