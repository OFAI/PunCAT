package at.ofai.punderstanding.puncat.component;

import java.util.ArrayList;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;


public class CorpusInstanceKeywordTextFlow {
    public static TextFlow build(ArrayList<String> keywords) {
        var keywordStart = new Text("Keywords: ");
        keywordStart.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, Font.getDefault().getSize()));
        var keywordList = new Text(String.join(", ", keywords));

        return new TextFlow(keywordStart, keywordList);
    }
}
