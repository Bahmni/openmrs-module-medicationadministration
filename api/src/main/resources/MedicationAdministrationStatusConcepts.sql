SET @concept_id = 0;
SET @concept_name_short_id = 0;
SET @concept_name_full_id = 0;
SET @concept_source_id = 0;
SET @concept_map_type_id = 0;

SELECT concept_source_id INTO @concept_source_id FROM concept_reference_source WHERE name = 'HL7-MedicationAdministrationStatus';
SELECT concept_map_type_id INTO @concept_map_type_id FROM concept_map_type WHERE name = 'SAME-AS';

call add_concept(@concept_id, @concept_name_short_id, @concept_name_full_id, 'Medication Administration Status', 'Medication Administration Status', 'Coded', 'Misc', false, 'Medication Administration Status');
SET @parent_concept_id = @concept_id;

call add_concept(@concept_id, @concept_name_short_id, @concept_name_full_id, 'MA-In Progress', 'In Progress', 'N/A', 'Misc', false, 'In Progress Status of Medication Administration');
call add_concept_reference_map (@concept_id, @concept_source_id, 'In Progress', 'in-progress', @concept_map_type_id);
INSERT INTO concept_answer (concept_id, answer_concept, date_created, creator, uuid, sort_weight) VALUES (@parent_concept_id, @concept_id, now(), 1, uuid(), 1);

call add_concept(@concept_id, @concept_name_short_id, @concept_name_full_id, 'MA-Not Done', 'Not Done', 'N/A', 'Misc', false, 'Not Done Status of Medication Administration');
call add_concept_reference_map (@concept_id, @concept_source_id, 'Not Done', 'not-done', @concept_map_type_id);
INSERT INTO concept_answer (concept_id, answer_concept, date_created, creator, uuid, sort_weight) VALUES (@parent_concept_id, @concept_id, now(), 1, uuid(), 2);

call add_concept(@concept_id, @concept_name_short_id, @concept_name_full_id, 'MA-On Hold', 'On Hold', 'N/A', 'Misc', false, 'On Hold Status of Medication Administration');
call add_concept_reference_map (@concept_id, @concept_source_id, 'On Hold', 'on-hold', @concept_map_type_id);
INSERT INTO concept_answer (concept_id, answer_concept, date_created, creator, uuid, sort_weight) VALUES (@parent_concept_id, @concept_id, now(), 1, uuid(), 3);

call add_concept(@concept_id, @concept_name_short_id, @concept_name_full_id, 'MA-Completed', 'Completed', 'N/A', 'Misc', false, 'Completed Status of Medication Administration');
call add_concept_reference_map (@concept_id, @concept_source_id, 'Completed', 'completed', @concept_map_type_id);
INSERT INTO concept_answer (concept_id, answer_concept, date_created, creator, uuid, sort_weight) VALUES (@parent_concept_id, @concept_id, now(), 1, uuid(), 4);

call add_concept(@concept_id, @concept_name_short_id, @concept_name_full_id, 'MA-Entered in Error', 'Entered in Error', 'N/A', 'Misc', false, 'Entered in Error Status of Medication Administration');
call add_concept_reference_map (@concept_id, @concept_source_id, 'Entered in Error', 'entered-in-error', @concept_map_type_id);
INSERT INTO concept_answer (concept_id, answer_concept, date_created, creator, uuid, sort_weight) VALUES (@parent_concept_id, @concept_id, now(), 1, uuid(), 5);

call add_concept(@concept_id, @concept_name_short_id, @concept_name_full_id, 'MA-Stopped', 'Stopped', 'N/A', 'Misc', false, 'Stopped Status of Medication Administration');
call add_concept_reference_map (@concept_id, @concept_source_id, 'Stopped', 'stopped', @concept_map_type_id);
INSERT INTO concept_answer (concept_id, answer_concept, date_created, creator, uuid, sort_weight) VALUES (@parent_concept_id, @concept_id, now(), 1, uuid(), 6);

call add_concept(@concept_id, @concept_name_short_id, @concept_name_full_id, 'MA-Unknown', 'Unknown', 'N/A', 'Misc', false, 'Unknown Status of Medication Administration');
call add_concept_reference_map (@concept_id, @concept_source_id, 'Unknown', 'unknown', @concept_map_type_id);
INSERT INTO concept_answer (concept_id, answer_concept, date_created, creator, uuid, sort_weight) VALUES (@parent_concept_id, @concept_id, now(), 1, uuid(), 7);

INSERT INTO fhir_concept_source (concept_source_id, url, name, creator, date_created, uuid)
VALUES (@concept_source_id, 'http://hl7.org/fhir/CodeSystem/medication-admin-status', 'HL7-MedicationAdministrationStatus', 1, now(), uuid());
