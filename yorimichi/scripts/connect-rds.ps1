param(
    [string]$Profile = "yorimichi-dev",
    [int]$LocalPort = 13306
)

$ErrorActionPreference = "Stop"

$region = "ap-northeast-2"
$asgName = "yorimichi-prod-ecs-asg"
$dbIdentifier = "yorimichi-prod-mysql"

if (-not (Get-Command aws -ErrorAction SilentlyContinue)) {
    throw "AWS CLI is not installed or is not available in PATH."
}

if (-not (Get-Command session-manager-plugin -ErrorAction SilentlyContinue)) {
    throw "Session Manager Plugin is not installed or is not available in PATH."
}

# Verify the configured AWS identity and determine the developer username.
$identityJson = aws sts get-caller-identity `
    --profile $Profile `
    --output json

if ($LASTEXITCODE -ne 0) {
    throw "Unable to verify the AWS identity for profile '$Profile'."
}

$identity = $identityJson | ConvertFrom-Json
$userName = ($identity.Arn -split "/")[-1]

# Locate a healthy EC2 instance currently registered with the ECS Auto Scaling group.
$instanceId = aws autoscaling describe-auto-scaling-groups `
    --auto-scaling-group-names $asgName `
    --query "AutoScalingGroups[0].Instances[?LifecycleState=='InService' && HealthStatus=='Healthy'] | [0].InstanceId" `
    --output text `
    --region $region `
    --profile $Profile

if ($LASTEXITCODE -ne 0 -or [string]::IsNullOrWhiteSpace($instanceId) -or $instanceId -eq "None") {
    throw "No healthy ECS instance was found. Try again during service hours or contact the administrator."
}

# Retrieve the current RDS endpoint without storing it in the repository.
$dbEndpoint = aws rds describe-db-instances `
    --db-instance-identifier $dbIdentifier `
    --query "DBInstances[0].Endpoint.Address" `
    --output text `
    --region $region `
    --profile $Profile

if ($LASTEXITCODE -ne 0 -or [string]::IsNullOrWhiteSpace($dbEndpoint) -or $dbEndpoint -eq "None") {
    throw "Unable to retrieve the RDS endpoint."
}

# Retrieve only the database password that matches the current IAM username.
$dbPassword = aws ssm get-parameter `
    --name "/yorimichi/prod/db/developers/$userName/password" `
    --with-decryption `
    --query "Parameter.Value" `
    --output text `
    --region $region `
    --profile $Profile

if ($LASTEXITCODE -ne 0 -or [string]::IsNullOrWhiteSpace($dbPassword)) {
    throw "Unable to retrieve the database password for '$userName'."
}

$dbPassword.Trim() | Set-Clipboard
$dbPassword = $null

Write-Host ""
Write-Host "MySQL Workbench connection"
Write-Host "Host:     127.0.0.1"
Write-Host "Port:     $LocalPort"
Write-Host "Username: $userName"
Write-Host "Password: copied to clipboard"
Write-Host ""
Write-Host "Keep this terminal open while using MySQL Workbench."

# Forward the local port to the private RDS instance through Session Manager.
aws ssm start-session `
    --target $instanceId `
    --document-name AWS-StartPortForwardingSessionToRemoteHost `
    --parameters "host=$dbEndpoint,portNumber=3306,localPortNumber=$LocalPort" `
    --region $region `
    --profile $Profile

if ($LASTEXITCODE -ne 0) {
    throw "The Session Manager port-forwarding session ended with an error."
}
