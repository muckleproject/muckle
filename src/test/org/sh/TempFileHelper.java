package test.org.sh;

/*
*	Copyright 2013 The Muckle Project
*
*	Licensed under the Apache License, Version 2.0 (the "License");
*	you may not use this file except in compliance with the License.
*	You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*	Unless required by applicable law or agreed to in writing, software
*	distributed under the License is distributed on an "AS IS" BASIS,
*	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*	See the License for the specific language governing permissions and
*	limitations under the License.
*/


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * TempFileHelper
 */
public class TempFileHelper {

	ArrayList<File> filesCreated = new ArrayList<File>();
	ArrayList<File> directoriesCreated = new ArrayList<File>();
	
	File tempRoot;
	
	public TempFileHelper() throws IOException {
		File t = File.createTempFile("test", ".tst");
		tempRoot = t.getParentFile();
		t.delete();
	}
	
	public File createDir(Object name){
		return createDir(tempRoot, name.getClass().getSimpleName());
	}
	
	public File createDir(String name){
		return createDir(tempRoot, name);
	}

	public File createDir(File parent, String name){
		File d = new File(parent, name);
		d.mkdir();
		d.deleteOnExit();
		directoriesCreated.add(d);
		return d;
	}

	public File createFile(File parent, String name, String ext) throws IOException{
		File f = new File(parent, name+ext);
		f.deleteOnExit();
		FileWriter fw = new FileWriter(f);
		fw.close();
		filesCreated.add(f);
		return f;
	}
	
	public void cleanDir(File dir){
		if(dir.isDirectory()){
			File[] files = dir.listFiles();
			for(int i=0; i<files.length; i++){
				File f = files[i];
				if(f.isFile()){
					f.delete();
				}
			}
		}
	}
	
	public void cleanUp(){
		for(File f : filesCreated){
			f.delete();
		}
		for(int i=directoriesCreated.size() -1 ; i >= 0; i--){
			directoriesCreated.get(i).delete();
		}
	}
	
	public File copyResource(String path, File parent, String name, String ext) throws IOException{
		File dest = createFile(parent, name, ext);
		
		FileOutputStream fos = new FileOutputStream(dest);
		InputStream is = this.getClass().getResourceAsStream(path);
		
		byte[] buffer = new byte[50000];
		int len;
		
		while((len = is.read(buffer)) > 0){
			fos.write(buffer, 0, len);
		}
		
		fos.close();
		is.close();
		
		return dest;
	}
	
	public File copyFile(File src, File destDir) throws IOException {
		File dest = new File(destDir, src.getName());
		filesCreated.add(dest);
		FileChannel srcCh = new FileInputStream(src).getChannel();
		FileChannel destCh = new FileOutputStream(dest).getChannel();
		srcCh.transferTo(0, src.length(), destCh);
		srcCh.close();
		destCh.close();
		return dest;
	}

}
