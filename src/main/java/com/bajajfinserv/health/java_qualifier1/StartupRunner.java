package com.bajajfinserv.health.java_qualifier1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class StartupRunner implements CommandLineRunner {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void run(String... args) throws Exception {
        // Step 1: Generate webhook and access token
        String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "Sai Kiran Karnena");
        requestBody.put("regNo", "22BCE20537");
        requestBody.put("email", "kiran.22bce20537@vitapstudent.ac.in");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);  // fixed

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        // get JSON as Map
        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        System.out.println("Webhook Response: " + response.getBody());

        String webhookUrl = (String) response.getBody().get("webhook");
        String accessToken = (String) response.getBody().get("accessToken");

        // Step 2: Prepare the SQL query (removed backslashes)
        String finalSqlQuery =
                "WITH salary_filter AS ( " +
                        "SELECT p.AMOUNT AS SALARY, e.FIRST_NAME, e.LAST_NAME, e.DOB, d.DEPARTMENT_NAME " +
                        "FROM PAYMENTS p " +
                        "JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID " +
                        "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
                        "WHERE DAY(p.PAYMENT_TIME) != 1 " +
                        ") " +
                        "SELECT SALARY, CONCAT(FIRST_NAME, ' ', LAST_NAME) AS NAME, " +
                        "FLOOR(DATEDIFF(CURDATE(), DOB) / 365) AS AGE, DEPARTMENT_NAME " +
                        "FROM salary_filter " +
                        "WHERE SALARY = (SELECT MAX(SALARY) FROM salary_filter)";

        // Step 3: Submit finalQuery with Authorization header
        HttpHeaders headers2 = new HttpHeaders();
        headers2.setContentType(MediaType.APPLICATION_JSON);  // fixed
        headers2.set("Authorization", "Bearer " + accessToken); // first try with Bearer

        Map<String, String> body2 = new HashMap<>();
        body2.put("finalQuery", finalSqlQuery);

        HttpEntity<Map<String, String>> entity2 = new HttpEntity<>(body2, headers2);

        try {
            ResponseEntity<String> response2 =
                    restTemplate.postForEntity(webhookUrl, entity2, String.class);
            System.out.println("Submit SQL Response: " + response2.getBody());
        } catch (HttpClientErrorException.Unauthorized e) {
            // Retry without "Bearer"
            System.out.println("401 with Bearer, retrying without...");
            headers2.set("Authorization", accessToken);
            entity2 = new HttpEntity<>(body2, headers2);

            ResponseEntity<String> response2 =
                    restTemplate.postForEntity(webhookUrl, entity2, String.class);
            System.out.println("Submit SQL Response (no Bearer): " + response2.getBody());
        }
    }
}
