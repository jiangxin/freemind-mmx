
;****************************************************************************
;* Install Script for FreeMind
;****************************************************************************
;* Before using this be sure to download and install Inno Setup from
;* www.jrsoftware.org and ISTool from www.istool.org. These are required to
;* make changes and compile this script. To use the billboard feature please
;* dowload and install the ISX BillBoard DLL.
;****************************************************************************
;* Andrew J. Iggleden (AJI) 8/12/2003 - Initial Release
;* Andrew J. Iggleden (AJI) 20/12/2003 - Version 0.7.0
;* Christian Foltin   (FC ) 10/01/2004 - Version 0.7.1
;* Christian Foltin   (FC ) 08/07/2005 - Version 0.8.0
;* Christian Foltin   (FC ) 04/03/2007 - Version 0.9.0 Beta9
;* Christian Foltin   (FC ) 25/06/2007 - Version 0.9.0 Beta10
;* Dimitry Polivaev   (DP ) 15/07/2007 - Version 0.9.0 Beta11
;* Christian Foltin   (FC ) 21/07/2007 - Version 0.9.0 Beta12
;* Christian Foltin   (FC ) 07/08/2007 - Version 0.9.0 Beta13
;* Dimitry Polivaev   (DP ) 19/10/2007 - Version 0.9.0 Beta14
;* Christian Foltin   (FC ) 30/11/2007 - Version 0.9.0 Beta15
;* Christian Foltin   (FC ) 02/01/2008 - Version 0.8.1
;* Christian Foltin   (FC ) 22/02/2008 - Universial Version 
;****************************************************************************

[Files]
Source: isxbb.dll; DestDir: {tmp}; Flags: dontcopy

;Source: ..\..\..\bin\dist\Freemind.exe; DestDir: {app}; Flags: promptifolder overwritereadonly
;Source: ..\..\..\bin\dist\Freemind.bat; DestDir: {app}; Flags: promptifolder overwritereadonly
Source: ..\..\..\bin\dist\Freemind.exe; DestDir: {app}; Flags: promptifolder overwritereadonly
Source: ..\..\..\bin\dist\Freemind.bat; DestDir: {app}; Flags: promptifolder overwritereadonly
Source: ..\..\..\bin\dist\accessories\*.*; DestDir: {app}\accessories; Flags: promptifolder overwritereadonly
Source: ..\..\..\bin\dist\browser\*.*; DestDir: {app}\browser; Flags: promptifolder overwritereadonly
Source: ..\..\..\bin\dist\doc\*.*; DestDir: {app}\doc; Flags: promptifolder overwritereadonly
Source: ..\..\..\bin\dist\lib\*.*; DestDir: {app}\lib; Flags: promptifolder overwritereadonly  recursesubdirs
Source: ..\..\..\bin\dist\plugins\*.*; DestDir: {app}\plugins; Flags: promptifolder overwritereadonly  recursesubdirs
Source: jre_installer.exe; Flags: dontcopy
Source: license.txt; DestDir: {app}; Flags: promptifolder overwritereadonly
Source: ..\..\..\bin\dist\patterns.xml; DestDir: {app}; Flags: promptifolder overwritereadonly
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

Source: FreeMind.gif; DestDir: {tmp}; Flags: dontcopy
Source: FreeMind1.gif; DestDir: {tmp}; Flags: dontcopy

[Code]
const
  TOPLEFT			= 1;
  TOPRIGHT			= 2;
  BOTTOMLEFT		= 3;
  BOTTOMRIGHT		= 4;
  CENTER			= 5;
  BACKGROUND		= 6;
  TOP				= 7;
  BOTTOM			= 8;
  LEFT				= 9;
  RIGHT				= 10;
  TIMER				= 16;



function isxbb_AddImage(Image: PChar; Flags: Cardinal): Integer;
external 'isxbb_AddImage@files:isxbb.dll stdcall';

function isxbb_Init(hWnd: Integer): Integer;
external 'isxbb_Init@files:isxbb.dll stdcall';

function isxbb_StartTimer(Seconds: Integer; Flags: Cardinal): Integer;
external 'isxbb_StartTimer@files:isxbb.dll stdcall';

function isxbb_KillTimer(Flags: Cardinal): Integer;
external 'isxbb_KillTimer@files:isxbb.dll stdcall';


function SearchForJavaVersion: Boolean;
var
  AVersion: String;
begin
  Result := False;
  if RegQueryStringValue(HKEY_LOCAL_MACHINE, 'SOFTWARE\JavaSoft\Java Runtime Environment', 'CurrentVersion', AVersion) then
  begin
	if (AVersion = '1.4') or (AVersion = '1.5') or (AVersion = '1.6') or (AVersion = '1.7') then
		Result := True;
  end;
  if IsWin64 and RegQueryStringValue(HKEY_LOCAL_MACHINE_64, 'SOFTWARE\JavaSoft\Java Runtime Environment', 'CurrentVersion', AVersion) then
  begin
	if (AVersion = '1.4') or (AVersion = '1.5') or (AVersion = '1.6') or (AVersion = '1.7') then
		Result := True;
  end;
end;

function CheckJavaVersion: Boolean;
begin
  Result := SearchForJavaVersion;
  if Result = False then	// Java not found/detected
  begin
	if MsgBox( 'Java 1.4 or greater not detected. - You have to download and install Java from http://java.sun.com/ - \nContinue with installation?', mbError, MB_YESNO) = MRYES then
		Result := True
	else
		Result := False;
  end;
end;

function InstallJavaVersion: Boolean;
var
  ErrorCode: Integer;
        	
begin
  Result := SearchForJavaVersion;
  if Result = False then	// Java not found/detected
	  begin
		if MsgBox( 'Java not detected. Do you want to install it?', mbError, MB_YESNO) = MRYES then
        	begin
	        	ExtractTemporaryFile('jre_installer.exe');
	        	if not Exec(ExpandConstant('{tmp}\jre_installer.exe'), '', '',  SW_SHOWNORMAL, ewWaitUntilTerminated, ErrorCode) then
	        	begin
	        		MsgBox('Java Installation:' #13#13 'Execution of ''jre_installer.exe'' failed. ' + SysErrorMessage(ErrorCode) + '.', mbError, MB_OK);
	        		Result := False;
	        	end;
        	end;
      end;
  // in any case, we proceed.
  Result := True;
end;

function InitializeSetup(): Boolean;
begin
// AJI - Displays the two gifs on the main (blue) screen.
// AJI - Cannot guarantee that this will work on ALL Windows versions. May need tweaking.
    ExtractTemporaryFile('FreeMind.gif');
    ExtractTemporaryFile('FreeMind1.gif');

    isxbb_AddImage(ExpandConstant('{tmp}')+'\FreeMind.gif',BOTTOMLEFT);
    isxbb_AddImage(ExpandConstant('{tmp}')+'\FreeMind1.gif',TOPRIGHT);

    isxbb_Init(StrToInt(ExpandConstant('{hwnd}')));
  Result := InstallJavaVersion;
        	
end;
[Setup]
AppName=FreeMind
AppVerName=FreeMind 0.9.0_Beta_17
AppPublisherURL=http://freemind.sourceforge.net
AppSupportURL=http://freemind.sourceforge.net
AppUpdatesURL=http://freemind.sourceforge.net
DefaultDirName={pf}\FreeMind
DefaultGroupName=FreeMind
AllowNoIcons=true
LicenseFile=license.txt
WindowVisible=true
ShowLanguageDialog=true


AppCopyright=Copyright © 2000-2008 Jörg Müller, Daniel Polansky, Petr Novak, Christian Foltin, Dimitry Polivaev and others
;AppCopyright=Copyright © {code:InstallationDate}
AppVersion=0.9.0_Beta_17
InfoAfterFile=after.txt
InfoBeforeFile=before.txt
PrivilegesRequired=admin
UninstallDisplayIcon={app}\Freemind.exe
UninstallDisplayName=FreeMind

AppID=B991B020-2968-11D8-AF23-444553540000
UninstallRestartComputer=false
ChangesAssociations=true
FlatComponentsList=false
OutputBaseFilename=FreeMind-Windows-Installer-0.9.0_Beta_17-max-java-installer-embedded
SolidCompression=false
; old: InternalCompressLevel=9
Compression=zip/9
ShowTasksTreeLines=true
[Messages]
DiskSpaceMBLabel=The program requires at least [kb] KB of disk space.
ComponentsDiskSpaceMBLabel=Current selection requires at least [kb] KB of disk space.

[Tasks]
; NOTE: The following entry contains English phrases ("Create a desktop icon" and "Additional icons"). You are free to translate them into another language if required.
Name: desktopicon; Description: Create a &desktop icon; GroupDescription: Additional icons:
; NOTE: The following entry contains English phrases ("Create a Quick Launch icon" and "Additional icons"). You are free to translate them into another language if required.
Name: quicklaunchicon; Description: Create a &Quick Launch icon; GroupDescription: Additional icons:; Flags: unchecked
Name: fileassoc; Description: &Associate FreeMind Extensions with the .mm file extension; GroupDescription: File Association:

[Icons]
Name: {group}\FreeMind; Filename: {app}\Freemind.exe; WorkingDir: {app}; IconIndex: 0; IconFilename: {app}\Freemind.exe
Name: {group}\Uninstall FreeMind; Filename: {uninstallexe}
Name: {userdesktop}\FreeMind; Filename: {app}\Freemind.exe; Tasks: desktopicon; WorkingDir: {app}; IconIndex: 0; IconFilename: {app}\Freemind.exe
Name: {userstartmenu}\FreeMind; Filename: {app}\Freemind.exe; Tasks: quicklaunchicon; WorkingDir: {app}; IconIndex: 0; IconFilename: {app}\Freemind.exe

[Run]
; NOTE: The following entry contains an English phrase ("Launch"). You are free to translate it into another language if required.
Filename: {app}\Freemind.exe; Description: Launch FreeMind; Flags: skipifsilent postinstall unchecked
[_ISTool]
Use7zip=false
EnableISX=true

[Registry]
Root: HKCR; SubKey: .mm; ValueType: string; ValueData: FreeMind Map; Flags: uninsdeletekey; Tasks: fileassoc
Root: HKCR; SubKey: FreeMind Map; ValueType: string; ValueData: FreeMind Map; Flags: uninsdeletekey; Tasks: fileassoc
Root: HKCR; SubKey: FreeMind Map\Shell\Open\Command; ValueType: string; ValueData: """{app}\Freemind.exe"" ""%1"""; Flags: uninsdeletekey; Tasks: fileassoc
Root: HKCR; Subkey: FreeMind Map\DefaultIcon; ValueType: string; ValueData: {app}\Freemind.exe,0; Flags: uninsdeletekey; Tasks: fileassoc
