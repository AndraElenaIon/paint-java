/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML2.java to edit this template
 */
package paint;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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

/**
 *
 * @author Andra
 */
public class paintfxmlController implements Initializable {

    @FXML
    private ColorPicker colorpicker;

    @FXML
    private TextField bsize;

    boolean toolSelected = false;

    @FXML
    private Canvas canvas;

    @FXML
    private CheckBox eraser;

    @FXML
    private ChoiceBox<String> brushTypeChoiceBox;

    GraphicsContext brushTool;

    private String currentBrushType = "Circle";
    private boolean isDrawing = false;
    private double startX, startY;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Image eraserIcon = new Image(getClass().getResourceAsStream("eraser.png"));
        Image resizedIcon = PaintUtils.resizeImage(eraserIcon, 20, 20);

        ImageView eraserImageView = new ImageView(resizedIcon);
        eraser.setGraphic(eraserImageView);

        brushTypeChoiceBox.getItems().addAll("Circle", "Square", "Line");
        brushTypeChoiceBox.setValue("Circle");

        brushTypeChoiceBox.setOnAction(e -> {
            currentBrushType = brushTypeChoiceBox.getValue();
        });
        brushTool = canvas.getGraphicsContext2D();
        canvas.setOnMouseDragged(e -> {
    double size = Double.parseDouble(bsize.getText());
    double x = e.getX() - size / 2;
    double y = e.getY() - size / 2;

    // Check if eraser tool is selected
    if (eraser.isSelected()) {
        brushTool.clearRect(x, y, size, size);
    } else if (toolSelected && !bsize.getText().isEmpty()) {
        switch (currentBrushType) {
            case "Circle":
                brushTool.setFill(colorpicker.getValue());
                brushTool.fillOval(x, y, size, size);
                break;
            case "Square":
                brushTool.setFill(colorpicker.getValue());
                brushTool.fillRect(x, y, size, size);
                break;
            case "Line":
                brushTool.setStroke(colorpicker.getValue());
                brushTool.setLineWidth(size);
                if (!isDrawing) {
                    startX = x;
                    startY = y;
                    isDrawing = true;
                } else {
                    brushTool.strokeLine(startX, startY, x, y);
                    isDrawing = false;
                }
                break;
            default:
                brushTool.setFill(colorpicker.getValue());
                brushTool.fillOval(x, y, size, size);
                break;
        }
    }
});


    }

@FXML
public void newCanvas(ActionEvent e) {
    TextField getCanvasWidth = new TextField();
    getCanvasWidth.setPromptText("Width");
    getCanvasWidth.setPrefWidth(150);
    getCanvasWidth.setAlignment(Pos.CENTER);

    TextField getCanvasHeight = new TextField();
    getCanvasHeight.setPromptText("Height");
    getCanvasHeight.setPrefWidth(150);
    getCanvasHeight.setAlignment(Pos.CENTER);

    Button createButton = new Button();
    createButton.setText("Create Canvas");

    VBox vBox = new VBox();
    vBox.setSpacing(5);
    vBox.setAlignment(Pos.CENTER);
    vBox.getChildren().addAll(getCanvasWidth, getCanvasHeight, createButton);

    Stage createStage = new Stage();
    AnchorPane root = new AnchorPane();
    root.setPrefWidth(200);
    root.setPrefHeight(200);
    root.getChildren().add(vBox);

    Scene CanvasScene = new Scene(root);
    createStage.setTitle("Create Canvas");
    createStage.setScene(CanvasScene);
    createStage.show();

    createButton.setOnAction((ActionEvent event) -> {
        double canvasWidthReceived = Double.parseDouble(getCanvasWidth.getText());
        double canvasHeightReceived = Double.parseDouble(getCanvasHeight.getText());

        clearCanvas(canvas);
        canvas.setWidth(canvasWidthReceived);
        canvas.setHeight(canvasHeightReceived);
        createStage.close();
    });
}

private void clearCanvas(Canvas canvas) {
    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
}





    @FXML
    public void toolselected(ActionEvent e) {
        toolSelected = true;
    }

    @FXML
    public void onSave() {
        try {
            Image snapshot = canvas.snapshot(null, null);
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", new File("paint.png"));
        } catch (Exception e) {
            System.out.println("Failed to save image : " + e);
        }
    }

    @FXML
    public void onExit() {
        Platform.exit();
    }

}
