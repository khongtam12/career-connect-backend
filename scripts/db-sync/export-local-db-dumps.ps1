param(
    [string]$ContainerName = "postgres-server",
    [string]$OutputDir = ".\db-dumps",
    [string[]]$Databases = @(
        "user-service",
        "company-service",
        "job-service",
        "application-service",
        "payment-service",
        "cv-service"
    )
)

$ErrorActionPreference = "Stop"

if (!(Test-Path $OutputDir)) {
    New-Item -ItemType Directory -Path $OutputDir | Out-Null
}

foreach ($Database in $Databases) {
    $RemoteDumpPath = "/tmp/$Database.sql"
    $LocalDumpPath = Join-Path $OutputDir "$Database.sql"

    Write-Host "Exporting $Database from container $ContainerName..."
    docker exec $ContainerName sh -c "pg_dump -U postgres -d `"$Database`" -f $RemoteDumpPath"

    Write-Host "Copying $Database dump to $LocalDumpPath..."
    docker cp "${ContainerName}:$RemoteDumpPath" $LocalDumpPath

    Write-Host "Cleaning temporary dump inside container for $Database..."
    docker exec $ContainerName sh -c "rm -f $RemoteDumpPath"
}

Write-Host "Done. Dump files are in $OutputDir"
