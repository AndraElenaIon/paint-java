/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package paint;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 *
 * @author Andra
 */
public class PaintUtils {
    
    public static Image resizeImage(Image originalImage, double width, double height) {
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

    
}
