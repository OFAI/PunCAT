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
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;


public class ArrowButton {
    private static final GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");

    public static void setArrows(Button button, FontAwesome.Glyph fontAwesomeGlyph) {
        var glyph = fontAwesome.create(fontAwesomeGlyph);
        glyph.setFontSize(18);
        button.setContentDisplay(ContentDisplay.CENTER);
        button.setGraphic(glyph);
        button.setPadding(new Insets(2));
        button.prefWidthProperty().bind(button.heightProperty().add(2));
    }
}
