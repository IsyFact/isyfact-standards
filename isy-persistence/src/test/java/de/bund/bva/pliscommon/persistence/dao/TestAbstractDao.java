/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 * licenses this file to you under the Apache License, Version 2.0 (the
 * License). You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package de.bund.bva.pliscommon.persistence.dao;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.junit.Test;

import de.bund.bva.pliscommon.persistence.dao.test.TestDao;
import de.bund.bva.pliscommon.persistence.dao.test.TestEntity;

public class TestAbstractDao {

	@Test
	public void testConstructor() {
		AbstractDao dao = new TestDao();
		assertEquals(TestEntity.class, dao.getPersistentClass());
	}
	
	@Test
	public void testSpeichere(){
		TestDao dao = new TestDao();
		EntityManager manager = mock(EntityManager.class);
		dao.setEntityManager(manager);
		assertEquals(manager, dao.getEntityManager());
		TestEntity entity = new TestEntity();
		dao.speichere(entity);
		verify(manager, times(1)).persist(entity);
	}
	
	@Test
	public void testLoesche(){
		TestDao dao = new TestDao();
		EntityManager manager = mock(EntityManager.class);
		dao.setEntityManager(manager);
		assertEquals(manager, dao.getEntityManager());
		TestEntity entity = new TestEntity();
		dao.loesche(entity);
		verify(manager, times(1)).remove(entity);
	}
	
	@Test
	public void testSucheMitId(){
		TestDao dao = new TestDao();
		EntityManager manager = mock(EntityManager.class);		
		dao.setEntityManager(manager);
		assertEquals(manager, dao.getEntityManager());
		UUID id = UUID.randomUUID();		
		TestEntity entity = new TestEntity(id, "wert");
		when(manager.find(TestEntity.class,id)).thenReturn(entity);
		assertEquals(entity, dao.sucheMitId(id));
		
	}
	
	@Test
	public void testGetSingleOptionalResult(){
		TestEntity entity = new TestEntity();
		TestDao dao = new TestDao();
		TypedQuery<TestEntity> query = mock(TypedQuery.class);
		when(query.getSingleResult()).thenReturn(entity);
		assertEquals(entity, dao.getSingleOptionalResult(query));
	}
	
	@Test
	public void testGetSingleOptionalResultNoResult(){
		TestDao dao = new TestDao();
		TypedQuery<TestEntity> query = mock(TypedQuery.class);
		when(query.getSingleResult()).thenThrow(NoResultException.class);
		assertEquals(null, dao.getSingleOptionalResult(query));
	}

}
