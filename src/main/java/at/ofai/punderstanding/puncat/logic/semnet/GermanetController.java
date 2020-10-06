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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import javax.xml.stream.XMLStreamException;

import de.tuebingen.uni.sfs.germanet.api.ConRel;
import de.tuebingen.uni.sfs.germanet.api.FilterConfig;
import de.tuebingen.uni.sfs.germanet.api.GermaNet;
import de.tuebingen.uni.sfs.germanet.api.RelDirection;
import de.tuebingen.uni.sfs.germanet.api.SemanticUtils;
import de.tuebingen.uni.sfs.germanet.api.Synset;
import net.sf.extjwnl.data.POS;

import at.ofai.punderstanding.puncat.logic.ResourcePaths;


public class GermanetController {
    static final HashMap<POS, String> posToIndexSuffix = new HashMap<>();

    static {
        posToIndexSuffix.put(POS.NOUN, "-n");
        posToIndexSuffix.put(POS.VERB, "-v");
        posToIndexSuffix.put(POS.ADJECTIVE, "-a");
        posToIndexSuffix.put(POS.ADVERB, "-r");
    }

    private final GermaNet germanet;
    private final GermanetFrequencies frequencies;

    public GermanetController() {
        try {
            this.germanet = new GermaNet(
                    ResourcePaths.germanet.toFile(),
                    ResourcePaths.nounfreq.toFile(),
                    ResourcePaths.verbfreq.toFile(),
                    ResourcePaths.adjfreq.toFile());
        } catch (IOException | XMLStreamException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }


        this.frequencies = GermanetFrequencies.loadFrequencies(
                ResourcePaths.nounfreq,
                ResourcePaths.verbfreq,
                ResourcePaths.adjfreq);
    }

    public List<Synset> getSynsets(String word) {
        if (word.toLowerCase().equals("gnroot")) {
            return List.of(this.germanet.getSynsetByID(GermaNet.GNROOT_ID));
        }
        FilterConfig filterConfig = new FilterConfig(word);
        filterConfig.setIgnoreCase(true);
        return this.germanet.getSynsets(filterConfig);
    }

    public Synset equivalentByWordnetOffset(POS pos, long offset) {
        String pwn30Id = offset + posToIndexSuffix.get(pos);
        var synonym = new ArrayList<Synset>();
        var near_synonym = new ArrayList<Synset>();
        var has_hyponym = new ArrayList<Synset>();
        var has_hypernym = new ArrayList<Synset>();
        var other = new ArrayList<Synset>();

        for (var ir : this.germanet.getIliRecords()) {
            if (ir.getPwn30Id().contains(pwn30Id)) {
                var relation = ir.getEwnRelation();
                switch (relation) {
                    case synonym -> synonym.add(germanet.getLexUnitByID(ir.getLexUnitId()).getSynset());
                    case near_synonym -> near_synonym.add(germanet.getLexUnitByID(ir.getLexUnitId()).getSynset());
                    case has_hyponym -> has_hyponym.add(germanet.getLexUnitByID(ir.getLexUnitId()).getSynset());
                    case has_hypernym -> has_hypernym.add(germanet.getLexUnitByID(ir.getLexUnitId()).getSynset());
                    default -> other.add(germanet.getLexUnitByID(ir.getLexUnitId()).getSynset());
                }
            }
        }
        if (!synonym.isEmpty()) {
            return synonym.get(0);
        } else if (!near_synonym.isEmpty()) {
            return near_synonym.get(0);
        } else if (!has_hyponym.isEmpty()) {
            return has_hyponym.get(0);
        } else if (!has_hypernym.isEmpty()) {
            return has_hypernym.get(0);
        } else if (!other.isEmpty()) {
            return other.get(0); // TODO: consider more relations
        } else {
            return null;
        }
    }

    public Long getSynsetCumulativeFrequency(Synset synset) {
        return this.frequencies.getSynsetCumulativeFrequency(synset);
    }

    public String getMostFrequentOrthForm(Synset synset) {
        return this.frequencies.getMostFrequentOrthForm(synset);
    }

    public List<Synset> getHypernyms(int synsetId) {
        Synset synset = this.germanet.getSynsetByID(synsetId);
        List<Synset> relations = synset.getRelatedSynsets(ConRel.has_hypernym, RelDirection.outgoing);
        relations.sort(Comparator.comparing(frequencies::getSynsetCumulativeFrequency));
        return relations;

    }

    public List<Synset> getHyponyms(int synsetId) {
        Synset synset = this.germanet.getSynsetByID(synsetId);
        List<Synset> relations = synset.getRelatedSynsets(ConRel.has_hyponym, RelDirection.outgoing);
        relations.sort(Comparator.comparing(frequencies::getSynsetCumulativeFrequency));
        return relations;
    }

    public Synset getSynsetById(int id) {
        return this.germanet.getSynsetByID(id);
    }

    public String getLexUnitById(int lexUnitId) {
        if (lexUnitId == 72133) {
            return this.germanet.getSynsetByID(GermaNet.GNROOT_ID).getLexUnits().get(0).getOrthForm();
        }
        return this.germanet.getLexUnitByID(lexUnitId).getOrthForm();
    }

    public SemanticUtils getSemanticUtils() {
        try {
            return this.germanet.getSemanticUtils();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}