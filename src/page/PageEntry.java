package page;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import filemeta.FileChooser;
import input.CustomEventReceiver;
import input.manager.actionevent.MouseActionEvent;
import tool.Data;
import tool.TitleDraw;
import visual.composite.HandlePanel;

/**
 * 
 *  - Relevant information to edit: (* = needed)
 *    - Strings
 *      - Title
 *      - Title image path (toggle its usage)
 *      - Image path when On
 *      - Image path when Off (can leave blank/null for grayscale)
 *      - Manual background (if none then use default that is set at TrackerPage level)
 *    - Boolean
 *      - Display title
 *      - Active (on/off)
 *      - Edit mode
 *      - Animation (is the image path to a folder of images or not?) - Can probably interpret this at run-time?
 * 
 * 
 * 
 * TODO: Allow for animations, too
 * 
 * @author Ada Clevinger
 *
 */

public class PageEntry {

//---  Constants   ----------------------------------------------------------------------------
	
	private final static String DATA_LABEL_TITLE = "title";
	
	private final static String DATA_LABEL_TITLE_PATH = "title path";
	
	private final static String DATA_LABEL_PATH_ON = "pathOn";
	
	private final static String DATA_LABEL_PATH_OFF = "pathOff";
	
	private final static String DATA_LABEL_ACTIVE = "active";
	
	private final static String DATA_LABEL_BACKGROUND = "background";
	
	private final static String DATA_LABEL_SHOW_TITLE = "show title";
	
	private final static int BOOLEAN_ON = 1;
	
	private final static int BOOLEAN_OFF = 0;
	
	private final static String SUFFIX_TITLE_ENTRY = "text_entry_title";

	private final static int CODE_BASE = 100;
	
	private final static int CODE_LID = 150;
	
	private final static int CODE_TITLE_PATH = 101;
	
	private final static int CODE_PATH_ON = 102;
	
	private final static int CODE_PATH_OFF = 103;
	
	private final static int CODE_PATH_BACKGROUND = 104;
	
	private final static int CODE_TOGGLE_SHOW_TITLE = 105;
	
	private final static int CODE_TOGGLE_ACTIVE = 106;
	/** Accessed by TrackerPage to also react to a confirm edit to do a redraw - Actually, any of these need to cause that*/
	public final static int CODE_CONFIRM_EDIT = 107;
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private TitleDraw title;

	private String pathOn;
	
	private String pathOff;
	
	private String manualBackground;
	
	private boolean active;
	
	private boolean showTitle;
	
	//-- Not privy to user  -----------------------------------
	
	private boolean edit;
	
	private int identity;
	
	private static String lastPath;
	
	private static String defaultBackground;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public PageEntry(int in) {
		edit = true;
		identity = in;
	}
	
	public PageEntry(Data importInfo, int in) {
		importData(importInfo);
		edit = false;
		identity = in;
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public CustomEventReceiver generateEventReceiver(HandlePanel p) {
		CustomEventReceiver cer = new CustomEventReceiver() {
			
			@Override
			public void clickEvent(int code, int x, int y, int mouseType) {
				boolean right = mouseType == MouseActionEvent.CLICK_TYPE_RIGHT;
				String path;
				switch(code) {
					case CODE_TITLE_PATH:
						if(right) {
							title.setTitlePath(null);
						}
						else {
							path = getFilePath();
							if(path != null) {
								title.setTitlePath(path);
							}
						}
						break;
					case CODE_PATH_ON:
						if(right) {
							pathOn = null;
						}
						else {
							path = getFilePath();
							if(path != null) {
								pathOn = path;
								if(pathOff == null) {
									makeOffGrayscale();
								}
							}
						}
						break;
					case CODE_PATH_OFF:
						if(right) {
							makeOffGrayscale();
						}
						else {
							path = getFilePath();
							if(path != null) {
								pathOff = path;
							}
						}
						break;
					case CODE_PATH_BACKGROUND:
						if(right) {
							manualBackground = null;
						}
						else {
							path = getFilePath();
							if(path != null) {
								manualBackground = path;
							}
						}
						break;
					case CODE_TOGGLE_ACTIVE:
						if(right) {
							toggleEdit();
						}
						else {
							toggleActive();
						}
						break;
					case CODE_TOGGLE_SHOW_TITLE:
						toggleShowTitle();
						break;
					case CODE_CONFIRM_EDIT:
						String get = p.getElementStoredText(getPrefix() + SUFFIX_TITLE_ENTRY);
						get = get == null ? null : get.equals("") ? null : get;
						title.setTitle(get);
						toggleEdit();
						break;
					default:
						break;
				}
			}
			
		};
		return cer;
	}
	
	private String getFilePath() {
		String defaultPath = lastPath == null ? "" : lastPath;
		File use =  FileChooser.promptSelectFile(defaultPath, true, true);
		if(use == null) {
			return null;
		}
		lastPath = use.getParent();
		return use.getAbsolutePath();
	}
	
	private void makeOffGrayscale() {
		if(pathOn.indexOf("/") == -1) {
			return;
		}
		String path = pathOn.substring(0, pathOn.lastIndexOf("/"));
		String nom = pathOn.substring(pathOn.lastIndexOf("/") + 1);
		nom = nom.substring(0, nom.indexOf('.'));
		String outputPath = path + "/" + nom + "_monochrome." + "png";
		try {
			BufferedImage bI = ImageIO.read(new File(pathOn));
			for(int i = 0; i < bI.getWidth(); i++) {
				for(int j = 0; j < bI.getHeight(); j++) {
					Color base = new Color(bI.getRGB(i, j));
					int r = base.getRed();
					int g = base.getGreen();
					int b = base.getBlue();
					int a = base.getAlpha();
					int av = (r + g + b) / 3;
					bI.setRGB(i, j, new Color(av, av, av, a).getRGB());
				}
			}
			ImageIO.write(bI, "png", new File(outputPath));
			pathOff = outputPath;
		}
		catch(Exception e) {
			e.printStackTrace();
			pathOff = null;
		}
	}
	
	public void draw(HandlePanel p, int centerX, int centerY, int wid, int hei) {
		if(edit) {
			drawEdit(p, centerX, centerY, wid, hei);
		}
		else {
			drawDisplay(p, centerX, centerY, wid, hei);
		}
		p.handleThickRectangle(getPrefix() + "_outline_frame", "move", 20, centerX - wid / 2, centerY - hei / 2, centerX + wid / 2, centerY + hei / 2, Color.black, 3);
	}
	
	private void drawEdit(HandlePanel p, int centerX, int centerY, int wid, int hei) {
		p.removeElementPrefixed(getPrefix());
		
		int numThings = 6;
		
		int distMove = hei / (numThings + 1);
		
		int currY = centerY - hei / 2;
		

		handleSelectButton(p, centerX, currY, wid * 9 / 10, hei / (numThings + 2), defaultBackground, -1);
		
		currY += distMove;
		
		handleSelectButton(p, centerX, currY, wid * 9 / 10, hei / (numThings + 2), defaultBackground, -1);
		
		currY += distMove;
		
		handleSelectButton(p, centerX, currY, wid * 9 / 10, hei / (numThings + 2), defaultBackground, -1);
		
		currY += distMove;
		
	}
	
	private void handleSelectButton(HandlePanel p, int x, int y, int wid, int hei, String currDisp, int code) {
		int lWid = wid * 7 / 10;
		int lX = x + lWid / 2;
		int rWid = wid * 2 / 10;
		int rX = x + wid - rWid / 2;
		
		p.handleRectangle(getPrefix() + "_edit_select_" + code, "move", 35, lX, y, lWid, hei, Color.white, Color.black);
		
		if(currDisp != null) {
			p.handleImage(getPrefix() + "_edit_select_image_" + code, "move", 35, rX, y, rWid, hei, true, currDisp);
		}
		
	}
	
	private void drawDisplay(HandlePanel p, int centerX, int centerY, int wid, int hei) {
		String imgUse = active ? pathOn : pathOff;
		String useBack = manualBackground == null ? defaultBackground : manualBackground;
		p.removeElementPrefixed(getPrefix());

		p.handleImage(getPrefix() + "background_image", "no_move", 30, centerX, centerY, wid, hei, false, useBack);
		p.handleImage(getPrefix() + "image", "no_move", 30, centerX, centerY, wid, hei, true, imgUse);
		p.handleButton(getPrefix() + "button_active_toggle", "move", 30, centerX, centerY, wid, hei, CODE_TOGGLE_ACTIVE);
		if(showTitle) {
			title.drawTitle(p, centerX, centerY + 3 * hei / 8, wid, hei / 4);
		}
	}

	public void importData(Data in) {
		setTitle(in.getString(DATA_LABEL_TITLE));
		setTitlePath(in.getString(DATA_LABEL_TITLE_PATH));
		setImageOn(in.getString(DATA_LABEL_PATH_ON));
		setImageOff(in.getString(DATA_LABEL_PATH_OFF));
		setActive(in.getInt(DATA_LABEL_ACTIVE) == BOOLEAN_ON ? true : false);
		setShowTitle(in.getInt(DATA_LABEL_SHOW_TITLE) == BOOLEAN_ON ? true : false);
		setManualBackground(in.getString(DATA_LABEL_BACKGROUND));
	}
	
	public Data exportData() {
		Data out = new Data();
		out.setTitle(title.getTitle());
		
		out.addString(title.getTitle(), DATA_LABEL_TITLE);
		out.addString(title.getTitlePath(), DATA_LABEL_TITLE_PATH);
		out.addString(pathOn, DATA_LABEL_PATH_ON);
		out.addString(pathOff, DATA_LABEL_PATH_OFF);
		out.addInt(active ? BOOLEAN_ON : BOOLEAN_OFF, DATA_LABEL_ACTIVE);
		out.addInt(showTitle ? BOOLEAN_ON : BOOLEAN_OFF, DATA_LABEL_SHOW_TITLE);
		out.addString(manualBackground, DATA_LABEL_BACKGROUND);
		
		return out;
	}
	
	public void toggleEdit() {
		edit = !edit;
	}
	
	public void toggleActive() {
		active = !active;
	}
	
	public void toggleShowTitle() {
		showTitle = !showTitle;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setTitle(String tit) {
		if(title == null) {
			title = new TitleDraw(identity);
		}
		title.setTitle(tit);
	}
	
	public void setTitlePath(String titPath) {
		if(title == null) {
			title = new TitleDraw(identity);
		}
		title.setTitlePath(titPath);
	}
	
	public void setImageOn(String imgPathOn) {
		pathOn = imgPathOn;
		if(pathOff == null) {
			makeOffGrayscale();
		}
	}
	
	public void setImageOff(String imgPathOff) {
		pathOff = imgPathOff;
	}
	
	public void setActive(boolean in) {
		active = in;
	}
	
	public void setEdit(boolean in) {
		edit = in;
	}
	
	public void setShowTitle(boolean in) {
		showTitle = in;
	}
	
	public void setManualBackground(String path) {
		manualBackground = path;
	}
	
	public static void setDefaultBackground(String in) {
		defaultBackground = in;
	}
	
	public static boolean isCodeReactable(int code){
		return code >= CODE_BASE && code < CODE_LID;
	}
	
//---  Helper Methods   -----------------------------------------------------------------------
	
	private String getPrefix() {
		return identity + "_" + title + "_";
	}
	
}
