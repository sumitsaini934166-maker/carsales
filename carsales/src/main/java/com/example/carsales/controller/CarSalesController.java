package com.example.carsales.controller;

import com.example.carsales.commons.response.ApiResponse;
import com.example.carsales.dto.MonthlyCountDto;
import com.example.carsales.dto.UploadSalesResponse;
import com.example.carsales.dto.YearlyCountDto;
import com.example.carsales.service.CarSalesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/car-sales")
public class CarSalesController {


    private final CarSalesService carSalesService;

    public CarSalesController(CarSalesService carSalesService) {
        this.carSalesService = carSalesService;
    }


    @PostMapping("/upload-csv")
    public ResponseEntity<ApiResponse<UploadSalesResponse>> uploadFile(@RequestParam("file") MultipartFile file){

        //file is available or not
        if(file.isEmpty()){
            //res


  //           UploadSalesResponse response= new UploadSalesResponse(totalRecords:0,successCount:0,failedCount:0);

            UploadSalesResponse response= new UploadSalesResponse(0,0,0);


            ApiResponse<UploadSalesResponse> apiResponse= new ApiResponse<UploadSalesResponse>(
                    false,
                    "file is Emplty",
                    response,
                    HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity< ApiResponse<UploadSalesResponse>>( apiResponse,HttpStatus.BAD_REQUEST);
        }
        //res

        UploadSalesResponse response = carSalesService.uploadCsv(file);
        ApiResponse<UploadSalesResponse> apiResponse = getApiResponse(response);
        return ResponseEntity.ok(apiResponse);
    }

    private static ApiResponse<UploadSalesResponse> getApiResponse(UploadSalesResponse response){

        String message;
        boolean success;

        if(response.getFailedCount()==0){
            message="All records uploads successfully";
            success=true;
        }
        else if (response.getSuccessCount()==0) {
            message="All records fail to upload";
            success=false;

        }
        else{
            message="uploaded with some errors"+response.getFailedCount()+"row Failed";
            success=false;
        }

        return new ApiResponse<UploadSalesResponse>(success,message,response,HttpStatus.OK.value());
    }

    @GetMapping("/yearly-count")
    public ResponseEntity<ApiResponse<List<YearlyCountDto>>> yearlyCount(){
        List<YearlyCountDto>  carsCount = carSalesService.getYearlyCarsCount();
        ApiResponse<List<YearlyCountDto>> response = new ApiResponse<List<YearlyCountDto>>(
                true,
                "data read successfully",
                carsCount,
                HttpStatus.OK.value()
        );

        return ResponseEntity.ok(response);
    }


    @GetMapping("/monthly-count")
    public ResponseEntity<ApiResponse<List<MonthlyCountDto>>> monthlyCount(@RequestParam int year){


        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "monhtly data Read successfully",
                carSalesService.getMonthlyCountByYear(year),
                HttpStatus.OK.value()
        ));


    }

}
