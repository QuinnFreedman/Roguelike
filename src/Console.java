public class Console{
	
	private static int logIndex = 0;
	
	
	public static void log(String s){
		String output = "";
		
		for(int i = 0; i < logIndex; i++){
			output += " . ";
		}
		output += s;
		System.out.println(output);
	}
	
	public static void log(int n, String s){
		if(s.length() > 0){
			log(s);
		}
		if(n == 0){
			logIndex = 0;
		}else{
			logIndex += n;
		}
			
	}
	public static void log(String s, int n){
		if(n == 0){
			logIndex = 0;
		}else{
			logIndex += n;
		}
		if(s.length() > 0){
			log(s);
		}
			
	}
	public static void log(){
		log("");
	}
	
	public static void benchmark(){
		benchmark(" ");
	}
	public static void benchmark(String string){
		long time = System.nanoTime();
		System.out.println("@"+string+((time - Debug.startTime) - Debug.lastTime)/1000000+" : "+(time - Debug.startTime)/1000000);
		Debug.lastTime = time - Debug.startTime;
	}

	public static void log(String a, String b) {
		String output = "";
		
		for(int i = 0; i < logIndex; i++){
			output += " . ";
		}
		output += a+","+b;
		System.out.println(output);
	}
	public static void log(int a, int b) {
		String output = "";
		
		for(int i = 0; i < logIndex; i++){
			output += " . ";
		}
		output += Integer.toString(a)+","+Integer.toString(b);
		System.out.println(output);
	}
}