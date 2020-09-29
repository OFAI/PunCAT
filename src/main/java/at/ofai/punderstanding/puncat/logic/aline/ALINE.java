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

package at.ofai.punderstanding.puncat.logic.aline;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.ejml.simple.SimpleMatrix;


public class ALINE {
    private final double C_skip;
    private final double C_sub;
    private final double C_exp;
    private final double C_vwl;
    private SimpleMatrix S;

    public ALINE() {
        this.C_skip = 10;
        this.C_sub = 35;
        this.C_exp = 45;
        this.C_vwl = 5;
    }

    public static double similarity(String str1, String str2) {
        return similarity(str1, str2, 1);
    }

    public static double similarity(String str1, String str2, double epsilon) {
        str1 = removeUnhandledChar(str1);
        str2 = removeUnhandledChar(str2);

        ALINE aline = new ALINE();
        aline.align(str1, str2, epsilon);
        double score12 = score(aline.S);

        ALINE aline11 = new ALINE();
        aline11.align(str1, str1, epsilon);
        double score11 = score(aline11.S);

        ALINE aline22 = new ALINE();
        aline22.align(str2, str2, epsilon);
        double score22 = score(aline22.S);

        return score12 / Math.max(score11, score22);
    }

    private static String removeUnhandledChar(String ipaTranscription) {
        var sb = new StringBuilder(ipaTranscription);
        int charIndex = sb.indexOf("ː");
        while (charIndex != -1) {
            sb.deleteCharAt(charIndex);
            charIndex = sb.indexOf("ː");
        }
        return sb.toString();
    }

    private static double score(SimpleMatrix S) {
        int rows = S.getMatrix().getNumRows();
        int cols = S.getMatrix().getNumCols();
        List<Double> rowMax = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            List<Double> items = new ArrayList<>();
            for (int j = 0; j < cols; j++) {
                items.add(S.get(i, j));
            }
            rowMax.add(Collections.max(items));
        }
        return Collections.max(rowMax) / 100;
    }

    public List<List<List<String>>> align(String str1, String str2, double epsilon) {
        int m = str1.length();
        int n = str2.length();
        SimpleMatrix S = new SimpleMatrix(m + 1, n + 1);
        S.fill(0);

        for (int i = 1; i < m + 1; i++) {
            for (int j = 1; j < n + 1; j++) {
                var edit1 = S.get(i - 1, j) + this.sigma_skip(str1.substring(i - 1, i));
                var edit2 = S.get(i, j - 1) + this.sigma_skip(str2.substring(j - 1, j));
                var edit3 = S.get(i - 1, j - 1) + this.sigma_sub(str1.substring(i - 1, i), str2.substring(j - 1, j));

                double edit4;
                if (i > 1) {
                    edit4 = S.get(i - 2, j - 1) + this.sigma_exp(str2.substring(j - 1, j), str1.substring(i - 2, i));
                } else {
                    edit4 = Double.NEGATIVE_INFINITY;
                }
                double edit5;
                if (j > 1) {
                    edit5 = S.get(i - 1, j - 2) + this.sigma_exp(str1.substring(i - 1, i), str2.substring(j - 2, j));
                } else {
                    edit5 = Double.NEGATIVE_INFINITY;
                }
                S.set(i, j, Collections.max(Arrays.asList(edit1, edit2, edit3, edit4, edit5, 0D)));
            }
        }

        double maxV = Double.NEGATIVE_INFINITY;
        int maxC = S.getMatrix().getNumCols();
        int maxR = S.getMatrix().getNumRows();
        for (int c = 0; c < maxC; c++) {
            for (int r = 0; r < maxR; r++) {
                if (S.get(r, c) > maxV) maxV = S.get(r, c);
            }
        }
        double T = (1 - epsilon) * maxV;

        List<List<List<String>>> alignments = new ArrayList<>();
        for (int i = 1; i < m + 1; i++) {
            for (int j = 1; j < n + 1; j++) {
                if (S.get(i, j) >= T) {
                    List<List<String>> out = new ArrayList<>();
                    alignments.add(this.retrieve(i, j, 0, S, T, str1, str2, out));
                }
            }
        }
        this.S = S;
        return alignments;
    }

    private List<List<String>> retrieve(int i, int j, double s, SimpleMatrix S, double T, String str1, String str2, List<List<String>> out) {
        if (S.get(i, j) == 0) {
            return out;
        } else {
            if (j > 1 && S.get(i - 1, j - 2) + this.sigma_exp(str1.substring(i - 1, i), str2.substring(j - 2, j)) + s >= T) {
                List<String> o = new ArrayList<>();
                o.add(str1.substring(i - 1, i));
                o.add(str2.substring(j - 2, j));
                out.add(0, o);
                this.retrieve(
                        i - 1,
                        j - 2,
                        s + this.sigma_exp(str1.substring(i - 1, i), str2.substring(j - 2, j)),
                        S,
                        T,
                        str1,
                        str2,
                        out
                );
            } else if (i > 1 && S.get(i - 2, j - 1) + this.sigma_exp(str2.substring(j - 1, j), str1.substring(i - 2, i)) + s >= T) {
                List<String> o = new ArrayList<>();
                o.add(str1.substring(i - 2, i));
                o.add(str2.substring(j - 1, j));
                out.add(0, o);
                this.retrieve(
                        i - 2,
                        j - 1,
                        s + this.sigma_exp(str2.substring(j - 1, j), str1.substring(i - 2, i)),
                        S,
                        T,
                        str1,
                        str2,
                        out
                );
            } else if (S.get(i, j - 1) + this.sigma_skip(str2.substring(j - 1, j)) + s >= T) {
                List<String> o = new ArrayList<>();
                o.add("-");
                o.add(str2.substring(j - 1, j));
                out.add(0, o);
                this.retrieve(
                        i,
                        j - 1,
                        s + this.sigma_skip(str2.substring(j - 1, j)),
                        S,
                        T,
                        str1,
                        str2,
                        out
                );
            } else if (S.get(i - 1, j) + this.sigma_skip(str1.substring(i - 1, i)) + s >= T) {
                List<String> o = new ArrayList<>();
                o.add(str1.substring(i - 1, i));
                o.add("-");
                out.add(0, o);
                this.retrieve(
                        i - 1,
                        j,
                        s + this.sigma_skip(str1.substring(i - 1, i)),
                        S,
                        T,
                        str1,
                        str2,
                        out
                );
            } else if (S.get(i - 1, j - 1) + this.sigma_sub(str1.substring(i - 1, i), str2.substring(j - 1, j)) + s >= T) {
                List<String> o = new ArrayList<>();
                o.add(str1.substring(i - 1, i));
                o.add(str2.substring(j - 1, j));
                out.add(0, o);
                this.retrieve(
                        i - 1,
                        j - 1,
                        s + this.sigma_sub(str1.substring(i - 1, i), str2.substring(j - 1, j)),
                        S,
                        T,
                        str1,
                        str2,
                        out
                );
            }
        }
        return out;
    }

    private double sigma_sub(String p, String q) {
        return this.C_sub - this.delta(p, q) - V(p) - V(q);
    }

    private double sigma_skip(String s) {
        return C_skip;
    }

    private double sigma_exp(String p, String q) {
        String q1 = q.substring(0, 1);
        String q2 = q.substring(1, 2);
        return this.C_exp - this.delta(p, q1) - this.delta(p, q2) - this.V(p) - Math.max(this.V(q1), this.V(q2));
    }

    private double delta(String p, String q) {
        var features = R(p, q);
        double total = 0;
        for (String f : features) {
            total += this.diff(p, q, f) * ALINEconsts.salience.get(f);
        }
        return total;
    }

    private double diff(String p, String q, String f) {
        var p_features = ALINEconsts.featureMatrix.get(p);
        var q_features = ALINEconsts.featureMatrix.get(q);
        try {
            return Math.abs(ALINEconsts.similarityMatrix.get(p_features.get(f)) - ALINEconsts.similarityMatrix.get(q_features.get(f)));
        } catch (NullPointerException e) {
            return 0;
        }

    }

    private HashSet<String> R(String p, String q) {
        if (ALINEconsts.consonants.contains(p) || ALINEconsts.consonants.contains(q)) {
            return ALINEconsts.R_c;
        }
        return ALINEconsts.R_v;
    }

    private double V(String p) {
        if (ALINEconsts.consonants.contains(p)) {
            return 0;
        }
        return C_vwl;
    }
}
