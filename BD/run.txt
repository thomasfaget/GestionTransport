@echo off
set fich=.
set jour=10
forfiles -p %fich% -s -m backup*.zip -d-%jour% -c "cmd /c del @FILE"
@exit