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
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
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
   
    
    GraphicsContext brushTool;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Image eraserIcon = new Image(getClass().getResourceAsStream("eraser.png"));
        Image resizedIcon = resizeImage(eraserIcon, 20, 20);
       
        ImageView eraserImageView = new ImageView(resizedIcon);
        eraser.setGraphic(eraserImageView);
        
        brushTool = canvas.getGraphicsContext2D();
        canvas.setOnMouseDragged(e -> {
            double size = Double.parseDouble(bsize.getText());
            double x = e.getX() - size / 2;
            double y = e.getY() - size / 2;
            if(toolSelected && !bsize.getText().isEmpty()){
                brushTool.setFill(colorpicker.getValue());
                brushTool.fillRoundRect(x, y, size, size, size, size);
            }
        });
        
    }  
    
     private Image resizeImage(Image originalImage, double width, double height) {
        // Create a new writable image with the desired width and height
        WritableImage resizedImage = new WritableImage((int) width, (int) height);

        // Get the pixel reader and writer for the original and resized images
        PixelReader reader = originalImage.getPixelReader();
        PixelWriter writer = resizedImage.getPixelWriter();

        // Copy the original image data to the new resized image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                writer.setArgb(x, y, reader.getArgb((int) (x * originalImage.getWidth() / width), (int) (y * originalImage.getHeight() / height)));
            }
        }

        // Return the resized image
        return resizedImage;
    }
    
    @FXML
    public void newcanvas(ActionEvent e){
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
        
        
        createButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                double canvasWidthReceived = Double.parseDouble(getCanvasWidth.getText());
                double canvasHeightReceived = Double.parseDouble(getCanvasHeight.getText());
                canvas = new Canvas();
                canvas.setWidth(canvasWidthReceived);
                canvas.setHeight(canvasHeightReceived);
                vBox.getChildren().add(canvas);
                createStage.close();
            }
        });
        
        
    }
    
    @FXML 
    public void toolselected (ActionEvent e){
        toolSelected = true;
    }
   
    @FXML
    public void onSave(){
        try{
            Image snapshot = canvas.snapshot(null, null);
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", new File("paint.png"));
        }catch(Exception e){
            System.out.println("Failed to save image : " + e);
        }
    }
    
    @FXML
     public void onExit(){
         Platform.exit();
    }
    
    
}
