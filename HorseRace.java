import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class HorseRace {
	
	private final int NUM_HORSES = 5;
	private final int TRACK_LENGTH = 1000;
	private final int FINISH_LINE = 850;
	private final int MIN_DIST = 10; //minimum distance that a horse can run on their turn
	private final int SLEEP = 175; //Changes speed that horses run

	private Canvas[] horses;
	private Thread[] threads;
	private GraphicsContext gc;	
	private int winner;
	private Random rand;
	private Lock lock;
	private boolean ongoingRace;
	private long t0;
	private long t1;
	
	public HorseRace() {
		
		horses = new Canvas[NUM_HORSES];
		threads = new Thread[NUM_HORSES];
		lock = new ReentrantLock();
		rand = new Random();
		winner = -1;
		ongoingRace = false;
		for(int i = 0; i < NUM_HORSES; i++) {
			
			horses[i] = new Canvas(TRACK_LENGTH, 71);
			drawHorse(i);
		}
	}
	
	public void startRace() {
		
		//If already ran race and completed, reset winner
		if(winner != -1) 		
			winner = -1;
		 		
		//if already running, reset race and stop all threads
		if(ongoingRace == true) {
			stopHorses();
			resetRace();
		}
		
		t0 = System.nanoTime();
		createThreads();
		ongoingRace = true;			
	}
	private int updateRace(int position, int horseNum) {
		
		lock.lock();
		
		int randDist = rand.nextInt(70) + MIN_DIST;		
		if(winner == -1) {
			
			if((randDist + position) > FINISH_LINE)						
				randDist = FINISH_LINE - position;
			
			moveHorse(horseNum - 1, randDist);
			position += randDist;
			if(position >= FINISH_LINE && !Thread.currentThread().isInterrupted()) {

				winner = horseNum;
				t1 = System.nanoTime();
				stopHorses();
				
				Platform.runLater(
					() -> {
						winnerDialogBox(winner, t1 - t0);
					}
				);				
			}
		}	
		
		lock.unlock();
		
		return position;
	}
	
	private void drawHorse(int horseIndex) {
		
		gc = horses[horseIndex].getGraphicsContext2D();				
		//head
		gc.strokeRect(70, 0, 30, 15);
		//body
		gc.strokeRect(0, 15, 80, 35);
		//feet
		gc.strokeRect(0, 50, 14, 14);
		gc.strokeRect(66, 50, 14, 14);
		gc.save();
	}
	
	private void moveHorse(int horseIndex, int distance) {

		gc = horses[horseIndex].getGraphicsContext2D();	
		
		gc.clearRect(0, 0, horses[horseIndex].getWidth(), horses[horseIndex].getHeight());		
    	gc.translate(distance, 0);   	
		//head
		gc.strokeRect(70, 0, 30, 15);
		//body
		gc.strokeRect(0, 15, 80, 35);
		//feet
		gc.strokeRect(0, 50, 14, 14);
		gc.strokeRect(66, 50, 14, 14);	
	}
	
	public void resetRace() {
		
		if(ongoingRace == true) 
			stopHorses();
		
		for(int i = 0; i < NUM_HORSES; i++) {
			
			gc = horses[i].getGraphicsContext2D();	
			gc.clearRect(0, 0, horses[i].getWidth(), horses[i].getHeight());
			gc.restore();			
			drawHorse(i);
		}
	}
	
	private void createThreads() {

		class Horse implements Runnable{
	
			private int horseNum;
			private int position;
					
			public Horse(int horseNum){
			
				this.horseNum = horseNum;
				this.position = 0;		
			}
						
			@Override
			public void run() {
				
				try {
									
					while(!Thread.currentThread().isInterrupted()) {
						
						if(winner == -1) {
	
							position = updateRace(position, horseNum);
							Thread.sleep(SLEEP);	
						}
				  }
				}
				catch (InterruptedException exception){
						
		            	return;
				}								
			}
		}
						
		Runnable[] horses = new Horse[NUM_HORSES];
		
		for(int i = 0; i < NUM_HORSES; i++) {

			horses[i] = new Horse(i + 1);
			threads[i] = new Thread(horses[i]);
			threads[i].start();
		}					
	}
	
	//IF quit mid race, stops all threads before exiting
	public void handleQuit() {
		if(ongoingRace == true)
			stopHorses();
	}
	
	private void stopHorses() {
		
		for(int i = 0; i < NUM_HORSES; i++) {
				threads[i].interrupt();
		}
	}
	
	private void winnerDialogBox(int winner, long time) {
		
	
		Alert alert = new Alert(AlertType.INFORMATION);
		
		alert.setTitle("Winner!");
		alert.setHeaderText(null);
			
		time /= 1000000;
		alert.setContentText("Horse " + winner + " has won the race in " + time + " milliseconds!");
		alert.showAndWait();		
	} 
	
	public Canvas[] getCanvasArr() {
		
		return horses;
	}
	public int getWinner() {		
		return winner;
	}

	
}
