SET @concept_id = 0;
SET @concept_name_short_id = 0;
SET @concept_name_full_id = 0;

call add_concept(@concept_id, @concept_name_short_id, @concept_name_full_id, 'Medication Administration Performer Function', 'Medication Administration Performer Function', 'Coded', 'Misc', false, 'Medication Administration Performer Function');
SET @parent_concept_id = @concept_id;

call add_concept(@concept_id, @concept_name_short_id, @concept_name_full_id, 'Performer', 'Performer', 'N/A', 'Misc', false, 'Performer role of Medication Administration');
INSERT INTO concept_answer (concept_id, answer_concept, date_created, creator, uuid, sort_weight) VALUES (@parent_concept_id, @concept_id, now(), 1, uuid(), 1);

call add_concept(@concept_id, @concept_name_short_id, @concept_name_full_id, 'Verifier', 'Verifier', 'N/A', 'Misc', false, 'Verifier role of Medication Administration');
INSERT INTO concept_answer (concept_id, answer_concept, date_created, creator, uuid, sort_weight) VALUES (@parent_concept_id, @concept_id, now(), 1, uuid(), 2);

call add_concept(@concept_id, @concept_name_short_id, @concept_name_full_id, 'Witness', 'Witness', 'N/A', 'Misc', false, 'Witness role of Medication Administration');
INSERT INTO concept_answer (concept_id, answer_concept, date_created, creator, uuid, sort_weight) VALUES (@parent_concept_id, @concept_id, now(), 1, uuid(), 3);
