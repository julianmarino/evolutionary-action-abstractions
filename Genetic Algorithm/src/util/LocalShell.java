package util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Logger;

public class LocalShell {

	private static final Logger log = Logger.getLogger(LocalShell.class.getName());
	
	public String executeCommand(final String command) throws IOException {
		String retorno="";
		final ArrayList<String> commands = new ArrayList<String>();
		commands.add("/bin/bash");
		commands.add("-c");
		commands.add(command);

		BufferedReader br = null;        

		try {                        
			final ProcessBuilder p = new ProcessBuilder(commands);
			final Process process = p.start();
			final InputStream is = process.getInputStream();
			final InputStreamReader isr = new InputStreamReader(is);
			br = new BufferedReader(isr);

			String line;            
			while((line = br.readLine()) != null) {
				//System.out.println("Retorno do comando = [" + line + "]");
				retorno = line;
			}
		} catch (IOException ioe) {
			log.severe("Erro ao executar comando shell" + ioe.getMessage());
			throw ioe;
		} finally {
			secureClose(br);
		}
		return retorno;
	}

	private void secureClose(final Closeable resource) {
		try {
			if (resource != null) {
				resource.close();
			}
		} catch (IOException ex) {
			log.severe("Erro = " + ex.getMessage());
		}
	}
	
	public String cmdExecScript(String script) throws IOException, InterruptedException {
        String line;
        StringBuilder output = new StringBuilder();
        Process p = Runtime.getRuntime().exec(new String[]{"/bin/sh" ,"-c", script});
        BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        while ((line = bri.readLine()) != null) {
            output.append(line).append("\n");
        }
        bri.close();
        while ((line = bre.readLine()) != null) {
            output.append(line).append("\n");
        }
        bre.close();
        p.waitFor();
        System.out.println(output.toString());
        return output.toString();
    }
	
	
	public String cmdExec(String cmdLine) throws IOException, InterruptedException {
        String line;
        StringBuilder output = new StringBuilder();
        Process p = Runtime.getRuntime().exec(cmdLine);
        BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        while ((line = bri.readLine()) != null) {
            output.append(line).append("\n");
        }
        bri.close();
        while ((line = bre.readLine()) != null) {
            output.append(line).append("\n");
        }
        bre.close();
        p.waitFor();
        return output.toString();
    }
	
	public String executeScript(String caminhoScript){
		ProcessBuilder pb = new ProcessBuilder(caminhoScript);
		
		BufferedReader br = null; 
		Process process;
		try {
			process = pb.start();
			
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			br = new BufferedReader(isr);

			String line;            
			while((line = br.readLine()) != null) {
				System.out.println("Retorno do comando = [" + line + "]");
			}
			br.close();
			
			is = process.getErrorStream();
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			while((line = br.readLine()) != null) {
				System.out.println("Erros do comando = [" + line + "]");
			}
			br.close();
			
			
			process.waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Erro ao executar Script!");
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return br.toString();
	}

	/*public static void main (String[] args) throws IOException {
		final LocalShell shell = new LocalShell();
		shell.executeCommand("ls ~");
	}
	*/
}
