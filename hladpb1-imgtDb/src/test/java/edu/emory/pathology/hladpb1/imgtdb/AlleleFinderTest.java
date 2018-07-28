/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.emory.pathology.hladpb1.imgtdb;

import edu.emory.pathology.hladpb1.imgtdb.data.Allele;
import edu.emory.pathology.hladpb1.imgtdb.data.HypervariableRegion;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ghsmith
 */
public class AlleleFinderTest {
    
    public AlleleFinderTest() {
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
    public void getAlleleList() throws JAXBException {
        AlleleFinder alleleFinder = new AlleleFinder();
        List<Allele> alleleList = alleleFinder.getAlleleList();
        assertTrue(alleleList.size() > 0);
        System.out.println();
        alleleList.stream().forEach((allele) -> {
            System.out.print(String.format("%-25s: ", allele.getAlleleName()));
            for(int i = 1; i <= 100; i++) {
                System.out.print(allele.getCodonMap().get(i) != null ? allele.getCodonMap().get(i).getAminoAcid() : "*");
            }
            System.out.println();
        });
    }

    @Test
    public void assignHypervariableRegionVariantIds() throws JAXBException {
        AlleleFinder alleleFinder = new AlleleFinder();
        List<Allele> alleleList = alleleFinder.getAlleleList();
        HypervariableRegionFinder hypervariableRegionFinder = new HypervariableRegionFinder();
        List<HypervariableRegion> hypervariableRegionList = hypervariableRegionFinder.getHypervariableRegionList();
        alleleFinder.assignHypervariableRegionVariantIds(hypervariableRegionList);
        alleleList.stream().forEach((allele) -> {
            System.out.print(String.format("%-25s %-20s: ", allele.getAlleleName(), allele.getHvrVariantMap().values()));
            for(int i = 1; i <= 100; i++) {
                System.out.print(allele.getCodonMap().get(i) != null ? allele.getCodonMap().get(i).getAminoAcid() : "*");
            }
            System.out.println();
        });
    }
    
}
