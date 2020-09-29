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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;


@XmlRootElement
public class CorpusPun {
    @XmlValue
    String pun;
    @XmlAttribute(name = "first_lemma")
    String firstLemma;
    @XmlAttribute(name = "first_sense")
    String firstSense;
    @XmlAttribute(name = "second_lemma")
    String secondLemma;
    @XmlAttribute(name = "second_sense")
    String secondSense;

    public void printAll() {
        System.out.println("pun: " + pun);
        System.out.println("firstLemma: " + firstLemma + ", firstSense: " + firstSense);
        System.out.println("secondLemma: " + secondLemma + ", secondSense: " + secondSense);
    }

    public String getPun() {
        return pun;
    }

    public String getFirstLemma() {
        return firstLemma;
    }

    public String getFirstSense() {
        return firstSense;
    }

    public String getSecondLemma() {
        return secondLemma;
    }

    public String getSecondSense() {
        return secondSense;
    }
}
