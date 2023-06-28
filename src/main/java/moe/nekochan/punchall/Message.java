package moe.nekochan.punchall;

public class Message {

	private int weight;
	private String message;


	public Message(int weight, String message) {
		this.weight = weight;
		this.message = message;
	}


	public int getWeight() {
		return weight;
	}


	public String getMessage() {
		return message;
	}

}
