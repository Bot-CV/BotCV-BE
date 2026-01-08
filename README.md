# 🚀 Job Recruitment Platform - Server

Dịch vụ Backend cho nền tảng tuyển dụng việc làm, được xây dựng với Spring Boot 3 và Java 21.

## 📋 Tổng quan

Đây là dịch vụ backend RESTful API cho nền tảng tuyển dụng việc làm, cung cấp các chức năng quản lý người dùng, việc làm, ứng viên và tích hợp với các dịch vụ AI/ML để gợi ý việc làm thông minh.

## ✨ Tính năng

- 👤 **Quản lý người dùng**: Đăng ký, đăng nhập, phân quyền (Ứng viên/Nhà tuyển dụng/Admin)
- 🔐 **Xác thực & Phân quyền**: JWT, OAuth2 (Google), Spring Security
- 💼 **Quản lý việc làm**: CRUD việc làm, tìm kiếm, lọc theo tiêu chí
- 📄 **Quản lý hồ sơ**: Upload/parse CV, trích xuất thông tin tự động
- 🤖 **Tích hợp AI/ML**: 
  - Gợi ý việc làm cá nhân hóa (Recommendation Service)
  - Tìm kiếm ngữ nghĩa (Search Service)
  - Trích xuất thông tin CV (Resume Extractor Service)
- 📧 **Gửi email**: Thông báo, xác nhận tài khoản
- 🖼️ **Lưu trữ media**: Tích hợp Cloudinary cho ảnh đại diện, CV
- 🔄 **Caching**: Redis để tối ưu hiệu suất

## 🛠️ Công nghệ sử dụng

| Thành phần | Công nghệ |
|------------|-----------|
| **Framework** | Spring Boot 3.5.6 |
| **Ngôn ngữ** | Java 21 |
| **Database** | PostgreSQL 18 |
| **Cache** | Redis 7.4 |
| **ORM** | Spring Data JPA, Hibernate |
| **Migration** | Flyway |
| **Xác thực** | Spring Security, OAuth2, JWT |
| **Email** | Spring Mail |
| **Cloud Storage** | Cloudinary |
| **Build Tool** | Maven |
| **Container** | Docker |

## 📁 Cấu trúc dự án

```
src/main/java/org/toanehihi/jobrecruitmentplatformserver/
├── application/          # Application services, DTOs, mappers
├── domain/               # Entities, repositories, domain logic
├── infrastructure/       # External integrations, configurations
└── interfaces/           # Controllers, REST endpoints
```

Kiến trúc theo mô hình **Clean Architecture** với các tầng rõ ràng:
- **Domain Layer**: Chứa business logic và entities
- **Application Layer**: Xử lý use cases, DTOs
- **Infrastructure Layer**: Cấu hình, tích hợp bên ngoài
- **Interfaces Layer**: REST Controllers, API endpoints

## 🚀 Khởi chạy

### Yêu cầu

- Java 21+
- Maven 3.8+
- Docker & Docker Compose
- PostgreSQL 18+
- Redis 7.4+

### Sử dụng Docker Compose (Khuyến nghị)

1. **Clone repository**
   ```bash
   git clone <repository-url>
   cd Job-Recruitment-Platform-Server
   ```

2. **Cấu hình biến môi trường**
   ```bash
   cp .env.example .env
   # Chỉnh sửa file .env với các thông tin cần thiết
   ```

3. **Khởi chạy services**
   ```bash
   docker-compose up -d
   ```

   Server sẽ chạy tại: `http://localhost:8080`

### Chạy local (Development)

1. **Đảm bảo PostgreSQL và Redis đang chạy**

2. **Cấu hình biến môi trường trong file `.env`**

3. **Build và chạy**
   ```bash
   ./mvnw spring-boot:run
   ```

## ⚙️ Biến môi trường

| Biến | Mô tả |
|------|-------|
| `POSTGRES_HOST` | Host của PostgreSQL |
| `POSTGRES_PORT` | Port của PostgreSQL |
| `POSTGRES_DB` | Tên database |
| `POSTGRES_USERNAME` | Username database |
| `POSTGRES_PASSWORD` | Password database |
| `REDIS_HOST` | Host của Redis |
| `REDIS_PORT` | Port của Redis |
| `JWT_SECRET` | Secret key cho JWT |
| `CLIENT_ID` | Google OAuth2 Client ID |
| `CLIENT_SECRET` | Google OAuth2 Client Secret |
| `MAIL_USERNAME` | Email username (SMTP) |
| `MAIL_PASSWORD` | Email password (App password) |
| `CLOUDINARY_NAME` | Cloudinary cloud name |
| `CLOUDINARY_API_KEY` | Cloudinary API key |
| `CLOUDINARY_API_SECRET` | Cloudinary API secret |
| `FRONTEND_URL` | URL của Frontend |
| `NER_SERVICE_URL` | URL của Resume Extractor Service |
| `SEARCH_SERVICE_URL` | URL của Search Service |
| `RECOMMENDATION_SERVICE_URL` | URL của Recommendation Service |

## 📡 API Endpoints

### Xác thực
- `POST /api/auth/register` - Đăng ký tài khoản
- `POST /api/auth/login` - Đăng nhập
- `POST /api/auth/google` - Đăng nhập bằng Google

### Việc làm
- `GET /api/jobs` - Danh sách việc làm
- `GET /api/jobs/{id}` - Chi tiết việc làm
- `POST /api/jobs` - Tạo việc làm mới
- `PUT /api/jobs/{id}` - Cập nhật việc làm
- `DELETE /api/jobs/{id}` - Xóa việc làm

### Ứng viên
- `GET /api/candidates/profile` - Lấy hồ sơ
- `PUT /api/candidates/profile` - Cập nhật hồ sơ
- `POST /api/candidates/resume` - Upload CV

### Gợi ý
- `GET /api/recommendations` - Lấy gợi ý việc làm

### Health Check
- `GET /actuator/health` - Kiểm tra trạng thái service

## 🐳 Docker

### Build image
```bash
docker build -t jrp-server:1.0 .
```

### Chạy container
```bash
docker run -p 8080:8080 --env-file .env jrp-server:1.0
```

## 🏗️ Kiến trúc hệ thống

```
┌─────────────────┐     ┌─────────────────┐
│   Frontend      │────▶│   Backend API   │
│   (Next.js)     │     │  (Spring Boot)  │
└─────────────────┘     └────────┬────────┘
                                 │
                    ┌────────────┼────────────┐
                    │            │            │
              ┌─────▼─────┐ ┌────▼────┐ ┌─────▼─────┐
              │   Resume  │ │  Search │ │ Recommend │
              │ Extractor │ │ Service │ │  Service  │
              └───────────┘ └─────────┘ └───────────┘
                    │            │            │
              ┌─────▼─────┐ ┌────▼────────────▼────┐
              │   NER     │ │      Milvus          │
              │   Model   │ │   (Vector DB)        │
              └───────────┘ └──────────────────────┘
```

## 📝 Ghi chú phát triển

- Sử dụng Flyway để quản lý database migration
- Tích hợp Spring Actuator để monitoring
- Hỗ trợ hot-reload với Spring DevTools trong môi trường development

## 📄 License

Dự án được phát triển cho mục đích học tập và nghiên cứu.
