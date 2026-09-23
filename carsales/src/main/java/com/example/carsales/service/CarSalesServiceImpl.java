package com.example.carsales.service;

import com.example.carsales.commons.response.entity.CarSales;
import com.example.carsales.dto.MonthlyCountDto;
import com.example.carsales.dto.UploadSalesResponse;
import com.example.carsales.dto.YearlyCountDto;
import com.example.carsales.repo.CarSalesRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;

import java.io.InputStreamReader;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class CarSalesServiceImpl implements CarSalesService{

    private final CarSalesRepository repository;

    public CarSalesServiceImpl(CarSalesRepository repository) {

        this.repository = repository;
    }


    @Override
    public UploadSalesResponse uploadCsv(MultipartFile file) {

        List<CarSales> cars = new ArrayList<CarSales>();


        int failCount=0;

        int totalRecords =0;

        try( BufferedReader reader =
                     new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))){

            //CSVFormate
            CSVFormat csvFormate = CSVFormat.DEFAULT.builder()
                    .setHeader() //Header
                    .setSkipHeaderRecord(true)//skip(Not Treated as Date)
                    .setIgnoreHeaderCase(true)//Case-insensitive
                    .setTrim(true)
                    .build();

            CSVParser csvParser = CSVParser.parse(reader, csvFormate);

            for(CSVRecord record:csvParser) {
                totalRecords++;

                try {

                    String carNumber = record.get("Car Number");
                    boolean exist = repository.existsByCarNumber(carNumber);

                    if (exist) {
                        failCount++;
                        System.out.println("Duplicate Data Found " + carNumber);
                        continue;

                    }

                    CarSales carSales = new CarSales();

                    carSales.setCarNumber(record.get("Car Number"));
                    carSales.setBrand(record.get("Brand"));
                    carSales.setModel(record.get("Model"));
                    carSales.setDateOfPurchase(LocalDate.parse(record.get("Date of Purchase")));
                    carSales.setTimeOfPurchase(LocalTime.parse(record.get("Time of Purchase")));
                    carSales.setColor(record.get("Color"));
                    carSales.setYear(Integer.parseInt(record.get("Year")));
                    carSales.setPrice(Long.parseLong(record.get("Price")));
                    carSales.setMileage(Double.parseDouble(record.get("Mileage")));   // double hai entity me
                    carSales.setEngineCC(Integer.parseInt(record.get("Engine CC")));
                    carSales.setFuelType(record.get("Fuel Type"));
                    carSales.setPayment(record.get("Payment"));
                    carSales.setState(record.get("State"));
                    carSales.setCity(record.get("City"));
                    carSales.setCustomer(record.get("Customer"));
                    carSales.setContactNo(record.get("Contact No"));
                    carSales.setEmail(record.get("Email"));
                    carSales.setWarrantyPeriod(Integer.parseInt(record.get("Warranty Period(years)")));

                    cars.add(carSales);

                } catch (Exception e) {
                     failCount++;
                    System.out.println("Fail to process Row :"+record.getRecordNumber());
                }

            }

            if(!cars.isEmpty()){
                repository.saveAll(cars);
            }

        } catch (Exception exception) {
            throw new RuntimeException("Unable to parse CSV"+exception.getMessage());
        }

       int successCount= totalRecords-failCount;

        return new UploadSalesResponse(totalRecords,successCount,failCount);
    }

    @Override
    public List<YearlyCountDto> getYearlyCarsCount() {

        return   repository.getYearlyCount();
    }

    @Override
    public List<MonthlyCountDto> getMonthlyCountByYear(int year) {

        List<MonthlyCountDto> data = repository.getMonthlyCountByYear(year);

        Map<Integer,Long> map = data.stream()
                .collect(Collectors.toMap(
                        MonthlyCountDto::month,
                        MonthlyCountDto::count
                ) ) ;
        List<MonthlyCountDto> result=new ArrayList<>();
        for(int i=1; i<=12;i++){
            result.add(new MonthlyCountDto(
                    i,
                    map.getOrDefault(i, 0L)
            ));
        }


        return result;
    }
}

