package net.sf.marineapi.nmea.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import net.sf.marineapi.nmea.sentence.RMBSentence;
import net.sf.marineapi.nmea.util.CompassPoint;
import net.sf.marineapi.nmea.util.DataStatus;
import net.sf.marineapi.nmea.util.Direction;
import net.sf.marineapi.nmea.util.Waypoint;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the RMB sentence parser.
 * 
 * @author Kimmo Tuukkanen
 */
public class RMBTest {

    /** Example sentence */
    public static final String EXAMPLE = "$GPRMB,A,0.00,R,,RUSKI,5536.200,N,01436.500,E,432.3,234.9,,V*58";

    private RMBSentence rmb;

    /**
     * setUp
     */
    @Before
    public void setUp() {
        try {
            rmb = new RMBParser(EXAMPLE);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMBParser#getArrivalStatus()}.
     */
    @Test
    public void testArrivalStatus() {

        assertEquals(DataStatus.VALID, rmb.getArrivalStatus());
        assertTrue(rmb.hasArrived());

        rmb.setArrivalStatus(DataStatus.INVALID);
        assertEquals(DataStatus.INVALID, rmb.getArrivalStatus());
        assertFalse(rmb.hasArrived());

        rmb.setArrivalStatus(DataStatus.VALID);
        assertEquals(DataStatus.VALID, rmb.getArrivalStatus());
        assertTrue(rmb.hasArrived());
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMBParser#getBearing()} .
     */
    @Test
    public void testGetBearing() {
        assertEquals(234.9, rmb.getBearing(), 0.001);
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMBParser#getCrossTrackError()}.
     */
    @Test
    public void testGetCrossTrackError() {
        assertEquals(0.0, rmb.getCrossTrackError(), 0.001);
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMBParser#getDestination()} .
     */
    @Test
    public void testGetDestination() {
        final String id = "RUSKI";
        final double lat = 55 + (36.200 / 60);
        final double lon = 14 + (36.500 / 60);

        Waypoint wp = rmb.getDestination();
        assertNotNull(wp);
        assertEquals(id, wp.getId());
        assertEquals(lat, wp.getLatitude(), 0.0000001);
        assertEquals(lon, wp.getLongitude(), 0.0000001);
        assertEquals(CompassPoint.NORTH, wp.getLatHemisphere());
        assertEquals(CompassPoint.EAST, wp.getLonHemisphere());
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMBParser#getOriginId()}.
     */
    @Test
    public void testGetOriginId() {
        // FIXME test data should contain ID
        try {
            assertEquals("", rmb.getOriginId());
            fail("Did not throw ParseException");
        } catch (Exception e) {
            assertTrue(e instanceof DataNotAvailableException);
        }
    }

    /**
     * Test method for {@link net.sf.marineapi.nmea.parser.RMBParser#getRange()}
     * .
     */
    @Test
    public void testGetRange() {
        assertEquals(432.3, rmb.getRange(), 0.001);
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMBParser#getStatus()}.
     */
    @Test
    public void testGetStatus() {
        assertEquals(DataStatus.INVALID, rmb.getStatus());
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMBParser#getSteerTo()}.
     */
    @Test
    public void testGetSteerTo() {
        assertEquals(Direction.RIGHT, rmb.getSteerTo());
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMBParser#getVelocity()}.
     */
    @Test
    public void testGetVelocity() {
        // FIXME test data should contain velocity
        try {
            assertEquals(0.0, rmb.getVelocity(), 0.001);
            fail("Did not throw ParseException");
        } catch (Exception e) {
            assertTrue(e instanceof DataNotAvailableException);
        }
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMBParser#setBearing(double)} .
     */
    @Test
    public void testSetBearing() {
        rmb.setBearing(180.0);
        assertTrue(rmb.toString().contains(",180.0,"));
        assertEquals(180.0, rmb.getBearing(), 0.001);
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMBParser#setBearing(double)} .
     */
    @Test
    public void testSetBearingWithNegativeValue() {
        try {
            rmb.setBearing(-0.001);
            fail("Did not throw exception");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("0..360"));
        }
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMBParser#setBearing(double)} .
     */
    @Test
    public void testSetBearingWithValueGreaterThanAllowed() {
        try {
            rmb.setBearing(360.001);
            fail("Did not throw exception");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("0..360"));
        }
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMBParser#setCrossTrackError(double)}
     * .
     */
    @Test
    public void testSetCrossTrackError() {
        rmb.setCrossTrackError(1.111);
        assertTrue(rmb.toString().contains(",1.111,"));
        assertEquals(1.111, rmb.getCrossTrackError(), 0.001);
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMBParser#setDestination(Waypoint)} .
     */
    @Test
    public void testSetDestination() {

        final String id = "MYDEST";
        final double lat = 61 + (1.111 / 60);
        final double lon = 27 + (7.777 / 60);
        Waypoint d = new Waypoint(id, lat, CompassPoint.NORTH, lon,
                CompassPoint.EAST);

        rmb.setDestination(d);

        String str = rmb.toString();
        Waypoint wp = rmb.getDestination();

        assertTrue(str.contains(",MYDEST,6101.111,N,02707.777,E,"));
        assertNotNull(wp);
        assertEquals(id, wp.getId());
        assertEquals(lat, wp.getLatitude(), 0.0000001);
        assertEquals(lon, wp.getLongitude(), 0.0000001);
        assertEquals(CompassPoint.NORTH, wp.getLatHemisphere());
        assertEquals(CompassPoint.EAST, wp.getLonHemisphere());
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMBParser#setOriginId(String)}.
     */
    @Test
    public void testSetOriginId() {
        rmb.setOriginId("ORIGIN");
        assertTrue(rmb.toString().contains(",ORIGIN,RUSKI,"));
        assertEquals("ORIGIN", rmb.getOriginId());
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMBParser#setRange(double)} .
     */
    @Test
    public void testSetRange() {
        rmb.setRange(12.345);
        assertTrue(rmb.toString().contains(",12.345,"));
        assertEquals(12.345, rmb.getRange(), 0.001);
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMBParser#setStatus(DataStatus)}.
     */
    @Test
    public void testSetStatus() {
        rmb.setStatus(DataStatus.VALID);
        assertEquals(DataStatus.VALID, rmb.getStatus());
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMBParser#setSteerTo(CompassPoint)}.
     */
    @Test
    public void testSetSteerTo() {
        rmb.setSteerTo(Direction.LEFT);
        assertTrue(rmb.toString().contains(",L,"));
        assertEquals(Direction.LEFT, rmb.getSteerTo());
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMBParser#setSteerTo(CompassPoint)}.
     */
    @Test
    public void testSetSteerToWithNull() {
        try {
            rmb.setSteerTo(null);
            fail("Did not throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("LEFT or RIGHT"));
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMBParser#setVelocity()}.
     */
    @Test
    public void testSetVelocity() {
        rmb.setVelocity(40.5);
        assertTrue(rmb.toString().contains(",40.5,"));
        assertEquals(40.5, rmb.getVelocity(), 0.001);
    }

    /**
     * Test method for
     * {@link net.sf.marineapi.nmea.parser.RMBParser#setVelocity()}.
     */
    @Test
    public void testSetVelocityWithNegativeValue() {
        final double v = -0.123;
        rmb.setVelocity(v);
        assertTrue(rmb.toString().contains("," + v + ","));
        assertEquals(v, rmb.getVelocity(), 0.01);
    }

}
