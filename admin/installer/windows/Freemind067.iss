;****************************************************************************
;* Install Script for FreeMind version 0.6.7
;****************************************************************************
;* Andrew J. Iggleden (AJI) 8/12/2003 - Initial Release
;****************************************************************************

#include "isxbb.iss"

[Setup]
AppName=FreeMind
AppVerName=FreeMind 0.6.7
AppPublisherURL=http://freemind.sourceforge.net
AppSupportURL=http://freemind.sourceforge.net
AppUpdatesURL=http://freemind.sourceforge.net
DefaultDirName={pf}\FreeMind
DefaultGroupName=FreeMind
AllowNoIcons=true
LicenseFile=license.txt
WindowVisible=true


AppCopyright=Copyright © 2003
;AppCopyright=Copyright © {code:InstallationDate}
AppVersion=0.6.7
InfoAfterFile=after.txt
InfoBeforeFile=before.txt
PrivilegesRequired=admin
UninstallDisplayIcon={app}\Freemind.exe
UninstallDisplayName=FreeMind

AppID={B991B020-2968-11D8-AF23-444553540000}
UninstallRestartComputer=false
ChangesAssociations=true
FlatComponentsList=false
OutputBaseFilename=FreeMind-Windows-Installer-6.7
SolidCompression=false
InternalCompressLevel=9
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

[Files]
Source: ..\..\..\freemind\windows-launcher\Freemind.exe; DestDir: {app}; Flags: promptifolder overwritereadonly
Source: ..\..\..\freemind\accessories\mm2xbel.xsl; DestDir: {app}\accessories; Flags: promptifolder overwritereadonly
Source: ..\..\..\freemind\accessories\xbel2mm.xsl; DestDir: {app}\accessories; Flags: promptifolder overwritereadonly
Source: ..\..\..\freemind\doc\freemind.mm; DestDir: {app}\doc; Flags: promptifolder overwritereadonly
Source: ..\..\..\freemind\lib\freemind.jar; DestDir: {app}\lib; Flags: promptifolder overwritereadonly
Source: license.txt; DestDir: {app}; Flags: promptifolder overwritereadonly
Source: ..\..\..\freemind\patterns.xml; DestDir: {app}; Flags: promptifolder overwritereadonly
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

Source: {app}\*.*; DestDir: {app}\backup; Flags: external skipifsourcedoesntexist uninsneveruninstall
Source: {app}\accessories\*.*; DestDir: {app}\backup\accessories; Flags: external skipifsourcedoesntexist uninsneveruninstall
Source: {app}\doc\*.*; DestDir: {app}\backup\doc; Flags: external skipifsourcedoesntexist uninsneveruninstall
Source: {app}\lib\*.*; DestDir: {app}\backup\lib; Flags: external skipifsourcedoesntexist uninsneveruninstall

Source: FreeMind.gif; DestDir: {tmp}; Flags: dontcopy
Source: FreeMind1.gif; DestDir: {tmp}; Flags: dontcopy

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

[Code]
// AJI - Displays the two gifs on the main (blue) screen.
procedure CurStepChanged(CurStep: Integer);
begin
  if CurStep=csStart then begin
    ExtractTemporaryFile('FreeMind.gif');
    ExtractTemporaryFile('FreeMind1.gif');

    isxbb_AddImage(ExpandConstant('{tmp}')+'\FreeMind.gif',BOTTOMLEFT);
    isxbb_AddImage(ExpandConstant('{tmp}')+'\FreeMind1.gif',TOPRIGHT);

    isxbb_Init(StrToInt(ExpandConstant('{hwnd}')));
   end;
end;

// AJI - Cannot guarantee that this will work on ALL Windows versions. May need tweaking.
function CheckJavaVersion: Boolean;
var
  AVersion: String;
begin
  Result := False;
  if RegQueryStringValue(HKEY_LOCAL_MACHINE, 'SOFTWARE\JavaSoft\Java Runtime Environment', 'CurrentVersion', AVersion) then
  begin
	if AVersion = '1.4' then
		Result := True;
  end;

  if Result = False then	// Java 1.4 not found/detected
  begin
	if MsgBox( 'Java 1.4 or greater not detected - Continue with installation ?', mbError, MB_YESNO) = MRYES then
		Result := True
	else
		Result := False;
  end;
end;

function InitializeSetup(): Boolean;
begin
  Result := CheckJavaVersion;
end;
