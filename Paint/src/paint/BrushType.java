package paint;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;

public enum BrushType {
    CIRCLE, SQUARE, LINE, CALLIGRAPHY, AIRBRUSH, CRAYON, NATURAL_PENCIL;

    public double[] draw(GraphicsContext brushTool, ColorPicker colorpicker, double x, double y, double size,
                     boolean isDrawing, double startX, double startY, MouseEvent e) {
        switch (this) {
            case CIRCLE:
                        brushTool.setFill(colorpicker.getValue());
                        brushTool.fillOval(x, y, size, size);
                        break;
                    case SQUARE:
                        brushTool.setFill(colorpicker.getValue());
                        brushTool.fillRect(x, y, size, size);
                        break;
                    case LINE:
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
                    case CALLIGRAPHY:
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
                    case AIRBRUSH:
                        brushTool.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, colorpicker.getValue(), size * 0.8, 0, 0, 0));
                        brushTool.setFill(colorpicker.getValue());
                        brushTool.fillOval(x, y, size, size);
                        brushTool.setEffect(null);
                        break;
                    case CRAYON:
                        brushTool.setEffect(new InnerShadow(BlurType.ONE_PASS_BOX, colorpicker.getValue().darker(), 5, 0.1, 1, 1));
                        brushTool.setFill(colorpicker.getValue());
                        brushTool.fillOval(x, y, size, size);
                        brushTool.setEffect(null);
                        break;
                    case NATURAL_PENCIL:
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
        return new double[]{isDrawing ? 1.0 : 0.0, startX, startY};
    }
}
