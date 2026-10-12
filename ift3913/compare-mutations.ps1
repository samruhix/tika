param(
    [string]$Before = "$PSScriptRoot/artifacts/A-original/mutations.xml",
    [string]$After = "$PSScriptRoot/artifacts/C-manual/mutations.xml",
    [string]$Output = "$PSScriptRoot/artifacts/mutation-comparison.csv"
)
$ErrorActionPreference = 'Stop'
function Get-Key($m) {
    "$($m.mutatedClass)|$($m.mutatedMethod)|$($m.methodDescription)|$($m.lineNumber)|$($m.mutator)|$($m.indexes.index -join ',')|$($m.blocks.block -join ',')"
}
[xml]$a = Get-Content -LiteralPath $Before -Raw
[xml]$b = Get-Content -LiteralPath $After -Raw
$original = @{}
foreach ($m in $a.mutations.mutation) { $original[(Get-Key $m)] = $m }
if ($original.Count -ne @($b.mutations.mutation).Count) { throw 'Mutation sets have different sizes' }
$rows = foreach ($m in $b.mutations.mutation) {
    $key = Get-Key $m
    if (!$original.ContainsKey($key)) { throw "Unmatched mutation: $key" }
    $old = $original[$key]
    [pscustomobject]@{ Key=$key; Method=$m.mutatedMethod; Line=$m.lineNumber;
        Mutator=$m.mutator; Description=$m.description; Before=$old.status;
        After=$m.status; NewlyKilled=($old.status -ne 'KILLED' -and $m.status -eq 'KILLED');
        KillingTest=$m.killingTest }
}
$rows | Export-Csv -LiteralPath $Output -NoTypeInformation -Encoding UTF8
$rows | Group-Object After | Select-Object Name,Count
