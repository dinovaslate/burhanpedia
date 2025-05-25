## Cara Menjalankan

### 1. Kompilasi Program

javac -d bin $(Get-Content sources.txt)

### 2. Menjalankan Program

java -cp bin com.burhanpedia.core.menu.MainMenuSystem

### 3. Menjalankan Unit Test

#### pastikan Kompilasi Program terlebih dahulu
javac -d bin $(Get-Content sources.txt)

#### jalankan test

.\test_burhanpedia.bat