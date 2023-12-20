package entities;

import java.math.BigDecimal;

import org.junit.Test;

import com.hotelco.entities.Adjustment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for the Adjustment Class
 */
public class AdjustmentTest {
    Adjustment adjustment1 = new Adjustment();
    Adjustment adjustment2 = new Adjustment("Pillow underfluff credit", new BigDecimal("-25.50"));

    @Test
    public void testAdjustment(){
        assertNotNull(adjustment1);

    }

    // @Test
    // public void testAdjustment(String newComment, BigDecimal newAmount){
    //     assertEquals(-25.50, adjustment2.getAmount());
    //     assertEquals("Pillow underfluff credit", adjustment2.getComment());
    // }

    @Test
    public void testGetAmount() {
        assertEquals(new BigDecimal("-25.50"), adjustment2.getAmount());
    }

    @Test
    public void testGetComment() {
        assertEquals("Pillow underfluff credit", adjustment2.getComment());
    }

    @Test
    public void testSetAmount() {
       adjustment2.setAmount(new BigDecimal("47.31"));
       assertEquals(new BigDecimal("47.31"), adjustment2.getAmount());
    }

    @Test
    public void testSetComment() {
        adjustment2.setComment("Stolen towel");
        assertEquals("Stolen towel", adjustment2.getComment());
    }    
}
