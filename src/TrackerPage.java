import java.awt.Font;
import java.util.ArrayList;

import visual.composite.HandlePanel;

/**
 * 
 *   - TrackerPage
 *     - Reorder the PageEntries
 *     - Potentially, scroll through list?
 *     - Edit TrackerPage title/title image path
 *     - Edit TrackerPage # of columns
 *     - Edit TrackerPage background path
 *     - Edit TrackerPage default background path for PageEntries
 *     - Add new PageEntry to current TrackerPage
 *     - Remove a PageEntry
 *     - Edit a PageEntry
 *       - Use same interface for these but have information fill in with current state of PageEntry if editing
 *     - Go into Display Mode and Edit Mode
 * 
 * @author Ada Clevinger
 *
 */

public class TrackerPage {

//---  Constants   ----------------------------------------------------------------------------
	
	private final static String DATA_LABEL_TITLE = "title";
	
	private final static String DATA_LABEL_TITLE_PATH = "title path";
	
	private final static String DATA_LABEL_COLUMN = "col";
	
	private final static String DATA_LABEL_ENTRY_NUMBER = "position";
	
	private final static String DATA_LABEL_NUM_ENTRIES = "number";
	
	private final static String DATA_LABEL_ENTRY_LIST = "entry list";
	
	private final static int CODE_TOGGLE_EDIT = 50;
	
	private final static int CODE_REMOVE_PAGE_ENTRY_BASE = 500;
	
	private final static int CODE_ADD_PAGE_ENTRY = 51;
	
	private final static int CODE_EDIT_PAGE_ENTRY_BASE = 1000;
	
	private final static int CODE_EDIT_PAGE_TITLE = 52;
	
	private final static String DEFAULT_TITLE = "title";
	
	private final static Font DEFAULT_FONT = new Font("Serif", Font.BOLD, 14);
	
	private final static String DEFAULT_BACKGROUND =  "C:\\Users\\Borinor\\Pictures\\angy.jpg";
	
//---  Instance Variables   -------------------------------------------------------------------

	private TitleDraw title;
	/** int value denoting either the font size or image size depending on if the TitleDraw has an image to display*/
	private int titleSize;
	
	private int columns;
	
	private ArrayList<PageEntry> entries;
	
	private String backgroundPath;
	
	private String defaultPageEntryBackground;
	
	private boolean edit;
	
	private static int identity;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public TrackerPage() {
		columns = 3;
		title = new TitleDraw(DEFAULT_TITLE, null, identity++);
		entries = new ArrayList<PageEntry>();
		
		backgroundPath = DEFAULT_BACKGROUND;
	}
	
	public TrackerPage(String tit, String titPath, int c) {
		columns = c;
		title = new TitleDraw(tit, titPath, identity++);
		entries = new ArrayList<PageEntry>();
	}
	
	public TrackerPage(Data importInfo) {
		entries = new ArrayList<PageEntry>();
		importData(importInfo);
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public void draw(HandlePanel p) {
		int screenWidth = p.getWidth();
		int entryWid = screenWidth / columns;
		int headerHeight = p.getTextHeight(title.getFont()) * 11 / 10;
		
		p.handleImage(getPrefix() + "_background", "no_move", 5, screenWidth / 2, p.getHeight() / 2, screenWidth, p.getHeight(), false, backgroundPath);
		
		//TODO: Make more dynamic to the current actual title status
		title.drawTitle(p, screenWidth / 2, headerHeight / 2, screenWidth, headerHeight);
		
		int startX = entryWid / 2;
		int startY = entryWid / 2 + headerHeight;
		
		int counter = 0;
		int runX = startX;
		int runY = startY;
		for(PageEntry page : entries) {
			page.draw(p, runX, runY, entryWid, entryWid);
			counter++;
			if(counter % columns == 0) {
				runX = startX;
				runY += entryWid / 2;
			}
			else {
				runX += entryWid / 2;
			}
		}
		p.handleText(getPrefix() + "add_new_text", "move", 30, runX, runY, entryWid, entryWid, DEFAULT_FONT, "Add new");
		p.handleButton(getPrefix() + "add_new_butt", "move", 30, runX, runY, entryWid, entryWid, CODE_ADD_PAGE_ENTRY);
	}
	
	private String getPrefix() {
		return title.getTitle() + "_ui_";
	}
	
	public Data exportData() {
		Data out = new Data();
		out.setTitle(title.getTitle());
		
		out.addString(title.getTitle(), DATA_LABEL_TITLE);
		out.addString(title.getTitlePath(), DATA_LABEL_TITLE_PATH);
		out.addInt(columns, DATA_LABEL_COLUMN);
		
		Data[] ent = new Data[entries.size()];
		int counter = 0;
		for(PageEntry p : entries) {
			Data d = p.exportData();
			d.addInt(counter, DATA_LABEL_ENTRY_NUMBER);
			ent[counter++] = d;
		}
		
		out.addDataArray(DATA_LABEL_ENTRY_LIST, ent);
		
		out.addInt(counter + 1, DATA_LABEL_NUM_ENTRIES);
		
		return out;
	}
	
	public void importData(Data data) {
		setTitle(data.getString(DATA_LABEL_TITLE));
		setTitlePath(data.getString(DATA_LABEL_TITLE_PATH));
		setColumns(data.getInt(DATA_LABEL_COLUMN));
		
		PageEntry[] track = new PageEntry[data.getInt(DATA_LABEL_NUM_ENTRIES)];
		
		Data[] ent = data.getDatasetArray(DATA_LABEL_ENTRY_LIST);
		
		for(int i = 0; i < ent.length; i++) {
			Data d = ent[i];
			track[d.getInt(DATA_LABEL_ENTRY_NUMBER)] = new PageEntry(d, identity++);
		}
		
		for(PageEntry p : track) {
			entries.add(p);
		}
		
	}
	
	public void addEntry() {
		PageEntry ent = new PageEntry(identity++);
		entries.add(ent);
	}
	
	public void removeEntry(int pos) {
		if(pos >= 0 && pos < entries.size()) {
			entries.remove(pos);
		}
	}
	
	public void reorderEntry(int currPos, int moveTo) {
		PageEntry hold = entries.get(currPos);
		moveTo -= moveTo > currPos ? 1 : 0;
		entries.remove(hold);
		entries.add(moveTo, hold);
	}
	
	public void toggleEditMode() {
		edit = !edit;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setTitle(String tit) {
		if(title == null) {
			title = new TitleDraw(identity++);
		}
		title.setTitle(tit);
	}
	
	public void setTitlePath(String titPath) {
		if(title == null) {
			title = new TitleDraw(identity++);
		}
		title.setTitlePath(titPath);
	}
	
	public void setColumns(int in) {
		columns = in;
	}
	
	public void setEdit(boolean in) {
		edit = in;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public String getTitle() {
		return title.getTitle();
	}
	
}
