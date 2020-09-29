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

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


public class CorpusText {
    @XmlMixed
    @XmlJavaTypeAdapter(CorpusTextAdapter.class)
    ArrayList<String> text;
    @XmlElement
    CorpusPun pun;

    public void printAll() {
        System.out.println("text: " + text);
        pun.printAll();
    }

    public ArrayList<String> getText() {
        return text;
    }

    public CorpusPun getPun() {
        return pun;
    }

    static class CorpusTextAdapter extends XmlAdapter<String, String> {
        @Override
        public String unmarshal(String s) {
            return s.strip();
        }

        @Override
        public String marshal(String strings) {
            return null;
        }
    }
}
