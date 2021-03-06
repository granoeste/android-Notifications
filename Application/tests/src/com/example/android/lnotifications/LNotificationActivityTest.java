package com.example.android.lnotifications;

import android.support.v4.app.Fragment;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Unit tests for {@link LNotificationActivity}.
 */
public class LNotificationActivityTest extends
        ActivityInstrumentationTestCase2<LNotificationActivity> {

    private LNotificationActivity mActivity;

    public LNotificationActivityTest() {
        super(LNotificationActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = getActivity();
    }

    public void testPreconditions() {
        assertNotNull(String.format("%s is null", LNotificationActivity.class.getSimpleName()),
                mActivity);
    }

    public void testFirstTabInActionBarIsHeadsUpNotificationFragment() {
        mActivity.getSupportActionBar().setSelectedNavigationItem(0);
        getInstrumentation().waitForIdleSync();
        Fragment fragment = mActivity.getSupportFragmentManager().findFragmentById(R.id.container);
        assertTrue(fragment instanceof HeadsUpNotificationFragment);
    }

    public void testSecondtabInActionBarIsVisibilityMetadataFragment() {
        mActivity.getSupportActionBar().setSelectedNavigationItem(1);
        getInstrumentation().waitForIdleSync();
        Fragment fragment = mActivity.getSupportFragmentManager().findFragmentById(R.id.container);
        assertTrue(fragment instanceof VisibilityMetadataFragment);
    }

    public void testThirdtabInActionBarIsOtherMetadataFragment() {
        mActivity.getSupportActionBar().setSelectedNavigationItem(2);
        getInstrumentation().waitForIdleSync();
        Fragment fragment = mActivity.getSupportFragmentManager().findFragmentById(R.id.container);
        assertTrue(fragment instanceof OtherMetadataFragment);
    }
}
