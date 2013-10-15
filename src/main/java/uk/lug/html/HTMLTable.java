package uk.lug.html;

/*
 * htmlTable.java      1.00 30/08/01
 * Paul Loveridge
 *
*/
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Vector;
/**
 *
 * Used for the easy creation of a table that can be later rendered into a string of HTML
 * Treats the table as a 2 dimensional grid of objects
 *
 * @version	1.0
 * @author	Paul Loveridfge
 */
public class HTMLTable implements HTMLRenderer {
	/** A vector containing the rows of the table definition*/
	Vector tableRows;
	/** Vector containing rows of cell attributes*/
	Vector tableRowsAttr;
	/** Vector containing row attributes */
	Vector rowAttrs;
	/** attributes to go inside of the initial table definition*/
	String tableTags;
	/** Creates an empty table object*/
	public HTMLTable() {
		tableRows = new Vector();
		tableRowsAttr = new Vector();
		tableTags=null;
		rowAttrs=new Vector();
	}

	/** creates an empty table object, and inserts the specified attributes into the table definition
	 * @param tags	contains the HTML attributes for the table definition.
	 *				e.g. Centre, Width=80%, BG.
	 */
	public HTMLTable(String tags) {
		tableRows = new Vector();
		tableRowsAttr = new Vector();
		tableTags=tags;
		rowAttrs=new Vector();
	}

	/**
	 * Sets the specified attributes to the table
	 * @param attrs contains HTML attributes for the table.
	 */
	public void setTableAttributes(String attrs) {
		tableTags=attrs;
	}

	public String getTableAttributes() {
		return tableTags;
	}


	/**
	 *	adds a new row to the table at the bottom
	 *	@param newRow a vector object containing the cells of this row.
	 */
	public void addRow(Vector newRow) {
		tableRows.add(newRow);
		tableRowsAttr.add(newRow);
	}
	/**
	 * adds a new row to the table at the specified position,
	 *			and shifts all the subsequent rows down one.
	 *	@param newRow a vector object containing the cells of this row.
	 *	@param position an int specifying the location to instert
	*/
	public void addRowAt(Vector newRow,int position) {
		try {
			tableRows.insertElementAt(newRow,position);
			tableRowsAttr.insertElementAt(newRow,position);
		} catch (ArrayIndexOutOfBoundsException e) {
			addRow(newRow);
		}
	}
	/**
	 * sets the attributes of this table row
	 *  @param row the number of the row (starts at 0)
	 *  @param attrs string containing the html attributes for this row.
	 */
	 public void setRowAttributes(int row,String attrs) {
		while (rowAttrs.size()<row+1) {
			rowAttrs.addElement(null);
		}
		rowAttrs.setElementAt(attrs,row);
	 }
	/**
	 * returns the attributes of this table row
	 *  @param row the number of the row (starts at 0)
	 */
	 public String getRowAttributes(int row) {
		if (rowAttrs.size()>row) {
			return (String)rowAttrs.elementAt(row);
		} else {
		 	return null;
		}
	 }
	/**
	 *	creates new content of the table at specified coordinates.
	 *	@param cell the cell number , using 0 as the base
	 *	@param row the row number, using 0 as the base
	 */
	public void addCell(Object contents,int cell, int row) {
		Vector currentRow;
		Vector currentRowAttr;
		int rows;
		//Pad out the rows to required
		while (tableRows.size()<row+1) {
			tableRows.addElement(null);
		}
		//attributes
		while (tableRowsAttr.size()<row+1) {
			tableRowsAttr.addElement(null);
		}
		//Create or get current row as required
		if (tableRows.elementAt(row)==null) {
			currentRow= new Vector();
			tableRows.setElementAt(currentRow,row);
		} else {
			currentRow=(Vector)tableRows.elementAt(row);
		}
		//attributes
		if (tableRowsAttr.elementAt(row)==null) {
			currentRowAttr=new Vector();
			tableRowsAttr.setElementAt(currentRowAttr,row);
		} else {
			currentRowAttr=(Vector)tableRowsAttr.elementAt(row);
		}
		//Pad out current Row, and add contents
		while (currentRow.size()<cell+1) {
			currentRow.addElement(null);
		}
		currentRow.setElementAt(contents,cell);
		//attributes
		while (currentRowAttr.size()<cell+1) {
			currentRowAttr.addElement(null);
		}
		currentRowAttr.setElementAt(null,cell);
	}
	public void setAttributes(String attr) {
		tableTags=attr;
	}
	/**
	 *	creates new content of the table at specified coordinates, using the specified attributes
	 *	@param cell the cell number , using 0 as the base
	 *	@param row the row number, using 0 as the base
	 *	@param attrs attributes to go into the cell definition
	 */
	public void addCell(Object contents,int cell, int row,String attrs) {
		Vector currentRow;
		Vector currentRowAttr;
		int rows;
		//Pad out the rows to required
		while (tableRows.size()<row+1) {
			tableRows.addElement(null);
		}
		//attributes
		while (tableRowsAttr.size()<row+1) {
			tableRowsAttr.addElement(null);
		}
		//Create or get current row as required
		if (tableRows.elementAt(row)==null) {
			currentRow= new Vector();
			tableRows.setElementAt(currentRow,row);
		} else {
			currentRow=(Vector)tableRows.elementAt(row);
		}
		//attributes
		if (tableRowsAttr.elementAt(row)==null) {
			currentRowAttr=new Vector();
			tableRowsAttr.setElementAt(currentRowAttr,row);
		} else {
			currentRowAttr=(Vector)tableRowsAttr.elementAt(row);
		}
		//Pad out current Row, and add contents
		while (currentRow.size()<cell+1) {
			currentRow.addElement(null);
		}
		currentRow.setElementAt(contents,cell);
		//attributes
		while (currentRowAttr.size()<cell+1) {
			currentRowAttr.addElement(null);
		}
		currentRowAttr.setElementAt(attrs,cell);
		tableRows.setElementAt(currentRow,row);

	}
	/**
	 *	sets the attributes of the specified table cell, and creates the table cell if it doesn't exist
	 *	@param cell the cell number , using 0 as the base
	 *	@param row the row number, using 0 as the base
	 *	@param attrs attributes to go into the cell definition
	 */
	public void setCellAttributes(int cell, int row,String attrs) {
		Vector currentRow;
		Vector currentRowAttr;
		int rows;
		//Pad out the rows to required
		while (tableRows.size()<row+1) {
			tableRows.addElement(null);
		}
		//attributes
		while (tableRowsAttr.size()<row+1) {
			tableRowsAttr.addElement(null);
		}
		//Create or get current row as required
		if (tableRows.elementAt(row)==null) {
			currentRow= new Vector();
			tableRows.setElementAt(currentRow,row);
		} else {
			currentRow=(Vector)tableRows.elementAt(row);
		}
		//attributes
		if (tableRowsAttr.elementAt(row)==null) {
			currentRowAttr=new Vector();
			tableRowsAttr.setElementAt(currentRowAttr,row);
		} else {
			currentRowAttr=(Vector)tableRowsAttr.elementAt(row);
		}
		//attributes
		while (currentRowAttr.size()<cell+1) {
			currentRowAttr.addElement(null);
		}
		currentRowAttr.setElementAt(attrs,cell);
	}
	/**
	 *	returns the specified cell's attributes.
	 *	returns null if the cell is non-existant or has no attributes
	 *	@param cell	cell number (starts at 0)
	 *	@param row	row number (starts at 0)
	 */
	public String getCellAttributes(int cell, int row) {
		String result=null;
		Vector currentRowAttr;
		if (tableRowsAttr.size()>=row+1) {
			currentRowAttr=(Vector)tableRowsAttr.elementAt(row);
			if (currentRowAttr.size()>=cell+1) {
				result=(String)currentRowAttr.elementAt(cell);
			}
		}
		return result;
	}
	/**
	 *	returns the specified cell's attributes.
	 *	returns null if the cell is non-existant or has no attributes
	 *	@param cell	cell number (starts at 0)
	 *	@param row	row number (starts at 0)
	 */
	public Object getCellContent(int cell, int row) {
		Object result=null;
		Vector currentRowAttr;
		if (tableRowsAttr.size()>=row+1) {
			currentRowAttr=(Vector)tableRowsAttr.elementAt(row);
			if (currentRowAttr.size()>=cell+1) {
				result=currentRowAttr.elementAt(cell);
			}
		}
		return result;
	}
	
	/**
	 *	renders the table into a string as Html formatted text.
	 */
	 public String getHTML() {
		int c=1;
		StringWriter sw= new StringWriter();
		PrintWriter out = new PrintWriter(sw);
		out.print("<table ");
		if (tableTags!=null) {
			out.print(tableTags+" ");}
		out.println(">");
		int row=0;
		int col;
		Method allowHtml;
		Method[] objMethods=null;
		Object[] params=null;
		Class objdef;
		Object cell;
		String cellAttr;
		Vector currentRow,currentRowAttr;
		while (row<tableRows.size()) {
			out.print("<tr");
			//add attributes for this row
			if (rowAttrs.size()>row) {
				if (rowAttrs.elementAt(row)!=null) {
					out.print(" "+rowAttrs.elementAt(row));
				}
			}
			out.print(">");
			col=0;
			currentRow=(Vector)tableRows.get( row );
			currentRowAttr=(Vector)tableRowsAttr.get(row);
			if (currentRow==null) {
				currentRow = new Vector();
			}
			while (col<currentRow.size()) {
				cell=currentRow.elementAt(col);
				if (cell!=null) {
					c=1;
					cellAttr=(String)currentRowAttr.elementAt(col);
					objdef=cell.getClass();
					objMethods=objdef.getMethods();
					allowHtml=null;

					//Add attributes for this cell					
					out.print("<td");
					if (cellAttr != null) {

						out.print(" "+cellAttr);
					}
					out.print(">");
					
					if ( cell instanceof HTMLRenderer ) {
						out.print( ((HTMLRenderer)cell).getHTML() );
					} else {
						out.print(cell.toString());
					}
					out.println("</td>");
				}
				col=col+1;
			}
			out.println("</tr>");
			row=row+1;
		}
		out.print("</table>");
		out.close();
		return sw.toString();
	}
}
