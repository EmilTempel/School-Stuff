package assembly;

public class EternalException extends Exception{

	
	public void printStackTrace() {
		System.out.println("ERROR! This code would have ran indefinetely.");
	}
}
