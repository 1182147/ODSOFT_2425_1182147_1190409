# ODSOFT_2425_1182147_1190409

Repository for ODSOFT's Semester Project

## Kickstart

To run the application:

```shell
mvn spring-boot:run
```

- Go to the following URL: http://localhost:8080/swagger-ui
- Go to the bottom of the page to the Authentication API.
- You can log in as one of the following Users:

| User             | Password          | Role              |
|------------------|-------------------|-------------------|
| maria@gmail.com  | Mariaroberta!123  | Librarian (Admin) |
| manuel@gmail.com | Manuelino123!     | Reader            |
| joao@gmail.com   | Joaoratao!123     | Reader            |
| pedro@gmail.com  | Pedrodascenas!123 | Reader            |
| ...other Readers | ...               | Reader            |

- Once you log in with a User, extract the Auth Token from the Response.
- Go to the top-right of the page to the green button with a padlock saying 'Authorize'
- Paste the Authorization Token there.
- Now you can use the APIs freely (if the Role allows).

### API-Ninja

It doesn't expose many endpoints. Only important part is that you need a custom `X-API-KEY` HTTP Request Header. Therefore:

```shell
curl 'https://api.api-ninjas.com/v1/jokes' -H 'X-API-KEY: a5nSlaa4JxIubY09H+NYuQ==cY9FegnFmAvYi6fN';

curl 'https://api.api-ninjas.com/v1/historicalEvents?year=2015&month=12' -H 'X-API-KEY: a5nSlaa4JxIubY09H+NYuQ==cY9FegnFmAvYi6fN'
```