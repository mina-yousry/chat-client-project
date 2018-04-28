package cellFactories;

import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 *
 * @author Mina
 *
 * this class is used in rendering message color combo box cells
 */
public class MsgColorCellFactory extends ListCell<Color> {

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

            if (itemC == Color.BLACK) {
                text.setText("Black");
            } else if (itemC == Color.YELLOW) {
                text.setText("Yellow");
            } else if (itemC == Color.RED) {
                text.setText("Red");
            } else {
                text.setText("Green");
            }

            //HBox to add string and color circle
            hbox.spacingProperty().set(10);
            //status color circle
            Rectangle rect = new Rectangle(20, 10);
            rect.setFill(itemC);
            rect.setStroke(Color.BLACK);
            rect.setStrokeWidth(2);
            hbox.getChildren().addAll(rect, text);
            setGraphic(hbox);
        }
    }
}
