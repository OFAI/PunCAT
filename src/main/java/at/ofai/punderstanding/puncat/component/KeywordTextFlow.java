package at.ofai.punderstanding.puncat.component;

import java.util.ArrayList;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;


public class KeywordTextFlow {
    public static Text build(ArrayList<String> keywords) {
        var keywordStart = new Text("Keywords: ");
        keywordStart.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, Font.getDefault().getSize()));

        return new Text(String.join(", ", keywords));
    }
}
