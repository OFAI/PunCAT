package at.ofai.punderstanding.puncat.logic.semnet;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;
import net.sf.extjwnl.dictionary.Dictionary;


public class WordnetController implements SemnetController<Synset> {
    private final Dictionary wordnet;

    public WordnetController() throws JWNLException {
        this.wordnet = Dictionary.getDefaultResourceInstance();
    }

    public WordnetController(Dictionary wordnet) {
        this.wordnet = wordnet;
    }

    @Override
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

    public List<String> getWordsByOffset(Long offset) {
        var words = new ArrayList<String>();
        for (POS p : POS.getAllPOS()) {
            try {
                Synset synset = this.wordnet.getSynsetAt(p, offset);
                if (synset != null) {
                    words.addAll(this.wordnet.getSynsetAt(p, offset).getWords().
                            stream().map(Word::toString).collect(Collectors.toList())
                    );
                }
            } catch (JWNLException e) {
                e.printStackTrace();
            }
        }
        return words;
    }

    public Dictionary getObject() {
        return this.wordnet;
    }

    public String getBaseForm(String word, Synset synset) {
        try {
            return this.wordnet.getMorphologicalProcessor().lookupBaseForm(synset.getPOS(), word).getLemma();
        } catch (JWNLException e) {
            throw new RuntimeException(e);
        }
    }
}
