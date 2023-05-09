package paint;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.transform.Scale;
import java.util.LinkedList;

public class UndoManager {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final LinkedList<CanvasState> undoStack;
    private final LinkedList<CanvasState> redoStack;

    public UndoManager(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.undoStack = new LinkedList<>();
        this.redoStack = new LinkedList<>();
    }

    public void saveUndoState(Scale zoom) {
        Image snapshot = canvas.snapshot(null, null);
        undoStack.push(new CanvasState(snapshot, zoom));
        redoStack.clear();
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            CanvasState currentState = undoStack.pop();
            redoStack.push(currentState);

            if (!undoStack.isEmpty()) {
                CanvasState previousState = undoStack.peek();
                gc.drawImage(previousState.getImage(), 0, 0);
                canvas.getTransforms().setAll(previousState.getZoom());
            } else {
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            }
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            CanvasState redoState = redoStack.pop();
            gc.drawImage(redoState.getImage(), 0, 0);
            canvas.getTransforms().setAll(redoState.getZoom());
            undoStack.push(redoState);
        }
    }

    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }

    private class CanvasState {
        private final Image image;
        private final Scale zoom;

        public CanvasState(Image image, Scale zoom) {
            this.image = image;
            this.zoom = zoom;
        }

        public Image getImage() {
            return image;
        }

        public Scale getZoom() {
            return zoom;
        }
    }
}
