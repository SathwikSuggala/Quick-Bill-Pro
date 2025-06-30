[Setup]
AppName=QuickBillPro
AppVersion=1.0
DefaultDirName={pf}\QuickBillPro
DefaultGroupName=QuickBillPro
OutputDir=.
OutputBaseFilename=QuickBillProInstaller

[Files]
Source: "QuickBillPro.jar"; DestDir: "{app}"
Source: "QuickBillPro.bat"; DestDir: "{app}"
Source: "lib\*"; DestDir: "{app}\lib"; Flags: recursesubdirs
Source: "javafx-lib\*"; DestDir: "{app}\javafx-lib"; Flags: recursesubdirs
Source: "javafx-bin\*"; DestDir: "{app}\javafx-bin"; Flags: recursesubdirs
Source: "assets\*"; DestDir: "{app}\assets"; Flags: recursesubdirs

[Icons]
Name: "{group}\QuickBillPro"; Filename: "{app}\QuickBillPro.bat"
Name: "{commondesktop}\QuickBillPro"; Filename: "{app}\QuickBillPro.bat"; IconFilename: "{app}\assets\logo.ico"; Tasks: desktopicon

[Tasks]
Name: "desktopicon"; Description: "Create a &desktop icon"; GroupDescription: "Additional icons:"