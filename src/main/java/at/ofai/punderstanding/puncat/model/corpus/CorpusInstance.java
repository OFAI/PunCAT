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

package at.ofai.punderstanding.puncat.model.corpus;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;


public class CorpusInstance {
    @XmlAttribute
    String id;
    @XmlElement
    CorpusImg img;
    @XmlElement
    CorpusText text;

    public void printAll() {
        System.out.println("id: " + this.id);
        this.img.printAll();
        this.text.printAll();
    }

    public String getId() {
        return id;
    }

    public CorpusImg getImg() {
        return img;
    }

    public CorpusText getText() {
        return text;
    }
}
