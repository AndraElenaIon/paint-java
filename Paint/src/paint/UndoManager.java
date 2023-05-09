package paint;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.LinkedList;

public class UndoManager {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final LinkedList<Image> undoStack;
    private final LinkedList<Image> redoStack;

    public UndoManager(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.undoStack = new LinkedList<>();
        this.redoStack = new LinkedList<>();
    }

    public void saveUndoState() {
        Image snapshot = canvas.snapshot(null, null);
        undoStack.push(snapshot);
        redoStack.clear();
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            redoStack.push(undoStack.pop());
            if (!undoStack.isEmpty()) {
                gc.drawImage(undoStack.peek(), 0, 0);
            } else {
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            }
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            Image redoImage = redoStack.pop();
            gc.drawImage(redoImage, 0, 0);
            undoStack.push(redoImage);
        }
    }

    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }
}
