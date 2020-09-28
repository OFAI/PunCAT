import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.dictionary.Dictionary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import at.ofai.punderstanding.puncat.logic.Search;
import at.ofai.punderstanding.puncat.logic.similarity.PhoneticSimilarity;
import at.ofai.punderstanding.puncat.logic.similarity.SemanticSimilarity;


public class SearchTests {
    private static Search search;
    private static ByteArrayOutputStream errContent;
    private static PrintStream originalErr;

    @BeforeAll
    static void configureEnvironment() {
        SharedConfig.configureEnvironment();
        search = new Search();
    }

    @BeforeEach
    void configureErrStream() {
        errContent = new ByteArrayOutputStream();
        originalErr = System.err;
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void restoreErrStream() {
        System.setErr(originalErr);
    }

    @Test
    void calculateSemanticSimilarity() {
        long catOffset = 2121620;
        int tierId = 48805;
        double result = search.calculateSemanticSimilarity(
                POS.NOUN, catOffset, tierId, SemanticSimilarity.algs.JiangAndConrath);
        Assertions.assertTrue(0 < result && 1 > result);
    }

    @Test
    void calculateSemanticSimilarity_sameWords() {
        long catOffset = 2121620;
        int katzeId = 48836;
        Assertions.assertEquals(1, search.calculateSemanticSimilarity(
                POS.NOUN, catOffset, katzeId, SemanticSimilarity.algs.JiangAndConrath)
        );
    }

    @Test
    void calculateSemanticSimilarity_noMapping() {
        long hoarseOffset = 299690;
        int heiserId = 1753;
        search.calculateSemanticSimilarity(
                POS.ADJECTIVE, hoarseOffset, heiserId, SemanticSimilarity.algs.JiangAndConrath);
        Assertions.assertFalse(errContent.toString().isEmpty());
    }

    @Test
    void calculateSemanticSimilarity_differentPOS() {
        long catOffset = 824767;
        int schelteId = 21172;
        search.calculateSemanticSimilarity(
                POS.NOUN, catOffset, schelteId, SemanticSimilarity.algs.JiangAndConrath);
        Assertions.assertEquals(0, search.calculateSemanticSimilarity(
                POS.NOUN, catOffset, schelteId, SemanticSimilarity.algs.JiangAndConrath)
        );
    }

    @Test
    void calculatePhoneticSimilarity_canFindTranscription() {
        String katze = "Katze";
        String hund = "Hund";
        search.calculatePhoneticSimilarity(katze, hund, PhoneticSimilarity.algs.ALINE);
        Assertions.assertTrue(errContent.toString().isEmpty());
    }

    @Test
    void calculatePhoneticSimilarity_canNotFindTranscription() {
        String katze = "schneller Hund";
        String hund = "Hund";
        search.calculatePhoneticSimilarity(katze, hund, PhoneticSimilarity.algs.ALINE);
        Assertions.assertFalse(errContent.toString().isEmpty());
    }

    @Test
    void calculatePhoneticSimilarity_notEqualTranscriptions() {
        String katze = "Katze";
        String hund = "Hund";
        Assertions.assertNotEquals(1,
                search.calculatePhoneticSimilarity(katze, hund, PhoneticSimilarity.algs.ALINE));
    }

    @Test
    void calculatePhoneticSimilarity_equalTranscriptions() {
        String katze = "Katze";
        String katze2 = "Katze";
        Assertions.assertEquals(1,
                search.calculatePhoneticSimilarity(katze, katze2, PhoneticSimilarity.algs.ALINE));
    }

    @Test
    void getIpaTranscriptionEnglish() {
        String word = "hadrosaur";
        Synset synset;
        try {
            var wordnet = Dictionary.getDefaultResourceInstance();
            synset = wordnet.getSynsetAt(POS.NOUN, 1705934);
        } catch (JWNLException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals("hædrʌsɔr", search.getIpaTranscriptionEnglish(word, synset));
    }

    @Test
    void getIpaTranscriptionEnglish_uppercase() {
        String word = "HADRoSAUR";
        Synset synset;
        try {
            var wordnet = Dictionary.getDefaultResourceInstance();
            synset = wordnet.getSynsetAt(POS.NOUN, 1705934);
        } catch (JWNLException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals("hædrʌsɔr", search.getIpaTranscriptionEnglish(word, synset));
    }

    @Test
    void getIpaTranscriptionEnglish_derivedForm() {
        String word = "laughing";
        Synset synset;
        try {
            var wordnet = Dictionary.getDefaultResourceInstance();
            synset = wordnet.getSynsetAt(POS.VERB, 31820);
        } catch (JWNLException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals("læf", search.getIpaTranscriptionEnglish(word, synset));
    }

    @Test
    void getIpaTranscriptionEnglish_noResult() {
        String word = "irgum-burgum";
        Synset synset;
        try {
            var wordnet = Dictionary.getDefaultResourceInstance();
            synset = wordnet.getSynsetAt(POS.VERB, 31820);
        } catch (JWNLException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals("", search.getIpaTranscriptionEnglish(word, synset));
    }

    @Test
    void getIpaTranscriptionGerman() {
        String word = "vogelnest";
        Assertions.assertEquals("foːgəlnɛst", search.getIpaTranscriptionGerman(word));
    }

    @Test
    void getIpaTranscriptionGerman_uppercase() {
        String word = "vOgElnEst";
        Assertions.assertEquals("foːgəlnɛst", search.getIpaTranscriptionGerman(word));
    }

    @Test
    void getIpaTranscriptionGerman_noResult() {
        String word = "irgum-burgum";
        Assertions.assertEquals("", search.getIpaTranscriptionGerman(word));
    }

    @Test
    void mapWordnetOffsetToGermanet() {
        Assertions.assertNotNull(search.wordnetSynsetToGermanetOrNull(POS.NOUN, 2037110));
    }

    @Test
    void mapWordnetOffsetToGermanet_noMapping() {
        Assertions.assertNull(search.wordnetSynsetToGermanetOrNull(POS.ADJECTIVE, 299690));
    }
}
