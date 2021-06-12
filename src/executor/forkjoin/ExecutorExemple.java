/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package executor.forkjoin;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author osanf
 */

// ############################ EXECUTOR ACTIVITY ###########################
// Look for a string into a several files (1 file -> task).
// Get phil's passwords

// Callable Task
class CodeSearchTask implements Callable<String[]>
{
	String file;
	String user;
	public CodeSearchTask(String file, String user)
	{
		this.file = file;
		this.user = user;
	}

	@Override
	public String[] call()
	{
		// DELETE. Get total lines.
		/*
		long lineCount = 0;
		try (Stream<String> stream = Files.lines(FileSystems.getDefault().getPath(file), StandardCharsets.UTF_8)) {
			lineCount = stream.count();
		} catch (IOException ex) {
			System.err.println("Error amb el fitxer "+ file + ": "+ ex);
		}
		//~System.out.println(lineCount);
		*/
		
		// Get the codes =>  user:code
//		StringBuilder strBuilder = new StringBuilder();
		try (BufferedReader in = new BufferedReader( new FileReader(file));)
		{
			String line;
			List<String> codes = new ArrayList<>();

			while ((line = in.readLine()) != null)
			{
				String[] map = line.split(":");
				if(map[0].equals(user))
					codes.add(map[1]);
			}
			
			System.out.println("Procesed file:" + file + " by " + Thread.currentThread().getName() );
			return codes.toArray(new String[codes.size()]);
		} catch (FileNotFoundException ex) {
			System.err.println("Fitxer \""+ file + "\" no trobat: "+ ex);
		} catch (IOException ex) {
			System.err.println("Error amb el fitxer "+ file + ": "+ ex);
		}
		throw new UnsupportedOperationException("Not supported yet.");
	}
}



public class ExecutorExemple
{
	// Executed one time, to create files for the stufents
	public static void createFile(String file)
	{
		int lines = 500000;
		
		try (FileWriter writer = new FileWriter(file)) {
			SecureRandom randomizer;
				randomizer = SecureRandom.getInstance("SHA1PRNG");
			
			for(int i = 0; i < lines; i++)
			{
				String[] errorSoon = {"admin", "root", "user", "default", "manager", "master", "cisco", "super", "argo", "paul", "rick", "john", "steve","pete","ray"};
				if(i == 440999 && "Files/passwords-5.txt".equals(file))	// line 441000
					writer.write("phil" + ":"+ "2267" + "\n");
				if(i == 230599 && "Files/passwords-11.txt".equals(file))	// line 230600
					writer.write("phil" + ":"+ "1122" + "\n");
				if(i == 51233 && "Files/passwords-16.txt".equals(file))	// line 51234
					writer.write("phil" + ":"+ "4791" + "\n");
				if(i == 127999 && "Files/passwords-26.txt".equals(file))	// line 128000
					writer.write("phil" + ":"+ "7714" + "\n");
				if(i == 499999 && "Files/passwords-47.txt".equals(file))	// line 500000
					writer.write("phil" + ":"+ "phili" + "\n");
				if(i == 499999 && "Files/passwords-50.txt".equals(file))	// line 500000
					writer.write("phil" + ":"+ "phili" + "\n");
				if(i == 499999 && "Files/passwords-78.txt".equals(file))	// line 500000
					writer.write("phil" + ":"+ "phili" + "\n");
				if(i == 499999 && "Files/passwords-98.txt".equals(file))	// line 500000
					writer.write("phil" + ":"+ "phili" + "\n");
				if(i == 499999 && "Files/passwords-159.txt".equals(file))	// line 500000
					writer.write("phil" + ":"+ "phili" + "\n");
				
				else
					writer.write(errorSoon[randomizer.nextInt(errorSoon.length)] + ":"+ randomizer.nextInt(10000) + "\n");
			}
			writer.write("end" + ":"+ "9999" + "\n");
			
		} catch (IOException | NoSuchAlgorithmException ex) {
			Logger.getLogger(ExecutorForkJoin.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public static void createFiles()
	{
		for(int i = 0; i < 300; i++)
		{
			createFile("Files/passwords-" +(i+1)+ ".txt");
		}
	}
	
	
	public static void start()
	{
		//~createFiles();
		String workingDir = System.getProperty("user.dir");
		System.out.println("Current working directory : " + workingDir);
		
		// Tasks list
		List<CodeSearchTask> tasks = new ArrayList<>();
        for (int i = 0; i < 300; i++) {
            CodeSearchTask task = new CodeSearchTask("Files/passwords-"+(i+1)+".txt", "phil");
            tasks.add(task);
        }
		
		
		ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
		//~ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newCachedThreadPool();

		List<Future<String[]>> responseList = null;
		try {
			responseList = pool.invokeAll(tasks);
			pool.shutdown();
			//~pool.awaitTermination(5, TimeUnit.SECONDS);
			
		} catch (InterruptedException ex) {
			Logger.getLogger(ExecutorForkJoin.class.getName()).log(Level.SEVERE, null, ex);
        }
		
		// Tasks finished. Show results
		if(responseList == null)	return;
		for (int i = 0; i < tasks.size(); i++)
		{
			// 'get' method waits for the thread to finish de task. There is no need
			// to check finish with 'isDone'
            Future<String[]> resultat = responseList.get(i);
            try {
				for(String s : resultat.get())
				{
					System.out.println("Usuari trobat al fitxer passwords-" + i + ".txt. Clau:" + s);
				}
                
            } catch (InterruptedException | ExecutionException ex) {
				Logger.getLogger(ExecutorForkJoin.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
	}	
}
