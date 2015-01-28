call mvn clean install 
call mvn clean install -P license,executable -Dlicense.excludedGroups=org.paxml -Dmaven.test.skip=true
pause