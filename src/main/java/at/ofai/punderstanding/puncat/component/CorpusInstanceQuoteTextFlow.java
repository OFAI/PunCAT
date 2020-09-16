package at.ofai.punderstanding.puncat.component;

import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import at.ofai.punderstanding.puncat.model.corpus.CorpusText;


public class CorpusInstanceQuoteTextFlow {
    public static TextFlow build(CorpusText corpusText) {
        // TODO: parse xml with whitespace intact
        var firstPart = corpusText.getText().get(0);
        if (!firstPart.isEmpty()) {
            firstPart += " ";
        }
        var textFirstPart = new Text(firstPart);

        var secondPart = corpusText.getText().get(1);
        if (!secondPart.isEmpty() && Character.isLetter(secondPart.charAt(0))) {
            secondPart = " " + secondPart;
        }
        var textSecondPart = new Text(secondPart);

        var textPun = new Text(corpusText.getPun().getFirstLemma());
        textPun.setUnderline(true);

        var textFlow = new TextFlow(textFirstPart, textPun, textSecondPart);
        textFlow.setTextAlignment(TextAlignment.CENTER);

        textFlow.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        textFlow.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        textFlow.setPadding(new Insets(10));

        return textFlow;
    }
}
