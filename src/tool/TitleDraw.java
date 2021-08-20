package tool;
import java.awt.Font;

import visual.composite.HandlePanel;

public class TitleDraw {

//---  Constants   ----------------------------------------------------------------------------
	
	private final static String TITLE_PATH_NULL = "no.no.no";
	
	private final static Font DEFAULT_TITLE_FONT = new Font("Serif", Font.BOLD, 30);
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private String title;
	
	private String titlePath;
	
	private Font titleFont;
	
	private int identity;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public TitleDraw(int in) {
		title = "";
		identity = in;
		setTitlePath(null);
		titleFont = DEFAULT_TITLE_FONT;
	}
	
	public TitleDraw(String tit, int in) {
		title = tit;
		identity = in;
		setTitlePath(null);
		titleFont = DEFAULT_TITLE_FONT;
	}
	
	public TitleDraw(String tit, String titPath, int in) {
		title = tit;
		identity = in;
		setTitlePath(titPath);
		titleFont = DEFAULT_TITLE_FONT;
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public void drawTitle(HandlePanel p, int centerX, int centerY, int wid, int hei) {
		if(titlePath.equals(TITLE_PATH_NULL) || p.retrieveImage(titlePath) == null) {
			p.handleText(identity + "_" + title + "_title_text", "move", 35, centerX, centerY, wid, hei, titleFont, title);
		}
		else {
			p.handleImage(identity + "_" + title + "_title_image", "move", 35, centerX, centerY, wid, hei, true, titlePath);
		}
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setTitle(String tit) {
		title = tit;
	}
	
	public void setTitlePath(String titPath) {
		titlePath = titPath == null ? TITLE_PATH_NULL : titPath;
	}
	
	public void setTitleFont(Font in) {
		titleFont = in;
	}
	
	public void setTitleFontSize(int in) {
		titleFont = new Font(titleFont.getFontName(), titleFont.getStyle(), in);
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public String getTitle() {
		return title;
	}
	
	public String getTitlePath() {
		return titlePath;
	}
	
	public Font getFont() {
		return titleFont;
	}
	
}
