package com.highfive.meetu.domain.job.common.controller;

import com.highfive.meetu.domain.job.common.dto.LocationOptionDTO;
import com.highfive.meetu.domain.job.common.service.LocationService;
import com.highfive.meetu.global.common.response.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    /**
     * 1단계 지역 드롭다운 (도/특별시/광역시 등)
     */
//    @GetMapping("/provinces")
//    public ResultData<List<LocationOptionDTO>> getProvinces() {
//        return locationService.getProvinceDropdown();
//    }

}
