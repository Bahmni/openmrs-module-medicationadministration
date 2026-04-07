/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.fhir2.apiext.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.module.ipd.api.model.MedicationAdministration;
import org.openmrs.module.ipd.api.model.MedicationAdministrationNote;
import org.openmrs.module.ipd.api.model.MedicationAdministrationPerformer;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

public class MedicationAdministrationHibernateIntegrationTest {

	private static SessionFactory sessionFactory;

	@BeforeClass
	public static void buildSessionFactory() throws Exception {
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource(
		        new org.h2.Driver(),
		        "jdbc:h2:mem:med_admin_mapping_test;DB_CLOSE_DELAY=-1;REFERENTIAL_INTEGRITY=FALSE",
		        "sa", "");

		LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();
		factoryBean.setDataSource(dataSource);

		factoryBean.setConfigLocations(
		        new org.springframework.core.io.ClassPathResource("hibernate.cfg.xml"));

		factoryBean.setMappingResources(
		        "MedicationAdministration.hbm.xml",
		        "MedicationAdministrationPerformer.hbm.xml",
		        "MedicationAdministrationNote.hbm.xml");

		factoryBean.setPackagesToScan("org.openmrs");

		Properties hibernateProps = new Properties();
		hibernateProps.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		hibernateProps.setProperty("hibernate.hbm2ddl.auto", "create");
		hibernateProps.setProperty("hibernate.show_sql", "false");
		hibernateProps.setProperty("hibernate.cache.use_second_level_cache", "false");
		hibernateProps.setProperty("hibernate.cache.use_query_cache", "false");
		hibernateProps.setProperty("hibernate.search.autoregister_listeners", "false");
		hibernateProps.setProperty("hibernate.id.new_generator_mappings", "false");
		factoryBean.setHibernateProperties(hibernateProps);

		factoryBean.afterPropertiesSet();
		sessionFactory = factoryBean.getObject();
	}

	@AfterClass
	public static void closeSessionFactory() {
		if (sessionFactory != null) {
			sessionFactory.close();
		}
	}

	@Test
	public void shouldPersistAndReloadMedicationAdministrationWithPerformersAndNotes() {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		try {
			User creator = session.load(User.class, 1);
			Provider provider = session.load(Provider.class, 1);

			MedicationAdministrationPerformer performer = new MedicationAdministrationPerformer();
			performer.setActor(provider);
			performer.setCreator(creator);
			performer.setDateCreated(new Date());
			performer.setVoided(false);

			MedicationAdministrationNote note = new MedicationAdministrationNote();
			note.setAuthor(provider);
			note.setRecordedTime(new Date());
			note.setText("Patient tolerated medication well");
			note.setCreator(creator);
			note.setDateCreated(new Date());
			note.setVoided(false);

			MedicationAdministration medAdmin = new MedicationAdministration();
			medAdmin.setStatus(
			    org.hl7.fhir.r4.model.MedicationAdministration.MedicationAdministrationStatus.COMPLETED);
			medAdmin.setAdministeredDateTime(new Date());
			medAdmin.setDose(500.0);
			medAdmin.setDosingInstructions("Take with food");
			medAdmin.setPerformers(new HashSet<>(Collections.singleton(performer)));
			medAdmin.setNotes(new HashSet<>(Collections.singleton(note)));
			medAdmin.setCreator(creator);
			medAdmin.setDateCreated(new Date());
			medAdmin.setVoided(false);

			Integer savedId = (Integer) session.save(medAdmin);

			session.flush();
			session.clear();

			MedicationAdministration retrieved =
			        session.get(MedicationAdministration.class, savedId);

			assertNotNull("Reloaded entity must not be null", retrieved);
			assertNotNull("UUID must have been auto-generated and persisted",
			    retrieved.getUuid());
			assertEquals(
			    "Status enum must survive the COMPLETED → VARCHAR → COMPLETED round-trip",
			    org.hl7.fhir.r4.model.MedicationAdministration.MedicationAdministrationStatus.COMPLETED,
			    retrieved.getStatus());
			assertEquals("Dose round-trip failed", 500.0, retrieved.getDose(), 0.001);
			assertEquals("DosingInstructions round-trip failed",
			    "Take with food", retrieved.getDosingInstructions());

			assertNotNull("Performers collection must not be null after reload",
			    retrieved.getPerformers());
			assertEquals("Exactly one performer must be reloaded",
			    1, retrieved.getPerformers().size());
			MedicationAdministrationPerformer retrievedPerformer =
			        retrieved.getPerformers().iterator().next();
			assertNotNull("Performer PK must be assigned by the identity generator",
			    retrievedPerformer.getMedicationAdministrationPerformerId());
			assertEquals("Performer actor_id FK must match the stub provider id",
			    Integer.valueOf(1), retrievedPerformer.getActor().getProviderId());

			assertNotNull("Notes collection must not be null after reload",
			    retrieved.getNotes());
			assertEquals("Exactly one note must be reloaded",
			    1, retrieved.getNotes().size());
			MedicationAdministrationNote retrievedNote =
			        retrieved.getNotes().iterator().next();
			assertNotNull("Note PK must be assigned by the identity generator",
			    retrievedNote.getMedicationAdministrationNoteId());
			assertEquals("Note text must survive the round-trip",
			    "Patient tolerated medication well", retrievedNote.getText());
			assertEquals("Note author_id FK must match the stub provider id",
			    Integer.valueOf(1), retrievedNote.getAuthor().getProviderId());

			tx.commit();
		}
		catch (Exception e) {
			tx.rollback();
			throw e;
		}
		finally {
			session.close();
		}
	}
}
