package com.team766.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.team766.tests.GearCollectorTest;
import com.team766.tests.HopperTest;

@RunWith(Suite.class)
@SuiteClasses({HopperTest.class, GearCollectorTest.class, ArchitectureTest.class, HDriveTest.class, ClimberTest.class, DriveTest.class})
public class AllTests {

}