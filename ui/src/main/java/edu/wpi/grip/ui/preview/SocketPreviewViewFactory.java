package edu.wpi.grip.ui.preview;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.wpi.grip.core.OutputSocket;
import edu.wpi.grip.core.operations.composite.BlobsReport;
import edu.wpi.grip.core.operations.composite.ContoursReport;
import edu.wpi.grip.core.operations.composite.LinesReport;
import org.bytedeco.javacpp.IntPointer;

import static org.bytedeco.javacpp.opencv_core.*;


/**
 * Factory for constructing {@link SocketPreviewView}s
 */
@Singleton
public class SocketPreviewViewFactory {

    @Inject
    private EventBus eventBus;

    SocketPreviewViewFactory() {/* no-op */}

    /**
     * Create an instance of {@link SocketPreviewView} appropriate for the given socket.  Sockets of different types
     * (like numbers, arrays, images, etc...) are rendered in different ways, and the role of this class is to figure
     * out what control to use to render a given socket.
     */
    @SuppressWarnings("unchecked")
    public <T> SocketPreviewView<T> create(OutputSocket<T> socket) {
        final SocketPreviewView<T> previewView;
        if (socket.getSocketHint().getType() == Mat.class) {
            previewView = (SocketPreviewView) new ImageSocketPreviewView((OutputSocket<Mat>) socket);
        } else if (socket.getSocketHint().getType() == Point.class || socket.getSocketHint().getType() == Size.class) {
            previewView = (SocketPreviewView) new PointSizeSocketPreviewView((OutputSocket<IntPointer>) socket);
        } else if (socket.getSocketHint().getType() == ContoursReport.class) {
            previewView = (SocketPreviewView) new ContoursSocketPreviewView((OutputSocket<ContoursReport>) socket);
        } else if (socket.getSocketHint().getType() == LinesReport.class) {
            previewView = (SocketPreviewView) new LinesSocketPreviewView((OutputSocket<LinesReport>) socket);
        } else if (socket.getSocketHint().getType() == BlobsReport.class) {
            previewView = (SocketPreviewView) new BlobsSocketPreviewView((OutputSocket<BlobsReport>) socket);
        } else {
            previewView = new TextAreaSocketPreviewView<>(socket);
        }
        eventBus.register(previewView);
        return previewView;
    }
}
