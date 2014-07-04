package data;

public class Dosage {
	private int time;
	private int amount;

	public Dosage(int time, int amount) {
		this.time = time;
		this.amount = amount;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

}
