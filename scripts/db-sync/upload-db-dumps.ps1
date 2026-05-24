param(
    [string]$SshKeyPath = "C:\Users\ACER\.ssh\careerconnect.pem",
    [string]$RemoteHost = "ubuntu@47.131.92.245",
    [string]$DumpDir = ".\db-dumps",
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

foreach ($Database in $Databases) {
    $LocalDumpPath = Join-Path $DumpDir "$Database.sql"

    if (!(Test-Path $LocalDumpPath)) {
        throw "Missing dump file: $LocalDumpPath"
    }

    Write-Host "Uploading $LocalDumpPath to $RemoteHost..."
    scp -i $SshKeyPath $LocalDumpPath "${RemoteHost}:~/"
}

Write-Host "Done. All dump files uploaded to $RemoteHost"
