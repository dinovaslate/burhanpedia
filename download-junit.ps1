# Buat folder lib jika belum ada
if (!(Test-Path -Path "lib")) {
    New-Item -Path "lib" -ItemType Directory
}

# Unduh JUnit dan Hamcrest JAR
$junitUrl = "https://repo1.maven.org/maven2/junit/junit/4.13.2/junit-4.13.2.jar"
$hamcrestUrl = "https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar"

$junitFile = "lib\junit-4.13.2.jar"
$hamcrestFile = "lib\hamcrest-core-1.3.jar"

# Unduh file JAR
Invoke-WebRequest -Uri $junitUrl -OutFile $junitFile
Invoke-WebRequest -Uri $hamcrestUrl -OutFile $hamcrestFile

Write-Host "JUnit dan Hamcrest JAR berhasil diunduh ke folder lib"