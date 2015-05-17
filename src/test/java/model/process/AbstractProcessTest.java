package model.process;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Observable;
import java.util.Observer;

@SuppressWarnings({"unused", "AnonymousInnerClassMayBeStatic"})
public class AbstractProcessTest implements Observer {
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    private boolean isUpdated;

    @Test
    public void testSpreadUpdate() {
        AbstractProcess abstractProcess = new AbstractProcess() {
            @Override
            public void reset() {
            }
        };
        abstractProcess.addObserver(this);
        isUpdated = false;
        abstractProcess.spreadUpdate();
        Assert.assertTrue(isUpdated);
    }

    @Override
    public void update(Observable o, Object arg) {
        isUpdated = true;
    }
}
