[Files]
Source: isxbb.dll; DestDir: {tmp}; Flags: dontcopy

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

