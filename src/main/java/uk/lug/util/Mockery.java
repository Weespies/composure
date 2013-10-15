package uk.lug.util;

import java.util.ArrayList;
import java.util.List;

public class Mockery {
    private static List<Object> mockedInterfaces = null;
    private static List<Object> mockedExtendedClasses = null;
    
    enum State { UNINITIALISED, SETUP, REPLAY, VERIFIED, TORNDOWN};
    private static State state = State.UNINITIALISED;
    
    static State getState() {
        return state;
    }
    
    
    /**
     * Return to uninitialised state.
     */
    public static void uninitialise() {
        mockedInterfaces=null;
        mockedExtendedClasses=null;
        state=State.UNINITIALISED;
    }

    /**
     * Setup the mockhandler.  1. Initialise list to hold mock objects and 2. create and setup() the
     * FacadeFixture.
     */
    public static void setUp() {
        if ( state!=State.UNINITIALISED && state!=State.TORNDOWN ) {
            throw new IllegalStateException("Cannot call setup when Mockery is state other than UNITIALISED or TORNDOWn.");
        } 
        mockedInterfaces = new ArrayList<Object>();
        mockedExtendedClasses = new ArrayList<Object>();
        state=State.SETUP;
    }
    
    /**
     * Clean this Mockery instance ready for re-use.
     */
    public static void tearDown() {
        mockedInterfaces.clear();
        mockedInterfaces=null;
        mockedExtendedClasses.clear();
        mockedExtendedClasses=null;
        System.gc();
        state = State.TORNDOWN;
        
    }
    
    /**
     * Replay all mocks in the following order: 
     * <ol><li>Mocked interfaces.
     * <li>Mocked class extensions.
     * <li>Mocked facades from the FacadeFixture</ol>
     */
    public static void replayAll() {
        if ( state==State.UNINITIALISED ) {
            throw new IllegalStateException("Cannot replay without a prior call to setUp() . ");
        } else if ( state==State.REPLAY ) {
            throw new IllegalStateException("Mockery is already in replay() state.");
        } else if ( state==State.VERIFIED ) {
            throw new IllegalStateException("Mockery is already in replay() state.");
        } else if ( state==State.TORNDOWN ) {
            throw new IllegalStateException("Cannot replay because Mockery has been torn down.");
        }
        for ( Object obj : mockedInterfaces ) {
            org.easymock.EasyMock.replay( obj );
        }
        for ( Object obj : mockedExtendedClasses ) {
            org.easymock.classextension.EasyMock.replay( obj );
        }
        state=State.REPLAY;
    }
    
    /**
     * Verify all mocks in the following order: 
     * <ol><li>Mocked interfaces.
     * <li>Mocked class extensions.
     * <li>Mocked facades from the FacadeFixture</ol>
     */
    public static void verifyAll() {
        if ( state!=State.REPLAY ) {
            throw new IllegalStateException("Cannot verify when Mockery is not in replay state. ");
        }
        for ( Object obj : mockedInterfaces ) {
            org.easymock.EasyMock.verify( obj );
        }
        for ( Object obj : mockedExtendedClasses ) {
            org.easymock.classextension.EasyMock.verify( obj );
        }
        state = State.VERIFIED;
    }
    
    /**
     * Reset all mocks in the following order :
     * <ol><li>Mocked interfaces.
     * <li>Mocked class extensions.
     * <li>Mocked facades from the FacadeFixture</ol>
     */
    public static void resetAll() {
        if ( state!=State.VERIFIED ) {
            throw new IllegalStateException("Cannot reset when Mockery is not in verified state. ");
        }

        for ( Object obj : mockedInterfaces ) {
            org.easymock.EasyMock.reset( obj );
        }
        for ( Object obj : mockedExtendedClasses ) {
            org.easymock.classextension.EasyMock.reset( obj );
        }
        
        state = State.VERIFIED;
    }
        
    /**
     * Create a mock for an interface or a class extension.
     * @param clazz
     * @return
     */
    public static <T> T issueMock(Class<T>  clazz ) {
        if ( state!=State.SETUP ) {
            throw new IllegalStateException("Mockery can only create mocks after setUp() and before replay().");
        }
        T mock;
        if ( clazz.isInterface() ) {
            mock = org.easymock.EasyMock.createMock( clazz );
            mockedInterfaces.add( mock );
        } else {
            mock = org.easymock.classextension.EasyMock.createMock( clazz );
            mockedExtendedClasses.add( mock );
        }
        return mock;
    }
        
    /**
     * Create a mock for an interface or a class extension.
     * @param clazz
     * @return
     */
    public static <T> T issueStrictMock(Class<T>  clazz ) {
        if ( state!=State.SETUP ) {
            throw new IllegalStateException("Mockery can only create mocks after setUp() and before replay().");
        }
        T mock;
        if ( clazz.isInterface() ) {
            mock = org.easymock.EasyMock.createStrictMock( clazz );
            mockedInterfaces.add( mock );
        } else {
            mock = org.easymock.classextension.EasyMock.createStrictMock( clazz );
            mockedExtendedClasses.add( mock );
        }
        return mock;
    }

    public static void doNotVerify( Object obj ) {
        mockedExtendedClasses.remove(obj);
        mockedInterfaces.remove(obj);
    }
    
}
