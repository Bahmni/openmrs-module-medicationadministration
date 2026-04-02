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

/**
 * Standalone integration test — no Spring application context — verifying that
 * {@link MedicationAdministration}, together with its cascaded
 * {@link MedicationAdministrationPerformer} and {@link MedicationAdministrationNote} collections,
 * can be persisted and reloaded through Hibernate using the module's HBM XML mappings.
 *
 * <h3>Why standalone?</h3>
 * The OpenMRS test application context (loaded by {@code BaseContextSensitiveTest}) scans
 * {@code org.openmrs.*} and instantiates all fhir2-api {@code @Component} beans. Those beans
 * depend on {@code SearchQueryInclude<T>} generic-typed beans that are only available in the full
 * FHIR2 OMOD context (not in an isolated test classpath). Bootstrapping the Hibernate
 * {@link SessionFactory} directly — using Spring's {@link LocalSessionFactoryBean} for its JPA
 * package-scan capability — sidesteps that issue entirely without modifying any production bean
 * definitions.
 *
 * <h3>Schema and test data</h3>
 * A fresh H2 in-memory database is created once per test class. The URL includes
 * {@code REFERENTIAL_INTEGRITY=FALSE} so that stub FK references ({@code User} and
 * {@code Provider} proxies backed by hard-coded IDs) can be written without seeding the entire
 * OpenMRS reference table set. {@code hbm2ddl.auto=create} generates the full schema from the
 * combined HBM and JPA entity mappings automatically.
 *
 * <h3>Round-trip being tested</h3>
 * <ol>
 *   <li>Build entity graph (parent + one performer + one note).</li>
 *   <li>Persist via {@code session.save()} — {@code cascade="all"} in the HBM XML saves the
 *       children.</li>
 *   <li>Flush SQL to the DB and clear the first-level cache.</li>
 *   <li>Reload by generated PK and assert every mapped field.</li>
 * </ol>
 */
public class MedicationAdministrationHibernateIntegrationTest {

	private static SessionFactory sessionFactory;

	@BeforeClass
	public static void buildSessionFactory() throws Exception {
		/*
		 * H2 in-memory data source.  REFERENTIAL_INTEGRITY=FALSE lets us use
		 * Hibernate proxy stubs for User and Provider without inserting those rows.
		 */
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource(
		        new org.h2.Driver(),
		        "jdbc:h2:mem:med_admin_mapping_test;DB_CLOSE_DELAY=-1;REFERENTIAL_INTEGRITY=FALSE",
		        "sa", "");

		/*
		 * LocalSessionFactoryBean gives us Spring's packagesToScan capability so that
		 * JPA-annotated OpenMRS entities (Encounter, Allergy, Condition …) are
		 * registered alongside the HBM-mapped ones — exactly as the production
		 * HibernateSessionFactoryBean does in TestingApplicationContext.xml.
		 * No Spring application context is started, so no fhir2 service beans are
		 * instantiated and no FHIR2 infrastructure is needed.
		 */
		LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();
		factoryBean.setDataSource(dataSource);

		// Core OpenMRS HBM mappings (Patient, Drug, Order, Concept, User, Provider …)
		factoryBean.setConfigLocations(
		        new org.springframework.core.io.ClassPathResource("hibernate.cfg.xml"));

		// Module-specific mappings under test
		factoryBean.setMappingResources(
		        "MedicationAdministration.hbm.xml",
		        "MedicationAdministrationPerformer.hbm.xml",
		        "MedicationAdministrationNote.hbm.xml");

		// JPA-annotated OpenMRS entities (Encounter, Allergy, Condition …)
		factoryBean.setPackagesToScan("org.openmrs");

		Properties hibernateProps = new Properties();
		hibernateProps.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		hibernateProps.setProperty("hibernate.hbm2ddl.auto", "create");
		hibernateProps.setProperty("hibernate.show_sql", "false");
		hibernateProps.setProperty("hibernate.cache.use_second_level_cache", "false");
		hibernateProps.setProperty("hibernate.cache.use_query_cache", "false");
		hibernateProps.setProperty("hibernate.search.autoregister_listeners", "false");
		// Required for OpenMRS identity-generator mappings (Hibernate 5 default changed)
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
			// ── Stub FK references ─────────────────────────────────────────────────
			// session.load() returns a Hibernate proxy carrying the given PK.
			// Hibernate writes only the PK to the FK column; it never loads the actual
			// row, so these stubs work even though the user / provider tables are empty
			// (REFERENTIAL_INTEGRITY=FALSE on the H2 URL).
			User creator = session.load(User.class, 1);
			Provider provider = session.load(Provider.class, 1);

			// ── Build performer ────────────────────────────────────────────────────
			MedicationAdministrationPerformer performer = new MedicationAdministrationPerformer();
			performer.setActor(provider);
			performer.setCreator(creator);
			performer.setDateCreated(new Date());
			performer.setVoided(false);

			// ── Build note ─────────────────────────────────────────────────────────
			MedicationAdministrationNote note = new MedicationAdministrationNote();
			note.setAuthor(provider);
			note.setRecordedTime(new Date());
			note.setText("Patient tolerated medication well");
			note.setCreator(creator);
			note.setDateCreated(new Date());
			note.setVoided(false);

			// ── Build parent entity ────────────────────────────────────────────────
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

			// ── Persist (cascade="all" saves performer and note automatically) ─────
			Integer savedId = (Integer) session.save(medAdmin);

			// Flush SQL to the DB, then evict the first-level cache so the subsequent
			// get() must issue a SELECT rather than returning the in-memory instance.
			session.flush();
			session.clear();

			// ── Reload from the database ───────────────────────────────────────────
			MedicationAdministration retrieved =
			        session.get(MedicationAdministration.class, savedId);

			// ── Assert: top-level scalar fields ────────────────────────────────────
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

			// ── Assert: performers collection (one-to-many, cascade="all") ─────────
			assertNotNull("Performers collection must not be null after reload",
			    retrieved.getPerformers());
			assertEquals("Exactly one performer must be reloaded",
			    1, retrieved.getPerformers().size());
			MedicationAdministrationPerformer retrievedPerformer =
			        retrieved.getPerformers().iterator().next();
			assertNotNull("Performer PK must be assigned by the identity generator",
			    retrievedPerformer.getMedicationAdministrationPerformerId());
			// getProviderId() reads the PK from the Hibernate proxy identity without
			// triggering a DB load, confirming the actor_id FK column was written correctly.
			assertEquals("Performer actor_id FK must match the stub provider id",
			    Integer.valueOf(1), retrievedPerformer.getActor().getProviderId());

			// ── Assert: notes collection (one-to-many, cascade="all") ─────────────
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
