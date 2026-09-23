package com.example.carsales.service;

import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AIQueryServiceImpl implements AIQueryService{


    private final ChatClient chatClient;
    private final JdbcTemplate jdbcTemplate;


    public AIQueryServiceImpl(ChatClient.Builder builder,JdbcTemplate jdbcTemplate) {

        this.chatClient = builder.build();
        this.jdbcTemplate=jdbcTemplate;
    }


    @Override
    public String process(String question) {
        String sql = generateSQL(question);
        System.out.println(sql);

        if(sql.equalsIgnoreCase("INVALID")){
            return "only table-related questions allowed";

        }

        if(!isSafe(sql)){
            return "Unsafe query";
        }

        try{
            List<Map<String , Object>> result = jdbcTemplate.queryForList(sql);
            if(result.isEmpty()){
                return "no data found";
            }
            System.out.println(result);


            //Convert Result->HFM

            return toNaturalLanguage(question,result);


        } catch (Exception e) {
            return "query failed";
        }


    }

    private String toNaturalLanguage(String question, List<Map<String, Object>> result) {

        String prompt= """
                Convert database result into a human readable answer.
                
                User Question:
                """ + question + """
               
               DB Result:
               """+result.toString()+ """
               
               Rules:
               - Answer clearty (don't write too much)
               - Do not show JSON
               - Do not explain SQL
               """;
        
        
        return  chatClient.prompt()
                .user(prompt)
                .call()
                .content()
                .trim();
    }


    //validation

    private boolean isSafe(String sql) {
        String lower=sql.toLowerCase();
        return lower.startsWith("select")
                && !lower.contains("drop")
                && !lower.contains("delete")
                && !lower.contains("update")
                && !lower.contains("insert");


    }

    private String generateSQL(String question){

        String prompt= """
                you are sql generate.
                
                Table:car_sales
                Columns:Car Number,Brand,Model,Date of Purchase,Time of Purchase,Color,Year,Price,Mileage,Engine CC,Fuel Type,Payment,State,City,Customer,Contact No,Email,Warranty Period(years)
                
               
                Rules:
                - Only SELECT queries
                - Use only given columns
                - if not related , return:INVALID
                - return only SQL(do not write anything like here is there or your sql...etc
                
             
                Question:
                """+question;

        return chatClient.prompt().user(prompt).call().content().trim();

    }

}
