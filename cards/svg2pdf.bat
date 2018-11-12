@echo off

rmdir /s /q pdf
mkdir pdf
for /R %%a IN ("*.svg") do (
echo %%~na
inkscape -f %%~na.svg -A pdf/%%~na.pdf
)
pause