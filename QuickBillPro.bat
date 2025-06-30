@echo off
setlocal

REM Get the directory of the batch file
set DIR=%~dp0
set PATH=%DIR%javafx-bin;%PATH%

REM Run the JAR with JavaFX module path and required VM options
"C:\Program Files\Java\jdk-21\bin\java.exe" ^
  --module-path "%DIR%javafx-lib" ^
  --add-modules javafx.controls,javafx.fxml,javafx.graphics ^
  --add-exports javafx.base/com.sun.javafx.event=ALL-UNNAMED ^
  --add-opens java.base/java.lang=ALL-UNNAMED ^
  --add-opens java.base/java.io=ALL-UNNAMED ^
  -Dorg.apache.commons.io.DEBUG=true ^
  -Dprism.order=sw ^
  -Dprism.verbose=true ^
  -jar "%DIR%QuickBillPro.jar"

pause
endlocal
