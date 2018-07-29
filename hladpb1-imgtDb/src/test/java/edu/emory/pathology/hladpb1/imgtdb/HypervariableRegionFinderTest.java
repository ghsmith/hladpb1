/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.emory.pathology.hladpb1.imgtdb;

import edu.emory.pathology.hladpb1.imgtdb.data.HypervariableRegion;
import edu.emory.pathology.hladpb1.imgtdb.data.HypervariableRegionVariant;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ghsmith
 */
public class HypervariableRegionFinderTest {
    
    public HypervariableRegionFinderTest() {
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

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    
    @Test
    public void getHypervariableRegionList() throws JAXBException {
        HypervariableRegionFinder hypervariableRegionFinder = new HypervariableRegionFinder("10");
        List<HypervariableRegion> hypervariableRegionList = hypervariableRegionFinder.getHypervariableRegionList();
        /*for(HypervariableRegion hvRegion : hypervariableRegionList) {
            System.out.println(hvRegion.getHypervariableRegionName());
            for(Integer codon : hvRegion.getCodonNumberList()) {
                System.out.println(codon);
            }
            for(HypervariableRegionVariant hvrVariant : hvRegion.getVariantMap().values()) {
                System.out.println(hvrVariant.getVariantId());
                System.out.println(hvrVariant.getProteinSequenceList());
                System.out.println(hvrVariant.getBeadAlleleNameList());
            }
        }*/
    }
    
}
