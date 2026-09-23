package com.example.carsales.service;

import com.example.carsales.dto.MonthlyCountDto;
import com.example.carsales.dto.UploadSalesResponse;
import com.example.carsales.dto.YearlyCountDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CarSalesService {

    UploadSalesResponse uploadCsv(MultipartFile file);

    List<YearlyCountDto> getYearlyCarsCount();

    List<MonthlyCountDto> getMonthlyCountByYear(int year);

}
