# Backend với JWT Authentication và Phân Quyền

## Cấu trúc Project

Project sử dụng Spring Boot với JWT authentication và phân quyền dựa trên roles.

### Các thành phần chính:

1. **JWT Token Provider**: Xử lý tạo và validate JWT tokens
2. **JWT Authentication Filter**: Filter để xử lý JWT trong request headers
3. **Security Config**: Cấu hình Spring Security với JWT
4. **Controllers**: 
   - `UserControll`: Xử lý đăng nhập/đăng ký
   - `AdminController`: Endpoints dành cho ADMIN
   - `UserController`: Endpoints dành cho USER

## API Endpoints

### Authentication
- `POST /api/auth/login` - Đăng nhập và nhận JWT token
- `POST /api/auth/register` - Đăng ký user thường
- `POST /api/auth/register-admin` - Đăng ký admin
- `POST /api/auth/logout` - Đăng xuất

### Admin Endpoints (Yêu cầu role ADMIN)
- `GET /api/admin/dashboard` - Dashboard admin
- `GET /api/admin/users` - Lấy danh sách tất cả users

### User Endpoints (Yêu cầu role USER)
- `GET /api/user/profile` - Profile của user hiện tại
- `GET /api/user/dashboard` - Dashboard user

### Orders Endpoints (Yêu cầu authentication)
- `GET /api/orders/test-auth` - Test authentication
- `POST /api/orders` - Tạo order mới
- `GET /api/orders/user` - Lấy orders của user hiện tại
- `GET /api/orders` - Lấy tất cả orders (admin only)
- `PUT /api/orders/{orderId}/status` - Cập nhật status order (admin only)

## Cách sử dụng

### 1. Đăng ký User
```bash
POST /api/auth/register
Content-Type: application/json

{
  "username": "user1",
  "password": "password123",
  "email": "user1@example.com"
}
```

### 2. Đăng ký Admin
```bash
POST /api/auth/register-admin
Content-Type: application/json

{
  "username": "admin1",
  "password": "password123",
  "email": "admin1@example.com"
}
```

### 3. Đăng nhập
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "user1",
  "password": "password123"
}
```

Response sẽ trả về JWT token:
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer"
}
```

### 4. Sử dụng JWT Token
Thêm header `Authorization: Bearer <token>` vào các request cần xác thực:

```bash
GET /api/user/profile
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

## Frontend Integration (React + Vite)

### 1. Tạo React App với Vite
```bash
npm create vite@latest frontend -- --template react
cd frontend
npm install
```

### 2. Cài đặt dependencies
```bash
npm install axios react-router-dom @mui/material @emotion/react @emotion/styled
```

### 3. Tạo Auth Context
```javascript
// src/contexts/AuthContext.js
import { createContext, useContext, useState, useEffect } from 'react';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('token'));

  const login = (token) => {
    setToken(token);
    localStorage.setItem('token', token);
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('token');
  };

  return (
    <AuthContext.Provider value={{ user, token, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
```

### 4. Tạo API Service
```javascript
// src/services/api.js
import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_URL,
});

// Add token to requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const authAPI = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (userData) => api.post('/auth/register', userData),
  registerAdmin: (userData) => api.post('/auth/register-admin', userData),
};

export const userAPI = {
  getProfile: () => api.get('/user/profile'),
  getDashboard: () => api.get('/user/dashboard'),
};

export const adminAPI = {
  getDashboard: () => api.get('/admin/dashboard'),
  getUsers: () => api.get('/admin/users'),
};

export default api;
```

### 5. Tạo Login Component
```javascript
// src/components/Login.jsx
import { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { authAPI } from '../services/api';

const Login = () => {
  const [credentials, setCredentials] = useState({ username: '', password: '' });
  const { login } = useAuth();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await authAPI.login(credentials);
      login(response.data.accessToken);
    } catch (error) {
      console.error('Login failed:', error);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        placeholder="Username"
        value={credentials.username}
        onChange={(e) => setCredentials({...credentials, username: e.target.value})}
      />
      <input
        type="password"
        placeholder="Password"
        value={credentials.password}
        onChange={(e) => setCredentials({...credentials, password: e.target.value})}
      />
      <button type="submit">Login</button>
    </form>
  );
};

export default Login;
```

## Chạy Project

### Backend
```bash
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
npm run dev
```

Backend sẽ chạy trên `http://localhost:8080` và Frontend trên `http://localhost:5173`.
