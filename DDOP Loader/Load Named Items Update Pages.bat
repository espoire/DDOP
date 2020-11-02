@echo off
REM Invokes ./wget.exe
REM Saves output to ./src/*

REM Set working directory to directory in which the .bat file was double-clicked.
CD %~dp0

ECHO Preparing to download from https://ddowiki.com
TIMEOUT 10

TIMEOUT /T 1 /NOBREAK > NUL
wget -q -nc -P src --no-check-certificate https://ddowiki.com/page/Update_22_named_items
ECHO Update 22...

TIMEOUT /T 1 /NOBREAK > NUL
wget -q -nc -P src --no-check-certificate https://ddowiki.com/page/Update_23_named_items
ECHO Update 23...

TIMEOUT /T 1 /NOBREAK > NUL
wget -q -nc -P src --no-check-certificate https://ddowiki.com/page/Update_24_named_items
ECHO Update 24...

TIMEOUT /T 1 /NOBREAK > NUL
wget -q -nc -P src --no-check-certificate https://ddowiki.com/page/Update_25_named_items
ECHO Update 25...

TIMEOUT /T 1 /NOBREAK > NUL
wget -q -nc -P src --no-check-certificate https://ddowiki.com/page/Update_26_named_items
ECHO Update 26...

TIMEOUT /T 1 /NOBREAK > NUL
wget -q -nc -P src --no-check-certificate https://ddowiki.com/page/Update_27_named_items
ECHO Update 27...

TIMEOUT /T 1 /NOBREAK > NUL
wget -q -nc -P src --no-check-certificate https://ddowiki.com/page/Update_28_named_items
ECHO Update 28...

TIMEOUT /T 1 /NOBREAK > NUL
wget -q -nc -P src --no-check-certificate https://ddowiki.com/page/Update_29_named_items
ECHO Update 29...

TIMEOUT /T 1 /NOBREAK > NUL
wget -q -nc -P src --no-check-certificate https://ddowiki.com/page/Update_30_named_items
ECHO Update 30...

TIMEOUT /T 1 /NOBREAK > NUL
wget -q -nc -P src --no-check-certificate https://ddowiki.com/page/Update_31_named_items
ECHO Update 31...

TIMEOUT /T 1 /NOBREAK > NUL
wget -q -nc -P src --no-check-certificate https://ddowiki.com/page/Update_32_named_items
ECHO Update 32...

TIMEOUT /T 1 /NOBREAK > NUL
wget -q -nc -P src --no-check-certificate https://ddowiki.com/page/Update_33_named_items
ECHO Update 33...

TIMEOUT /T 1 /NOBREAK > NUL
wget -q -nc -P src --no-check-certificate https://ddowiki.com/page/Update_34_named_items
ECHO Update 34...

TIMEOUT /T 1 /NOBREAK > NUL
wget -q -nc -P src --no-check-certificate https://ddowiki.com/page/Update_35_named_items
ECHO Update 35...

TIMEOUT /T 1 /NOBREAK > NUL
wget -q -nc -P src --no-check-certificate https://ddowiki.com/page/Update_36_named_items
ECHO Update 36...

TIMEOUT /T 1 /NOBREAK > NUL
wget -q -nc -P src --no-check-certificate https://ddowiki.com/page/Update_37_named_items
ECHO Update 37...

TIMEOUT /T 1 /NOBREAK > NUL
wget -q -nc -P src --no-check-certificate https://ddowiki.com/page/Update_38_named_items
ECHO Update 38...

TIMEOUT /T 1 /NOBREAK > NUL
wget -q -nc -P src --no-check-certificate https://ddowiki.com/page/Update_39_named_items
ECHO Update 39...

TIMEOUT /T 1 /NOBREAK > NUL
wget -q -nc -P src --no-check-certificate https://ddowiki.com/page/Update_40_named_items
ECHO Update 40...

TIMEOUT /T 1 /NOBREAK > NUL
wget -q -nc -P src --no-check-certificate https://ddowiki.com/page/Update_41_named_items
ECHO Update 41...

TIMEOUT /T 1 /NOBREAK > NUL
wget -q -nc -P src --no-check-certificate https://ddowiki.com/page/Update_42_named_items
ECHO Update 42...

TIMEOUT /T 1 /NOBREAK > NUL
wget -q -nc -P src --no-check-certificate https://ddowiki.com/page/Update_43_named_items
ECHO Update 43...

TIMEOUT /T 1 /NOBREAK > NUL
wget -q -nc -P src --no-check-certificate https://ddowiki.com/page/Update_44_named_items
ECHO Update 44...

TIMEOUT /T 1 /NOBREAK > NUL
wget -q -nc -P src --no-check-certificate https://ddowiki.com/page/Update_45_named_items
ECHO Update 45...

TIMEOUT /T 1 /NOBREAK > NUL
wget -q -nc -P src --no-check-certificate https://ddowiki.com/page/Update_46_named_items
ECHO Update 46...

TIMEOUT /T 1 /NOBREAK > NUL
wget -q -nc -P src --no-check-certificate https://ddowiki.com/page/Update_47_named_items
ECHO Update 47...


ECHO Downloads complete!
PAUSE
EXIT