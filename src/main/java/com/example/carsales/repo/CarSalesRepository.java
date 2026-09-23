package com.example.carsales.repo;

import com.example.carsales.commons.response.entity.CarSales;
import com.example.carsales.dto.MonthlyCountDto;
import com.example.carsales.dto.YearlyCountDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarSalesRepository extends JpaRepository<CarSales, Long>{

    boolean existsByCarNumber(String carNumber);

    @Query("""
            select new com.example.carsales.dto.YearlyCountDto(c.year,count(c))
            from CarSales c
            Group by c.year
            Order by c.year
            """)

    List<YearlyCountDto> getYearlyCount();


    @Query("""
        SELECT new com.example.carsales.dto.MonthlyCountDto(
                MONTH(c.dateOfPurchase),COUNT(c)
        )
        
        FROM CarSales c
        WHERE YEAR(c.dateOfPurchase)=:year
        GROUP BY MONTH(c.dateOfPurchase)
        ORDER BY MONTH(c.dateOfPurchase)
                
                
""")

    List<MonthlyCountDto> getMonthlyCountByYear(@Param("year") int year);



}
