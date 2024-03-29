/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML2.java to edit this template
 */
package paint;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;

/**
 *
 * @author Andra
 */
public class paintfxmlController implements Initializable {

    @FXML
    private ColorPicker colorpicker;

    @FXML
    private TextField bsize;

    @FXML
    private Canvas canvas;

    @FXML
    private CheckBox eraser;

    @FXML
    private ChoiceBox<String> brushTypeChoiceBox;

    @FXML
    private StackPane stackPane;

    @FXML
    private Button zoomInButton;

    @FXML
    private Button zoomOutButton;

    @FXML
    GraphicsContext brushTool;

    @FXML
    private Group canvasGroup;

    @FXML
    private AnchorPane anchorPane;

    private String currentBrushType = "Circle";
    private boolean isDrawing = false;
    private double startX, startY;
    private final DoubleProperty zoomLevel = new SimpleDoubleProperty(1.0);
    private UndoManager undoManager;

//    private ArrayList<Image> canvasSnapshots = new ArrayList<>();
//    private ArrayList<Image> redoSnapshots = new ArrayList<>();
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        startX = -1;
        startY = -1;

        anchorPane.prefWidthProperty().bind(canvas.widthProperty());
        anchorPane.prefHeightProperty().bind(canvas.heightProperty());
        undoManager = new UndoManager(canvas);

        Image eraserIcon = new Image(getClass().getResourceAsStream("/resources/eraser.png"));
        Image resizedIcon = PaintUtils.resizeImage(eraserIcon, 20, 20);
        ImageView eraserImageView = new ImageView(resizedIcon);
        eraser.setGraphic(eraserImageView);

        stackPane.prefWidthProperty().bind(canvas.widthProperty());
        stackPane.prefHeightProperty().bind(canvas.heightProperty());

        brushTypeChoiceBox.getItems().addAll("Circle", "Square", "Line", "Calligraphy", "Crayon", "Airbrush", "Natural_Pencil");
        brushTypeChoiceBox.setValue("Circle");
        brushTypeChoiceBox.setOnAction(e -> {
            currentBrushType = brushTypeChoiceBox.getValue();
        });

        canvas.scaleXProperty().bind(zoomLevel);
        canvas.scaleYProperty().bind(zoomLevel);
        zoomInButton.setOnAction(e -> onZoomIn());
        zoomOutButton.setOnAction(e -> onZoomOut());

        brushTool = canvas.getGraphicsContext2D();
        brushTool.setFill(Color.WHITE);
        canvas.setOnMouseReleased(e -> {
            if (currentBrushType.equals("Natural_Pencil")) {
                startX = -1;
                startY = -1;
            } else {
                isDrawing = false;
            }
            undoManager.saveUndoState(new Scale(zoomLevel.get(), zoomLevel.get()));
        });

        canvas.setOnMouseDragged(this::brushHandler);
        canvas.setOnDragDetected(event -> {
            // Disable default dragging behavior
            canvas.startFullDrag();
        });
        canvas.addEventFilter(ScrollEvent.ANY, ScrollEvent::consume);

    }

    @FXML
    public void newCanvas(ActionEvent e) {
        TextField getCanvasWidth = new TextField();
        getCanvasWidth.setPromptText("Width");
        getCanvasWidth.setStyle("-fx-pref-width: 200px; -fx-max-width: 200px; -fx-pref-height: 40px;");
        getCanvasWidth.setAlignment(Pos.CENTER);

        TextField getCanvasHeight = new TextField();
        getCanvasHeight.setPromptText("Height");
        getCanvasHeight.setStyle("-fx-pref-width: 200px; -fx-max-width: 200px; -fx-pref-height: 40px;");
        getCanvasHeight.setAlignment(Pos.CENTER);

        Button createButton = new Button();
        createButton.setText("Create Canvas");
        createButton.setStyle("-fx-pref-width: 250px; -fx-max-width: 250px; -fx-pref-height: 50px;");

        VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(getCanvasWidth, getCanvasHeight, createButton);

        Stage createStage = new Stage();
        AnchorPane root = new AnchorPane();
        root.getChildren().add(vBox);

        // Calculate the preferred width and height based on the content
        double prefWidth = createButton.getWidth() + 50;
        double prefHeight = getCanvasWidth.getHeight() + getCanvasHeight.getHeight() + createButton.getHeight() + 60;

        root.setPrefWidth(prefWidth);
        root.setPrefHeight(prefHeight);

        // Set the top, bottom, left, and right anchors of the VBox to center it in the AnchorPane
        AnchorPane.setTopAnchor(vBox, (root.getPrefHeight() - vBox.getBoundsInParent().getHeight()) / 2);
        AnchorPane.setBottomAnchor(vBox, (root.getPrefHeight() - vBox.getBoundsInParent().getHeight()) / 2);
        AnchorPane.setLeftAnchor(vBox, (root.getPrefWidth() - vBox.getBoundsInParent().getWidth()) / 2);
        AnchorPane.setRightAnchor(vBox, (root.getPrefWidth() - vBox.getBoundsInParent().getWidth()) / 2);

        Scene canvasScene = new Scene(root);
        createStage.setTitle("Create Canvas");
        createStage.setScene(canvasScene);
        createStage.show();

        createButton.setOnAction((ActionEvent event) -> {
            double canvasWidthReceived = Double.parseDouble(getCanvasWidth.getText());
            double canvasHeightReceived = Double.parseDouble(getCanvasHeight.getText());

            PaintUtils.clearCanvas(canvas);
            canvas.setWidth(canvasWidthReceived);
            canvas.setHeight(canvasHeightReceived);
            PaintUtils.resetGraphicsContext(brushTool); // Reset GraphicsContext properties
            createStage.close();
        });
    }

    @FXML
    public void toolselected(ActionEvent event) {
        canvas.setOnMouseDragged(this::brushHandler);
    }

    private void brushHandler(MouseEvent e) {
        double size = Double.parseDouble(bsize.getText());
        double x = e.getX() - size / 2;
        double y = e.getY() - size / 2;

        if (eraser.isSelected()) {
            brushTool.clearRect(x, y, size, size);
        } else if (!bsize.getText().isEmpty()) {
            brushTool.setFill(colorpicker.getValue());

            BrushType currentBrush = BrushType.valueOf(currentBrushType.toUpperCase());
            double[] updatedValues = currentBrush.draw(brushTool, colorpicker, x, y, size, isDrawing, startX, startY, e);
            isDrawing = updatedValues[0] == 1.0;
            startX = updatedValues[1];
            startY = updatedValues[2];
        }
    }

    @FXML
    private void onSave(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();

        // Setam extensia implicita la PNG
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);

        // Afisam fereastra de dialog si asteptam ca utilizatorul sa aleaga un fisier
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                // Salvam canvas-ul in fisierul selectat de utilizator
                WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                canvas.snapshot(null, writableImage);
                BufferedImage bImage = SwingFXUtils.fromFXImage(writableImage, null);
                ImageIO.write(bImage, "png", file);
            } catch (IOException ex) {
                // Afisam mesaj de eroare in caz de esec la salvare
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to save image");
                alert.setContentText("Failed to save image: " + ex.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    public void onExit() {
        Platform.exit();
    }

    @FXML
    public void onZoomIn() {
        canvas.setOnMouseDragged(null);
        double zoomFactor = 1.1; // Adjust this value to change the zoom speed
        zoomLevel.set(zoomLevel.get() * zoomFactor);
    }

    @FXML
    public void onZoomOut() {
        canvas.setOnMouseDragged(null);
        double zoomFactor = 1.1; // Adjust this value to change the zoom speed
        zoomLevel.set(zoomLevel.get() / zoomFactor);
    }

    @FXML
    public void onScroll(ScrollEvent event) {
        if (!event.isControlDown()) { // Make sure control key is pressed
            return;
        }
        double zoomFactor = 1.05;
        double deltaY = event.getDeltaY();

        if (deltaY < 0) {
            zoomFactor = 1 / zoomFactor;
        }

        canvasGroup.setScaleX(canvasGroup.getScaleX() * zoomFactor);
        canvasGroup.setScaleY(canvasGroup.getScaleY() * zoomFactor);

        event.consume();
    }

    @FXML
    public void onUndo() {
        undoManager.undo();
    }

    @FXML
    public void onRedo() {
        undoManager.redo();
    }

    @FXML
    public void onLoad() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG files", "*.png")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                Image loadedImage = new Image(selectedFile.toURI().toString());
                brushTool.drawImage(loadedImage, 0, 0);

                // Update the canvas size to match the loaded image size
                canvas.setWidth(loadedImage.getWidth());
                canvas.setHeight(loadedImage.getHeight());
                PaintUtils.resetGraphicsContext(brushTool);

                // Clear the undoManager
                undoManager.clear();

                // Save the loaded image state
                undoManager.saveUndoState(new Scale(zoomLevel.get(), zoomLevel.get()));
            } catch (Exception ex) {
                System.out.println("Failed to load image: " + ex.getMessage());
                Alert errorAlert = new Alert(AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Failed to load image: " + ex.getMessage());
                errorAlert.showAndWait();
            }
        }
    }

}
