# Project Task Manager
pgr203eksamen-UnstableCodebase created by GitHub Classroom
![Java CI with Maven](https://github.com/kristiania/pgr203eksamen-UnstableCodebase/workflows/Java%20CI%20with%20Maven/badge.svg)


Dette er vår Project Task manager som er en eksamensoppgave i PGR203 Avansert Java Høst 2020. 
Oppgavene er laget av: Fredrik, Andreas og Pernille. 
I Project Task Manager har du mulighet til å opprette et prosjektmedlem, en prosjektoppgave. Tildele oppgaver til et prosjektmedlem og sette status for oppgaven. Man har også mulighet til å endre status etter hvert som oppgaven er under behandling eller løst. Oppgaven ble sensurert til en c.  

# Programmet kjøres ved å:
Bygg og test Executable jar-fil 
1.	Kjør først en Maven clean
2.	Kjør Maven package for å opprette jar filen som skal kjøres
3.	Du må ha en konfigureringsfil som heter pgr203.properties, den må inneholde

- dataSource.url=
- dataSource.username=
- dataSource.password=

# Funksjonalitet:
Man kan besøke forside på localhos:8080/index.html hvor man kan velge mellom:

Add new project member
 - Her kan du legge inn et prosjektmedlem med navn, etternavn og e-postadresse

Project member list
 - Her får du listet opp prosjektmedlemmene som er opprettet

Add task
 - Her kan du opprettet en oppgave

Task list
 - Her får du listet opp oppgavene som er opprettet

Edit project task
 - Her kan du tildele en oppgave til et prosjekt medlem

List Members and assigned tasks
 - Denne viser hvilke oppgaver som er tildelt til prosjektmedlem

Edit status to task
 - Alle oppgavene er automatisk satt med en default to do og her kan du endre statusen på oppgaven
