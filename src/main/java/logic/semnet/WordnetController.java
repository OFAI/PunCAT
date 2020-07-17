package logic.semnet;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.dictionary.Dictionary;

import java.util.ArrayList;
import java.util.List;

public class WordnetController implements SemnetController<Synset> {
    private final Dictionary wordnet;

    public WordnetController() throws JWNLException {
        this.wordnet = Dictionary.getDefaultResourceInstance();
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

}
