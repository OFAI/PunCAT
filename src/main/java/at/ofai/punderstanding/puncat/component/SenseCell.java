/*
  Copyright 2020 Máté Lajkó

  This file is part of PunCAT.

  PunCAT is free software: you can redistribute it and/or modify it
  under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PunCAT is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with PunCAT.  If not, see <https://www.gnu.org/licenses/>.
 */

package at.ofai.punderstanding.puncat.component;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import at.ofai.punderstanding.puncat.model.SenseModel;
import at.ofai.punderstanding.puncat.model.SenseModelSource;


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

    @Override
    protected void updateItem(SenseModel item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null || empty) {
            setGraphic(null);
        } else {
            this.pronunciation.setText("/" + item.getPronunciation() + "/");
            this.synonyms.setText(" (" + String.join(", ", item.getSynonyms().values()) + ") ");
            this.description.setText(item.getDescription());

            if (item instanceof SenseModelSource
                    && !((SenseModelSource) item).hasGermanetEquivalent()) {
                this.pronunciation.setFill(Color.LIGHTGRAY);
                this.synonyms.setFill(Color.LIGHTGRAY);
                this.description.setFill(Color.LIGHTGRAY);
            } else {
                this.pronunciation.setFill(Color.BLACK);
                this.synonyms.setFill(Color.BLACK);
                this.description.setFill(Color.BLACK);
            }

            setGraphic(this.textFlow);
        }
    }
}
