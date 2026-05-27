# DB Sync Scripts

Bo script nay dung de dong bo lai toan bo PostgreSQL data tu may local len EC2 khi schema/data thay doi.

## 1. Export dump tu Postgres local

Chay tren PowerShell tai may local:

```powershell
Set-Location D:\KTTKPM\BTL\BE
.\scripts\db-sync\export-local-db-dumps.ps1
```

Ket qua:
- Tao thu muc `db-dumps`
- Xuat 6 file:
  - `user-service.sql`
  - `company-service.sql`
  - `job-service.sql`
  - `application-service.sql`
  - `payment-service.sql`
  - `cv-service.sql`

Script nay dump ngay ben trong container Postgres local roi `docker cp` ra ngoai, nen khong bi loi encoding do PowerShell redirect.

## 2. Upload dump len EC2

Chay tren PowerShell tai may local:

```powershell
Set-Location D:\KTTKPM\BTL\BE
.\scripts\db-sync\upload-db-dumps.ps1
```

Mac dinh script se:
- dung key `C:\Users\ACER\.ssh\careerconnect.pem`
- upload den `ubuntu@{IP_HOST}`

Neu doi host hoac key:

```powershell
.\scripts\db-sync\upload-db-dumps.ps1 -SshKeyPath "C:\path\key.pem" -RemoteHost "ubuntu@YOUR_EC2_IP"
```

## 3. Import dump tren EC2

SSH vao EC2:

```powershell
ssh -i "C:\Users\ACER\.ssh\careerconnect.pem" ubuntu@{IP_HOST}
```

Cap quyen chay script lan dau:

```bash
cd ~/career-connect-backend
chmod +x scripts/db-sync/import-db-dumps-ec2.sh
```

Chay import:

```bash
cd ~/career-connect-backend
./scripts/db-sync/import-db-dumps-ec2.sh
```

Script se:
- drop schema `public`
- tao lai schema rong
- import file dump moi

Dung script nay khi ban muon server lay dung schema/data moi nhat tu local.

## 4. Restart service sau khi import

Sau khi import xong:

```bash
cd ~/career-connect-backend
docker compose -f docker-compose.ec2.core.yml restart user-service company-service job-service api-gateway
```

Neu dang demo full:

```bash
cd ~/career-connect-backend
docker compose -f docker-compose.ec2.full.yml restart user-service company-service job-service application-service payment-service cv-service api-gateway
```

## 5. Kiem tra nhanh

### Job data

```bash
docker exec -it postgres-server psql -U postgres -d "job-service"
```

Trong `psql`:

```sql
select count(*) from jobs;
\q
```

### API qua gateway

```bash
curl "http://localhost:8080/api/v1/job/search?page=0&size=5"
```

## 6. Ghi chu quan trong

- Khong dung `docker exec ... pg_dump ... > file.sql` tren PowerShell de dump nua, vi co the gay loi encoding.
- Script import hien tai se ghi de toan bo schema/data trong 6 database dich.
- Neu sau nay them database moi, chi can bo sung ten database vao 3 script.
