package edu.wpi.grip.ui.preview;

import com.google.common.eventbus.EventBus;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import edu.wpi.grip.core.events.RenderEvent;
import edu.wpi.grip.core.operations.network.MockGripNetworkModule;
import edu.wpi.grip.core.sockets.OutputSocket;
import edu.wpi.grip.core.sockets.SocketHint;
import edu.wpi.grip.ui.GripUiModule;
import edu.wpi.grip.ui.util.MockGripPlatform;
import edu.wpi.grip.util.Files;
import edu.wpi.grip.util.GripCoreTestModule;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacv.Blobs;
import org.junit.After;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationAdapter;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.util.WaitForAsyncUtils;
import edu.wpi.grip.core.operations.composite.BlobsReport;

import static org.junit.Assert.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.junit.Assert.assertArrayEquals;
/**
 * Created by Mingfei on 12/3/2016.
 */
public class BlobsSocketPreviewViewByMingfeiTest extends ApplicationTest {
    private static final String identifier = "blob";
    private GripCoreTestModule testModule;

    @Override
    public void start(Stage stage) {
        testModule = new GripCoreTestModule();
        testModule.setUp();

        final Injector injector = Guice.createInjector(Modules.override(testModule)
                .with(new GripUiModule(), new MockGripNetworkModule()));
        final BlobsSocketPreviewView blobsSocketPreviewView =
                new BlobsSocketPreviewView(new MockGripPlatform(new EventBus()),
                        injector.getInstance(OutputSocket.Factory.class)
                                .create(new SocketHint.Builder<>(BlobsReport.class)
                                        .identifier(identifier)
                                        .initialValueSupplier(BlobsReport::new)
                                        .build()));
        final Scene scene = new Scene(blobsSocketPreviewView);
        stage.setScene(scene);
        stage.show();
        blobsSocketPreviewView.onRender(new RenderEvent());
        assertTrue(blobsSocketPreviewView.showInputImage);
    }

    @After
    public void tearDown() {
        testModule.tearDown();
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    public void testBlobView() {
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat(identifier, NodeMatchers.isVisible());
    }
}
