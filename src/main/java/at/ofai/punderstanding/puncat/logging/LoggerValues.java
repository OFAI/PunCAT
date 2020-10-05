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

package at.ofai.punderstanding.puncat.logging;

public class LoggerValues {
    public static final String LOGGING_DISABLED = "interactionloggingdisabled";

    public static final String EVENT = "event";

    public static final String NEW_VALUE = "newValue";
    public static final String PANEL_ID = "panelId";
    public static final String AUTO_SELECTED_SYNSET_ID = "autoSelectedSynsetId";
    public static final String SELECTED_SYNSET_ID = "selectedSynsetId";
    public static final String SELECTION_INDEX = "selectionIndex";
    public static final String NEW_KEYWORD = "newKeyword";
    public static final String NODE_SYNSET_ID = "synsetId";

    public static final String CANDIDATE_PUN = "pun";
    public static final String CANDIDATE_TARGET = "target";
    public static final String CANDIDATE_PHON = "phon";
    public static final String CANDIDATE_SEM = "sem";
    public static final String CANDIDATE_SEM_ALG = "semAlg";
    public static final String CANDIDATE_PHON_ALG = "phonAlg";

    public static final String PUNCAT_STARTED_EVENT = "PunCAT started";
    public static final String PUNCAT_CLOSED_EVENT = "PunCAT closed";

    public static final String FIRST_TASK_BUTTON_CLICKED_EVENT = "'First task (<<)' button clicked";
    public static final String PREVIOUS_TASK_BUTTON_CLICKED_EVENT = "'Previous task (<)' button clicked";
    public static final String NEXT_TASK_BUTTON_CLICKED_EVENT = "'Next task (>)' button clicked";
    public static final String LAST_TASK_BUTTON_CLICKED_EVENT = "'Last task (>>)' button clicked";

    public static final String PREV_INSTANCE_ID = "prevInstanceId";
    public static final String CURRENT_INSTANCE_ID = "currentInstanceId";

    public static final Object FIRST_GRAPH_BUTTON_CLICKED_EVENT = "'First graph' button clicked";
    public static final String PREV_GRAPH_BUTTON_CLICKED_EVENT = "'Prev graph' button clicked";
    public static final String NEXT_GRAPH_BUTTON_CLICKED_EVENT = "'Next graph' button clicked";
    public static final Object LAST_GRAPH_BUTTON_CLICKED_EVENT = "'Last graph' button clicked";
    public static final String PREV_GRAPH_IDX = "prevGraphIdx";
    public static final String CURRENT_GRAPH_IDX = "currentGraphIdx";
    public static final String GRAPH_PANE_ID = "Graph pane id";

    public static final String SOURCE_KEYWORD_CHANGED_EVENT = "Source keyword changed";
    public static final String SOURCE_SENSE_SELECTED_EVENT = "Source sense selected";

    public static final String TARGET_KEYWORD_CHANGED_EVENT = "Target keyword changed";
    public static final String TARGET_SENSE_SELECTED_EVENT = "Target sense selected";

    public static final String GRAPH_NODE_HOVERED_EVENT = "Graph node hovered";  // TODO: delete if layout improved
    public static final String GRAPH_NODE_CLICKED_EVENT = "Graph node clicked";

    public static final String SELECTION_ON_ROOT_NODE = "Selection on root node";
    public static final String PREV_ORTH_FORM = "Previous orth form";
    public static final String NEW_ORTH_FORM = "New orth form";

    public static final String NEW_CANDIDATE_ADDED_EVENT = "New candidate added";

    public static final String SEM_ALG_CHANGED_EVENT = "Semantic algorithm changed";
    public static final String PHON_ALG_CHANGED_EVENT = "Phonetic algorithm changed";
    public static final String NEW_ALG = "Selected algorithm";

    public static final String CORPUS_OPENED_EVENT = "Corpus xml file opened";
    public static final String CORPUS_FILE = "file";

    public static final Object CSV_EXPORT_EVENT = "Csv exported";
    public static final String CSV_PATH = "Csv path";
}
