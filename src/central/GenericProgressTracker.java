package central;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import filemeta.FileChooser;
import page.TrackerPage;
import tool.Data;
import visual.composite.HandlePanel;
import visual.frame.WindowFrame;

/**
 * 
 *  - Load in
 *    - Shown empty TrackerPage, unnamed but able to immediately start editing
 *    - Options:
 *      - Start editing the default TrackerPage
 *    - Once content is loaded in or using default TrackerPage:
 * 
 * 
 *   - GenericProgressTracker
 *     - Change to next/previous TrackerPage (looping around)
 * 	   - Add new TrackerPage
 * 	   - Remove current TrackerPage
 *     - Can save the current TrackerPage to its own file
 *     - Can save the entire set of TrackerPages to a joined file
 *       - Implies we should save TrackerPages to individual files and have reference file paths in the conglomerate?
 *       - Would have two kinds of files; a TrackerPage file and a grouped set file
 *     - Load a file for adding a predefined set of TrackerPages, overwrites the default empty one
 *     - Load a file for adding a single TrackerPage
 *    
 * 
 * @author Ada Clevinger
 *
 */

public class GenericProgressTracker {
	
//---  Constants   ----------------------------------------------------------------------------
	
	private static final String DATA_LABEL_PAGE_TITLES = "titles";
	
	private static final String WINDOW_PANEL_NAME = "progress";

//---  Instance Variables   -------------------------------------------------------------------
	
	private ArrayList<TrackerPage> pages;
	
	private String lastPath;
	
	private int index;
	
	private WindowFrame window;
	
	private HandlePanel p;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public GenericProgressTracker() {
		lastPath = "";
		pages = new ArrayList<TrackerPage>();
		pages.add(new TrackerPage());
		index = 0;
		window = new WindowFrame(800, 800);
		p = generateHandlePanel();
		window.reserveWindow(WINDOW_PANEL_NAME);
		window.addPanelToWindow(WINDOW_PANEL_NAME, "pan", p);
		window.showActiveWindow(WINDOW_PANEL_NAME);
		
		drawCurrentPage();
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	private void setIndex(int in) {
		in = in < 0 ? 0 : in >= pages.size() ? pages.size() - 1 : in;
		index = in;
		drawCurrentPage();
	}
	
	private void drawCurrentPage() {
		pages.get(index).draw(p);
	}
	
	private HandlePanel generateHandlePanel() {
		HandlePanel out = new HandlePanel(0, 0, 800, 800);
		
		return out;
	}
	
	private void exportData(String path) {
		Data out = new Data();
		for(TrackerPage tP : pages) {
			Data d = tP.exportData();
			out.addData(d.getTitle(), d);
		}
		out.save(getFilePath());
	}
	
	private void importData(String path) {
		try {
			Data d = new Data(path);
			String[] pageTitles = d.getStringArray(DATA_LABEL_PAGE_TITLES);
			for(String s : pageTitles) {
				pages.add(new TrackerPage(d.getDataset(s)));
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getFilePath() {
		File out = FileChooser.promptSelectFile(lastPath, true, true);
		if(out != null) {
			lastPath = out.getParent();
		}
		return out.getAbsolutePath();
	}
	
}
