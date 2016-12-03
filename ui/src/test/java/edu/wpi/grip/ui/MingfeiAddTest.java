package edu.wpi.grip.ui;


import edu.wpi.grip.core.AddOperation;
import edu.wpi.grip.core.AdditionOperation;
import edu.wpi.grip.core.OperationMetaData;
import edu.wpi.grip.core.Pipeline;
import edu.wpi.grip.core.PipelineRunner;
import edu.wpi.grip.core.events.OperationAddedEvent;
import edu.wpi.grip.core.operations.network.MockGripNetworkModule;
import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.OutputSocket;
import edu.wpi.grip.ui.codegeneration.Exporter;
import edu.wpi.grip.ui.codegeneration.Language;
import edu.wpi.grip.ui.util.DPIUtility;
import edu.wpi.grip.ui.util.StyleClassNameUtility;
import edu.wpi.grip.util.GripCoreTestModule;

import com.google.common.eventbus.EventBus;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

import org.junit.After;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.util.WaitForAsyncUtils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static org.junit.Assert.assertEquals;
import static org.testfx.api.FxAssert.verifyThat;

@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
public class MingfeiAddTest extends ApplicationTest {
  private static final String STEP_NOT_ADDED_MSG = "Step was not added to pipeline";
  private final GripCoreTestModule testModule = new GripCoreTestModule();
  private Pipeline pipeline;
  private PipelineRunner pipelineRunner;
  private OperationMetaData addOperation;
  private OperationMetaData additionOperation;
  @Override
  @SuppressWarnings("PMD.SignatureDeclareThrowsException")
  public void start(Stage stage) throws Exception {
    testModule.setUp();

    Injector injector = Guice.createInjector(
        Modules.override(testModule, new MockGripNetworkModule()).with(new GripUiModule()));

    final Parent root =
        FXMLLoader.load(Main.class.getResource("MainWindow.fxml"), null, null,
            injector::getInstance);
    root.setStyle("-fx-font-size: " + DPIUtility.FONT_SIZE + "px");

    pipeline = injector.getInstance(Pipeline.class);
    pipelineRunner = injector.getInstance(PipelineRunner.class);
    final EventBus eventBus = injector.getInstance(EventBus.class);

    addOperation = new OperationMetaData(
        AddOperation.DESCRIPTION,
        () -> new AddOperation(eventBus)
    );
    additionOperation = new OperationMetaData(
        AdditionOperation.DESCRIPTION,
        () -> new AdditionOperation(injector.getInstance(InputSocket.Factory.class), injector
            .getInstance(OutputSocket.Factory.class))
    );
    eventBus.post(new OperationAddedEvent(addOperation));
    eventBus.post(new OperationAddedEvent(additionOperation));
    stage.setScene(new Scene(root));
    stage.show();
  }

  @After
  public void tearDown() {
    testModule.tearDown();
    pipelineRunner.stopAsync().awaitTerminated();
  }
  @Test
  public void testExploterbyMingfei() {
      Exporter exporter = new Exporter(pipeline.getSteps(), null, null, true);
      exporter.getNonExportableSteps();
//      exporter.run();

  }

}
