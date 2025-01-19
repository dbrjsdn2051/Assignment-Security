# 인텔리픽 과제 테스트 

Spring Boot와 JWT를 활용한 사용자 인증/인가 애플리케이션입니다. 

## 📌 API 명세

### 회원가입 (Sign Up)
```http
POST /auth/signup
Content-Type: application/json

{
    "username": "string",
    "password": "string",
    "nickname": "string"
}
```

<br><br>

### 로그인 (login)
```http
POST /auth/login
Content-Type: application/json

{
"nickname": "string",
"password": "string"
}
```

<br><br>

### 회원 정보 조회 
```http
GET /api/users
Authorization: Bearer {access_token}
```

<br><br>

### 액세스 토큰 재발급
```http
GET /auth/refresh
```

<br><br>

## ✅ 접속 URL 
### http://211.226.249.97:8080/swagger-ui/index.html 

<br> 

### 🚨 주의사항
1. 로그인 이후 헤더에 토큰이 발급되므로 회원 정보 조회 요청 시 헤더에 토큰값을 넣어 요청해야합니다. 
2. 액세스 토큰 재발급 시 `F12` 로 개발자 모드를 열어 쿠키 값을 확인후 Swagger Authorize 에 넣어주세요.  

