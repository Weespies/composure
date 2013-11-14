/**
 * (c) 2005 Paul Loveridge
 */
package uk.lug.gui.archetype.skills;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import javax.swing.undo.UndoManager;

import org.apache.commons.lang.StringUtils;

import uk.lug.gui.util.CachedImageLoader;


/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>
 * Simple panel that allows editing of HTML formatted text.
 * </p>
 */
/**
 * @author Luggy
 */
public class HTMLEditorPanel extends JPanel implements CaretListener, DocumentListener {
	private static final long		serialVersionUID		= 1L;

	public static final String		DEFAULT_BODY_STYLE		= "body {font: 12px Arial;}";
	public static final String		DEFAULT_PARAGRAPH_STYLE	= "p {margin-top:0px; margin-bottom:0px;}";
	public static final String		DEFAULT_HYPERLINK_STYLE	= "a {color:#0000ff; text-decoration: underline;}";

	private static final String		BOLD_ICON				= "images/text_bold.png";
	private static final String		UNDERLINE_ICON			= "images/text_underlined.png";
	private static final String		ITALIC_ICON				= "images/text_italics.png";
	private static final String		COPY_ICON				= "images/copy.png";
	private static final String		CUT_ICON				= "images/cut.png";
	private static final String		PASTE_ICON				= "images/paste.png";
	private static final String		LEFT_JUSTIFY_ICON		= "images/text_align_left.png";
	private static final String		RIGHT_JUSTIFY_ICON		= "images/text_align_right.png";
	private static final String		CENTER_JUSTIFY_ICON		= "images/text_align_center.png";
	private static final String		ADD_LINK_ICON 			= "images/link_add_16.png";

	private static Icon				boldIcon, underlineIcon, italicIcon = null;
	private static Icon				copyIcon, cutIcon, pasteIcon = null;
	private static Icon				leftIcon, rightIcon, centerIcon, linkIcon = null;

	private Action					boldAction, italicAction, underlineAction = null;
	private Action					copyAction, cutAction, pasteAction = null;
	private Action					leftAction, rightAction, centerAction = null;
	private Action					undoAction, redoAction, linkAction = null;

	private JEditorPane				editorPane				= null;
	private JToolBar				toolbar					= null;
	private JButton					boldButton, italicButton, underlineButton = null;
	private JButton					copyButton, cutButton, pasteButton = null;
	private JButton					leftButton, rightButton, centerButton, linkButton = null;

	private HashMap<String,Action>	editorActions			= new HashMap<String,Action>();
	private FixedHTMLEditorKit		htmlKit					= null;

	private static final Insets		BUTTON_MARGINS			= new Insets( 0, 2, 0, 2 );
	private StyleSheet				baseStyleSheet;

	private UndoManager				undoManager;

	/**
	 * Construct the simple editor panel
	 */
	public HTMLEditorPanel() {
		baseStyleSheet = new StyleSheet();
		baseStyleSheet.addRule( DEFAULT_BODY_STYLE );
		baseStyleSheet.addRule( DEFAULT_PARAGRAPH_STYLE );
		baseStyleSheet.addRule( DEFAULT_HYPERLINK_STYLE );
		makeGUI();
	}
	
	/**
	 * Construct the simple editor panel
	 */
	public HTMLEditorPanel( StyleSheet sheet) {
		baseStyleSheet = sheet;
		makeGUI();
	}

	/**
	 * Build user interface.
	 */
	private void makeGUI() {
		setLayout( new BorderLayout() );

		undoManager = new UndoManager();

		// Editor
		editorPane = new JEditorPane();
		editorPane.setOpaque( true );
		editorPane.setBackground( null );
		editorPane.setContentType( "text/html" );
		editorPane.setDocument( new HTMLDocument() );
		htmlKit = new FixedHTMLEditorKit();
		htmlKit.setStyleSheet( baseStyleSheet );

		JScrollPane scrollPane = new JScrollPane( editorPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		editorPane.setEditorKit( htmlKit );
		editorPane.setEditable( true );
		add( scrollPane, BorderLayout.CENTER );

		// Initialise the actions
		buildToolbar();
		Action[] actionsArray = editorPane.getActions();
		for ( int i = 0; i < actionsArray.length; i++ ) {
			Action a = actionsArray[ i ];
			editorActions.put( (String)a.getValue( Action.NAME ), a );
		}
		editorPane.getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_B, InputEvent.CTRL_MASK ), boldAction );
		editorPane.getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_U, InputEvent.CTRL_MASK ), underlineAction );
		editorPane.getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_I, InputEvent.CTRL_MASK ), italicAction );
		editorPane.getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_E, InputEvent.CTRL_MASK ), centerAction );
		editorPane.getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_L, InputEvent.CTRL_MASK ), leftAction );
		editorPane.getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_R, InputEvent.CTRL_MASK ), rightAction );
		editorPane.getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_C, InputEvent.CTRL_MASK ), copyAction );
		editorPane.getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_X, InputEvent.CTRL_MASK ), cutAction );
		editorPane.getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_V, InputEvent.CTRL_MASK ), pasteAction );
		editorPane.getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_Z, InputEvent.CTRL_MASK ), undoAction );
		editorPane.getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_Z, InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK ), redoAction );
		editorPane.getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_Y, InputEvent.CTRL_MASK ), redoAction );
		toolbar = new JToolBar();
		
		toolbar.add( boldButton );
		toolbar.add( italicButton );
		toolbar.add( underlineButton );
		toolbar.addSeparator();
		toolbar.add( copyButton );
		toolbar.add( cutButton );
		toolbar.add( pasteButton );
		toolbar.addSeparator();
		toolbar.add( leftButton );
		toolbar.add( centerButton );
		toolbar.add( rightButton );
		toolbar.addSeparator();
		toolbar.add( linkButton );

		boldButton.setToolTipText("Bold");
		italicButton.setToolTipText("Italics");
		underlineButton.setToolTipText("Underlined");
		copyButton.setToolTipText("Copy to clipboard");
		cutButton.setToolTipText("Cut to clipboard");
		pasteButton.setToolTipText("Paste from clipboard");
		leftButton.setToolTipText("Left align select text");
		centerButton.setToolTipText("Center selected text");
		rightButton.setToolTipText("Right align selected text");
		linkButton.setToolTipText("Assign hyperlink to selected text");
		
		// toolbar.add( unorderedListButton );
		add( toolbar, BorderLayout.NORTH );
		toolbar.setFloatable( false );

		editorPane.addCaretListener( this );
		editorPane.setText( "" );
		editorPane.getDocument().addDocumentListener( this );
	}

	/**
	 * Create editing actions.
	 */
	private void buildToolbar() {
		loadIcons();
		boldAction = new AbstractAction( "Bold", boldIcon ) {
			private static final long	serialVersionUID	= 1L;

			public void actionPerformed( ActionEvent ae ) {
				editorActions.get( "font-bold" ).actionPerformed( ae );
				boldButton.setSelected( true );
			}
		};
		boldButton = createToolbarButton( boldAction );

		italicAction = new AbstractAction( "Italic", italicIcon ) {
			private static final long	serialVersionUID	= 1L;

			public void actionPerformed( ActionEvent ae ) {
				editorActions.get( "font-italic" ).actionPerformed( ae );
				italicButton.setSelected( true );
			}
		};
		italicButton = createToolbarButton( italicAction );

		underlineAction = new AbstractAction( "Underline", underlineIcon ) {
			private static final long	serialVersionUID	= 1L;

			public void actionPerformed( ActionEvent ae ) {
				editorActions.get( "font-underline" ).actionPerformed( ae );
				underlineButton.setSelected( true );
			}
		};
		underlineButton = createToolbarButton( underlineAction );

		copyAction = new AbstractAction( "Copy", copyIcon ) {
			private static final long	serialVersionUID	= 1L;

			public void actionPerformed( ActionEvent ae ) {
				editorPane.copy();
			}
		};
		copyButton = createToolbarButton( copyAction );

		cutAction = new AbstractAction( "Cut", cutIcon ) {
			private static final long	serialVersionUID	= 1L;

			public void actionPerformed( ActionEvent ae ) {
				editorPane.cut();
			}
		};
		cutButton = createToolbarButton( cutAction );

		pasteAction = new AbstractAction("Paste", pasteIcon ) {
			private static final long	serialVersionUID	= 1L;

			public void actionPerformed( ActionEvent ae ) {
				editorPane.paste();
				
			}
		};
		pasteButton = createToolbarButton( pasteAction );

		leftAction = new AbstractAction( "Justify Text Left", leftIcon ) {
			private static final long	serialVersionUID	= 1L;

			public void actionPerformed( ActionEvent ae ) {
				editorActions.get( "left-justify" ).actionPerformed( ae );
			}
		};
		leftButton = createToolbarButton( leftAction );

		centerAction = new AbstractAction( "Justify Text center", centerIcon ) {
			private static final long	serialVersionUID	= 1L;

			public void actionPerformed( ActionEvent ae ) {
				editorActions.get( "center-justify" ).actionPerformed( ae );
			}
		};
		centerButton = createToolbarButton( centerAction );

		rightAction = new AbstractAction( "Justify Text right", rightIcon ) {
			private static final long	serialVersionUID	= 1L;

			public void actionPerformed( ActionEvent ae ) {
				editorActions.get( "right-justify" ).actionPerformed( ae );
			}
		};
		rightButton = createToolbarButton( rightAction );

		undoAction = new AbstractAction( "Undo " ) {
			private static final long	serialVersionUID	= 1L;

			public void actionPerformed( ActionEvent ae ) {
				if ( undoManager.canUndo() ) {
					undoManager.undo();
				}
			}
		};

		redoAction = new AbstractAction( "Undo " ) {
			private static final long	serialVersionUID	= 1L;

			public void actionPerformed( ActionEvent ae ) {
				if ( undoManager.canRedo() ) {
					undoManager.redo();
				}

			}
		};

		linkAction = new AbstractAction( "Create Hyperlink" , linkIcon ) {
			private static final long	serialVersionUID	= 1L;

			public void actionPerformed( ActionEvent ae ) {
				try {
					createLink();
				} catch (BadLocationException e) {
					String msg = "Unable to insert hyperlink due to a client error.\nSpecific Error : "+e.getMessage();
					JOptionPane.showMessageDialog( editorPane, msg, "Bad Location Error", JOptionPane.ERROR_MESSAGE );
				}
			}
		};
		linkButton = createToolbarButton( linkAction );
	}
	
	/**
	 * Create a hyperlink on the selected text.
	 * @throws BadLocationException 
	 */
	protected void createLink() throws BadLocationException {
		//Get selection start and end
		int start = editorPane.getCaret().getDot();
		int finish = editorPane.getCaret().getMark();
		if ( start==finish ) {
			return;
		}
		if ( start>finish ) {
			//Selection is backwards
			int tmp = finish;
			finish = start;
			start = tmp;
		}
		
		//Trim off spaces at the start and end of the selection
		while ( editorPane.getDocument().getText( start,1).equals(" ") && start<finish ) {
			start++;
		}
		while ( editorPane.getDocument().getText( finish-1,1).equals(" ") && start<finish ) {
			finish--;
		}
		editorPane.getCaret().setDot( start );
		editorPane.getCaret().moveDot( finish );

		
		//Get and check the selected text
		String selectedText =null;
		try {
			selectedText = editorPane.getDocument().getText( start, finish-start );
		} catch (BadLocationException e1) {
			return;
		}
		if ( selectedText==null ) {
			return; //Shouldn't happen but fail gracefully and quietly if it does
		}
		if ( selectedText.trim().length()==0 ) {
			String msg = "Cannot create hyperlinks on selections that only consist of whitespace.";
			JOptionPane.showMessageDialog( editorPane, msg, "Bad Selection", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		
		//Step 3 : get link and ensure it's all dandy
		URL linkURL=null;
		String linkRef="";
		while ( StringUtils.isEmpty( linkRef ) || linkURL==null ) {
			linkRef = JOptionPane.showInputDialog(editorPane, "Enter the URL to link to",linkRef );
			if ( linkRef==null  ) {
				//aborted
				return;
			}
			
			try {
				linkURL = new URL( linkRef ) ;
			} catch (MalformedURLException e) {
				String msg = "That link does not conform to the rules for a hyperlink destination.";
				JOptionPane.showMessageDialog( editorPane, msg, "Bad Link Format", JOptionPane.ERROR_MESSAGE );
				return;
			}
		}
		linkRef = linkURL.toString();
		
		//Step 4 : Construct the link
		StringBuilder link = new StringBuilder();
		link.append( "<a href=\"" );
		link.append( linkRef );
		link.append( "\">" );
		link.append( selectedText ) ;
		link.append( "</a>" );
			
		//Remove selected text from document and place in new shiny linked text
		HTMLDocument doc = (HTMLDocument)editorPane.getDocument();
		doc.remove(start, finish-start);
		
		try {
			htmlKit.insertHTML( doc, start, link.toString(), 0, 0, HTML.Tag.A );
		} catch (IOException e) {
			String msg = "Unable to insert hyperlink due to a client error\nSpecific Error : "+e.getMessage();
			JOptionPane.showMessageDialog( editorPane, msg, "IO Error", JOptionPane.ERROR_MESSAGE );
		}
		
	}

	/**
	 * Create a JButton from an Action. This method also adjusts the border, focusability and other
	 * piddling crap I wouldn't touch with a 10 foot clown pole.
	 * @param jb
	 */
	private JButton createToolbarButton( Action action ) {
		JButton jb = new JButton( action );
		jb.setFocusable( false );
		jb.setText( "" );
		jb.setMargin( BUTTON_MARGINS );
		jb.setFocusable( false );
		return jb;
	}

	/**
	 * Retreive icons for the toolbar buttons
	 * @throws IOException
	 */
	private void loadIcons() {
		boldIcon = CachedImageLoader.getCachedIcon( BOLD_ICON );
		underlineIcon = CachedImageLoader.getCachedIcon( UNDERLINE_ICON );
		italicIcon = CachedImageLoader.getCachedIcon( ITALIC_ICON );
		copyIcon = CachedImageLoader.getCachedIcon( COPY_ICON );
		cutIcon = CachedImageLoader.getCachedIcon( CUT_ICON );
		pasteIcon = CachedImageLoader.getCachedIcon( PASTE_ICON );
		leftIcon = CachedImageLoader.getCachedIcon( LEFT_JUSTIFY_ICON );
		centerIcon = CachedImageLoader.getCachedIcon( CENTER_JUSTIFY_ICON );
		rightIcon = CachedImageLoader.getCachedIcon( RIGHT_JUSTIFY_ICON );
		linkIcon = CachedImageLoader.getCachedIcon( ADD_LINK_ICON );
	}

	/**
	 * Sets whether or not this editor is enabled to edit.
	 */
	public void setEditable( boolean b ) {
		editorPane.setEditable( b );
	}

	/**
	 * Sets the text within the editor.
	 * @param string
	 */
	public void setText( String string ) {

		// Is html or raw text ?
		if ( string.toLowerCase().startsWith( "<html>" ) ) {
			editorPane.setText( string );
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append( "<html><head><title></title></head><body><p>" );
			sb.append( string );
			sb.append( "</p></body></html>" );
			editorPane.setText( sb.toString() );
			editorPane.repaint();
			if ( string.startsWith( "--" ) && string.endsWith( "--" ) ) {
				Runnable selectAll = new Runnable() {
					public void run() {
						editorPane.selectAll();
					}
				};
				SwingUtilities.invokeLater( selectAll );
			}
		}

		// Ensure we have the right style sheet
		htmlKit.setStyleSheet( baseStyleSheet );

		// Add the undo editor to it.
		editorPane.getDocument().addUndoableEditListener( new UndoableEditListener() {
			public void undoableEditHappened( UndoableEditEvent uee ) {
				undoManager.addEdit( uee.getEdit() );
			}
		} );
	}

	/**
	 * Implementation of CaretListener interface.
	 * @see javax.swing.event.CaretListener#caretUpdate(javax.swing.event.CaretEvent)
	 */
	public void caretUpdate( CaretEvent e ) {
		int caretPos = e.getDot();
		doButtonSettings( caretPos );
		copyButton.setEnabled( e.getDot() != e.getMark() );
		cutButton.setEnabled( e.getDot() != e.getMark() );
		linkButton.setEnabled( e.getDot() != e.getMark() );		
	}

	/**
	 * Check the document from the given point and set the text formatting button to reflect the
	 * text style at that point.
	 * @param caretPos offset within text of the caret position.
	 */
	private void doButtonSettings( int caretPos ) {
		int docEnd = editorPane.getDocument().getLength();
		if ( docEnd == caretPos ) {
			caretPos--;
		}
		StyledDocument sdoc = (StyledDocument)editorPane.getDocument();
		AttributeSet attr = sdoc.getCharacterElement( caretPos ).getAttributes();
		// boldButton.setBackground( StyleConstants.isBold(attr) ? TOGGLE_BACKGROUND :
		// DEFAULT_BACKGROUND );
		boldButton.setSelected( StyleConstants.isBold( attr ) );
		italicButton.setSelected( StyleConstants.isItalic( attr ) );
		underlineButton.setSelected( StyleConstants.isUnderline( attr ) );

	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
	 */
	public void insertUpdate( DocumentEvent e ) {

	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
	 */
	public void removeUpdate( DocumentEvent e ) {

	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
	 */
	public void changedUpdate( DocumentEvent e ) {

	}

	/**
	 * find the first occurrence of an <code>Element</code> in the element tree below a given
	 * <code>Element</code>
	 * @param name the name of the <code>Element</code> to search for
	 * @param parent the <code>Element</code> to start looking
	 * @return the found <code>Element</code> or null if none is found
	 */
	public Element findElementDown( String name, Element parent ) {
		Element foundElement = null;
		ElementIterator eli = new ElementIterator( parent );
		Element thisElement = eli.first();
		while ( thisElement != null && foundElement == null ) {
			if ( thisElement.getName().equalsIgnoreCase( name ) ) {
				foundElement = thisElement;
			}
			thisElement = eli.next();
		}
		return foundElement;
	}

	/**
	 * Subclass of the origianl Sun HTMLEditorKit that works around a bug with the <br>
	 * tag.
	 * @author paul.loveridge
	 */
	class FixedHTMLEditorKit extends HTMLEditorKit {
		private static final long	serialVersionUID	= 1L;

		// bug fixed for <br> tag, by default <br> does not have CONTENT.
		@Override
		protected void createInputAttributes( javax.swing.text.Element element, MutableAttributeSet set ) {
			super.createInputAttributes( element, set );
			Object o = set.getAttribute( StyleConstants.NameAttribute );
			if ( o == HTML.Tag.BR ) {
				set.addAttribute( StyleConstants.NameAttribute, HTML.Tag.CONTENT );
			}
		}
	}

	/**
	 * @return the HTMLDocument currently being edited.
	 */
	public HTMLDocument getHTMLDocument() {
		Document doc = editorPane.getDocument();
		try {
			String plain = doc.getText( 0, doc.getLength() );
			if ( plain.trim().length() == 0 ) {
				return null;
			}
		} catch ( BadLocationException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (HTMLDocument)editorPane.getDocument();
	}
	
	/**
	 * Sets the document to edit.
	 * @param document
	 */
	public void setHTMLDocument(HTMLDocument document ) {
		editorPane.setDocument( document );
	}

	/**
	 * @return the baseStyleSheet
	 */
	public StyleSheet getBaseStyleSheet() {
		return baseStyleSheet;
	}

	/**
	 * @param baseStyleSheet the baseStyleSheet to set
	 */
	public void setBaseStyleSheet(StyleSheet baseStyleSheet) {
		this.baseStyleSheet = baseStyleSheet;
	}
}
