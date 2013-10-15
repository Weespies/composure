package uk.lug.gui.gridbag;

/**
 * Runtime exception throwable from the GridBagParser.*/
public class GridBagException extends Exception
{
	public GridBagException()
	{
		super();
	}

	public GridBagException(String s)
	{
		super(s);
	}

	public GridBagException(Exception e)
	{
		super(e.getMessage());
	}
}