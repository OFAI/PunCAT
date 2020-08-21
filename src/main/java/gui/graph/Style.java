package gui.graph;

import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxStylesheet;

import java.util.Hashtable;

public class Style {
    public static final Hashtable<String, Object> style = new Hashtable<>();
    static {
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
        style.put(mxConstants.STYLE_FILLCOLOR, "white");
        style.put(mxConstants.STYLE_STROKECOLOR, "black");
        style.put(mxConstants.STYLE_FONTCOLOR, "black");
        style.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, "ALIGN_CENTER");
        style.put(mxConstants.STYLE_LABEL_POSITION, "ALIGN_CENTER");
        style.put(mxConstants.STYLE_RESIZABLE, "FALSE");
    }

}
