package cellFactories;

import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

/**
 *
 * @author MINA
 *
 * This class is responsible for rendering status ComboBox button
 */
public class ButtonCellFactory extends ListCell<Color> {

    /**
     * override update method which is called each time a cell is rendered
     *
     * @param itemC
     * @param empty
     *
     */
    @Override
    protected void updateItem(Color itemC, boolean empty) {

        super.updateItem(itemC, empty);

        if (itemC == null || empty) {
            setGraphic(null);
        } else {
            HBox hbox = new HBox();
            Text text = new Text();

            if (itemC == Color.LIMEGREEN) {
                text.setText("AVAILABLE");
            } else if (itemC == Color.YELLOW) {
                text.setText("AWAY");
            } else if (itemC == Color.RED) {
                text.setText("BUSY");
            } else if (itemC == Color.GRAY) {
                text.setText("OFFLINE");
            }

            //HBox to add string and color circle
            hbox.spacingProperty().set(10);
            //status color circle
            Circle circle = new Circle(10);
            circle.setFill(itemC);
            circle.setStroke(Color.BLACK);
            circle.setStrokeWidth(2);
            hbox.getChildren().addAll(circle, text);
            setGraphic(hbox);
        }
    }
}
