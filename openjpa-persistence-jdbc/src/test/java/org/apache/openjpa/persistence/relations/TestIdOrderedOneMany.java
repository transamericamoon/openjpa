/*
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.openjpa.persistence.relations;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import org.apache.openjpa.persistence.OpenJPAEntityManager;

/**
 * Test ordering a one-many field on the primary key of the related entity.
 *
 * @author Abe White
 */
public class TestIdOrderedOneMany
    extends TestCase {

    private EntityManagerFactory emf;
    private long id;

    public void setUp() {
        Map props = new HashMap(System.getProperties());
        props.put("openjpa.MetaDataFactory", "jpa(Types=" 
            + IdOrderedOneManyParent.class.getName() + ";"
            + IdOrderedOneManyChild.class.getName() + ")");
        emf = Persistence.createEntityManagerFactory("test", props);

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        IdOrderedOneManyParent parent = new IdOrderedOneManyParent();
        parent.setName("parent");
        em.persist(parent);

        for (int i = 0; i < 3; i++) {
            IdOrderedOneManyChild explicit = new IdOrderedOneManyChild();
            explicit.setId(3 - i);
            explicit.setName("explicit" + explicit.getId());
            explicit.setExplicitParent(parent);
            parent.getExplicitChildren().add(explicit);
            em.persist(explicit);

            IdOrderedOneManyChild implicit = new IdOrderedOneManyChild();
            implicit.setId(100 - i);
            implicit.setName("implicit" + implicit.getId());
            implicit.setImplicitParent(parent);
            parent.getImplicitChildren().add(implicit);
            em.persist(implicit);
        }

        em.getTransaction().commit();
        id = parent.getId();
        em.close();
    }

    public void tearDown() {
        if (emf == null)
            return;
        try {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            em.createQuery("delete from IdOrderedOneManyChild").executeUpdate();
            em.createQuery("delete from IdOrderedOneManyParent").
                executeUpdate();
            em.getTransaction().commit();
            em.close();
            emf.close();
        } catch (Exception e) {
        }
    }

    public void testExplicitOrdering() {
        EntityManager em = emf.createEntityManager();
        IdOrderedOneManyParent parent = em.find(IdOrderedOneManyParent.class, 
            id);
        assertNotNull(parent);
        assertEquals("parent", parent.getName());
        assertEquals(3, parent.getExplicitChildren().size());
        for (int i = 0; i < 3; i++) {
            assertEquals(i + 1, parent.getExplicitChildren().get(i).getId());
            assertEquals("explicit" + (i + 1), parent.getExplicitChildren().
                get(i).getName());
        }
        em.close();
    }

    public void testImplicitOrdering() {
        EntityManager em = emf.createEntityManager();
        IdOrderedOneManyParent parent = em.find(IdOrderedOneManyParent.class, 
            id);
        assertNotNull(parent);
        assertEquals("parent", parent.getName());
        assertEquals(3, parent.getExplicitChildren().size());
        for (int i = 0; i < 3; i++) {
            assertEquals(i + 98, parent.getImplicitChildren().get(i).getId());
            assertEquals("implicit" + (i + 98), parent.getImplicitChildren().
                get(i).getName());
        }
        em.close();
    }

    public static void main(String[] args) {
        TestRunner.run(TestIdOrderedOneMany.class);
    }
}

