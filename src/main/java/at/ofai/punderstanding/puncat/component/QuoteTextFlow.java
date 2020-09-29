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

import javafx.geometry.Insets;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import at.ofai.punderstanding.puncat.model.corpus.CorpusText;


public class QuoteTextFlow {
    public static TextFlow build(CorpusText corpusText) {
        // TODO: parse xml with whitespace intact
        var firstPartString = corpusText.getText().get(0);
        if (!firstPartString.isEmpty()) {
            firstPartString += " ";
        }
        var firstPart = new Text(firstPartString);

        var secondPartString = corpusText.getText().get(1);
        if (secondPartString.endsWith(System.lineSeparator())) {
            secondPartString = secondPartString.substring(0, secondPartString.length()-2);
        }
        if (!secondPartString.isEmpty() && Character.isLetter(secondPartString.charAt(0))) {
            secondPartString = " " + secondPartString;
        }
        var secondPart = new Text(secondPartString);

        var pun = new Text(corpusText.getPun().getPun());
        pun.setUnderline(true);

        var textFlow = new TextFlow(firstPart, pun, secondPart);
        textFlow.setTextAlignment(TextAlignment.CENTER);

        return textFlow;
    }
}
