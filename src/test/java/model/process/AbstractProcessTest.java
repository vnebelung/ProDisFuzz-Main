package model.process;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Observable;
import java.util.Observer;

public class AbstractProcessTest implements Observer {
    private boolean isUpdated;

    @Test
    public void testSpreadUpdate() throws Exception {
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
