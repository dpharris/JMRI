package jmri.jmrit.timetable;

import java.awt.GraphicsEnvironment;
import jmri.jmrit.timetable.swing.*;
import jmri.util.JUnitUtil;
import org.junit.*;

/**
 * Tests for the Stop Class
 * @author Dave Sand Copyright (C) 2018
 */
public class StopTest {

    @Test
    public void testCreate() {
        try {
            new Stop(0, 1);
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals(ex.getMessage(), "StopAddFail");  // NOI18N
        }
    }

    @Test
    public void testSettersAndGetters() {
        Layout layout = new Layout();
        int layoutId = layout.getLayoutId();
        TrainType type = new TrainType(layoutId);
        Segment segment = new Segment(layoutId);
        int segmentId = segment.getSegmentId();
        Station station = new Station(segmentId);
        int stationId = station.getStationId();
        Schedule schedule = new Schedule(layoutId);
        int scheduleId = schedule.getScheduleId();
        Train train = new Train (scheduleId);
        int trainId = train.getTrainId();

        Stop stop = new Stop(trainId, 1);
        Assert.assertTrue(stop.getStopId() > 0);
        Assert.assertTrue(stop.getTrainId() == trainId);
        stop.setSeq(2);
        Assert.assertEquals(2, stop.getSeq());
        stop.setStationId(stationId);
        Assert.assertTrue(stop.getStationId() == stationId);
        try {
            stop.setDuration(-2);
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals(ex.getMessage(), "StopDurationLt0");  // NOI18N
        }
        try {
            stop.setDuration(240);
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals(ex.getMessage(), "TimeOutOfRange");  // NOI18N
        }
        stop.setDuration(15);
        Assert.assertEquals(15, stop.getDuration());
        try {
            stop.setNextSpeed(-2);
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals(ex.getMessage(), "NextSpeedLt0");  // NOI18N
        }
        try {
            stop.setNextSpeed(1);
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals(ex.getMessage(), "TimeOutOfRange");  // NOI18N
        }
        stop.setNextSpeed(30);
        Assert.assertEquals(30, stop.getNextSpeed());
        stop.setArriveTime(600);
        Assert.assertEquals(600, stop.getArriveTime());
        stop.setDepartTime(630);
        Assert.assertEquals(630, stop.getDepartTime());
        try {
            stop.setStagingTrack(2);
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals(ex.getMessage(), "StagingRange");  // NOI18N
        }
        stop.setStagingTrack(0);
        Assert.assertEquals(0, stop.getStagingTrack());
        stop.setStopNotes("none");  // NOI18N
        Assert.assertEquals("none", stop.getStopNotes());  // NOI18N
        Assert.assertEquals("2 :: New Station", stop.toString());  // NOI18N
    }

    @Before
    public void setUp() {
        jmri.util.JUnitUtil.setUp();

        JUnitUtil.resetInstanceManager();
        JUnitUtil.resetProfileManager();
    }

    @After
    public void tearDown() {
        jmri.util.JUnitUtil.tearDown();
    }
}
