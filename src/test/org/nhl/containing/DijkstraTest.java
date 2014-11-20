/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nhl.containing;

import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Matthijs
 */
public class DijkstraTest {
    
    public DijkstraTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of computePaths method, of class Dijkstra.
     */
    @Test
    public void testComputePaths() {
        System.out.println("computePaths");
        Vertex source = null;
        Dijkstra.computePaths(source);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getShortestPathTo method, of class Dijkstra.
     */
    @Test
    public void testGetShortestPathTo() {
        System.out.println("getShortestPathTo");
        Vertex target = null;
        List expResult = null;
        List result = Dijkstra.getShortestPathTo(target);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of main method, of class Dijkstra.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        Dijkstra.main(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}