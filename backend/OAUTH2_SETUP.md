# OAuth2 Google Setup Guide

## Vấn đề đã được khắc phục:

1. **Thiếu cấu hình OAuth2 client đầy đủ** - Đã thêm các thuộc tính cần thiết
2. **Vòng lặp redirect** - Đã thêm `/error` vào permitAll
3. **Xử lý lỗi** - Đã cải thiện error handling trong các service

## Cấu hình cần thiết:

### 1. Google Console Setup:
- Tạo OAuth2 client credentials
- Thêm redirect URI: `http://localhost:8080/login/oauth2/code/google`
- Copy Client ID và Client Secret

### 2. Environment Variables (khuyến nghị):
```bash
GOOGLE_CLIENT_ID=your-client-id
GOOGLE_CLIENT_SECRET=your-client-secret
```

### 3. Frontend Integration:
Frontend cần có endpoint `/auth/oauth2/redirect` để nhận token từ backend.

## Test OAuth2 Flow:

1. **Bắt đầu flow**: `GET /oauth2/authorize/google`
2. **Google redirect**: User được redirect đến Google
3. **Callback**: Google redirect về `/login/oauth2/code/google`
4. **Success**: Backend tạo JWT và redirect về frontend với token
5. **Failure**: Backend redirect về frontend với error message

## Troubleshooting:

- Kiểm tra logs với level DEBUG
- Đảm bảo Google Console redirect URI đúng
- Kiểm tra CORS configuration
- Verify database connection và user creation

## Files đã được cập nhật:

- `application.properties` - OAuth2 configuration
- `SecurityConfig.java` - Security rules
- `CustomOAuth2UserService.java` - Error handling
- `OAuth2AuthenticationSuccessHandler.java` - Success handling
