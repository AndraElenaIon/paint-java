/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package paint;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

/**
 *
 * @author Andra
 */
public class PaintUtils {
    
    public static Image resizeImage(Image originalImage, double width, double height) {
       
        WritableImage resizedImage = new WritableImage((int) width, (int) height);
        
        PixelReader reader = originalImage.getPixelReader();
        PixelWriter writer = resizedImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                writer.setArgb(x, y, reader.getArgb((int) (x * originalImage.getWidth() / width), (int) (y * originalImage.getHeight() / height)));
            }
        }
        return resizedImage;
    }
    
    public static void clearCanvas(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

     public static void resetGraphicsContext(GraphicsContext gc) {
        gc.setEffect(null);
        gc.setLineWidth(1);
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.BLACK);
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineJoin(StrokeLineJoin.ROUND);
    }
    public static void brushHandler(MouseEvent e) {
        double size = Double.parseDouble(bsize.getText());
        double x = e.getX() - size / 2;
        double y = e.getY() - size / 2;

        // Check if eraser tool is selected
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
    
}
