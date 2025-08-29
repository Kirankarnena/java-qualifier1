# Bajaj Finserv Java Qualifier – SQL Challenge

## Candidate Details
- **Name**: Sai Kiran Karnena
- **RegNo**: 22BCE20537
- **Email**: kiran.22bce20537@vitapstudent.ac.in

## Problem Statement
The application:
1. Calls `generateWebhook/JAVA` API to receive webhook URL and access token.
2. Chooses SQL Question 1 (because regNo ends with 37 → odd).
3. Submits the final SQL query to `testWebhook/JAVA`.
4. Prints the server’s response.

## Final SQL Query
```sql
WITH salary_filter AS (
  SELECT p.AMOUNT AS SALARY, e.FIRST_NAME, e.LAST_NAME, e.DOB, d.DEPARTMENT_NAME
  FROM PAYMENTS p
  JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID
  JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID
  WHERE DAY(p.PAYMENT_TIME) != 1
)
SELECT SALARY,
       CONCAT(FIRST_NAME, ' ', LAST_NAME) AS NAME,
       FLOOR(DATEDIFF(CURDATE(), DOB) / 365) AS AGE,
       DEPARTMENT_NAME
FROM salary_filter
WHERE SALARY = (SELECT MAX(SALARY) FROM salary_filter);
