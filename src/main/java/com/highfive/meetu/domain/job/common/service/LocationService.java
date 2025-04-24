package com.highfive.meetu.domain.job.common.service;

import com.highfive.meetu.domain.job.common.dto.LocationOptionDTO;
import com.highfive.meetu.domain.job.common.repository.LocationRepository;
import com.highfive.meetu.global.common.response.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

//    public ResultData<List<LocationOptionDTO>> getProvinceDropdown() {
//        List<LocationOptionDTO> rawList = locationRepository.findProvincesForDropdown();
//
//        // 사용자 친화적 표기 변경
//        List<LocationOptionDTO> refined = rawList.stream()
//                .map(item -> new LocationOptionDTO(
//                        item.getId(),
//                        convertProvinceName(item.getLabel())
//                ))
//                .toList();
//
//        return ResultData.success(refined.size(), refined);
//    }

    private String convertProvinceName(String original) {
        return switch (original) {
            case "제주특별자치도" -> "제주도";
            case "세종특별자치시" -> "세종시";
            default -> original;
        };
    }

}
