package resources.ShellScriptRunner.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

import resources.common.Logger;
import resources.exceptions.InvalidAutomationParameterException;

public class FileTools {
	
	final static String RESULT_FILE = "results.txt";
	public final static String STAGING_DIR = "XMLs\\";
	final static String BN_PROP = "prompts";
	final static String BN_LIST = "big_nodes";
	//final static List<String> parentNodes = 
	//	Arrays.asList(Functions.readProperties(BN_PROP,BN_LIST).split(", "));
	
	/**
	 * Overloaded. Creates a new file for {@code openFileandReadLines(File)}
	 * 
	 * @throws InvalidAutomationParameterException 
	 */
	public static LinkedList<String> openFileAndReadLines(String filename)
			throws IOException { 
			return openFileAndReadLines(new File(filename));		
	}
	
	 /**
	 * Opens a {@code File} and reads with {@link BufferedReader}
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static LinkedList<String> openFileAndReadLines(File details)
			throws IOException {
		BufferedReader bfr;
		try{
			bfr = new BufferedReader(new FileReader(details));
		} catch (FileNotFoundException fnfe){
			throw new IOException(
					"[ft:rl:E] file " + details.getName()
					+ " was not found.");
		}
		LinkedList<String> lines = new LinkedList<String>();
		String str;
		
		while((str = bfr.readLine()) != null) {
			lines.add(str);
		}
		bfr.close();
		return lines;
	}
	
	/**
	 * Overloaded. Creates a {@code File} for {@link openFileAndWriteLines(File, LinkedList}
	 * 
	 * Uses a forEach lambda function. Java 1.8 or higher required. 
	 * 
	 * @throws IOException
	 */
	public static File openFileAndWriteLines(
			String argsFilename, LinkedList<String> cmdArgs) throws IOException {
		
		File argsFile = null;
		
		try {
			argsFile = new File(argsFilename);
		}  catch (NullPointerException npe) {
			throw new IOException(
					"[ft:wl:E] file " + argsFilename 
					+ " was not found.");
		}
		return openFileAndWriteLines(argsFile, cmdArgs); 
	}
	
	/**
	 * Opens a {@code File} and writes with {@link BufferedWriter}
	 * 
	 * Uses a forEach lambda function. Java 1.8 or higher required. 
	 * 
	 * @throws IOException
	 */
	public static File openFileAndWriteLines(
			File argsFile, LinkedList<String> cmdArgs)
			throws IOException {
		
		final BufferedWriter bfw = new BufferedWriter(new FileWriter(argsFile));
		
		cmdArgs.forEach((temp) -> {
			try {
				bfw.write(temp);
				bfw.newLine();
			} catch (IOException ioe){
				System.err.println(
						"[ft:wl:E] " + temp +
						" could not be written to "
						+ argsFile);
				System.exit(1);
			}
		});
		
		bfw.close();
		return argsFile;
	}
	
	/**
	 * Overloaded. Assumes RESULT_FILE as desitnation and calls openFileAndWriteLines(filename,results)
	 * @param results
	 * @return
	 * @throws IOException
	 */
	public static File openFileAndWriteLines(String results)
			throws IOException {
		return openFileAndWriteLines(RESULT_FILE,results);
	}
	
	/**
	 * Overloaded. Creates a {@code File} for {@link openFileAndWriteLines(File, LinkedList}
	 * 
	 * Uses a forEach lambda function. Java 1.8 or higher required. 
	 * 
	 * @throws IOException
	 */
	public static File openFileAndWriteLines(String filename, String results)
			throws IOException {
		File fileToWrite = null;
		try {
			fileToWrite = new File(filename);
		}  catch (NullPointerException npe) {
			fileToWrite.createNewFile();
		}
		
		final BufferedWriter bfw = new BufferedWriter(new FileWriter(fileToWrite));
		String files[] = results.split("\n");
		for(int i=0;i<files.length;i++){
			bfw.write(files[i]);
			bfw.newLine();
		}
		bfw.close();
		return fileToWrite; 
	}
	
	public static void prependLines(String filename, String prefix) 
			throws InvalidAutomationParameterException {
		LinkedList<String>fileContents = new LinkedList<String>();
		
		try {
			fileContents = openFileAndReadLines(filename);
		} catch (IOException e) {
			throw new InvalidAutomationParameterException(
					"[ft:pl:E] Unable to open "+filename);
		}
		Iterator<String> itr = fileContents.iterator();
		while(itr.hasNext()){
			String lineToEdit = itr.next();
			lineToEdit = prefix+lineToEdit;
		}
		try {
			openFileAndWriteLines(filename,fileContents);
		} catch (IOException e) {
			throw new InvalidAutomationParameterException(
					"[ft:pl:E] Unable to update "+filename);
		}
	}
	
	/**
	 * sftpBatch
	 * 
	 * @return a linkedList of filenames
	 * @throws InvalidAutomationParameterException
	 */
	static LinkedList<String> sftpBatch()
			throws InvalidAutomationParameterException {
		LinkedList<String> results;
		try {
			results = openFileAndReadLines(RESULT_FILE);
		} catch (IOException ioe) {
			throw new InvalidAutomationParameterException(ioe);
		}
		LinkedList<String> returns = new LinkedList<String> ();
		results.forEach((temp) -> {	returns.add(temp+":"+STAGING_DIR); });
		return returns;
	}
	
	public static boolean peekPlus(String filename, Scanner in)
			throws InvalidAutomationParameterException {
		LinkedList<String> fileContents = new LinkedList<String>();
		try {
			fileContents = openFileAndReadLines(filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final int LINES = 5;
		final int CEILING = fileContents.size();
		int range = 1;
		char choice;
				
		CommandlineMenu clm = new CommandlineMenu("peek",true);
		
		Logger.log("[ft:p+:I] File Preview:");
		while((LINES*range)<=CEILING){
			for(int i=0;i<(LINES*range);i++){
				Logger.log(fileContents.get(i));
			}
			
			choice = clm.uiAction(in).charAt(0);
			if(choice == 'Y'){
				return true;
			} else if (choice == 'N') {
				return false;
			} else {
				range++;
				if((LINES*range)>=CEILING)
					range = CEILING/LINES;
				continue;
			}
		}
		
		//When we reach the end of the file, ask if this is the file they want to parse
		clm = new CommandlineMenu("eof", false);
		if(Boolean.parseBoolean(clm.uiAction(in))){
		return true;
		} else {
			return false;
		}
	}
	
	public static HashMap<String, String> parseFileToMap()
			throws IOException, InvalidAutomationParameterException {
		return parseFileToMap(RESULT_FILE);
	}
	
	public static HashMap<String, String> parseFileToMap(String filename) 
			throws IOException, InvalidAutomationParameterException {				
		LinkedList<String> resultsList = FileTools.openFileAndReadLines(filename);
		Iterator<String> itr = resultsList.iterator();
		HashMap<String, String> mapToVerify = new HashMap<String,String>();
		LinkedList<String> nesting = new LinkedList<String>();
		String[] pair;
		while(itr.hasNext()) {
			String line = itr.next();
			pair = parseXmlLine(line);
			if(pair == null) continue; //special case for the xml header
			int count = 0;
			//If the current node is a Big Node
			//Keep that and append it to all subnodes
			if(isParentNode(pair[0])){
				if(nesting.contains(pair[0])){
					nesting.removeLastOccurrence(pair[0]);
					count = 0;
				} else {
					if (pair.length>1)
						nesting.add(pair[0]+pair[1]); //special case for JACKETNUMBER
					else
						nesting.add(pair[0]);
					count++;
				}
				continue;
			} else {
				String nestedPath = nesting.getFirst();
				if (nesting.size()>1){
					for(int i=1;i<nesting.size();i++){
						nestedPath = nestedPath + "\\" + nesting.get(i);
					}
				}
				if(count>1) {
					pair[0] = nestedPath + "("+count+")\\" + pair[0];
				} else {
					pair[0] = nestedPath + "\\" + pair[0];
				}
				mapToVerify.put(pair[0], pair[1]);
			}	
		}
		return mapToVerify;
	}
	
	private static boolean isParentNode(String nodeName){
		return false; //parentNodes.contains(nodeName);
	}
	
	private static String[] parseXmlLine(String line){
		line = line.replaceAll("</", "<");
		line = line.replaceAll("<", " ");
		line = line.replace('>', ' ');
		line = line.trim();
		if(line.startsWith("?")) //special case for XML header
			return null;
		String lineContents[] = line.split(" ");
		String pair[] = new String[2];
		if (lineContents.length < 2) //specialCase for node hierarchy
			return lineContents;
		for(int i=0;i<lineContents.length;i++) {
			//special case for JACKETNUMBER and BORROWER
			if(lineContents[i].contains("=/")) { 
				lineContents[i] = lineContents[i].replaceAll("=/", " ");
				lineContents[i] = lineContents[i].split(" ")[i];
				pair[i] = lineContents[i];
				Logger.log("pair["+i+"]"+pair[i]);
				pair[i+1] = lineContents[i+2];
				Logger.log("pair["+(i+1)+"]"+pair[i+2]);
			} else {
				if(i<pair.length) {
					pair[i] = (lineContents[i]);
					Logger.log("pair["+i+"]"+pair[i]);
				}
			}
		}
		return pair;
	}

	public static HashMap<String,String> verifyMap(String filename, Scanner in)
			throws InvalidAutomationParameterException, IOException {
		if(filename.equals(RESULT_FILE)){
			LinkedList<String> resultList = new LinkedList<String>();
			LinkedList<String> erroredFiles = new LinkedList<String>();
			
			resultList = openFileAndReadLines(filename);
			
			Iterator<String> itr = resultList.iterator();
			while(itr.hasNext()){
				String path[] = itr.next().split("/");
				String pathname = STAGING_DIR+path[path.length-1];
					try{
						if(FileTools.peekPlus(pathname, in)){
							return parseFileToMap(pathname);
						} else {
							continue;
						}
					} catch (IOException ioe) {
						String errMsg[] = ioe.getMessage().split(" ");
						erroredFiles.add(errMsg[2]);
						continue;
					}
			}
			openFileAndWriteLines(RESULT_FILE, erroredFiles);
			throw new IOException("copyFiles");
		} else {
				return parseFileToMap(STAGING_DIR+filename);
		}
	}
}
