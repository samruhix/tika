param([Parameter(Mandatory=$true)][string]$Maven)
$ErrorActionPreference = 'Stop'
$workspace = Split-Path $PSScriptRoot -Parent
$Maven = (Resolve-Path -LiteralPath $Maven).Path
$sourceDir = Join-Path $workspace 'tika-core/src/test/java/org/apache/tika/sax'
$output = Join-Path $PSScriptRoot ('artifacts/replay-' + (Get-Date -Format 'yyyyMMdd-HHmmss'))
$backup = Join-Path $output 'sources-before'
New-Item -ItemType Directory -Path $backup -Force | Out-Null
$sources = @(Get-ChildItem -LiteralPath $sourceDir -File | Where-Object {
    $_.Name -like 'BasicContentHandlerFactory_*_Test.java' -or
    $_.Name -eq 'BasicContentHandlerFactoryManualTest.java'
})
foreach ($file in $sources) { Copy-Item -LiteralPath $file.FullName -Destination $backup }
function Clear-TestOutputs {
    foreach ($relative in @('tika-core/target/test-classes', 'tika-core/target/surefire-reports',
                             'tika-core/target/jacoco.exec')) {
        $path = Join-Path $workspace $relative
        if (Test-Path -LiteralPath $path) {
            $resolved = (Resolve-Path -LiteralPath $path).Path
            if (!$resolved.StartsWith($workspace + '\')) { throw "Unsafe path: $resolved" }
            Remove-Item -LiteralPath $resolved -Recurse -Force
        }
    }
}
function Run-Maven([string]$Arguments, [string]$Log) {
    $command = '"' + $Maven + '" ' + $Arguments + ' > "' + $Log + '" 2>&1'
    & $env:ComSpec /d /c $command
    if ($LASTEXITCODE -ne 0) { throw "Maven failed; see $Log" }
}
Push-Location $workspace
try {
    foreach ($file in $sources) { Remove-Item -LiteralPath $file.FullName }
    foreach ($phase in @('A-final-original', 'B0-generated-passing', 'B-generated', 'C-manual')) {
        if ($phase -eq 'B0-generated-passing') { python "$PSScriptRoot/prepare-generated.py" passing }
        if ($phase -eq 'B-generated') { python "$PSScriptRoot/prepare-generated.py" repaired }
        if ($phase -eq 'C-manual') {
            Copy-Item -LiteralPath "$backup/BasicContentHandlerFactoryManualTest.java" -Destination $sourceDir
        }
        $phaseDir = Join-Path $output $phase
        New-Item -ItemType Directory -Path $phaseDir -Force | Out-Null
        Run-Maven '-pl tika-core spotless:apply' "$phaseDir/format.log"
        Clear-TestOutputs
        Run-Maven '-pl tika-core -am test -DskipTests=false -Dmaven.test.skip=false' "$phaseDir/tests.log"
        Run-Maven '-pl tika-core test-compile org.pitest:pitest-maven:mutationCoverage -DskipTests=false -Dmaven.test.skip=false' "$phaseDir/pit.log"
        foreach ($relative in @('tika-core/target/pit-reports', 'tika-core/target/surefire-reports',
                                 'tika-core/target/site/jacoco')) {
            Copy-Item -LiteralPath (Join-Path $workspace $relative) -Destination $phaseDir -Recurse
        }
        Write-Output "Verified $phase : $phaseDir"
    }
} finally {
    foreach ($file in Get-ChildItem -LiteralPath $backup -Filter '*.java') {
        Copy-Item -LiteralPath $file.FullName -Destination $sourceDir -Force
    }
    Pop-Location
}
