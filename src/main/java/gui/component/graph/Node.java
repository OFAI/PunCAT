package gui.component.graph;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class Node extends Group {
    private final Ellipse ellipse;
    private final Text label;

    public Node(String text, double x, double y) {
        this.label = new Text(text);
        this.label.setTextAlignment(TextAlignment.CENTER);
        double labelX = this.label.getLayoutBounds().getCenterX();
        double labelY = this.label.getLayoutBounds().getCenterY();
        this.label.setX(x - labelX);
        this.label.setY(y - labelY);

        double radiusX = Math.max(labelX + 15, 25);
        double radiusY = Math.max(labelY + 20, 15.5);
        this.ellipse = new Ellipse(x, y, radiusX, radiusY);
        this.ellipse.setStroke(Color.CORNFLOWERBLUE);
        this.ellipse.setFill(Color.WHITE);
        this.ellipse.setStrokeWidth(2);

        this.getChildren().addAll(ellipse, label);

        this.setOnMouseClicked(event -> {
            var graph = (Graph) this.getParent();
            graph.nodeSelected(this.getId());
        });

        this.setOnMouseEntered(event -> this.toFront());
    }

    public Rectangle getLabelBoundingBox() {
        Bounds lb = this.label.getLayoutBounds();
        Rectangle r = new Rectangle(
                lb.getMinX(), lb.getMinY(),
                lb.getWidth(), lb.getHeight()
        );
        r.setOpacity(0.5);
        r.setFill(Color.TRANSPARENT);
        r.setStroke(Color.BLACK);
        return r;
    }

    public Ellipse getEllipse() {
        return ellipse;
    }

    public Text getLabel() {
        return label;
    }

    public double getCenterX() {
        return this.ellipse.getCenterX();
    }

    public double getCenterY() {
        return this.ellipse.getCenterY();
    }

    public void setRootStyle() {
        this.label.setFont(Font.font(Font.getDefault().getName(), FontWeight.BOLD, Font.getDefault().getSize()));
        this.ellipse.setStrokeWidth(3);
    }
}
