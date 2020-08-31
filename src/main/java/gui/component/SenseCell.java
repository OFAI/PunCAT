package gui.component;

import gui.model.SenseModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ListCell;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class SenseCell extends ListCell<SenseModel> {
    TextFlow textFlow = new TextFlow();

    Text pronunciation = new Text();
    Text description = new Text();
    Text synonyms = new Text();

    public SenseCell() {
        this.getStyleClass().add("sense-cell");
        this.synonyms.setStyle("-fx-font-weight: bold");
        this.textFlow.getChildren().addAll(this.pronunciation, this.synonyms, this.description);
        this.setPrefWidth(0);
    }

    @Override
    protected void updateItem(SenseModel item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            setText(null);
            this.pronunciation.textProperty().bind(
                    new SimpleStringProperty("/")
                            .concat(item.pronunciationProperty())
                            .concat(new SimpleStringProperty("/")));
            this.synonyms.setText(" (" + String.join(", ", item.getSynonyms().values()) + ") ");
            this.description.setText(item.getDescription());
            setGraphic(this.textFlow);
        }
    }
}
