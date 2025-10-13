# Docker Deployment - Forum Website

## Cách chạy

```bash
cd docker
docker-compose up -d
```

## Xem logs

```bash
docker-compose logs -f
```

## Dừng services

```bash
docker-compose down
```

## Thông tin

- **Application**: http://localhost:8081
- **MySQL Port**: 3307
- **Database**: testdb_spring
- **Username**: root
- **Password**: rootpassword

## Rebuild sau khi sửa code

```bash
docker-compose up -d --build
```

