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
import de.tuebingen.uni.sfs.germanet.api.IliRecord;
import de.tuebingen.uni.sfs.germanet.api.RelDirection;
import de.tuebingen.uni.sfs.germanet.api.SemanticUtils;
import de.tuebingen.uni.sfs.germanet.api.Synset;
import net.sf.extjwnl.data.POS;

import at.ofai.punderstanding.puncat.logic.ResourcePaths;


public class GermanetController implements SemnetController<Synset> {
    static HashMap<POS, String> posToIndexSuffix = new HashMap<>();

    static {
        posToIndexSuffix.put(POS.NOUN, "-n");
        posToIndexSuffix.put(POS.VERB, "-v");
        posToIndexSuffix.put(POS.ADJECTIVE, "-a");
        posToIndexSuffix.put(POS.ADVERB, "-r");
    }

    private final GermaNet germanet;
    private final GermanetFrequencies frequencies;

    public GermanetController() throws IOException, XMLStreamException {
        this.germanet = new GermaNet(
                getClass().getResource(ResourcePaths.germaNetLocation).getPath(),
                getClass().getResource(ResourcePaths.nounFreq).getPath(),
                getClass().getResource(ResourcePaths.verbFreq).getPath(),
                getClass().getResource(ResourcePaths.adjFreq).getPath());
        this.frequencies = GermanetFrequencies.loadFrequencies(
                getClass().getResource(ResourcePaths.nounFreq).getPath(),
                getClass().getResource(ResourcePaths.verbFreq).getPath(),
                getClass().getResource(ResourcePaths.adjFreq).getPath());
    }

    @Override
    public List<Synset> getSynsets(String word) {
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

    public List<Long> getOffsetFromID(int id) {
        List<Long> offsets = new ArrayList<>();
        for (IliRecord ir : this.germanet.getSynsetByID(id).getIliRecords()) {
            offsets.add(Long.parseLong(ir.getPwn30Id()));
        }
        return offsets;
    }

    public GermaNet getObject() {
        return this.germanet;
    }

    public Long getSynsetCumulativeFrequency(Synset synset) {
        return this.frequencies.getSynsetCumulativeFrequency(synset);
    }

    public String getMostFrequentOrthForm(Synset synset) {
        return this.frequencies.getMostFrequentOrthForm(synset);
    }

    public List<Synset> getHypernyms(int synsetId) {
        Synset synset = this.germanet.getSynsetByID(synsetId);
        List<Synset> relations = synset.getRelatedSynsets(ConRel.has_hypernym, RelDirection.incoming);
        relations.sort(Comparator.comparing(frequencies::getSynsetCumulativeFrequency));
        return relations;

    }

    public List<Synset> getHyponyms(int synsetId) {
        Synset synset = this.germanet.getSynsetByID(synsetId);
        List<Synset> relations = synset.getRelatedSynsets(ConRel.has_hyponym, RelDirection.incoming);
        relations.sort(Comparator.comparing(frequencies::getSynsetCumulativeFrequency));
        return relations;
    }

    public Synset getSynsetById(int id) {
        return this.germanet.getSynsetByID(id);
    }

    public String getLexUnitById(int lexUnitId) {
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