package uk.lug.gui.gridbag;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Utility class for parsing xml into GridBagConstraints.*/
public class GridBagParser {
	private static final String[] anchorStrings = {
		"NORTH","NORTHEAST","EAST","SOUTHEAST",
		"SOUTH","SOUTHWEST","WEST","NORTHWEST",
		"CENTER","PAGE_START","PAGE_END","LINE_START",
		"LINE_END","FIRST_LINE_START","FIRST_LINE_END",
		"LAST_LINE_START","LAST_LINE_END"};
	private static final int[] anchorInts = {
		GridBagConstraints.NORTH,		GridBagConstraints.NORTHEAST,
		GridBagConstraints.EAST,		GridBagConstraints.SOUTHEAST,	GridBagConstraints.SOUTH,
		GridBagConstraints.SOUTHWEST,	GridBagConstraints.WEST,		GridBagConstraints.NORTHWEST,
		GridBagConstraints.CENTER,		GridBagConstraints.PAGE_START,	GridBagConstraints.PAGE_END,
		GridBagConstraints.LINE_START,	GridBagConstraints.LINE_END,	GridBagConstraints.FIRST_LINE_START,
		GridBagConstraints.FIRST_LINE_END,								GridBagConstraints.LAST_LINE_START,
		GridBagConstraints.LAST_LINE_END};
	private static final String[] fillStrings = {"NONE","HORIZONTAL","VERTICAL","BOTH"};
	private static final int[] fillInts = {GridBagConstraints.NONE,GridBagConstraints.HORIZONTAL,
		GridBagConstraints.VERTICAL,GridBagConstraints.BOTH};
	private Element source =null;
	public static final String layoutName = "gridBagLayout";
	private Insets globalInsets = null;

    /**
     * Empty constructor.
     */
    public GridBagParser() {
    }
    
	/**
	 * Construct a GridBagParser from a file.*/
	public GridBagParser(File f) throws IOException,JDOMException {
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(f);
		Element root = doc.getRootElement();
		if (root.getName().equals(layoutName)==false) {
			throw new IllegalArgumentException("Root element is of type \""+
				root.getName()+"\" not \""+layoutName+"\".");
		}
		source = root;
		if (source.getChild("insets")!=null) {
			globalInsets = getInsets(source.getChild("insets"));
		}
	}

	/**
	 * Construct a GridBagParser from a url.*/
	public GridBagParser(URL url) throws IOException,JDOMException {
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(url);
		Element root = doc.getRootElement();
		if (root.getName().equals(layoutName)==false) {
			throw new IllegalArgumentException("Root element is of type \""+
				root.getName()+"\" not \""+layoutName+"\".");
		}
		source = root;
		if (source.getChild("insets")!=null) {
			globalInsets = getInsets(source.getChild("insets"));
		}
	}

	/**
	 * Construct a GridBagParser from java.io.Reader.*/
	public GridBagParser(java.io.Reader reader) throws IOException,JDOMException {
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(reader);
		Element root = doc.getRootElement();
		if (root.getName().equals(layoutName)==false) {
			throw new IllegalArgumentException("Root element is of type \""+
				root.getName()+"\" not \""+layoutName+"\".");
		}
		source = root;
		if (source.getChild("insets")!=null) {
			globalInsets = getInsets(source.getChild("insets"));
		}
	}

	/**
	 * Construct a GridBagParser from an InputStream.*/
	public GridBagParser(InputStream in) throws IOException,JDOMException {
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(in);
		Element root = doc.getRootElement();
		if (root.getName().equals(layoutName)==false) {
			throw new IllegalArgumentException("Root element is of type \""+
				root.getName()+"\" not \""+layoutName+"\".");
		}
		source = root;
		if (source.getChild("insets")!=null) {
			globalInsets = getInsets(source.getChild("insets"));
		}
	}

	/**
	 * Construct a GridBagParser from an URI.*/
	public GridBagParser(String systemID) throws IOException,JDOMException {
		//find url
		ClassLoader loader = this.getClass().getClassLoader();
		URL url= loader.getResource(systemID);
		if (url==null) {
			throw new IOException("Cannot located resource : \""+systemID+"\".");
		}
		//process from url
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(url);
		Element root = doc.getRootElement();
		if (root.getName().equals(layoutName)==false) {
			throw new IllegalArgumentException("Root element is of type \""+
				root.getName()+"\" not \""+layoutName+"\".");
		}
		source = root;
		if (source.getChild("insets")!=null) {
			globalInsets = getInsets(source.getChild("insets"));
		}
	}



	/**
	 * Construct a GridBagParser from an layout element*/
	public GridBagParser(Element xml) {
		if (xml.getName().equals(layoutName)==false) {
			throw new IllegalArgumentException("Root element is of type \""+
				xml.getName()+"\" not \""+layoutName+"\".");
		}
		source = xml;
		if (source.getChild("insets")!=null) {
			globalInsets = getInsets(source.getChild("insets"));
		}
	}

	/**
	 * Return the source element for this parser.*/
	public Element getSourceElement() {
		return source;
	}

	/**
	 * Construct a XML element for the given GridBagConstraints.*/
	public static Element getXML(GridBagConstraints gc,String name) throws GridBagException {
		Element xml = new Element("component");
		xml.setAttribute("name",name);

		//Grid element
		Element grid = new Element("grid");
		grid.setAttribute("x",Integer.toString(gc.gridx));
		grid.setAttribute("y",Integer.toString(gc.gridy));
		grid.setAttribute("width",Integer.toString(gc.gridwidth));
		grid.setAttribute("height",Integer.toString(gc.gridheight));
		xml.addContent(grid);

		//resize
		Element resize = new Element("resize");
		resize.setAttribute("fill",getFillString(gc.fill));
		resize.setAttribute("anchor",getAnchorString(gc.anchor));
		resize.setAttribute("weightx",Double.toString(gc.weightx));
		resize.setAttribute("weighty",Double.toString(gc.weighty));
		xml.addContent(resize);

		//Insets
		Element ins = new Element("insets");
		ins.setAttribute("top",Integer.toString(gc.insets.top));
		ins.setAttribute("left",Integer.toString(gc.insets.left));
		ins.setAttribute("right",Integer.toString(gc.insets.right));
		ins.setAttribute("bottom",Integer.toString(gc.insets.bottom));
		xml.addContent(ins);

		//Ipad
		Element ipad = new Element("ipad");
		ipad.setAttribute("x",Integer.toString(gc.ipadx));
		ipad.setAttribute("y",Integer.toString(gc.ipady));
		xml.addContent(ipad);

		return xml;
	}

	/**
	 * Parse the insets out from the given element.*/
	private static Insets getInsets(Element xml) {
		if (xml.getName().equals("insets")==false) {
			throw new IllegalArgumentException("Wrong element type for insets.");
		}
		Insets res = new Insets(0,0,0,0);
		String top = xml.getAttributeValue("top");
		String left = xml.getAttributeValue("left");
		String right = xml.getAttributeValue("right");
		String bottom = xml.getAttributeValue("bottom");
		if (StringUtils.isEmpty(top)==false) {
			res.top = Integer.parseInt(top);
		}
		if (StringUtils.isEmpty(left)==false) {
			res.left = Integer.parseInt(left);
		}
		if (StringUtils.isEmpty(right)==false) {
			res.right = Integer.parseInt(right);
		}
		if (StringUtils.isEmpty(bottom)==false) {
			res.bottom = Integer.parseInt(bottom);
		}
		return res;
	}

	/**
	 * Return a completed GridBagConstraints for the given component name.
	 * @param gc GridBagConstraints to fill out.  If null then a new one
	 * will be created.
	 * @param name name of the component.  Matches the name attribute of the
	 * desired <component>*/
	public GridBagConstraints parse(GridBagConstraints gc,String name) throws GridBagException {
		if (gc==null) {
			gc = new GridBagConstraints();
		}
		Element xml = getElement(name);
		if (xml==null) {
			throw new GridBagException("Unknown component \""+name+"\".");
		}

		//grid
		setGrid(gc,xml);
		//Insets
		if (xml.getChild("insets")!=null) {
			gc.insets = getInsets(xml.getChild("insets"));
		}
		//resize
		Element resize = xml.getChild("resize");
		if (resize!=null) {
			setResize(gc,resize);
		}
		//Ipad
		Element ipad = xml.getChild("ipad");
		if (ipad!=null) {
			setIpad(gc,ipad);
		}
		return gc;
	}

	/**
	 * Return a completed GridBagConstraints for the given component name.
	 * @param name name of the component.  Matches the name attribute of the
	 * desired <component>*/
	public GridBagConstraints parse(String name) throws GridBagException {
		GridBagConstraints gc = new GridBagConstraints();
		Element xml = getElement(name);
		if (xml==null) {
			throw new GridBagException("Unknown component \""+name+"\".");
		}

		//grid
		setGrid(gc,xml);
		//Insets
		if (xml.getChild("insets")!=null) {
			gc.insets = getInsets(xml.getChild("insets"));
		}
		//resize
		Element resize = xml.getChild("resize");
		if (resize!=null) {
			setResize(gc,resize);
		}
		//Ipad
		Element ipad = xml.getChild("ipad");
		if (ipad!=null) {
			setIpad(gc,ipad);
		}
		return gc;
	}

	/**
	 * Extract the grid(x,y,w,h) for the given element.*/
	private void setGrid(GridBagConstraints gc, Element xml) throws GridBagException{
		Element grid = xml.getChild("grid");
		//X Position
		String xs = grid.getAttributeValue("x");
		if (StringUtils.isEmpty(xs)) {
			throw new GridBagException("Grid element missing attribute \"x\".");
		}
		try {
			gc.gridx = Integer.parseInt(xs);
		} catch (NumberFormatException nfe) {
			throw new GridBagException("Cannot parse \""+xs+"\" as a number for gridx");
		}
		//Y Position
		String ys = grid.getAttributeValue("y");
		if (StringUtils.isEmpty(ys)) {
			throw new GridBagException("Grid element missing attribute \"x\".");
		}
		try {
			gc.gridy = Integer.parseInt(ys);
		} catch (NumberFormatException nfe) {
			throw new GridBagException("Cannot parse \""+xs+"\" as a number for gridy");
		}
		//Gridwidth
		String ws = grid.getAttributeValue("width");
		if (StringUtils.isEmpty(ws)==false) {
			try {
				gc.gridwidth = Integer.parseInt(ws);
			} catch (NumberFormatException nfe1) {
				throw new GridBagException("Cannot parse \""+ws+"\" as gridwidth.");
			}
		} else {
			gc.gridwidth=1;
		}
		//Gridwidth
		String hs = grid.getAttributeValue("height");
		if (StringUtils.isEmpty(hs)==false) {
			try {
				gc.gridheight = Integer.parseInt(hs);
			} catch (NumberFormatException nfe1) {
				throw new GridBagException("Cannot parse \""+hs+"\" as gridheight.");
			}
		} else {
			gc.gridheight=1;
		}
	}

	/**
	 * Sets the resize (anchor, fill , weightx & weighty) for a GridBagConstraints.*/
	private void setResize(GridBagConstraints gc, Element xml) throws GridBagException {
		//Weightx
		String wxs = xml.getAttributeValue("weightx");
		if (StringUtils.isEmpty(wxs)==false) {
			try {
				gc.weightx = Double.parseDouble(wxs);
			} catch (NumberFormatException nfe1) {
				throw new GridBagException("Cannot parse \""+wxs+"\" as weightx.");
			}
		}
		//Weighty
		String wys = xml.getAttributeValue("weighty");
		if (StringUtils.isEmpty(wys)==false) {
			try {
				gc.weighty = Double.parseDouble(wys);
			} catch (NumberFormatException nfe1) {
				throw new GridBagException("Cannot parse \""+wys+"\" as weighty.");
			}
		}
		//Anchor
		String anc = xml.getAttributeValue("anchor");
		if (StringUtils.isEmpty(anc)==false) {
			gc.anchor=getAnchorValue(anc);
		} else {
			gc.anchor=GridBagConstraints.CENTER;
		}
		//Fill
		String fil = xml.getAttributeValue("fill");
		if (StringUtils.isEmpty(fil)==false) {
			gc.fill=getFillValue(fil);
		} else {
			gc.fill=GridBagConstraints.NONE;
		}
	}

	/**
	 * Sets the internal padding (ipadx,ipady) values.*/
	private void setIpad(GridBagConstraints gc, Element xml) throws GridBagException {
		//ipadx
		String ix = xml.getAttributeValue("x");
		if (StringUtils.isEmpty(ix)==false) {
			try {
				gc.ipadx = Integer.parseInt(ix);
			} catch (NumberFormatException nfe1) {
				throw new GridBagException("Cannot parse \""+ix+"\" as ipadx.");
			}
		}
		//ipadx
		String iy = xml.getAttributeValue("y");
		if (StringUtils.isEmpty(iy)==false) {
			try {
				gc.ipady = Integer.parseInt(iy);
			} catch (NumberFormatException nfe1) {
				throw new GridBagException("Cannot parse \""+iy+"\" as ipady.");
			}
		}
	}

	/**
	 * Return a GridBagConstraints.anchor value for a given string.*/
	private static final int getAnchorValue(String s) throws GridBagException {
		for (int i=0;i<anchorStrings.length;i++) {
			if (StringUtils.equals(s,anchorStrings[i])) {
				return anchorInts[i];
			}
		}
		throw new GridBagException("Invalid constant \""+s+"\" for anchor.");
	}

	/**
	 * Return a string description of a GridBagConstraints.anchor value.*/
	private static final String getAnchorString(int v) throws GridBagException {
		for (int i=0;i<anchorInts.length;i++) {
			if (anchorInts[i]==v) {
				return anchorStrings[i];
			}
		}
		throw new GridBagException("Unknown GridBagConstraints.anchor value "+Integer.toString(v));
	}

	/**
	 * Return a GridBagConstraints.fill value for a given string.*/
	private static final int getFillValue(String s) throws GridBagException {
		for (int i=0;i<fillStrings.length;i++) {
			if (StringUtils.equals(s,fillStrings[i])) {
				return fillInts[i];
			}
		}
		throw new GridBagException("Invalid constant \""+s+"\" for fill.");
	}

	/**
	 * Return a string description of a GridBagConstraints.fill value.*/
	private static final String getFillString(int v) throws GridBagException {
		for (int i=0;i<fillInts.length;i++) {
			if (fillInts[i]==v) {
				return fillStrings[i];
			}
		}
		throw new GridBagException("Unknown GridBagConstraints.fill value "+Integer.toString(v));
	}


	/**
	 * Conver the gridx,y,w,h to a string.*/
	public static String getGridString(GridBagConstraints gc) {
		String res="(";
		res=res+Integer.toString(gc.gridx)+",";
		res=res+Integer.toString(gc.gridy)+")-(";
		res=res+Integer.toString(gc.gridwidth)+" x ";
		res=res+Integer.toString(gc.gridheight)+" )";
		return res;
	}

	/**
	 * Find the given component name's xml element.*/
	private Element getElement(String name) {
		java.util.List children = source.getChildren("component");
		for (int i=0;i<children.size();i++) {
			Element e= (Element)children.get(i);
			if (StringUtils.equals(e.getAttributeValue("name"),name) ) {
				return e;
			}
		}
		return null;
	}

	/**
	 * Show the insets definition as a string.*/
	public static String getInsetsString(Insets i) {
		String res="(";
		res=res+Integer.toString(i.top)+",";
		res=res+Integer.toString(i.left)+",";
		res=res+Integer.toString(i.bottom)+",";
		res=res+Integer.toString(i.right)+")";
		return res;
	}

	/**
	 * Returns the a string containing resize info.*/
	public static String getResizeString(GridBagConstraints gc) throws GridBagException {
		String res="Wgt=(";
		res=res+Double.toString(gc.weightx)+" by ";
		res=res+Double.toString(gc.weighty)+")-";
		res=res+getFillString(gc.fill)+"@";
		res=res+getAnchorString(gc.anchor);
		return res;
	}

	/**
	 * Returns the a string containing ipad info.*/
	public static String getIpadString(GridBagConstraints gc) throws GridBagException {
		String res="ipad=(";
		res=res+Integer.toString(gc.ipadx)+" by ";
		res=res+Integer.toString(gc.ipady)+")";
		return res;
	}

	/**
	 * Converts the entire source GridBagLayout from XML to Properties.*/
	public Properties getLayoutProperties() throws GridBagException {
		Properties props = new Properties();
		java.util.List children = source.getChildren("component");
		for (int i=0;i<children.size();i++) {
		    Element e = (Element)children.get(i);
			String name=e.getAttributeValue("name");
			GridBagConstraints c= parse(name);
			storeConstraintsProperties(c,name,props);
		}
		return props;
	}

	/**
	 * Return a Properties version of a components GridBagConstraints.*/
	private void storeConstraintsProperties(GridBagConstraints c,String name,Properties p) throws GridBagException {
		//Grid properties
		p.setProperty(name+".gridx",Integer.toString(c.gridx));
		p.setProperty(name+".gridy",Integer.toString(c.gridy));
		p.setProperty(name+".gridwidth",Integer.toString(c.gridwidth));
		p.setProperty(name+".gridheight",Integer.toString(c.gridheight));
		//Resize properties
		p.setProperty(name+".anchor",getAnchorString(c.anchor));
		p.setProperty(name+".weightx",Double.toString(c.weightx));
		p.setProperty(name+".weighty",Double.toString(c.weighty));
		p.setProperty(name+".fill",getFillString(c.fill));
		//Ipad properties
		p.setProperty(name+".ipadx",Integer.toString(c.ipadx));
		p.setProperty(name+".ipady",Integer.toString(c.ipady));
		//Insets properties
		if (c.insets==null) {
			p.setProperty(name+".insets.top","0");
			p.setProperty(name+".insets.bottom","0");
			p.setProperty(name+".insets.left","0");
			p.setProperty(name+".insets.right","0");
		} else {
			p.setProperty(name+".insets.top",Integer.toString(c.insets.top));
			p.setProperty(name+".insets.bottom",Integer.toString(c.insets.bottom));
			p.setProperty(name+".insets.left",Integer.toString(c.insets.left));
			p.setProperty(name+".insets.right",Integer.toString(c.insets.right));
		}
	}

	public String[] getConstraintLines() throws GridBagException {
	    Vector<String> v = new Vector<String>();
        List children = source.getChildren("component");
        for (int i=0;i<children.size();i++) {
            Element e = (Element)children.get(i);
            String name = e.getAttributeValue("name");
            GridBagConstraints c = parse(name);
            StringBuilder sb = new StringBuilder();
            sb.append(name);
            sb.append(":");
            sb.append(ConstraintsLine.getString(c));
            v.add(sb.toString());
        }
        return v.toArray(new String[0]);
    }

    /**
     * @return the names of all components in the current parser.
     */
    public String[] getComponentNames() {
        Vector<String> v = new Vector<String>();
        java.util.List children = source.getChildren("component");
        for (int i=0;i<children.size();i++) {
            Element e = (Element)children.get(i);
            v.add(e.getAttributeValue("name"));
        }
        String[] res = v.toArray(new String[0]);
        v.clear();
        return res;
    }
}
