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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
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
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;

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
    private ScrollPane scrollPane;
    @FXML
    private Pane scrollContent;

    GraphicsContext brushTool;
    private String currentBrushType = "Circle";
    private boolean isDrawing = false;
    private double startX, startY;
    boolean toolSelected = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        scrollContent.prefWidthProperty().bind(canvas.widthProperty());
        scrollContent.prefHeightProperty().bind(canvas.heightProperty());

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

        brushTool = canvas.getGraphicsContext2D();

        startX = -1;
        startY = -1;

        canvas.setOnMouseReleased(e -> {
            if (currentBrushType.equals("Natural_Pencil")) {
                startX = -1;
                startY = -1;
            } else {
                isDrawing = false;
            }
        });

        canvas.setOnMouseDragged(e -> {
            double size = Double.parseDouble(bsize.getText());
            double x = e.getX() - size / 2;
            double y = e.getY() - size / 2;

            // Check if eraser tool is selected
            if (eraser.isSelected()) {
                brushTool.clearRect(x, y, size, size);
                // Otherwise proceed with the brush
            } else if (toolSelected && !bsize.getText().isEmpty()) {
                brushTool.setFill(colorpicker.getValue());

                BrushType currentBrush = BrushType.valueOf(currentBrushType.toUpperCase());
                double[] updatedValues = currentBrush.draw(brushTool, colorpicker, x, y, size, isDrawing, startX, startY, e);
                isDrawing = updatedValues[0] == 1.0;
                startX = updatedValues[1];
                startY = updatedValues[2];
            }
        });

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
    public void toolselected(ActionEvent e) {
        toolSelected = true;
    }

    @FXML
public void onSave() {
    try {
        Image snapshot = canvas.snapshot(null, null);
        ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", new File("paint.png"));

        // Show success message
        Alert successAlert = new Alert(AlertType.INFORMATION);
        successAlert.setTitle("Success");
        successAlert.setHeaderText(null);
        successAlert.setContentText("Image saved successfully!");
        successAlert.showAndWait();

    } catch (Exception e) {
        System.out.println("Failed to save image : " + e);

        // Show error message
        Alert errorAlert = new Alert(AlertType.ERROR);
        errorAlert.setTitle("Error");
        errorAlert.setHeaderText(null);
        errorAlert.setContentText("Failed to save image: " + e.getMessage());
        errorAlert.showAndWait();
    }
}


    @FXML
    public void onExit() {
        Platform.exit();
    }

}
