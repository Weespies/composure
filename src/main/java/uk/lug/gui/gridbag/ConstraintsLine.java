/*
 * Created on 01-Jun-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package uk.lug.gui.gridbag;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.apache.commons.lang.NumberUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author Luggy
 *
 * This class is a convienience class for translating constraints to and from single line strings.
 */
public class ConstraintsLine {
    private static Hashtable<String,String> constraints = null;
    private static final String[] anchorStrings = {
        "NORTH","NORTHEAST","EAST","SOUTHEAST",
        "SOUTH","SOUTHWEST","WEST","NORTHWEST",
        "CENTER","PAGE_START","PAGE_END","LINE_START",
        "LINE_END","FIRST_LINE_START","FIRST_LINE_END",
        "LAST_LINE_START","LAST_LINE_END"};
    private static final int[] anchorInts = {
        GridBagConstraints.NORTH,       GridBagConstraints.NORTHEAST,
        GridBagConstraints.EAST,        GridBagConstraints.SOUTHEAST,   GridBagConstraints.SOUTH,
        GridBagConstraints.SOUTHWEST,   GridBagConstraints.WEST,        GridBagConstraints.NORTHWEST,
        GridBagConstraints.CENTER,      GridBagConstraints.PAGE_START,  GridBagConstraints.PAGE_END,
        GridBagConstraints.LINE_START,  GridBagConstraints.LINE_END,    GridBagConstraints.FIRST_LINE_START,
        GridBagConstraints.FIRST_LINE_END,                              GridBagConstraints.LAST_LINE_START,
        GridBagConstraints.LAST_LINE_END};
    private static final String[] fillStrings = {"NONE","HORIZONTAL","VERTICAL","BOTH"};
    private static final int[] fillInts = {GridBagConstraints.NONE,GridBagConstraints.HORIZONTAL,
        GridBagConstraints.VERTICAL,GridBagConstraints.BOTH};
    
    /**
     * Convert this string to a constraints object
     */
    public static GridBagConstraints getConstraints(String s) {
        GridBagConstraints c =new GridBagConstraints();
        constraints = getValueTable(s);
        applyGrid(c);
        applyWeights(c);
        applyPosition(c);
        applyInsets(c);
        applyPadding(c);
        return c;
    }
   
    /**
     * Convert a GridBagConstraints object to a string (one capable of bieng parsed by this class).
     * @param c The GridBagConstraints to be parsed.
     * @return The String.
     */
    public static String getString(GridBagConstraints c) {
        StringBuilder builder = new StringBuilder();
        
        //Grid
        builder.append("gridx=");
        builder.append(c.gridx);
        builder.append("|gridy=");
        builder.append(c.gridy);
        builder.append("|gridwidth=");
        builder.append(c.gridwidth);
        builder.append("|gridheight=");
        builder.append(c.gridheight);
               
        //Weightings
        builder.append("|weightx=");
        builder.append(c.weightx);
        builder.append("|weighty=");
        builder.append(c.weighty);
        
        //Positional (anchor & fill)
        builder.append("|anchor=");
        for (int i=0;i<anchorInts.length;i++) {
            if (anchorInts[i]==c.anchor) {
                builder.append(anchorStrings[i]);
                break;
            }
        }
        builder.append("|fill=");
        for (int i=0;i<fillInts.length;i++) {
            if (fillInts[i]==c.fill) {
                builder.append(fillStrings[i]);
            }
        }
        
        //Insets
        builder.append("|insets=");
        builder.append(c.insets.top);
        builder.append(",");
        builder.append(c.insets.left);
        builder.append(",");
        builder.append(c.insets.bottom);
        builder.append(",");
        builder.append(c.insets.right);
        
        //Padding
        builder.append("|ipadx=");
        builder.append(c.ipadx);
        builder.append("|ipady=");
        builder.append(c.ipady);
        
        return builder.toString();
    }
    
    /**
     * Applies the gridx, gridy, gridwidth and gridheight values from the current table of entries.
     * @param c
     */
    private static void applyGrid(GridBagConstraints c) {
        String xStr = constraints.get("gridx");
        String yStr = constraints.get("gridy");
        String wStr = constraints.get("gridwidth");
        String hStr = constraints.get("gridheight");
        if (StringUtils.isNumeric(xStr)) {
            c.gridx=Integer.parseInt(xStr);
        }
        if (StringUtils.isNumeric(yStr)) {
            c.gridy=Integer.parseInt(yStr);
        }
        if (StringUtils.isNumeric(wStr)) {
            c.gridwidth=Integer.parseInt(wStr);
        }
        if (StringUtils.isNumeric(hStr)) {
            c.gridheight=Integer.parseInt(hStr);
        }
    }
    
    /**
     * Applies weighting (resizing) values to the constraint from the current table of values.
     * @param c
     */
    private static void applyWeights(GridBagConstraints c) {
        String xstr = constraints.get("weightx");
        String ystr = constraints.get("weighty");
        if (NumberUtils.isNumber(xstr)) {
            c.weightx=Double.parseDouble(xstr);
        }
        if (NumberUtils.isNumber(ystr)) {
            c.weighty=Double.parseDouble(ystr);
        }
    }
   
    /**
     * Applies the positional values (fill and anchor) from the current table of values.
     * @param c
     */
    private static void applyPosition(GridBagConstraints c) {
        String astr = constraints.get("anchor");
        String fstr = constraints.get("fill");
        //apply anchor
        for (int i=0;i<anchorStrings.length;i++) {
            if (anchorStrings[i].equalsIgnoreCase(astr)) {
                c.anchor = anchorInts[i];
                break;
            }
        }
        //apply fill
        for (int i=0;i<fillStrings.length;i++) {
            if (fillStrings[i].equalsIgnoreCase(fstr)) {
                c.fill = fillInts[i];
            }
        }
    }
    
    /**
     * Applies the insets value from the current table of values.
     * @param c
     */
    private static void applyInsets(GridBagConstraints c) {
        String istr = getValue("insets");
        if ( (StringUtils.isEmpty(istr))  || (StringUtils.countMatches( istr,",")!=3) ){
            return;
        }
        StringTokenizer token = new StringTokenizer(istr,",",false);
        String tstr = token.nextToken();
        String lstr = token.nextToken();
        String bstr = token.nextToken();
        String rstr = token.nextToken();
        int t=0;
        int l=0;
        int b=0;
        int r=0;
        if  (StringUtils.isNumeric(tstr)) {
            l=Integer.parseInt(tstr);
        }
        if  (StringUtils.isNumeric(lstr)) {
            l=Integer.parseInt(lstr);
        }
        if  (StringUtils.isNumeric(bstr)) {
            b=Integer.parseInt(bstr);
        }
        if  (StringUtils.isNumeric(rstr)) {
            r=Integer.parseInt(rstr);
        }
        c.insets = new Insets(t,l,b,r);
    }

    private static void applyPadding(GridBagConstraints c) {
        String xstr = constraints.get("ipadx");
        String ystr = constraints.get("ipady");
        if (StringUtils.isNumeric(xstr)) {
            c.ipadx = Integer.parseInt(xstr);
        }
        if (StringUtils.isNumeric(ystr)) {
            c.ipady = Integer.parseInt(xstr);
        }
    }
    
    /**
     *  Converts the supplied line into a Hashtable.
     */
    private static Hashtable<String,String> getValueTable(String line) {
        Hashtable<String,String> res =new Hashtable<String,String>();
        StringTokenizer token = new StringTokenizer(line,"|",false);
        while (token.hasMoreTokens()) {
            String entry = token.nextToken();
            res.put(getName(entry),getValue(entry));
        }
        return res;
    }

    /**
     * Returns the name part of this name/value pair.
     */
    private static String getName(String pair) {
        int ptr = pair.indexOf("=");
        if (ptr==-1) {
            return null;
        } else {
            return pair.substring(0,ptr);
        }
    }
    
    /**
     * @param pair
     * @return the value for this name/value pair.
     */
    private static String getValue(String pair) {
        int ptr=pair.indexOf("=");
        if (ptr==1) {
            return null;
        } else {
            return pair.substring(ptr+1,pair.length());
        }
    }
}
