# openmrs-module-medicationadministration
OpenMRS module for Medication Administration FHIR REST API

OpenMRS Module Medication Administration
=================================
This repository handles DB modelling and FHIR translations for Medication Administration. 
This is a temporary Bahmni module and will be merged with openmrs-core and openmrs-module-fhir2 in future.

## Packaging
```mvn clean package```

### Prerequisite
    JDK 1.8

## Deploy

Copy ```openmrs-module-medicationadministration/omod/target/medication-administration-1.0.0-SNAPSHOT.omod``` 
into OpenMRS modules directory and restart OpenMRS