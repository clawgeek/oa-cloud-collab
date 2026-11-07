@echo off
setlocal
set BASE=%~dp0
set JAR=%BASE%.mvn\wrapper\maven-wrapper.jar
set WRAPPER_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar
if not exist "%JAR%" (
  echo First run: downloading Maven Wrapper ...
  powershell -NoProfile -Command "Invoke-WebRequest -Uri '%WRAPPER_URL%' -OutFile '%JAR%'"
)
java -cp "%JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
endlocal
