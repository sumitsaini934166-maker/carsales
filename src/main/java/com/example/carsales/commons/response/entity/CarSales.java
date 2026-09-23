package com.example.carsales.commons.response.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "car_sales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarSales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "car_number", unique = true)
    private String carNumber;

    //@Column(nullable = false, length = 50)
    private String brand;

   // @Column(nullable = false, length = 50)
    private String model;

    @Column(name="dateOfPurchase")
    private LocalDate dateOfPurchase;

    @Column(name="timeOfPurchase")
    private LocalTime timeOfPurchase;

    @Column(length = 30)
    private String color;
    private int year;
    private long price;
    private double mileage;
    private int engineCC;

     @Column(length = 20)
    private String fuelType;


   @Column(name="payment_mode")
    private String payment;

    @Column(length = 50)
    private String state;

    @Column(length = 50)
    private String city;

    @Column(length = 100)
    private String customer;


    @Column(length = 10,name="contact_number")
    private String contactNo;

    @Column(length = 100)
    private String email;

    @Column(length = 10,name="warranty_period")
    private int warrantyPeriod;
}
