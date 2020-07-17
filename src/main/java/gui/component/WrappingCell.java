package gui.component;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class WrappingCell {
    public static void setWrappingCell(ListView<String> listView) {
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setMinWidth(param.getWidth());
                    setMaxWidth(param.getWidth() - 20);
                    setPrefWidth(param.getWidth() - 20);

                    setWrapText(true);

                    setText(item);
                }
            }
        });
    }
}
