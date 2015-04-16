package com.osu.tatoczenko.foodfinder;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.test.ActivityInstrumentationTestCase2;


/**
 * Created by tyler_cunnington on 4/15/15.
 */
public class ObjFragmentTest extends ActivityInstrumentationTestCase2<TestFragmentActivity> {

    private TestFragmentActivity mActivity;

    public ObjFragmentTest() {

        super(TestFragmentActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = getActivity();
    }

    private Fragment startFragment(Fragment fragment) {

        FragmentTransaction transaction = mActivity.getFragmentManager().beginTransaction();
        transaction.add(R.id.activity_test_fragment_linearlayout, fragment, "tag");
        transaction.commit();
        getInstrumentation().waitForIdleSync();
        Fragment frag = mActivity.getFragmentManager().findFragmentByTag("tag");

        return frag;


    }

    public void testFragment() {

        Fragment newFrag = new Fragment();
        Fragment testFrag=startFragment(newFrag);
        assertNotNull(testFrag);

        }


}










