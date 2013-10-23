package hr.zbc.remindme;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Environment;

public class ClaFindFiles {

	Context context;

	public ClaFindFiles(Context contex){
		this.context = contex;
	}

	@SuppressWarnings("unchecked")
	protected Map<String, String> getDirectories(String dirPath){
		String sd = Environment.getExternalStorageDirectory().getPath();
		Map<String, String> dirMap = new HashMap<String, String>();
		
		//List<String> item = new ArrayList<String>();
		//List<String> path = new ArrayList<String>();
		//List<String> itemFile = new ArrayList<String>();
		//List<String> pathFile = new ArrayList<String>();

		File f = new File(dirPath);
		File[] files = f.listFiles();

		for(int i=0; i < files.length; i++)
		{
			File file = files[i];
			
			// Add only directories
			if (!file.isHidden() && file.canRead()) {
				if (file.isDirectory()){
					dirMap.put(file.getPath(), file.getName() + "/");
				}
			}
		}
		Map tempMap = new LinkedHashMap<String, String>();
		List tempList = new ArrayList(dirMap.entrySet());
		Collections.sort(tempList, new Comparator() {
			
            public int compare(Object o1, Object o2) {
            	return ((Map.Entry) o1).getValue().toString().compareToIgnoreCase(((Map.Entry) o2).getValue().toString());
                //return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
            }
        });
		if (!f.getPath().equals(sd)) {
			tempMap.put(sd, "/");
			tempMap.put(f.getParent(), "../");
		}
		for (Iterator it = tempList.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			tempMap.put(entry.getKey(), entry.getValue());
		}
		return tempMap;
	}
	
	@SuppressWarnings("unchecked")
	protected Map<String, String> getFiles(String dirPath){
		Map<String, String> fileMap = new HashMap<String, String>();
		
		File f = new File(dirPath);
		File[] files = f.listFiles();
		
		for(File file : files){
			if(!file.isHidden() && file.canRead() && file.isFile() && file.getName().endsWith(".txt")){
				fileMap.put(file.getPath(), file.getName());
			}
		}
		Map tempMap = new LinkedHashMap<String, String>();
		List tempList = new ArrayList(fileMap.entrySet());
		Collections.sort(tempList, new Comparator() {
			
            public int compare(Object o1, Object o2) {
            	return ((Map.Entry) o1).getValue().toString().compareToIgnoreCase(((Map.Entry) o2).getValue().toString());
                //return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
            }
        });
		for (Iterator it = tempList.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			tempMap.put(entry.getKey(), entry.getValue());
		}
		return tempMap;
	}
}
