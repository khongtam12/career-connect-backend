# import-data.ps1
# Script to quickly import sample data into all microservices

$OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host "Starting data import..." -ForegroundColor Cyan

# 1. Company Service
Write-Host "Importing Company Service data..." -ForegroundColor Yellow
Get-Content -Path seed_company.sql -Encoding UTF8 | docker exec -i postgres-server psql -U postgres -d company-service
Get-Content -Path seed_extra.sql -Encoding UTF8 | docker exec -i postgres-server psql -U postgres -d company-service

# 2. User Service
Write-Host "Importing User Service data..." -ForegroundColor Yellow
Get-Content -Path seed_user.sql -Encoding UTF8 | docker exec -i postgres-server psql -U postgres -d user-service

# 3. Payment Service
Write-Host "Importing Payment Service data..." -ForegroundColor Yellow
Get-Content -Path seed_payment.sql -Encoding UTF8 | docker exec -i postgres-server psql -U postgres -d payment-service

# 4. Job Service
Write-Host "Importing Job Service data..." -ForegroundColor Yellow
Get-Content -Path seed_job.sql -Encoding UTF8 | docker exec -i postgres-server psql -U postgres -d job-service

Write-Host "Data import completed successfully!" -ForegroundColor Green
