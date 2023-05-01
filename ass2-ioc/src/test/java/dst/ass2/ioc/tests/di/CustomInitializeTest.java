package dst.ass2.ioc.tests.di;

import dst.ass2.ioc.di.IObjectContainer;
import dst.ass2.ioc.di.IObjectContainerFactory;
import dst.ass2.ioc.di.annotation.Component;
import dst.ass2.ioc.di.annotation.Initialize;
import dst.ass2.ioc.di.impl.ObjectContainerFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CustomInitializeTest {

    private IObjectContainerFactory factory;
    private IObjectContainer container;
    private static long value;
    private static long second_value;
    private static String setupOrder;

    @Before
    public void setUp() throws Exception {
        factory = new ObjectContainerFactory();
        container = factory.newObjectContainer(new Properties());
        value = 0L;
        second_value = 0L;
        setupOrder = "";
    }

    @Component
    public static class ParentInitializer {
        @Initialize
        private void setup() {
            value++;
            setupOrder += "parent_class,";
        }
    }

    @Component
    public static class ChildInitializer extends ParentInitializer {

        @Initialize
        private void setup() {
            value++;
            setupOrder += "first_child_setup";
        }

        @Initialize
        private void post_setup() {
            second_value = 1L;
            setupOrder += "second_child_setup";

        }

    }

    @Test
    public void getObject_correctlyCallsAllInitializers() throws Exception {
        container.getObject(ChildInitializer.class);

        assertEquals(2L, value);
        assertEquals(1L, second_value);
    }

    @Test
    public void getObject_correctlyInitializesParentBeforeChildren() throws Exception {
        container.getObject(ChildInitializer.class);
        var splitSetupOrder = setupOrder.split(",");

        assertEquals("parent_class", splitSetupOrder[0]);
        assertTrue(splitSetupOrder[2].contains("first_child_setup") &&
                splitSetupOrder[2].contains("second_child_setup"));
    }

}
