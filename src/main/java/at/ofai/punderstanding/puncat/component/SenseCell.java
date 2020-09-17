package at.ofai.punderstanding.puncat.component;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import at.ofai.punderstanding.puncat.model.SenseModel;


public class SenseCell extends ListCell<SenseModel> {
    private final TextFlow textFlow = new TextFlow();
    private final Text pronunciation = new Text();
    private final Text description = new Text();
    private final Text synonyms = new Text();

    public SenseCell(ListView<SenseModel> list) {
        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.synonyms.setStyle("-fx-font-weight: bold");
        this.textFlow.maxWidthProperty().bind(list.widthProperty().subtract(15));
        this.setPrefWidth(0);
        this.textFlow.getChildren().addAll(this.pronunciation, this.synonyms, this.description);
    }

    public boolean hasContent() {
        return this.getGraphic() != null;
    }

    @Override
    protected void updateItem(SenseModel item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null || empty) {
            setGraphic(null);
        } else {
            this.pronunciation.setText("/" + item.getPronunciation() + "/");
            this.synonyms.setText(" (" + String.join(", ", item.getSynonyms().values()) + ") ");
            this.description.setText(item.getDescription());
            setGraphic(this.textFlow);
        }
    }
}
