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

package at.ofai.punderstanding.puncat.logic.semnet;

import java.util.ArrayList;
import java.util.List;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;
import net.sf.extjwnl.dictionary.Dictionary;


public class WordnetController {
    private final Dictionary wordnet;

    public WordnetController() throws JWNLException {
        this.wordnet = Dictionary.getDefaultResourceInstance();
    }

    public List<Synset> getSynsets(String word) {
        var results = new ArrayList<Synset>();
        try {
            for (POS p : POS.getAllPOS()) {
                IndexWord iw = this.wordnet.lookupIndexWord(p, word);
                if (iw != null) {
                    results.addAll(iw.getSenses());
                }
            }
        } catch (JWNLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public Word getWordBySenseKey(String senseKey) {
        try {
            return this.wordnet.getWordBySenseKey(senseKey);
        } catch (JWNLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getBaseFormOrNull(String word, Synset synset) {
        try {
            var baseForm = this.wordnet.getMorphologicalProcessor().lookupBaseForm(synset.getPOS(), word);
            if (baseForm != null) {
                return baseForm.getLemma();
            } else {
                return null;
            }
        } catch (JWNLException e) {
            throw new RuntimeException(e);
        }
    }

}
