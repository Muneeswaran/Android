package hr.zbc.remainder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

// http://android-er.blogspot.com/2010/01/implement-simple-file-explorer-in.html
public class FilePicking extends Activity implements OnItemClickListener{

 private List<String> item = null;
 private List<String> path = null;
 private String root="/";
 private TextView myPath;
 private ListView folderList;

    /** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_picking);
        
        myPath = (TextView)findViewById(R.id.tvFilePicking);
        folderList = (ListView)findViewById(R.id.lvFilePicking);
        folderList.setOnItemClickListener(this);

        getDir(root);

    }

    

    private void getDir(String dirPath)

    {

     myPath.setText("Location: " + dirPath);

     item = new ArrayList<String>();
     path = new ArrayList<String>();

     File f = new File(dirPath);
     File[] files = f.listFiles();

     if(!dirPath.equals(root))

     {
      item.add(root);
      path.add(root);

      item.add("../");
      path.add(f.getParent());
     }
     
     for(int i=0; i < files.length; i++)

     {

       File file = files[i];
       path.add(file.getPath());

       if(file.isDirectory())
        item.add(file.getName() + "/");
       else
        item.add(file.getName());
     }
     ArrayAdapter<String> fileList =
      new ArrayAdapter<String>(this, R.layout.file_picking_row, item);
     folderList.setAdapter(fileList);

    }






	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {

		  File file = new File(path.get(position));

		  Log.d("Ovdje", "1");
		  if (file.isDirectory())

		  {
		   if(file.canRead())
		    getDir(path.get(position));
		   else
		   {

		    new AlertDialog.Builder(this)

		    .setTitle("[" + file.getName() + "] folder can't be read!")
		    .setPositiveButton("OK", 
		      new DialogInterface.OnClickListener() {


		       @Override

		       public void onClick(DialogInterface dialog, int which) {

		        // TODO Auto-generated method stub

		       }

		      }).show();

		   }

		  }

		  else

		  {

		   new AlertDialog.Builder(this)

		    .setTitle("[" + file.getName() + "]")

		    .setPositiveButton("OK", 

		      new DialogInterface.OnClickListener() {
		    	
		       @Override
		       public void onClick(DialogInterface dialog, int which) {
		        // TODO Auto-generated method stub
		       }
		      }).show();
		  }
		
	}
 
/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.file_picking, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
*/
}
