package hr.zbc.remainder;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import android.os.AsyncTask;
import android.util.Log;

// Koristit æe commons-io
// Treba vratiti listu fileova za ArrayAdapter ili Mapu (da ima i path i ime)
public class CommonsFileList extends AsyncTask<String, Void, Map<String, String>>{
	/*
	public CommonsFileList(){
		
	}
	
	protected Map<String, String> returnFileList(String root){
		Map<String, String> mapOfFiles = new HashMap<String, String>();
		File f = new File(root);
		
		try {
            boolean recursive = true;
            
            Collection files = FileUtils.listFiles(f, new NameFileFilter("txt"), TrueFileFilter.INSTANCE);
            Log.d("FOR", "Prije for");
            for (Iterator iterator = files.iterator(); iterator.hasNext();) {
                File file = (File) iterator.next();
                if (file.isFile())
                	Log.d("FOR", file.getName());
                	mapOfFiles.put(file.getPath(), file.getName());
            }
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mapOfFiles;
	}
*/
	@Override
	protected Map<String, String> doInBackground(String... params) {
		Map<String, String> mapOfFiles = new HashMap<String, String>();
		File f = new File(params.toString());
		String[] ext = {"txt"};
		
		try {
            boolean recursive = true;
            
            Collection files = FileUtils.listFiles(FileUtils.getFile(params[0]), FileFilterUtils.suffixFileFilter(".txt"), TrueFileFilter.INSTANCE);
            Log.d("FOR", "Prije for");
            for (Iterator iterator = files.iterator(); iterator.hasNext();) {
                File file = (File) iterator.next();
                if (file.isFile())
                	Log.d("FOR", file.getName());
                	mapOfFiles.put(file.getPath(), file.getName());
            }
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mapOfFiles;
	}

}
