package data;

import java.util.ArrayList;
import java.util.List;

public class Med {
	private int id;
	private String name;
	private List<Dosage> dosage;

	public Med() {
		this.dosage = new ArrayList<Dosage>();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Dosage> getDosage() {
		return dosage;
	}

	public void setDosage(List<Dosage> dosage) {
		this.dosage = dosage;
	}
}
