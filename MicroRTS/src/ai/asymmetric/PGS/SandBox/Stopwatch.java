package ai.asymmetric.PGS.SandBox;

import java.text.SimpleDateFormat;  
import java.util.Calendar;    
  
// Esta classe realizara a medicao de tempo dos testes de desempenho
// Seu conteudo e baseado na classe Stopwatch produzida por Carlos Quintanilla 
// e disponivel em: http://carlosqt.blogspot.com/2011/05/stopwatch-class-for-java.html

/*

Exemplo basico de uso:

Stopwatch timer = new Stopwatch();  

timer.start();     

// Processamento...

timer.stop();  

System.out.println("Tempo:" + timer.getElapsed());  

*/

public class Stopwatch {  
    // Constantes de tempo
    private final long nsPerTick = 100;  
    private final long nsPerMs = 1000000;  
    private final long nsPerSs = 1000000000;  
  
    // Variaveis que armazenam o tempo
    private long startTime = 0;  
    private long stopTime = 0;  
    private boolean running = false;  
      
    // Iniciando a medicaoo do intervalo de tempo 
    public void start() {  
        this.startTime = System.nanoTime();         
        this.running = true;  
    }  
      
     // Parando a medica0o do intervalo de tempo 
    public void stop() {  
        this.stopTime = System.nanoTime();  
        this.running = false;  
    }  
      
     // Reinicializando a medica0o de tempo. 
    public void reset() {  
        this.startTime = 0;  
        this.stopTime = 0;  
        this.running = false;  
    }  
      
    // Obtem o tempo medido em nanosegundos 
     // 1 Tick = 100 nanoseguntos  
    public long getElapsedTicks() {  
        long elapsed;  
        if (running) {  
             elapsed = (System.nanoTime() - startTime);  
        }  
        else {  
            elapsed = (stopTime - startTime);  
        }  
        return elapsed / nsPerTick;  
    }  
      
   
     // Retorna o tempo percorrido no formato 
     // 00:00:00.0000000 = 00:mm:ss.SSS + 9999 Ticks 
    public String getElapsed() {  
        String timeFormatted = "";  
        timeFormatted = this.formatTime(this.getElapsedTicks());          
        return timeFormatted;  
    }  
      
    // Retorna o tempo percorrido no formato 
    // 00:00:00.0000000 = 00:mm:ss.SSS + #### Ticks 
    private String formatTime(final long elapsedTicks) {          
        String formattedTime = "";  
      
        SimpleDateFormat formatter = new SimpleDateFormat("00:mm:ss.SSS");  
        Calendar calendar = Calendar.getInstance();          
          
        if (elapsedTicks <= 9999) {  
            calendar.setTimeInMillis(0);  
            formattedTime = formatter.format(calendar.getTime())   
                    + String.valueOf(String.format("%04d", elapsedTicks));  
        }  
        else {  
            calendar.setTimeInMillis(elapsedTicks * nsPerTick / nsPerMs);              
            String formattedTicks = String.format("%07d", elapsedTicks);  
            formattedTicks = formattedTicks.substring(formattedTicks.length() - 4);  
            formattedTime = formatter.format(calendar.getTime()) + formattedTicks;  
        }  
        return formattedTime;  
    }  
} 
