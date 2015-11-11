package edu.wpi.grip.ui.pipeline;

import com.google.common.eventbus.EventBus;
import edu.wpi.grip.core.events.SourceAddedEvent;
import edu.wpi.grip.core.sources.ImageFileSource;
import edu.wpi.grip.core.sources.WebcamSource;
import edu.wpi.grip.ui.util.DPIUtility;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Screen;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

/**
 * A box of buttons that let the user add different kinds of {@link Source}s.  Depending on which button is pressed,
 * a different dialog is presented for the user to construct that source.  As an example, the image file source results
 * in a file picker that the user can use to browse for an image.
 */
public class AddSourceView extends HBox {

    private final EventBus eventBus;

    /**
     * Sources typically have to block while they load, so that should be done in a separate daemon thread to avoid
     * freezing up the GUI.
     */
    private final Executor loadSourceExecutor = Executors.newSingleThreadExecutor(runnable -> {
        final Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        return thread;
    });

    public AddSourceView(EventBus eventBus) {
        this.eventBus = eventBus;

        this.setFillHeight(true);

        addButton("Add\nImage", getClass().getResource("/edu/wpi/grip/ui/icons/add-image.png"), mouseEvent -> {
            // Show a file picker so the user can open one or more images from disk
            final FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open an image");

            final List<File> imageFiles = fileChooser.showOpenMultipleDialog(this.getScene().getWindow());

            if (imageFiles == null) return;

            // Add a new source for each image .
            imageFiles.forEach(file -> {
                this.loadSourceExecutor.execute(() -> {
                    eventBus.post(new SourceAddedEvent(new ImageFileSource(eventBus, file)));
                });
            });
        });

        addButton("Add\nWebcam", getClass().getResource("/edu/wpi/grip/ui/icons/add-webcam.png"), mouseEvent -> {
            final Parent root = this.getScene().getRoot();

            // Show a dialog for the user to pick a camera index
            final Dialog<ButtonType> dialog = new Dialog<>();
            final Spinner<Integer> cameraIndex = new Spinner<Integer>(0, Integer.MAX_VALUE, 0);

            dialog.setTitle("Add Webcam");
            dialog.setHeaderText("Choose a camera");
            dialog.setContentText("index");
            dialog.getDialogPane().setContent(cameraIndex);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
            dialog.getDialogPane().setStyle(root.getStyle());
            dialog.getDialogPane().getStylesheets().addAll(root.getStylesheets());

            // If the user clicks OK, add a new webcam source
            dialog.showAndWait().filter(Predicate.isEqual(ButtonType.OK)).ifPresent(result -> {
                this.loadSourceExecutor.execute(() -> {
                    final WebcamSource source = new WebcamSource(eventBus, cameraIndex.getValue());
                    eventBus.post(new SourceAddedEvent(source));
                });
            });
        });
    }

    /**
     * Add a new button for adding a source.  This method takes care of setting the event handler.
     */
    private void addButton(String text, URL graphicURL, EventHandler<? super MouseEvent> onMouseClicked) {
        final ImageView graphic = new ImageView(graphicURL.toString());
        graphic.setFitWidth(DPIUtility.SMALL_ICON_SIZE);
        graphic.setFitHeight(DPIUtility.SMALL_ICON_SIZE);

        final Button button = new Button(text, graphic);
        button.setTextAlignment(TextAlignment.CENTER);
        button.setContentDisplay(ContentDisplay.TOP);
        button.setOnMouseClicked(onMouseClicked);

        this.getChildren().add(button);
    }
}