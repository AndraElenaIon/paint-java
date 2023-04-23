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
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
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

    boolean toolSelected = false;

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
    @FXML
    private ToggleGroup shapeToggleGroup;

    @FXML
    private ToggleButton rectangleToggleButton;

    @FXML
    private ToggleButton squareToggleButton;

    @FXML
    private ToggleButton circleToggleButton;
    @FXML
    private ImageView rectangleImageView;
    @FXML
    private ImageView squareImageView;
    @FXML
    private ImageView circleImageView;

    GraphicsContext brushTool;

    private String currentBrushType = "Circle";
    private boolean isDrawing = false;
    private double startX, startY;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollContent.prefWidthProperty().bind(canvas.widthProperty());
        scrollContent.prefHeightProperty().bind(canvas.heightProperty());

        Image eraserIcon = new Image(getClass().getResourceAsStream("eraser.png"));
        Image resizedIcon = PaintUtils.resizeImage(eraserIcon, 20, 20);

        ImageView eraserImageView = new ImageView(resizedIcon);

        stackPane.prefWidthProperty().bind(canvas.widthProperty());
        stackPane.prefHeightProperty().bind(canvas.heightProperty());

        eraser.setGraphic(eraserImageView);

        Image rectangleIcon = new Image(getClass().getResourceAsStream("/resources/rectangle-vertical.png"));
        Image resizedRectangleIcon = PaintUtils.resizeImage(rectangleIcon, 30, 30);
        rectangleImageView.setImage(resizedRectangleIcon);

        Image squareIcon = new Image(getClass().getResourceAsStream("/resources/square.png"));
        Image resizedSquareIcon = PaintUtils.resizeImage(squareIcon, 30, 30);
        squareImageView.setImage(resizedSquareIcon);

        Image circleIcon = new Image(getClass().getResourceAsStream("/resources/circle.png"));
        Image resizedCircleIcon = PaintUtils.resizeImage(circleIcon, 30, 30);
        circleImageView.setImage(resizedCircleIcon);

        brushTypeChoiceBox.getItems().addAll("Circle", "Square", "Line", "Calligraphy", "Crayon", "Airbrush", "Natural Pencil");

        brushTypeChoiceBox.setValue("Circle");

        brushTypeChoiceBox.setOnAction(e -> {
            currentBrushType = brushTypeChoiceBox.getValue();
        });

        canvas.setOnMouseReleased(e -> {
            if (currentBrushType.equals("Natural Pencil")) {
                startX = -1;
                startY = -1;
            } else {
                isDrawing = false;
            }
        });

        brushTool = canvas.getGraphicsContext2D();

        startX = -1;
        startY = -1;

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
                    case "Calligraphy":
                        brushTool.setFill(colorpicker.getValue());
                        if (!isDrawing) {
                            startX = x;
                            startY = y;
                            isDrawing = true;
                        } else {
                            double angle = Math.atan2(e.getY() - startY, e.getX() - startX);
                            double offsetX = size / 2 * Math.sin(angle);
                            double offsetY = size / 2 * Math.cos(angle);
                            brushTool.fillPolygon(new double[]{startX + offsetX, startX - offsetX, x - offsetX, x + offsetX},
                                    new double[]{startY - offsetY, startY + offsetY, y + offsetY, y - offsetY}, 4);
                            startX = x;
                            startY = y;
                        }
                        break;
                    case "Airbrush":
                        brushTool.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, colorpicker.getValue(), size * 0.8, 0, 0, 0));
                        brushTool.setFill(colorpicker.getValue());
                        brushTool.fillOval(x, y, size, size);
                        brushTool.setEffect(null);
                        break;
                    case "Crayon":
                        brushTool.setEffect(new InnerShadow(BlurType.ONE_PASS_BOX, colorpicker.getValue().darker(), 5, 0.1, 1, 1));
                        brushTool.setFill(colorpicker.getValue());
                        brushTool.fillOval(x, y, size, size);
                        brushTool.setEffect(null);
                        break;
                    case "Natural Pencil":
                        brushTool.setEffect(new InnerShadow(BlurType.ONE_PASS_BOX, colorpicker.getValue().darker(), 2, 0.1, 1, 1));
                        brushTool.setStroke(colorpicker.getValue());
                        brushTool.setLineWidth(size * 0.6);
                        if (startX != -1 && startY != -1) {
                            brushTool.strokeLine(startX, startY, x, y);
                        } else {
                            startX = x;
                            startY = y;
                        }
                        startX = x;
                        startY = y;
                        brushTool.setEffect(null);
                        break;

                }
            }
        });

    }

    @FXML
    private void onShapeSelected(ActionEvent event) {
        // Implement shape drawing logic here
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
            resetGraphicsContext(brushTool); // Reset GraphicsContext properties
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

    private void resetGraphicsContext(GraphicsContext gc) {
        gc.setEffect(null);
        gc.setLineWidth(1);
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.BLACK);
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineJoin(StrokeLineJoin.ROUND);
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
