package tool;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class Data {
	
//---  Constants   ----------------------------------------------------------------------------
	
	private final static String INT = "i";
	private final static String INT_ARR = "i*";
	private final static String DBL = "d";
	private final static String DBL_ARR = "d*";
	private final static String STR = "s";
	private final static String STR_ARR = "s*";
	private final static String DAT = "D";
	private final static String DAT_ARR = "D*";
	
	private final static String SEPARATOR = " : ";
	private final static char DAT_END = ';';
	private final static char ARR_START = '[';
	private final static char ARR_END = ']';
	
	
//---  Instance Variables   -------------------------------------------------------------------

	private String title;
	
	private HashMap<String, Object> data;
	
	private HashMap<String, String> types;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Data(String path) throws Exception{
		data = new HashMap<String, Object>();
		types = new HashMap<String, String>();
		
		InputStream in = retrieveInputStream(path);
		BufferedReader raf = new BufferedReader(new InputStreamReader(in));
		String line = raf.readLine();
		
		LinkedList<Data> next = new LinkedList<Data>();
		LinkedList<ArrayList<Data>> collec = new LinkedList<ArrayList<Data>>();
		LinkedList<String> names = new LinkedList<String>();
		LinkedList<Integer> posit = new LinkedList<Integer>();
		while(line != null) {
		  String[] details = line.split(SEPARATOR);
		  String name = cleanString(details[0].trim());
		  if(title == null) {
			  title = name;
		  }
		  if(details.length <= 1) {
			  name = details[0];
			  if(name.charAt(name.length()-1) == DAT_END) {
				  Data out = next.pollLast();
				  if(posit.size() > 0 && posit.peekLast() == next.size()) {
					  collec.peekLast().add(out);
				  }
				  else {
					  if(next.size() == 0) {
						  this.addData(out);
					  }
					  else {
						  next.peekLast().addData(out);
					  }
				  }
			  }
			  if(name.charAt(name.length() - 1) == ARR_END && posit.size() > 0) {
				  ArrayList<Data> datLis = collec.pollLast();
				  posit.pollLast();
				  if(next.size() == 0) {
					  this.addDataArray(names.pollLast(), datLis.toArray(new Data[datLis.size()]));
				  }
				  else {
					  next.peekLast().addDataArray(names.pollLast(), datLis.toArray(new Data[datLis.size()]));
				  }
			  }
			  line = raf.readLine();
			  continue;
		  }
		  if(details[2].charAt(details[2].length() - 1) == ',') {
			  details[2] = details[2].substring(0, details[2].length() - 1);
		  }
		  switch(details[1]) {
			  case INT:
				  if(next.size() == 0) {
					  this.addInt(Integer.parseInt(details[2]), name);
				  }
				  else {
					  next.peekLast().addInt(Integer.parseInt(details[2]), name);
				  }
				  break;
			  case INT_ARR:
				  String[] workInt = details[2].replaceAll("[^\\d,-]", "").split(",");
				  int[] outInt = new int[workInt.length];
				  for(int i = 0; i < workInt.length; i++) {
					  outInt[i] = Integer.parseInt(workInt[i]);
				  }
				  if(next.size() == 0) {
					  this.addIntArray(outInt, name);
				  }
				  else {
					  next.peekLast().addIntArray(outInt, name);
				  }
				  break;
			  case DBL:
				  if(next.size() == 0) {
					  this.addDouble(Double.parseDouble(details[2]), name);
				  }
				  else {
					  next.peekLast().addDouble(Double.parseDouble(details[2]), name);
				  }
				  break;
			  case DBL_ARR:
				  String[] workDbl = details[2].replaceAll("[^\\d,-]", "").split(",");
				  double[] outDbl = new double[workDbl.length];
				  for(int i = 0; i < workDbl.length; i++) {
					  outDbl[i] = Double.parseDouble(workDbl[i]);
				  }
				  if(next.size() == 0) {
					  this.addDoubleArray(outDbl, name);
				  }
				  else {
					  next.peekLast().addDoubleArray(outDbl, name);
				  }
				  break;
			  case STR:
				  String use = cleanString(details[2]);
				  if(next.size() == 0) {
					  this.addString(use, name);
				  }
				  else {
					  next.peekLast().addString(use, name);
				  }
				  break;
			  case STR_ARR:
				  String[] workStr = cleanString(details[2].replaceAll("[\\[\\]]", "")).split(" ");
				  if(next.size() == 0) {
					  this.addStringArray(workStr, name);
				  }
				  else {
					  next.peekLast().addStringArray(workStr, name);
				  }
				  break;
			  case DAT:
				  Data common = new Data();
				  common.setTitle(name);
				  next.add(common);
				  break;
			  case DAT_ARR:
				  collec.addLast(new ArrayList<Data>());
				  names.addLast(name);
				  posit.addLast(next.size());
				  break;
			  default:
				  System.out.println("Unrecognized data type: " + details[1]);
				  break;
		  }
		  line = raf.readLine();
		}
		raf.close();
	}
	
	public Data() {
		data = new HashMap<String, Object>();
		types = new HashMap<String, String>();
	}
	
//---  Operations   ---------------------------------------------------------------------------
		
	public void save(String name) {
		try {
			if(!(name.substring(name.length() - 4)).equals(".dta")) {
				name += ".dta";
			}
			File f = new File(name);
			f.delete();
			RandomAccessFile raf = new RandomAccessFile(f, "rw");
			raf.writeBytes(this.toString());
			raf.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setTitle(String name) {
		title = name;
	}
	
//---  Adder Methods   ------------------------------------------------------------------------
	
	public void addData(Data dat) {
		data.put(dat.getTitle(), dat);
		types.put(dat.getTitle(), DAT);
	}
	
	public void addData(String name, Data dat) {
		data.put(name, dat);
		types.put(name, DAT);
	}
	
	public void addDataArray(String name, Data[] dat) {
		data.put(name, dat);
		types.put(name, DAT_ARR);
	}
	
	public void addString(String dat, String name) {
		data.put(name, dat);
		types.put(name, STR);
	}
	
	public void addStringArray(String[] dat, String name) {
		data.put(name, dat);
		types.put(name, STR_ARR);
	}
	
	public void addInt(int dat, String name) {
		data.put(name, dat);
		types.put(name, INT);
	}
	
	public void addIntArray(int[] dat, String name) {
		data.put(name, dat);
		types.put(name, INT_ARR);
	}
	
	public void addDouble(double dat, String name) {
		data.put(name, dat);
		types.put(name, DBL);
	}
	
	public void addDoubleArray(double[] dat, String name) {
		data.put(name, dat);
		types.put(name, DBL_ARR);
	}
	
//---  Remover Methods   ----------------------------------------------------------------------
	
	public void removeData(String name) {
		data.remove(name);
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public String getTitle() {
		return title;
	}
	
	public HashMap<String, Object> retrieve(){
		return data;
	}
	
	public Data getDataset(String name) {
		return (Data)data.get(name);
	}
	
	public Data[] getDatasetArray(String name) {
		return (Data[])data.get(name);
	}
	
	public String[] getStringArray(String name) {
		return (String[])data.get(name);
	}
	
	public String getString(String name) {
		return (String)data.get(name);
	}
	
	public int getInt(String name) {
		return (int)data.get(name);
	}
	
	public int[] getIntArray(String name) {
		return(int[])data.get(name);
	}
	
	public double getDouble(String name) {
		return (double)data.get(name);
	}
	
	public double[] getDoubleArray(String name) {
		return (double[])data.get(name);
	}
	
//---  Mechanics   ----------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return toString(0);
	}
	
	private String toString(int depth){
		if(types.get(title) == DAT && data.values().size() == 1) {
			return getDataset(title).toString();
		}
		StringBuilder out = new StringBuilder();
		String fbuffer = "";
		for(int i = 0; i < depth ; i++) {
			fbuffer += "\t";
		}
		out.append(fbuffer + "\"" + getTitle() + "\"" + SEPARATOR + DAT + SEPARATOR + "{\n");
		String buffer = fbuffer + "\t";
		for(String s : data.keySet()) {
			switch(types.get(s)) {
				case DAT :
					out.append(this.getDataset(s).toString(depth + 1));
					break;
				case DAT_ARR : 
					out.append(buffer + "\"" + s + "\"" + SEPARATOR + DAT_ARR + SEPARATOR + "[\n");
					for(Data d : this.getDatasetArray(s)) {
						out.append(d.toString(depth + 2));
					}
					out.append(buffer + ARR_END + "\n");
					break;
				case INT : 
					out.append(buffer + "\"" + s + "\"" +  SEPARATOR + INT + SEPARATOR + this.getInt(s) + ",\n");
					break;
				case INT_ARR :
					out.append(buffer + "\"" + s + "\"" +  SEPARATOR + INT_ARR + SEPARATOR + Arrays.toString(this.getIntArray(s)) + ",\n");
					break;
				case DBL :
					out.append(buffer + "\"" + s + "\"" +  SEPARATOR + DBL + SEPARATOR + this.getDouble(s) + ",\n");
					break;
				case DBL_ARR :
					out.append(buffer + "\"" + s + "\"" +  SEPARATOR + DBL_ARR + SEPARATOR + Arrays.toString(this.getDoubleArray(s)) + ",\n");
					break;
				case STR :
					out.append(buffer + "\"" + s + "\"" +  SEPARATOR + STR + SEPARATOR + "\"" + this.getString(s).replaceAll("\"",  "\\\\\"") + "\",\n");
					break;
				case STR_ARR :
					String[] arr = this.getStringArray(s);
					String[] outA = new String[arr.length];
					for(int i = 0; i < arr.length; i++) {
						outA[i] = "\"" + arr[i].replaceAll("\"", "\\\\\"") + "\"";
					}
					out.append(buffer + "\"" + s + "\"" +  SEPARATOR + STR_ARR + SEPARATOR + Arrays.toString(outA) + ",\n");
					break;
				default :
					break;
			}
		}
		out.append(fbuffer + "};\n");
		return out.toString();
	}
	
	public String cleanString(String in) {
		return in.replaceAll("(?<![\\\\])\",", "").replaceAll("(?<![\\\\])\"", "").replaceAll("\\\\\"", "\"");
	}

	public InputStream retrieveInputStream(String pathIn) {
		String path = pathIn.replace("\\", "/");
		InputStream is = Data.class.getResourceAsStream(path); 
		if(is == null) {
			try {
				is = new FileInputStream(new File(path));
			}
			catch(Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return is;
	}
	
}