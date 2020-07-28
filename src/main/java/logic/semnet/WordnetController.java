package logic.semnet;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;
import net.sf.extjwnl.dictionary.Dictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WordnetController implements SemnetController<Synset> {
    // TODO: maybe no need for semnet "controller" classes
    private final Dictionary wordnet;

    public WordnetController() throws JWNLException {
        this.wordnet = Dictionary.getDefaultResourceInstance();
    }

    public WordnetController(Dictionary wordnet) {
        this.wordnet = wordnet;
    }

    @Override
    public List<Synset> getSynsets(String word) {
        List<Synset> results = new ArrayList<>();
        try {
            for (POS p : POS.getAllPOS()) {
                IndexWord iw = this.wordnet.lookupIndexWord(p, word);
                if (iw != null) {
                    results.addAll(iw.getSenses());
                }
            }
        } catch (JWNLException e) {
            System.out.println(e.getMessage());
        }
        return results;  // TODO: check if empty
    }

    public List<String> getWordsByOffset(Long offset) {
        List<String> words = new ArrayList<>();
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

    public List<String> getWordsByOffsetList(List<Long> offsets) {
        List<String> words = new ArrayList<>();
        for (Long o : offsets) {
            System.out.println(o);
            words.addAll(this.getWordsByOffset(o));
        }
        return words;
    }
}
